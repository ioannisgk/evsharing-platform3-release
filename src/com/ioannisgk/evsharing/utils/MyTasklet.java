package com.ioannisgk.evsharing.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.batch.core.StepContribution; 
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ioannisgk.evsharing.controllers.LiveChartsController;
import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.entities.Simulation;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.services.ProcessRequestService;
import com.ioannisgk.evsharing.services.RouteService;
import com.ioannisgk.evsharing.services.SimulationService;
import com.ioannisgk.evsharing.services.StationService;
import com.ioannisgk.evsharing.services.VehicleService;  

public class MyTasklet implements Tasklet {
	
	// Total 5-minute segments that the system will be in operation
	
	public static final int TIMEFRAME_SIZE = 192;
	public static final double CHARGE_COST_PER_MINUTE = 0.50;
	
	// Inject the station service
	@Autowired
	private StationService stationService;
	
	// Inject the vehicle service
	@Autowired
	private VehicleService vehicleService;
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Inject the simulation service
	@Autowired
	private SimulationService simulationService;
	
	// Inject the process request service short mode
	@Autowired
	@Qualifier("processRequestServiceShortImpl")
	private ProcessRequestService processRequestServiceShortImpl;
	
	// Inject the process request service long mode
	@Autowired
	@Qualifier("processRequestServiceLongImpl")
	private ProcessRequestService processRequestServiceLongImpl;
	
	// Inject the process mode helper
	@Autowired
	private ProcessModeHelper processModeHelper;
	
	// Attribute for tasklet mode
	private String mode = "";
	
	// Inject the live charts controller
	@Autowired
	private LiveChartsController liveChartsController;
	
	// Class attributes used for live charts
	
	private HashMap<Integer, int[]> stationsMap;
	private HashMap<Integer, int[]> vehiclesMap;
	private HashMap<Integer, double[]> chargeMap;
	private int[] totalRequests;
	private int[] acceptedRequests;
	private int[] deniedRequests;

	@Override 
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		
		// Get all stations from the service
        List<Station> theStations = stationService.getStations();
        
        // Get all vehicles from the service
        List<Vehicle> theVehicles = vehicleService.getVehicles();
        
        // Get all routes from the service
        List<Route> theRoutes = routeService.getRoutes();
        
        // Get number of current stations in the system
        int countStations = theStations.size();
        
        // Get number of current vehicles in the system
        int countVehicles = theVehicles.size();
        
        // Get number of current routes in the system
        int countRoutes = theRoutes.size();
        
		////////////////////////////////////////////////////////////////////////
		// STEP 1: Assign vehicles to stations with the highest traffic level //
		////////////////////////////////////////////////////////////////////////
        
        // Assign vehicles to stations with the highest traffic level  
        for (int i = 0; i < countVehicles; i++) {
        	
        	// Get each station id (the service gets stations by traffic level in desc order)
        	int currentStationId = theStations.get(i).getId();
        	
        	// Assign vehicles to stations with the highest traffic level
        	theVehicles.get(i).setStationId(currentStationId);
        	
        	// Update the vehicle object with each current station to the database
        	vehicleService.saveVehicle(theVehicles.get(i));
		}
		
        // Create a new hashmap that holds arrays of the sum of vehicles in a specific station
        stationsMap = new HashMap<Integer, int[]>();
    
        // Create a new hashmap that holds arrays of the current station of a specific vehicle
        vehiclesMap = new HashMap<Integer, int[]>();
        
        // Create a new hashmap that holds arrays of the current charge level of a specific vehicle
        chargeMap = new HashMap<Integer, double[]>();
        
		/////////////////////////////////////////////////////////////
		// STEP 2A: Set up "stations - number of vehicles" hashmap //
		/////////////////////////////////////////////////////////////
        
        // Add timeframe arrays to stations hashmap
        for (int i = 0; i < countStations; i++) {
            	
            // Get current station id
            int currentStationID = theStations.get(i).getId();
            	
            // Create a new array that holds array of the sum of vehicles in this station
            int[] timeframe = new int[TIMEFRAME_SIZE];
            	
            // Add a new map entry with current station id and an array of sum of vehicles
            stationsMap.put(currentStationID, timeframe);
        }
        
		//////////////////////////////////////////////////////////
		// STEP 2B: Set up "vehicles - current station" hashmap //
		//////////////////////////////////////////////////////////
		
