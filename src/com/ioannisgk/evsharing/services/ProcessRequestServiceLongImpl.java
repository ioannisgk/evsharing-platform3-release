package com.ioannisgk.evsharing.services;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.utils.MyTasklet;

@Component("processRequestServiceLongImpl")
@Scope("prototype")
public class ProcessRequestServiceLongImpl implements ProcessRequestService {
	
	// Constants for charge cost per minute and base speed for traffic level
	
	public static final double CHARGE_COST_PER_MINUTE = 0.50;
	public static final double BASE_SPEED_FOR_TRAFFIC_LEVEL = 85;
	
	// Inject the station service
	@Autowired
	private StationService stationService;
	
	// Inject the vehicle service
	@Autowired
	private VehicleService vehicleService;
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Method to process user request, get result and save new route to the database
	public String requestResult(String currentMessage) {
		
		String result = "";
		String userID = "";
		String startStationID = "";
		String finishStationID = "";
		String startTime = "";
		
		/////////////////////////////////////////////////////////////////////
		// STEP 1: Iterate vehicle request message string from Android app //
		// Extract userID, startStationID, finishStationID, startTime      //
		/////////////////////////////////////////////////////////////////////
		
		int space = 0;
		int n = currentMessage.length();
		for (int i = 0; i < n; i++) {
		    char c = currentMessage.charAt(i);
		    if (c == ' ') space++;
		    if (space == 0) userID = userID + c;
		    if (space == 1) startStationID = startStationID + c;
		    if (space == 2) finishStationID = finishStationID + c;
		    if (space == 3) startTime = startTime + c;
		}
		
		// Remove leading/trailing spaces on extracted variables
		
		userID = userID.trim();
		startStationID = startStationID.trim();
		finishStationID = finishStationID.trim();
		startTime = startTime.trim();

        // Calculate trip duration between two stations in minutes
        int duration = tripDuration(Integer.parseInt(startStationID), Integer.parseInt(finishStationID));
        
        // Calculate charge level cost for user request trip duration
        double chargeCost = duration * CHARGE_COST_PER_MINUTE;

        // Create a list of candidate vehicles available for the user request
        List<Vehicle> candidateVehiclesList = new ArrayList<Vehicle>();
        
        // Create a list of better candidate vehicles available for the user request
        List<Vehicle> betterCandidateVehiclesList = new ArrayList<Vehicle>();
        
        // Create a list of better candidate vehicles with late routes available for the user request
        List<Vehicle> betterCandidateVehiclesListWithLateRoutes = new ArrayList<Vehicle>();
        
        // Create a list of best candidate vehicles available for the user request
        List<Vehicle> bestCandidateVehiclesList = new ArrayList<Vehicle>();
        
		//////////////////////////////////////////////////////////////////////
		// STEP 2A: Get vehicles that are in the user request start station //
		// Add those vehicles to the candidate vehicles list                //
		//////////////////////////////////////////////////////////////////////
        
        // Get vehicles that are currently in user request start station
        List<Vehicle> theVehicles = vehicleService.getVehiclesByField("station_id", Integer.parseInt(startStationID));
 
        for (int i = 0; i < theVehicles.size(); i++) {
        	
        	// Add vehicles to candidate vehicles list
        	candidateVehiclesList.add(theVehicles.get(i));
    	}
        
		/////////////////////////////////////////////////////////////////////
		// STEP 2B: Get vehicles that finish at user request start station //
		// Add those vehicles to the candidate vehicles list               //
		/////////////////////////////////////////////////////////////////////
        		
        // Get routes that finish at user request station, earlier than user request time
        List<Route> theEarlyRoutes = routeService.getRoutesWithFinishStationEarlierThanTime(
        																	Integer.parseInt(startStationID), startTime);
        
        for (int i = 0; i < theEarlyRoutes.size(); i++) {
        	
        	// Get current vehicle from those routes
        	Vehicle currentVehicle = vehicleService.getVehicle(theEarlyRoutes.get(i).getVehicleId());
        
        	// Add vehicles to candidate vehicles list
        	candidateVehiclesList.add(currentVehicle);
    	}
        
        if (candidateVehiclesList.size() == 0) {
        	
        	// There are no available vehicles at user request start station
        	result = "Denied";
        	
        } else {
        	
        	// At this point candidate vehicles list contains the available vehicles at user request start station

            for (int i = 0; i < candidateVehiclesList.size(); i++) {
            	
            	// Get current vehicle id
            	int currentVehicleId = candidateVehiclesList.get(i).getId();
            	
            	// Get routes that start at user request station, later than user request time
                List<Route> theLateRoutes = routeService.getRoutesOfVehicleWithStartStationLaterThanTime(
                											currentVehicleId, Integer.parseInt(startStationID), startTime);
                
    			///////////////////////////////////////////////////////////////////////////
    			// STEP 3A: Check if candidate vehicles have future routes               //
    			// Add those without future routes to the better candidate vehicles list //
    			///////////////////////////////////////////////////////////////////////////
                   
                if (theLateRoutes.size() == 0) {
                	
                	// Get current vehicle from those routes
                	Vehicle currentVehicle = vehicleService.getVehicle(currentVehicleId);
                	
                	// Add vehicles to better candidate vehicles list
                	betterCandidateVehiclesList.add(currentVehicle);
                	
                	
                	
                } else if (theLateRoutes.size() == 1) {
                	
				///////////////////////////////////////////////////////////////////////////////
				// STEP 3B: Check if candidate vehicles have only one future route           //
				// Add those vehicles to the better candidate vehicles with late routes list //
				///////////////////////////////////////////////////////////////////////////////
                	
                	// Get current vehicle from those routes
                	Vehicle currentVehicle = vehicleService.getVehicle(currentVehicleId);
                	
                	// Add vehicles to better candidate vehicles with late routes list
                	betterCandidateVehiclesListWithLateRoutes.add(currentVehicle);
                }
            }
        }
        
        // At this point better candidate vehicles list contains the available vehicles without future routes
        
        if (betterCandidateVehiclesList.size() > 0) {
        	
			/////////////////////////////////////////////////////////////////////////////
			// STEP 4: Calculate and set future charge for each vehicle in the list    //
			// Sort better candidate vehicle list by future charge in descending order //
			/////////////////////////////////////////////////////////////////////////////
        	
        	// Calculate and set future charge for each vehicle in the list
            betterCandidateVehiclesList = betterCandidateVehiclesListWithFutureCharge(betterCandidateVehiclesList);
        	
            // Sort better candidate vehicle list by future charge in descending order
        	Collections.sort(betterCandidateVehiclesList, Vehicle.VehicleCharge);
        	
			//////////////////////////////////////////////////////////////////////////////////////
			// STEP 5: Check if each vehicle in the list has enough charge to cover the trip    //
			// Add those vehicles to the best candidate vehicles list                           //
			//////////////////////////////////////////////////////////////////////////////////////
        	
        	for (int i = 0; i < betterCandidateVehiclesList.size(); i++) {
        		
        		if (betterCandidateVehiclesList.get(i).getFutureCharge() > chargeCost) {
                
                	// If current vehicle has enough charge to cover the trip, add it to best candidate vehicles list
        			bestCandidateVehiclesList.add(betterCandidateVehiclesList.get(i));
        		}
        	}
        	
            // At this point best candidate vehicles list contains all vehicles suitable for the trip
        	
    		/////////////////////////////////////////////////////////////////////////////////////////
    		// STEP 6: The best vehicle is the first on the best candidate vehicles list           //
    		// If there is a best vehicle, save new accepted route, else result equals to "Denied" //
    		/////////////////////////////////////////////////////////////////////////////////////////
            
            if (bestCandidateVehiclesList.size() > 0) {
            	
            	// Best vehicle with the most charge is the first in the list
            	Vehicle bestVehicle = bestCandidateVehiclesList.get(0);       
            	
            	// Save new accepted route to the database
            	saveNewRouteAccepted(Integer.parseInt(userID),
            							Integer.parseInt(startStationID),
            							Integer.parseInt(finishStationID),
            							bestVehicle.getId(),
            							startTime,
            							endTime(startTime, duration));

            	// There is at least one vehicle with enough charge to cover the trip
            	result = "Accepted";
            	
            } else {
            	
            	// There are no vehicles with enough charge to cover the trip
            	result = "Denied";
            }
            
        } else if (betterCandidateVehiclesList.size() == 0 || result.equals("Denied")) {        	
        	
        	// At this point there are no vehicles without future routes, with enough charge to cover the trip
        	// We need to find a candidate vehicle with future route, assign its route to a substitute vehicle
        	// and then assign this vehicle, which will not have a future route now, to the user request trip
        	
        	// Create a list of substitute vehicles available to substitute the better candidate vehicle with future route
            List<Vehicle> substituteVehiclesList = new ArrayList<Vehicle>();
        	
        	// Create a new hashmap that holds better candidate vehicle ids and arraylist of substitute vehicles ids
            HashMap<Integer, ArrayList<Integer>> substitutesMap = new HashMap<Integer, ArrayList<Integer>>();

        	for (int i = 0; i < betterCandidateVehiclesListWithLateRoutes.size(); i++) {
        		
        		/////////////////////////////////////////////////////////////////////////////////////////////////////
        		// STEP 7A: The better candidate vehicles list with late routes contains the available vehicles    //
        		// We check the next route of each vehicle, if it starts later than the start time + trip duration //
        		/////////////////////////////////////////////////////////////////////////////////////////////////////
            	
            	// Get current vehicle id
            	int currentVehicleId = betterCandidateVehiclesListWithLateRoutes.get(i).getId();
            	
            	// Get routes that start at user request station, later than user request time
                List<Route> theLateRoutes = routeService.getRoutesOfVehicleWithStartStationLaterThanTime(
                											currentVehicleId, Integer.parseInt(startStationID), startTime);
                
                if (isTime1BeforeTime2(startTime, duration, theLateRoutes.get(0).getStartTime())) {
                	
                    //////////////////////////////////////////////////////////////////////
                    // STEP 7B: Get vehicles that are in the user request start station //
                    // Add those vehicles to the substitute vehicles list               //
                    //////////////////////////////////////////////////////////////////////
                	
                	// Get vehicles that are currently in user request start station
                    List<Vehicle> theSubstituteVehicles = vehicleService.getVehiclesByField("station_id", Integer.parseInt(startStationID));
             
                    for (int j = 0; j < theSubstituteVehicles.size(); j++) {
                    	
                    	// Add vehicles to substitute vehicles list
                    	substituteVehiclesList.add(theSubstituteVehicles.get(j));
                	}
                    
                    /////////////////////////////////////////////////////////////////////
                    // STEP 7C: Get vehicles that finish at user request start station //
                    // Add those vehicles to the substitute vehicles list              //
                    /////////////////////////////////////////////////////////////////////
                    		
                    // Get routes that finish at user request station, earlier than current vehicle future route start time
                    List<Route> theSubstituteEarlyRoutes = routeService.getRoutesWithFinishStationEarlierThanTime(
                    																	Integer.parseInt(startStationID),
                    																	theLateRoutes.get(0).getStartTime());
                    
                    for (int j = 0; j < theSubstituteEarlyRoutes.size(); j++) {
                    	
                    	// Get current vehicle from those routes
                    	Vehicle currentVehicle = vehicleService.getVehicle(theSubstituteEarlyRoutes.get(j).getVehicleId());
                    
                    	// Add vehicles to substitute vehicles list
                    	substituteVehiclesList.add(currentVehicle);
                	}
                	
                    if (substituteVehiclesList.size() == 0) {
                    	
                    	// There are no available vehicles at user request start station to substitute current vehicle future route
                    	result = "Denied";
                    	
                    } else {
                    	
                    	// At this point substitute vehicles list contains the available vehicles to substitute current vehicle future route
                    	// We need to iterate substitute vehicles and add better candidate vehicle ids and arraylist of substitute ids to hashmap
                    	
                    	// Arraylist that holds the substitute vehicle ids for the current vehicle
                    	ArrayList<Integer> substituteIds = new ArrayList<>();
                    	
            			///////////////////////////////////////////////////////////////////////////////////
            			// STEP 8: Check if substitute vehicles have future routes                       //
            			// Add those vehicle ids, without future routes, to the arraylist in the hashmap //
            			///////////////////////////////////////////////////////////////////////////////////
                    	
                    	for (int j = 0; j < substituteVehiclesList.size(); j++) {
                        	
                        	// Get current vehicle id
                        	int currentSubstituteVehicleId = substituteVehiclesList.get(i).getId();
                        	
                        	// Get routes that start at user request station, later than current vehicle future route start time
                            List<Route> theSubstituteLateRoutes = routeService.getRoutesOfVehicleWithStartStationLaterThanTime(
                            															currentSubstituteVehicleId,
                            															Integer.parseInt(startStationID),
                            															theLateRoutes.get(0).getStartTime());
                    	
                            if (theSubstituteLateRoutes.size() == 0) {
                            	
                            	// Add substitute vehicle ids for current better candidate vehicle to the arraylist
                            	substituteIds.add(currentSubstituteVehicleId);
                            }
                    	}
                    	
                    	// Add current better candidate vehicle id with its candidate substitute vehicle ids arraylist in the map
                    	substitutesMap.put(currentVehicleId, substituteIds);
                    }
                }
        	}
        	
        	// If there is a candidate vehicle and substitute vehicles in the hashmap, continue
        	
        	if (!substitutesMap.isEmpty()) {
        	
	        	// At this point we have a hashmap that holds better candidate vehicle ids and arraylist of substitute vehicles ids
	        	
				/////////////////////////////////////////////////////////////////
				// STEP 9A: Iterate hashmap and get vehicles from hashmap keys //
				// Add those vehicles to the better candidate vehicles list    //
				/////////////////////////////////////////////////////////////////
	        	
	        	for (Integer currentVehicleId : substitutesMap.keySet()) {
	        		
	        		// Get hashmap keys and add vehicles to better candidate vehicles list
	        		betterCandidateVehiclesList.add(vehicleService.getVehicle(currentVehicleId));
	        	}
	
				/////////////////////////////////////////////////////////////////////////////
				// STEP 9B: Calculate and set future charge for each vehicle in the list   //
				// Sort better candidate vehicle list by future charge in descending order //
				/////////////////////////////////////////////////////////////////////////////
	        	
	        	// Calculate and set future charge for each vehicle in the list
	            betterCandidateVehiclesList = betterCandidateVehiclesListWithFutureCharge(betterCandidateVehiclesList);
	        	
	        	// Sort better candidate vehicle list by future charge in descending order
	        	Collections.sort(betterCandidateVehiclesList, Vehicle.VehicleCharge);
	        	
				////////////////////////////////////////////////////////////////////////////////////
				// STEP 10: Check if each vehicle in the list has enough charge to cover the trip //
				// Add those vehicles to the best candidate vehicles list                         //
				////////////////////////////////////////////////////////////////////////////////////
	
	        	for (int i = 0; i < betterCandidateVehiclesList.size(); i++) {
	        		
	        		if (betterCandidateVehiclesList.get(i).getFutureCharge() > chargeCost) {
	                
	                	// If current vehicle has enough charge to cover the trip, add it to best candidate vehicles list
	        			bestCandidateVehiclesList.add(betterCandidateVehiclesList.get(i));
	        		}
	        	}
	
	        	// At this point best candidate vehicles list contains all vehicles with enough charge for the trip
	        	// We need to check if they have a valid substitute vehicle with enough charge by checking the hashmap
	
	        	// Create a new hashmap that holds the vehicle id to assign to the user request and the vehicle id to substitute the future route
	            HashMap<Integer, Integer> finalSubstitutesMap = new HashMap<Integer, Integer>();
	            
	        	for (int i = 0; i < bestCandidateVehiclesList.size(); i++) {
	        		
	    			///////////////////////////////////////////////////////////////////////
	    			// STEP 11: Iterate best candidate vehicle list and get future route //
	    			// Calculate future route charge cost for each vehicle               //
	    			///////////////////////////////////////////////////////////////////////
	        		
	        		// Get current vehicle id
	            	int currentVehicleId = bestCandidateVehiclesList.get(i).getId();
	            	
	            	// Get routes that start at user request station, later than user request time
	                List<Route> theLateRoutes = routeService.getRoutesOfVehicleWithStartStationLaterThanTime(
	                											currentVehicleId, Integer.parseInt(startStationID), startTime);
	        		
	                // Get future route start time and end time
	                
	                String futureStartTime = theLateRoutes.get(0).getStartTime();
	        		String futureEndTime = theLateRoutes.get(0).getEndTime();
	        		
	        		// Calculate future trip duration
	        		int futureDuration = differenceInMinutes(futureStartTime, futureEndTime);
	        		
	        		// Calculate future trip charge cost
	        		double futureChargeCost = futureDuration * CHARGE_COST_PER_MINUTE;
	        		
	    			///////////////////////////////////////////////////////////////////////////////////////////
	    			// STEP 12: Check if each substitute vehicle has enough charge to cover the future route //
	    			// Get final substitutes hashmap with best vehicle ids and best substitute vehicle ids   //
	    			///////////////////////////////////////////////////////////////////////////////////////////
	        		
	        		// Get hashmap that holds the vehicle id to assign to the user request and the vehicle id to substitute the future route
	        		finalSubstitutesMap = getFinalSubstitutesMap(substitutesMap, currentVehicleId, futureChargeCost);
	
	        	}
	        		
	        	// At this point we have the final substitutes map, where the first key value pair contains
	    		// the best vehicle id to assign to the user request
	    		// and the vehicle id without future routes, to substitute the best vehicle future route
	        		
				////////////////////////////////////////////////////////////////////////////
				// STEP 13: Get the best vehicle from first key in final substitutes map  //
				// Get the best substitute vehicle from hashmap to cover the future route //
				////////////////////////////////////////////////////////////////////////////
	        	
	        	if (finalSubstitutesMap.size() > 0) {
	        		
		    		// Get hashmap first key
		    		int firstKey = (int) finalSubstitutesMap.keySet().toArray()[0];
		    		
		    		// Get best vehicle using the service
		    		Vehicle bestVehicle = vehicleService.getVehicle(firstKey);
		    		
		    		// Get best substitute vehicle using the service
		    		Vehicle bestSubstituteVehicle = vehicleService.getVehicle(finalSubstitutesMap.get(firstKey));
		    		
					////////////////////////////////////////////////////////////////////////////////////////////
					// STEP 14: Get best vehicle future route and assign the substitute vehicle to this route //
					// Assign best vehicle to user request trip and save new accepted route                   //
					////////////////////////////////////////////////////////////////////////////////////////////
		    		
		        	// Get routes of best vehicle that start at user request station, later than user request time
		            List<Route> theFutureRoutes = routeService.getRoutesOfVehicleWithStartStationLaterThanTime(
		            												bestVehicle.getId(), Integer.parseInt(startStationID), startTime);
		    		
		    		// Save new future route with updated substitute vehicle
		    		
		    		saveNewRouteAccepted(theFutureRoutes.get(0).getUserId(),
		    								theFutureRoutes.get(0).getStartStationId(),
		    								theFutureRoutes.get(0).getFinishStationId(),
		    								bestSubstituteVehicle.getId(),
		    								theFutureRoutes.get(0).getStartTime(),
		    								theFutureRoutes.get(0).getEndTime());
		    		
		    		// Get the future route id of the best vehicle id, that we have assigned the best substitute vehicle
		    		int theFutureRouteId = theFutureRoutes.get(0).getId();
		    		
		    		// Delete the old future route
		    		routeService.deleteRoute(theFutureRouteId);
		    		
		    		// Save new accepted route to the database
		    		saveNewRouteAccepted(Integer.parseInt(userID),
		    								Integer.parseInt(startStationID),
		    								Integer.parseInt(finishStationID),
		    								bestVehicle.getId(),
		    								startTime,
		    								endTime(startTime, duration));
		    		
		    		// The best vehicle has become available and is assigned to the user request route
		        	result = "Accepted";
		        	
	        	}
        	}
        }
        
        // If result has not been set yet, set result to "Denied"
        if (result.isEmpty()) result = "Denied";
        
        if (result.equals("Denied")) {
        	
        	// Save new denied route to the database
        	saveNewRouteDenied(Integer.parseInt(userID),
        							Integer.parseInt(startStationID),
        							Integer.parseInt(finishStationID),
        							startTime,
        							endTime(startTime, duration));
        }
        
		return result;
	}

