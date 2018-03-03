package com.ioannisgk.evsharing.controllers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Simulation;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.VehicleDAO;
import com.ioannisgk.evsharing.services.LoadMapService;
import com.ioannisgk.evsharing.services.LoadSimulationsService;
import com.ioannisgk.evsharing.services.PopulateDropdownsService;
import com.ioannisgk.evsharing.services.SimulationService;
import com.ioannisgk.evsharing.services.StationService;
import com.ioannisgk.evsharing.services.UserService;
import com.ioannisgk.evsharing.services.VehicleService;
import com.ioannisgk.evsharing.utils.MyTasklet;

@Controller
@RequestMapping("/charts")
public class LiveChartsController {
	
	// Total 5-minute segments that the system will be in operation
	public static final int TIMEFRAME_SIZE = 192;
	
	// Inject the simulation service
	@Autowired
	private SimulationService simulationService;
	
	// Inject the load simulations service
	@Autowired
	private LoadSimulationsService loadSimulationsService;
	
	// Inject the user service
	@Autowired
	private UserService userService;
	
	// Inject the station service
	@Autowired
	private StationService stationService;
	
	// Inject the vehicle service
	@Autowired
	private VehicleService vehicleService;
	
	// Inject the my tasklet service
	@Autowired
	private MyTasklet myTasklet;
	
	// Inject the scheduler factory service
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	// Inject the populate dropdowns service
	@Autowired
	private PopulateDropdownsService populateDropdownsService;
	
	private LinkedHashMap<String, String> theVehiclesOptions;
	private LinkedHashMap<String, String> theStationsOptions;
	private LinkedHashMap<String, String> theTimeOptions;
	private LinkedHashMap<String, String> theReducedTimeOptions;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		// Trim input Strings to remove leading and trailing whitespace
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	// Class attributes used for live charts
	
	private HashMap<Integer, int[]> stationsMap;
	private HashMap<Integer, int[]> vehiclesMap;
	private HashMap<Integer, double[]> chargeMap;
	private int[] totalRequests = new int[TIMEFRAME_SIZE];
	private int[] acceptedRequests = new int[TIMEFRAME_SIZE];
	private int[] deniedRequests = new int[TIMEFRAME_SIZE];

	@GetMapping("/list")
	public String listCharts(Model theModel, HttpSession session) {
		
		boolean theSystemStatus = false;
		
		// Set status to "running" if service or simulation is running
		
		if (session.getAttribute("theServiceStatus") !=null) {
			if (session.getAttribute("theServiceStatus").equals("true")) theSystemStatus = true;
		}
			
		if (session.getAttribute("theSimulationStatus") !=null) {
			if (session.getAttribute("theSimulationStatus").equals("true")) theSystemStatus = true;
		}
		
		// Add attribute to the model
		theModel.addAttribute("systemStatus", theSystemStatus);
		
		// Get the dropdown options hashmaps from the service
		
		theVehiclesOptions = populateDropdownsService.getVehiclesOptions();
		theStationsOptions = populateDropdownsService.getStationsOptions();
		theTimeOptions = populateDropdownsService.getTimeOptions();
		theReducedTimeOptions = getReducedTimeOptions(theTimeOptions);
		
		// Add the dropdown options to the model
		
		theModel.addAttribute("vehiclesOptions", theVehiclesOptions);
		theModel.addAttribute("stationsOptions", theStationsOptions);
		theModel.addAttribute("reducedTimeOptions", theReducedTimeOptions);
		
		return "live-charts";
	}
	
	@GetMapping("/update-requests")
	@ResponseBody
	public String updateRequestsChart(Model theModel) {
		
		// Create requests array to be used for storing data and displaying them to chart
		String[][] theUserRequestsArray = new String[TIMEFRAME_SIZE][4];

		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
			
			theUserRequestsArray[i][0] = "'" + getDateString(i) + "'";
			theUserRequestsArray[i][1] = String.valueOf(totalRequests[i]);
			theUserRequestsArray[i][2] = String.valueOf(acceptedRequests[i]);
			theUserRequestsArray[i][3] = String.valueOf(deniedRequests[i]);
		}
		