        // Add timeframe arrays to vehicles hashmap
        for (int i = 0; i < countVehicles; i++) {
            	
            // Get current station id
            int currentVehicleID = theVehicles.get(i).getId();
            	
            // Create a new array that holds array of the current station of a specific vehicle
            int[] timeframe = new int[TIMEFRAME_SIZE];
            	
            // Add a new map entry with current vehicle id and an array of current stations
            vehiclesMap.put(currentVehicleID, timeframe);
        }
        
		//////////////////////////////////////////////////////////
		// STEP 2C: Set up "charge level - per vehicle" hashmap //
		//////////////////////////////////////////////////////////
        
        // Add timeframe arrays to charge hashmap
        for (int i = 0; i < countVehicles; i++) {
            	
            // Get current station id
            int currentVehicleID = theVehicles.get(i).getId();
            	
            // Create a new array that holds array of the current charge level of a specific vehicle
            double[] timeframe = new double[TIMEFRAME_SIZE];
            	
            // Add a new map entry with current vehicle id and an array of the current charge level
            chargeMap.put(currentVehicleID, timeframe);
        }
        
		/////////////////////////////////////////////////////////////////////////////
		// STEP 3: Update hashmaps according to starting positions of the vehicles //
		/////////////////////////////////////////////////////////////////////////////
        
        // Update hashmaps according to starting positions of the vehicles
        for (int i = 0; i < countVehicles; i++) {
        	
            // Get current station id from each vehicle
            int currentStationID = theVehicles.get(i).getStationId();
            
            // Get current vehicle id from each vehicle
            int currentVehicleID = theVehicles.get(i).getId();
            	
            // Create a new array that holds the sum of vehicles in this station
            int[] currentStationsTimeframe = new int[TIMEFRAME_SIZE];
            
            // Create a new array that holds the current station of a specific vehicle
            int[] currentVehiclesTimeframe = new int[TIMEFRAME_SIZE];
            
            // Create a new array that holds the current charge level of a specific vehicle
            double[] currentChargeTimeframe = new double[TIMEFRAME_SIZE];
            
            // Get current station array of the sum of vehicles timeframe
            currentStationsTimeframe = stationsMap.get(currentStationID);
            
            // Get current vehicle array of the current station of a specific vehicle
            currentVehiclesTimeframe = vehiclesMap.get(currentVehicleID);
            
            // Get current vehicle array of the current charge level of a specific vehicle
            currentChargeTimeframe = chargeMap.get(currentVehicleID);
              
            // Add a vehicle to current station timeframe
            currentStationsTimeframe = addVehicle(currentStationsTimeframe, "06:03");
            
            // Update a current station to vehicle timeframe
            currentVehiclesTimeframe = updateVehicle(currentVehiclesTimeframe, "06:03", "06:03", currentStationID);
            
            // Update a charge level to current charge timeframe
            currentChargeTimeframe = updateCharge(currentChargeTimeframe, "06:03", "06:03", 0);

            // Update hashmap with new station timeframe
            stationsMap.put(currentStationID, currentStationsTimeframe);
            
            // Update hashmap with new vehicle timeframe
            vehiclesMap.put(currentVehicleID, currentVehiclesTimeframe);
            
            // Update hashmap with new charge level timeframe
            chargeMap.put(currentVehicleID, currentChargeTimeframe);
        }
        
		////////////////////////////////////////////////////////////////////////////////////////////
		// STEP 4: If mode is simulation, get each request and pass it to process request service //
		////////////////////////////////////////////////////////////////////////////////////////////

        if (mode.equals("simulation")) {
        	
        	// Get all simulation requests from the service
        	List<Simulation> theSimulationRequests = simulationService.getSimulations();
        	
        	// Get message from each request and pass it to process request service
        	
        	for (int i = 0; i < theSimulationRequests.size(); i++) {
        		
        		// Process only simulation requests with a status that is not "Processed"
        		
        		if(!theSimulationRequests.get(i).getStatus().equals("Processed")) {
        			
	        		String result = "";
	        		String currentMessage = theSimulationRequests.get(i).getMessage();
	        		
	        		// Get process mode according to user selection
					String currentMode = processModeHelper.getMode();
					
					if (currentMode.equals("shortMode")) {
						
						// Process request and get result
						result = processRequestServiceShortImpl.requestResult(currentMessage);
						
					} else if (currentMode.equals("longMode")) {
						
						// Process request and get result
						result = processRequestServiceLongImpl.requestResult(currentMessage);
						
					}
					
					// Get current simulation request
					Simulation currentSimulationRequest = theSimulationRequests.get(i);
					
					// Set current simulation request status to "Processed"
					currentSimulationRequest.setStatus("Processed");
					
					// Save current simulation request
					simulationService.saveSimulation(currentSimulationRequest);
					
					System.out.println("Mode:" + currentMode);
					System.out.println("Result:" + result);
        		}
        	}
        }
        