	// Method to calculate trip duration between two stations in minutes
	public int tripDuration(int startStationID, int finishStationID) {
			
		Station station1 = stationService.getStation(startStationID);
		Station station2 = stationService.getStation(finishStationID);
		
		Double lat1 = station1.getLatitude();
		Double lon1 = station1.getLongitude();
		Double lat2 = station2.getLatitude();
		Double lon2 = station2.getLongitude();
		
		// Calculate distance in km between two stations
		// https://dzone.com/articles/distance-calculation-using-3
		
		double theta = lon1 - lon2;
        double distance = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) 
        															* Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        distance = Math.acos(distance);
        distance = rad2deg(distance);
        distance = distance * 60 * 1.1515;
        distance = distance * 1.609344;
        
        // Calculate total traffic level for both stations
        double totalTrafficLevel = station1.getTrafficLevel() + station2.getTrafficLevel();
        
        // Calculate theoretical speed in km/h
        double speed = BASE_SPEED_FOR_TRAFFIC_LEVEL / totalTrafficLevel;
        
        // Calculate trip duration in minutes
        double tripDuration = (distance / speed) * 60;
       
        // Round and return number of minutes
		return (int) Math.round(tripDuration);
	}
	
	// Convert decimal degrees to radians
	public double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}
	
	// Convert radians to decimal degrees
	public double rad2deg(double rad) {
	    return (rad * 180.0 / Math.PI);
	}
	
	// Calculate end time for route
	public String endTime(String startTime, int duration) {
		
		String formattedTime = "";
		
		try {
			
			// Set specific time for calendar 1 and add calculated trip duration
			
			Date time1 = new SimpleDateFormat("HH:mm").parse(startTime);
			Calendar calendar1 = Calendar.getInstance();
		    calendar1.setTime(time1);
		    calendar1.add(Calendar.MINUTE, duration);
		    
		    // Format time and convert to string
		    
		    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
		    formattedTime = format1.format(calendar1.getTime());
		    
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return formattedTime;
	}
	
	// Method to calculate trip duration between two times in minutes
	public int differenceInMinutes(String time1String, String time2String) {
			
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
	
	// Compare if time1 is before time2
	public boolean isTime1BeforeTime2(String time1String, int duration, String time2String) {
		
		boolean retVal = false;
		
		try {
			// Set specific time for calendar 1 and add calculated trip duration
			
			Date time1 = new SimpleDateFormat("HH:mm").parse(time1String);
			Calendar calendar1 = Calendar.getInstance();
		    calendar1.setTime(time1);
		    calendar1.add(Calendar.MINUTE, duration);
		    
		    // Set specific time for calendar 2
		    
		    Date time2 = new SimpleDateFormat("HH:mm").parse(time2String);
		    Calendar calendar2 = Calendar.getInstance();
		    calendar2.setTime(time2);
		    
		    // If time1 is before time2 set retVal to true
		    if (calendar1.getTime().before(calendar2.getTime())) retVal = true;
		    
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return retVal;
	}
	
	// Method to calculate and set future charge for each vehicle in the list
	public List<Vehicle> betterCandidateVehiclesListWithFutureCharge(List<Vehicle> betterCandidateVehiclesList) {
			
		for (int i = 0; i < betterCandidateVehiclesList.size(); i++) {
        	
        	// Get current vehicle id
        	int currentVehicleId = betterCandidateVehiclesList.get(i).getId();
        	
        	// Get all previous routes for current vehicle
        	List<Route> allPreviousRoutes = routeService.getRoutesByField("vehicle_id", currentVehicleId);
        	
        	double totalChargeCost = 0;
        	
        	// Calculate charge cost from all previous routes for this vehicle
        	
        	for (int j = 0; j < allPreviousRoutes.size(); j++) {
        		
        		double currentChargeCost = 0;
        		String currentStartTime = allPreviousRoutes.get(j).getStartTime();
        		String currentEndTime = allPreviousRoutes.get(j).getEndTime();
        		
        		// Calculate each trip duration
        		int currentDuration = differenceInMinutes(currentStartTime, currentEndTime);
        		
        		// Calculate each trip charge cost
        		currentChargeCost = currentDuration * CHARGE_COST_PER_MINUTE;
        		
        		// Calculate total charge cost
        		totalChargeCost = totalChargeCost + currentChargeCost;
        	}
        	
        	// Calculate and save future charge level for this vehicle
        	
        	double futureCharge = betterCandidateVehiclesList.get(i).getCharge() - totalChargeCost;
        	betterCandidateVehiclesList.get(i).setFutureCharge(futureCharge);
        }
		
		return betterCandidateVehiclesList;
	}
	
	// Method to get hashmap that holds the vehicle id to assign to the user request and the vehicle id to substitute the future route
	public HashMap<Integer, Integer> getFinalSubstitutesMap(HashMap<Integer, ArrayList<Integer>> substitutesMap,
																		int currentVehicleId, double futureChargeCost) {
		
		HashMap<Integer, Integer> finalSubstitutesMap = new HashMap<Integer, Integer>();
			
		// Iterate hashmap to see if there is a valid substitute with enough charge
		for (int j = 0; j < substitutesMap.get(currentVehicleId).size(); j++) {
			
			// Get current substitute vehicle id
        	int currentSubstituteVehicleId = substitutesMap.get(currentVehicleId).get(j);
        	
        	// Get all previous routes for current vehicle
        	List<Route> allPreviousRoutes = routeService.getRoutesByField("vehicle_id", currentSubstituteVehicleId);
        	
        	double totalChargeCost = 0;
        	
        	// Calculate charge cost from all previous routes for this vehicle
        	
        	for (int k = 0; k < allPreviousRoutes.size(); k++) {
        		
        		double currentChargeCost = 0;
        		String currentStartTime = allPreviousRoutes.get(k).getStartTime();
        		String currentEndTime = allPreviousRoutes.get(k).getEndTime();
        		
        		// Calculate each trip duration
        		int currentDuration = differenceInMinutes(currentStartTime, currentEndTime);
        		
        		// Calculate each trip charge cost
        		currentChargeCost = currentDuration * CHARGE_COST_PER_MINUTE;
        		
        		// Calculate total charge cost
        		totalChargeCost = totalChargeCost + currentChargeCost;
        	}
        	
        	// Get current substitute vehicle object
        	Vehicle currentVehicle = vehicleService.getVehicle(currentSubstituteVehicleId);
        	
        	// Calculate and save future charge level for this substitute vehicle
        	
        	double futureCharge = currentVehicle.getCharge() - totalChargeCost;
        	currentVehicle.setFutureCharge(futureCharge);
        	
        	if (futureCharge > futureChargeCost) {
        		
        		// Put vehicle ids to the hashmap
        		finalSubstitutesMap.put(currentVehicleId, currentSubstituteVehicleId);
        	}
		}
		
		return finalSubstitutesMap;
	}
	
	// Save new accepted route to the database
	public void saveNewRouteAccepted(int userID, int startStationID, int finishStationID, int vehicleId, String startTime, String endTime) {
		
		// Create new route
    	Route currentRoute = new Route();
    	
    	// Assign all attributes to the route object
    	
    	currentRoute.setUserId(userID);
    	currentRoute.setStartStationId(startStationID);
    	currentRoute.setFinishStationId(finishStationID);
    	currentRoute.setVehicleId(vehicleId);
    	currentRoute.setStartTime(startTime);
    	currentRoute.setEndTime(endTime);
    	currentRoute.setStatus("Accepted");
    	
    	// Save route to the database using the service
    	routeService.saveRoute(currentRoute);
	}
	
	// Save new denied route to the database
	public void saveNewRouteDenied(int userID, int startStationID, int finishStationID, String startTime, String endTime) {
		
		// Create new route
    	Route currentRoute = new Route();
    	
    	// Assign all attributes to the route object
    	
    	currentRoute.setUserId(userID);
    	currentRoute.setStartStationId(startStationID);
    	currentRoute.setFinishStationId(finishStationID);
    	currentRoute.setStartTime(startTime);
    	currentRoute.setEndTime(endTime);
    	currentRoute.setStatus("Denied");
    	
    	// Save route to the database using the service
    	routeService.saveRoute(currentRoute);
	}
}