		// Convert array to string so it can be passed to javascript file
		String theUserRequestsForJs = Arrays.deepToString(theUserRequestsArray);
		
		return theUserRequestsForJs;
	}
	
	@GetMapping("/update-efficiency")
	@ResponseBody
	public String updateEfficiencyRate(Model theModel) {
		
		// Create efficiency rate array to be used for storing data and displaying them to chart
		String[][] theEfficiencyRateArray = new String[2][2];
		
		int acceptedCounter = 0;
		int deniedCounter = 0;
		int previousAccepted = 0;
		int previousDenied = 0;
		String[] labels = {"Requests Accepted", "Requests Denied"};
		
		// Get sum of accepted requests and sum of denied requests

		for (int i = 0; i < TIMEFRAME_SIZE; i++) {

			if (acceptedRequests[i] > acceptedCounter) {
				
				// Calculate new sum of accepted routes
				acceptedCounter = acceptedCounter + (acceptedRequests[i] - previousAccepted);
				
				// Save sum of previous accepted routes
				previousAccepted = acceptedRequests[i];
			}
			
			if (deniedRequests[i] > deniedCounter) {
				
				// Calculate new sum of denied routes
				deniedCounter = deniedCounter + (deniedRequests[i] - previousDenied);
				
				// Save sum of previous denied routes
				previousDenied = deniedRequests[i];
			}
		}
		
		int[] values = {acceptedCounter, deniedCounter};
		
		for (int i = 0; i < 2; i++) {
		
			theEfficiencyRateArray[i][0] = "'" + labels[i] + "'";
			theEfficiencyRateArray[i][1] = String.valueOf(values[i]);
		}

		// Convert array to string so it can be passed to javascript file
		String theEfficiencyRateForJs = Arrays.deepToString(theEfficiencyRateArray);
		
		return theEfficiencyRateForJs;
	}
	
	@GetMapping("/update-stations/{stationId}")
	@ResponseBody
	public String updateStationsChart(@PathVariable("stationId") String stationId, Model theModel) {
		
		// Get selected station id
		int currentStationId = Integer.parseInt(stationId);
		
		int[] currentStationTimeframe = new int[TIMEFRAME_SIZE];
		currentStationTimeframe = stationsMap.get(currentStationId);
		
		// Create stations array to be used for storing data and displaying them to chart
		String[][] theStationsArray = new String[TIMEFRAME_SIZE][2];

		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
			
			theStationsArray[i][0] = "'" + getDateString(i) + "'";
			theStationsArray[i][1] = String.valueOf(currentStationTimeframe[i]);
		}
		
		// Convert array to string so it can be passed to javascript file
		String theStationsForJs = Arrays.deepToString(theStationsArray);
		
		return theStationsForJs;
	}
	
	@GetMapping("/update-charge/{vehicleId}")
	@ResponseBody
	public String updateChargeChart(@PathVariable("vehicleId") String vehicleId, Model theModel) {
		
		// Get selected vehicle id
		int currentVehicleId = Integer.parseInt(vehicleId);
		
		double[] currentChargeTimeframe = new double[TIMEFRAME_SIZE];
		currentChargeTimeframe = chargeMap.get(currentVehicleId);
		
		// Create charge array to be used for storing data and displaying them to chart
		String[][] theChargeArray = new String[TIMEFRAME_SIZE][2];

		for (int i = 0; i < TIMEFRAME_SIZE; i++) {
			
			theChargeArray[i][0] = "'" + getDateString(i) + "'";
			theChargeArray[i][1] = String.valueOf(currentChargeTimeframe[i]);
		}
		
		// Convert array to string so it can be passed to javascript file
		String theChargeForJs = Arrays.deepToString(theChargeArray);
		
		return theChargeForJs;
	}
	
	@GetMapping("/update-vehicles/{timeIndex}")
	@ResponseBody
	public String updateVehiclesTable(@PathVariable("timeIndex") String timeIndex, Model theModel) {
				
		// Get selected time index
		int currentTimeIndex = Integer.parseInt(timeIndex);
		
		// Get vehicles from the service
		List<Vehicle> theVehicles = vehicleService.getVehicles();
		
		String currentVehicleName = "";
		String currentStationName = "";
		int[] currentStationTimeframe = new int[TIMEFRAME_SIZE];
		
		// Create vehicles array to be used for storing data and displaying them to table
		String[][] theVehiclesArray = new String[theVehicles.size()][3];
		
		for (int i = 0; i < theVehicles.size(); i++) {
			
			// Get current vehicle object and license plates
			
			Vehicle currentVehicle = theVehicles.get(i);
			currentVehicleName = currentVehicle.getLicensePlates();
			
			// Get current vehicle timeframe
			currentStationTimeframe = vehiclesMap.get(theVehicles.get(i).getId());
			
			if (currentStationTimeframe[currentTimeIndex] != 0) {
				
				// If current value of timeframe index is not 0, get current station name
				
				int currentStationId = currentStationTimeframe[currentTimeIndex];
				Station currentStation = stationService.getStation(currentStationId);
				currentStationName = currentStation.getName();
				
			} else {
				
				// If current value of timeframe index is 0, set name to "Travelling"
				currentStationName = "...Travelling...";
			}
			
			theVehiclesArray[i][0] = "'" + currentVehicleName + "'";
			theVehiclesArray[i][1] = "'" + getDateString(currentTimeIndex) + "'";
			theVehiclesArray[i][2] = "'" + currentStationName + "'";
		}
		
		// Convert array to string so it can be passed to javascript file
		String theVehiclesForJs = Arrays.deepToString(theVehiclesArray);
		
		return theVehiclesForJs;
	}
	
	@GetMapping("/nodata")
	public String listChartsNoData(Model theModel) {
		
		// Load no data jsp page
		return "live-charts-nodata";
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
	
	// Method to get a new reduced hashmap with every 6th element of original hashmap
	private LinkedHashMap<String, String> getReducedTimeOptions(LinkedHashMap<String, String> theTimeOptions) {

		int elementIndex = 0;
		int requiredIndex = 0;
		theReducedTimeOptions = new LinkedHashMap<>();

		for (Map.Entry<String, String> entry : theTimeOptions.entrySet()) {
			
			String key = entry.getKey();
		    String value = entry.getValue();
		    
		    // Save each 6th element to new reduced hashmap
		    
		    if (elementIndex == requiredIndex) {
		    	
		    	theReducedTimeOptions.put(key, value);
		    	requiredIndex = requiredIndex + 6;
		    }
		    elementIndex++;
		}

        return theReducedTimeOptions;
    }
	
	// Getters and setters

	public HashMap<Integer, int[]> getStationsMap() {
		return stationsMap;
	}

	public void setStationsMap(HashMap<Integer, int[]> stationsMap) {
		this.stationsMap = stationsMap;
	}

	public HashMap<Integer, int[]> getVehiclesMap() {
		return vehiclesMap;
	}

	public void setVehiclesMap(HashMap<Integer, int[]> vehiclesMap) {
		this.vehiclesMap = vehiclesMap;
	}

	public HashMap<Integer, double[]> getChargeMap() {
		return chargeMap;
	}

	public void setChargeMap(HashMap<Integer, double[]> chargeMap) {
		this.chargeMap = chargeMap;
	}

	public int[] getTotalRequests() {
		return totalRequests;
	}

	public void setTotalRequests(int[] totalRequests) {
		this.totalRequests = totalRequests;
	}

	public int[] getAcceptedRequests() {
		return acceptedRequests;
	}

	public void setAcceptedRequests(int[] acceptedRequests) {
		this.acceptedRequests = acceptedRequests;
	}

	public int[] getDeniedRequests() {
		return deniedRequests;
	}

	public void setDeniedRequests(int[] deniedRequests) {
		this.deniedRequests = deniedRequests;
	}
	
}