		//////////////////////////////////////////////////////////////////////////
		// STEP 5: Update hashmaps according to accepted routes of the vehicles //
		//////////////////////////////////////////////////////////////////////////
        
        // Update hashmaps according to accepted (saved to the database) routes of the vehicles
        for (int i = 0; i < countRoutes; i++) {
        	
            // Get start station id from each route
            int startStationID = theRoutes.get(i).getStartStationId();
            
            // Get finish station id from each route
            int finishStationID = theRoutes.get(i).getFinishStationId();
            
            // Get current vehicle id from each route
            int currentVehicleID = theRoutes.get(i).getVehicleId();

            // Create a new array that holds the sum of vehicles in the start station
            int[] currentStartStationTimeframe = new int[TIMEFRAME_SIZE];
            
            // Get current route start station timeframe
            currentStartStationTimeframe = stationsMap.get(startStationID);
            
            // Create a new array that holds the sum of vehicles in the finish station
            int[] currentFinishStationTimeframe = new int[TIMEFRAME_SIZE];
            
            // Get current route finish station timeframe
            currentFinishStationTimeframe = stationsMap.get(finishStationID);
            
            // Create a new array that holds the current station of a specific vehicle
            int[] currentVehiclesTimeframe = new int[TIMEFRAME_SIZE];
            
            // Get current station of a specific vehicle timeframe
            currentVehiclesTimeframe = vehiclesMap.get(currentVehicleID);
            
            // Create a new array that holds the current charge level of a specific vehicle
            double[] currentChargeTimeframe = new double[TIMEFRAME_SIZE];
            
            // Get current charge level of a specific vehicle timeframe
            currentChargeTimeframe = chargeMap.get(currentVehicleID);
            
            // Get start time from each route
            String startTime = theRoutes.get(i).getStartTime();
            
            // Get finish time from each route
            String endTime = theRoutes.get(i).getEndTime();
            
            // Get duration from each route
            int duration = tripDuration(startTime, endTime);
            
            // Get charge level cost for duration
            double chargeCost = duration * CHARGE_COST_PER_MINUTE;

            // Remove a vehicle from the current start station timeframe
            currentStartStationTimeframe = removeVehicle(currentStartStationTimeframe, startTime);

            // Add a vehicle to the current finish station timeframe
            currentFinishStationTimeframe = addVehicle(currentFinishStationTimeframe, endTime);
            
            // Update a current station to vehicle timeframe
            currentVehiclesTimeframe = updateVehicle(currentVehiclesTimeframe, startTime, endTime, finishStationID);

            // Update a charge level to current charge timeframe
            currentChargeTimeframe = updateCharge(currentChargeTimeframe, startTime, endTime, chargeCost);
            
            // Update stations, vehicles and charge levels timeframes
            
            stationsMap.put(startStationID, currentStartStationTimeframe);
            stationsMap.put(finishStationID, currentFinishStationTimeframe);
            vehiclesMap.put(currentVehicleID, currentVehiclesTimeframe);
            chargeMap.put(currentVehicleID, currentChargeTimeframe);
        }
        
        // showHashmapContents1(stationsMap);
        // showHashmapContents1(vehiclesMap);
        // showHashmapContents2(chargeMap);
        
        // Array that holds total requests during time
        totalRequests = new int[TIMEFRAME_SIZE];
        
        // Array that holds total accepted requests during time
        acceptedRequests = new int[TIMEFRAME_SIZE];
        
        // Array that holds total denied requests during time
        deniedRequests = new int[TIMEFRAME_SIZE];
        
        // Get all accepted routes from the service
        List<Route> theAcceptedRoutes = routeService.getRoutes();
        
        // Get all denied routes from the service
        List<Route> theDeniedRoutes = routeService.getRoutesByField("status", "Denied");
        
