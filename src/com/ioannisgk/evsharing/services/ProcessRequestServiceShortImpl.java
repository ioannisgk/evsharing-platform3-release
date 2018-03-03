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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

@Component("processRequestServiceShortImpl")
@Scope("prototype")
public class ProcessRequestServiceShortImpl implements ProcessRequestService {
	
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
        	
			///////////////////////////////////////////////////////////////////////////
			// STEP 3: Check if candidate vehicles have future routes                //
			// Add those without future routes to the better candidate vehicles list //
			///////////////////////////////////////////////////////////////////////////
        	
            for (int i = 0; i < candidateVehiclesList.size(); i++) {
            	
            	// Get current vehicle id
            	int currentVehicleId = candidateVehiclesList.get(i).getId();
            	
            	// Get routes that start at user request station, later than user request time
                List<Route> theLateRoutes = routeService.getRoutesOfVehicleWithStartStationLaterThanTime(
                											currentVehicleId, Integer.parseInt(startStationID), startTime);
                
                if (theLateRoutes.size() == 0) {
                	
                	// Get current vehicle from those routes
                	Vehicle currentVehicle = vehicleService.getVehicle(currentVehicleId);
                	
                	// Add vehicles to better candidate vehicles list
                	betterCandidateVehiclesList.add(currentVehicle);
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
        	
			///////////////////////////////////////////////////////////////////////////////////
			// STEP 5: Check if each vehicle in the list has enough charge to cover the trip //
			// Add those vehicles to the best candidate vehicles list                        //
			///////////////////////////////////////////////////////////////////////////////////

        	for (int i = 0; i < betterCandidateVehiclesList.size(); i++) {
        		
        		if (betterCandidateVehiclesList.get(i).getFutureCharge() > chargeCost) {
                
                	// If current vehicle has enough charge to cover the trip, add it to best candidate vehicles list
        			bestCandidateVehiclesList.add(betterCandidateVehiclesList.get(i));
        		}
        	}
            
        } else {
        	
        	// There are no available vehicles at user request start station without future routes
        	result = "Denied";
        }
        
        // At this point best candidate vehicles list contains all vehicles suitable for the trip
        
		/////////////////////////////////////////////////////////////////////////////////////
		// STEP 6: The best vehicle is the first on the best candidate vehicles list       //
		// If there is a best vehicle, save new accepted route, else save new denied route //
		/////////////////////////////////////////////////////////////////////////////////////
        
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