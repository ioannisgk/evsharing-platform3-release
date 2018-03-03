package com.ioannisgk.evsharing.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.repositories.StationDAO;
import com.ioannisgk.evsharing.repositories.UserDAO;
import com.ioannisgk.evsharing.repositories.VehicleDAO;

@Service
public class PopulateDropdownsServiceImpl implements PopulateDropdownsService {
	
	// Total 5-minute segments that the system will be in operation
	public static final int TIMEFRAME_SIZE = 192;
	
	// Inject the user dao
	@Autowired
	private UserDAO userDAO;
	
	// Inject the administrator dao
	@Autowired
	private VehicleDAO vehicleDAO;
	
	// Inject the station service
	@Autowired
	private StationDAO stationDAO;
	
	// Hashmap needed to populate the dropdown options
	private LinkedHashMap<String, String> dropdownOptions;
	
	@Transactional
	public LinkedHashMap<String, String> getUsersOptions() {
		
		dropdownOptions = new LinkedHashMap<>();
	
		// Get all users in a list from the service
		List<User> theUsers = userDAO.getUsers();
		
		// Iterate list and create linked hashmap for dropdown options
		
		for (int i = 0; i < theUsers.size(); i++) {
			
			// Insert keys and values in hashmap for each user list iteration
			
			String key = Integer.toString(theUsers.get(i).getId());
			String value = theUsers.get(i).getUsername();
			dropdownOptions.put(key, value);
		}
		
		// Sort hashmap by value in ascending order
		dropdownOptions = (LinkedHashMap<String, String>) sortByComparator(dropdownOptions, true);
		
		return dropdownOptions;
	}
	
	@Transactional
	public LinkedHashMap<String, String> getVehiclesOptions() {
		
		dropdownOptions = new LinkedHashMap<>();
		
		// Get all vehicles in a list from the service
		List<Vehicle> theVehicles = vehicleDAO.getVehicles();
		
		// Iterate list and create linked hashmap for dropdown options
		
		for (int i = 0; i < theVehicles.size(); i++) {
			
			// Insert keys and values in hashmap for each vehicle list iteration
			
			String key = Integer.toString(theVehicles.get(i).getId());
			String value = theVehicles.get(i).getLicensePlates();
			dropdownOptions.put(key, value);
		}
		
		// Sort hashmap by value in ascending order
		dropdownOptions = (LinkedHashMap<String, String>) sortByComparator(dropdownOptions, true);
		
		return dropdownOptions;
	}
	
	@Transactional
	public LinkedHashMap<String, String> getStationsOptions() {
		
		dropdownOptions = new LinkedHashMap<>();
		
		// Get all stations in a list from the service
		List<Station> theStations = stationDAO.getStations();
		
		// Iterate list and create linked hashmap for dropdown options
		
		for (int i = 0; i < theStations.size(); i++) {
			
			// Insert keys and values in hashmap for each vehicle list iteration
			
			String key = Integer.toString(theStations.get(i).getId());
			String value = theStations.get(i).getName();
			dropdownOptions.put(key, value);
		}
		
		// Sort hashmap by value in ascending order
		dropdownOptions = (LinkedHashMap<String, String>) sortByComparator(dropdownOptions, true);
		
		return dropdownOptions;
	}
	
	@Transactional
	public LinkedHashMap<String, String> getTimeOptions() {
		
		dropdownOptions = new LinkedHashMap<>();
		
		// Iterate timeframe size and create linked hashmap for dropdown options
		
		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
			
			// Insert keys and values in hashmap for each timeframe option
			
			String key = Integer.toString(i);
			String value = getDateString(i);
			dropdownOptions.put(key, value);
		}
		
		// Sort hashmap by value in ascending order
		dropdownOptions = (LinkedHashMap<String, String>) sortByComparator(dropdownOptions, true);
		
		return dropdownOptions;
	}
	
	// Method to sort hashmap by comparator
	private Map<String, String> sortByComparator(Map<String, String> unsortMap, boolean order) {

        List<Entry<String, String>> list = new LinkedList<Entry<String, String>>(unsortMap.entrySet());

        // Sort the list based on values
        
        Collections.sort(list, new Comparator<Entry<String, String>>() {
            public int compare(Entry<String, String> o1, Entry<String, String> o2) {
            	
                if (order) return o1.getValue().compareTo(o2.getValue());
                else return o2.getValue().compareTo(o1.getValue());

            }
        });

        // Maintain insertion order with the help of linked list
        
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Entry<String, String> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	// Method to get date and time from minute index in current timeslice
	public String getDateString(int timeslice) {
		
		String currentDate = "";
		String formattedTime = "";
		int duration = timeslice * 5;
		
		try {
			
			// Set specific time for calendar 1
		    
		    Date time1 = new SimpleDateFormat("HH:mm").parse("06:00");
		    Calendar calendar1 = Calendar.getInstance();
		    calendar1.setTime(time1);
		    calendar1.add(Calendar.MINUTE, duration);
		    
		    // Format time and convert to string
		    
		    SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
		    formattedTime = format1.format(calendar1.getTime());
		    
		    // Get current date only in string format
		    
		    SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		    currentDate = format2.format(new Date());
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return currentDate + " " + formattedTime;
	}
}