		///////////////////////////////////////////////////////////////////////////////////////////
		// STEP 6A: Update total requests and accepted requests arrays according to saved routes //
		///////////////////////////////////////////////////////////////////////////////////////////
        
        // Update total requests and accepted requests arrays according to saved routes of the vehicles
        for (int i = 0; i < theAcceptedRoutes.size(); i++) {
        	
        	// Get the minute index from current route
    		int minuteIndex = getMinuteIndex(theAcceptedRoutes.get(i).getStartTime());
        	
    		for (int j = 0; j < TIMEFRAME_SIZE; j++) {
    			
    			// Add a total request to all 5 minute time slices after minute index
    			if (j >= minuteIndex) totalRequests[j]++;
    	     	   
    			// Add an accepted request to all 5 minute time slices after minute index
    			if (j >= minuteIndex) acceptedRequests[j]++;
            }
        }
        
		/////////////////////////////////////////////////////////////////////////////////////////
		// STEP 6B: Update total requests and denied requests arrays according to saved routes //
		/////////////////////////////////////////////////////////////////////////////////////////
        
        // Update denied requests arrays according to saved routes of the vehicles
        for (int i = 0; i < theDeniedRoutes.size(); i++) {
        	
        	// Get the minute index from current route
    		int minuteIndex = getMinuteIndex(theDeniedRoutes.get(i).getStartTime());
        	
    		for (int j = 0; j < TIMEFRAME_SIZE; j++) {
    			
    			// Add a total request to all 5 minute time slices after minute index
    			if (j >= minuteIndex) totalRequests[j]++;
    	     	   
    			// Add a denied request to all 5 minute time slices after minute index
    			if (j >= minuteIndex) deniedRequests[j]++;
            }
        }
        
		/////////////////////////////////////////////////////////////////////////////////////
		// STEP 6C: Use controller setter methods to send data to live charts periodically //
		/////////////////////////////////////////////////////////////////////////////////////
        
        liveChartsController.setTotalRequests(totalRequests);
        liveChartsController.setAcceptedRequests(acceptedRequests);
        liveChartsController.setDeniedRequests(deniedRequests);
        liveChartsController.setStationsMap(stationsMap);
        liveChartsController.setChargeMap(chargeMap);
        liveChartsController.setVehiclesMap(vehiclesMap);
        
		return RepeatStatus.FINISHED; 
	}
	
	// Getters and setters
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	// Method to show stations and vehicles hashmap contents
	public void showHashmapContents1(HashMap<Integer, int[]> theMap) {
		
		// Iterate hashmap and print contents
		
		Set set = theMap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
        	
           Map.Entry entry = (Map.Entry)iterator.next();
           System.out.println("StationID or VehicleID: "+ entry.getKey());
           
           int[] timeframe = theMap.get(entry.getKey());
           
           for (int j = 0; j < TIMEFRAME_SIZE; j++) {
        	   System.out.print("[" + j + "]: " + timeframe[j] + " ");
           }
           
           System.out.println("");
        }
	}
	
	// Method to show charge hashmap contents
	public void showHashmapContents2(HashMap<Integer, double[]> theMap) {
		
		// Iterate hashmap and print contents
		
		Set set = theMap.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
        	
           Map.Entry entry = (Map.Entry)iterator.next();
           System.out.println("VehicleID: "+ entry.getKey());
           
           double[] timeframe = theMap.get(entry.getKey());
           
           for (int j = 0; j < TIMEFRAME_SIZE; j++) {
        	   System.out.print("[" + j + "]: " + timeframe[j] + " ");
           }
           
           System.out.println("");
        }
	}
	
	// Method to calculate trip duration between two times in minutes
	public int tripDuration(String time1String, String time2String) {
			
		int diffMinutes = 0;
		
		try {
			// Set specific time for calendar 1
			
			Date time1 = new SimpleDateFormat("HH:mm").parse(time1String);
			Calendar calendar1 = Calendar.getInstance();
		    calendar1.setTime(time1);
		    
		    // Set specific time for calendar 2
		    
		    Date time2 = new SimpleDateFormat("HH:mm").parse(time2String);
		    Calendar calendar2 = Calendar.getInstance();
		    calendar2.setTime(time2);
		    
		    // Find difference between two times in minutes
		    
			long diffSeconds = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000;
			diffMinutes = (int) (diffSeconds / 60);
		    
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return diffMinutes;
	}
	
	// Method to remove a vehicle from the current station timeframe
	public int[] removeVehicle(int[] currentStationsTimeframe, String timeString) {
		
		// Get the minute index in current timeslice
		int minuteIndex = getMinuteIndex(timeString);
		
		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
     	   
			// Remove a vehicle from all 5 minute time slices after minute index
			if ((i >= minuteIndex) && (currentStationsTimeframe[i] > 0)) currentStationsTimeframe[i]--;
        }
		
		return currentStationsTimeframe;
	}
	
	// Method to add a vehicle to the current station timeframe
	public int[] addVehicle(int[] currentStationsTimeframe, String timeString) {
		
		// Get the minute index in current timeslice
		int minuteIndex = getMinuteIndex(timeString);
		
		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
     	   
			// Add a vehicle to all 5 minute time slices after minute index
			if (i >= minuteIndex) currentStationsTimeframe[i]++;
        }
		
		return currentStationsTimeframe;
	}
	
	// Method to update a current station to vehicle timeframe
	public int[] updateVehicle(int[] currentVehiclesTimeframe, String startTime, String endTime, int finishStationID) {
		
		// Get the minute index in current timeslice
		int minuteStartIndex = getMinuteIndex(startTime);
				
		// Get the minute index in current timeslice
		int minuteEndIndex = getMinuteIndex(endTime);
		
		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
	     	   
			if (!startTime.equals(endTime)) {
					
				// Update a vehicle to all 5 minute time slices during the trip	
				if ((i >= minuteStartIndex) && (i < minuteEndIndex)) currentVehiclesTimeframe[i] = 0;				
			} 
					
			// Update a vehicle to all 5 minute time slices after minute end index
			if (i >= minuteEndIndex) currentVehiclesTimeframe[i] = finishStationID;
		}

		return currentVehiclesTimeframe;
	}
	
	// Method to update a charge level to current charge timeframe
	public double[] updateCharge(double[] currentChargeTimeframe, String startTime, String endTime, double chargeCost) {
		
		// Get the minute index in current timeslice
		int minuteStartIndex = getMinuteIndex(startTime);
		
		// Get the minute index in current timeslice
		int minuteEndIndex = getMinuteIndex(endTime);
		
		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
				
			// Calculate the change step of charge level in each 5 minutes time
			double chargeChangeStep = chargeCost / (minuteEndIndex - minuteStartIndex + 1);
	
			if ((i >= minuteStartIndex) && (i < minuteEndIndex)) {
					
				// Update a charge level from start to finish 5 minute time slices
				currentChargeTimeframe[i] = currentChargeTimeframe[i - 1] - chargeChangeStep;
				
				// Round charge level up to 2 decimal places
				currentChargeTimeframe[i] = Math.round(currentChargeTimeframe[i] * 100.0) / 100.0;
			}

			if (i >= minuteEndIndex) {
				
				// Update a charge level to all 5 minute time slices after minute end index
				
				if (minuteStartIndex == 0) currentChargeTimeframe[i] = 100 - chargeCost;
				else currentChargeTimeframe[i] = currentChargeTimeframe[minuteStartIndex - 1] - chargeCost;
				
				// Round charge level up to 2 decimal places
				currentChargeTimeframe[i] = Math.round(currentChargeTimeframe[i] * 100.0) / 100.0;
			}
        }
		
		return currentChargeTimeframe;
	}
	
	// Method to get the minute index in current timeslice
	public int getMinuteIndex(String timeString) {
		
		int startMinutes = 0;
		try {
			
			// Set specific time for calendar 1
		    
		    Date time1 = new SimpleDateFormat("HH:mm").parse("06:00");
		    Calendar calendar1 = Calendar.getInstance();
		    calendar1.setTime(time1);
		    
		    // Set specific time for calendar 2
			
		    Date time2 = new SimpleDateFormat("HH:mm").parse(timeString);
		    Calendar calendar2 = Calendar.getInstance();
		    calendar2.setTime(time2);
		    
			// Find start index of array (which 5 minute slice corresponds to the start time)
		    
			long startSeconds = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000;
			startMinutes = (int) (startSeconds / 60);

			double start5Minutes = (startMinutes / 5) - 0.5;
			startMinutes = (int) Math.round(start5Minutes);
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return startMinutes;
	}
}