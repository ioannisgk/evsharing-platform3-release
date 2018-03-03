package com.ioannisgk.evsharing.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

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
import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.entities.Simulation;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.VehicleDAO;
import com.ioannisgk.evsharing.services.LoadMapService;
import com.ioannisgk.evsharing.services.LoadSimulationsService;
import com.ioannisgk.evsharing.services.PopulateDropdownsService;
import com.ioannisgk.evsharing.services.RemoteConnectionService;
import com.ioannisgk.evsharing.services.RouteService;
import com.ioannisgk.evsharing.services.SimulationService;
import com.ioannisgk.evsharing.services.StationService;
import com.ioannisgk.evsharing.services.UserService;
import com.ioannisgk.evsharing.services.VehicleService;
import com.ioannisgk.evsharing.utils.MyTasklet;
import com.ioannisgk.evsharing.utils.ProcessModeHelper;

@Controller
@RequestMapping("/simulation")
public class SimulationController {
	
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
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Inject the remote connection service
	@Autowired
	private RemoteConnectionService remoteConnectionService;
	
	// Inject the process mode helper
	@Autowired
	private ProcessModeHelper processModeHelper;
	
	// Inject the my tasklet service
	@Autowired
	private MyTasklet myTasklet;
	
	// Inject the scheduler factory service
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	// Inject the populate dropdowns service
	@Autowired
	private PopulateDropdownsService populateDropdownsService;
	
	private LinkedHashMap<String, String> theUsersOptions;
	private LinkedHashMap<String, String> theStationsOptions;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		// Trim input Strings to remove leading and trailing whitespace
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/list")
	public String listSimulations(HttpSession session, Model theModel) {
		
		// Get simulations from the service
		List<Simulation> theSimulations = simulationService.getSimulations();
		
		// Iterate list and save current helper values to transient attributes in entity class
		
		for (int i = 0; i < theSimulations.size(); i++) {
			
			// Get current user id, start station id, finish station id from current message
			String currentMessage = theSimulations.get(i).getMessage();
			
			String userID = "";
			String startStationID = "";
			String finishStationID = "";
			String startTime = "";
			
			// Iterate vehicle request message string from simulation requests
			// Extract userID, startStationID, finishStationID, time
			
			int space = 0;
			int n = currentMessage.length();
			for (int j = 0; j < n; j++) {
			    char c = currentMessage.charAt(j);
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

			// Get current user, start station, finish station objects with those ids
			
			User currentUser = userService.getUser(Integer.parseInt(userID));
			Station currentStationStart = stationService.getStation(Integer.parseInt(startStationID));
			Station currentStationFinish = stationService.getStation(Integer.parseInt(finishStationID));

			// Get current username, start station name, finish station name from those objects
			
			String currentUsername = currentUser.getUsername();
			String currentStationStartName = currentStationStart.getName();
			String currentStationFinishName = currentStationFinish.getName();
			
			// Save current username, start station name, finish station name to the list

			theSimulations.get(i).setCurrentUsername(currentUsername);
			theSimulations.get(i).setCurrentStationStartName(currentStationStartName);
			theSimulations.get(i).setCurrentStationFinishName(currentStationFinishName);
			theSimulations.get(i).setCurrentStartTime(startTime);
		}
		
		// Add simulations to the model
		theModel.addAttribute("simulations", theSimulations);
		
		// Get stations from the service
		List<Station> theStations = stationService.getStations();
		
		// Stations array to be used for storing station data and display them to map using javascript
		String[][] theStationsArray = new String[theStations.size()][4];

		for (int i = 0; i < theStations.size(); i++) {
			
			theStationsArray[i][0] = "'" + theStations.get(i).getName() + "'";
			theStationsArray[i][1] = String.valueOf(theStations.get(i).getLatitude());
			theStationsArray[i][2] = String.valueOf(theStations.get(i).getLongitude());
			theStationsArray[i][3] = String.valueOf(theStations.get(i).getTrafficLevel());
		}
		
		// Convert array to string so it can be passed from jsp to javascript file
		String theStationsForJs = Arrays.deepToString(theStationsArray);
		
		// Add stations string for javascript file to the model
		theModel.addAttribute("stations", theStationsForJs);
		
		// Get the dropdown options hashmaps from the service
		
		theUsersOptions = populateDropdownsService.getUsersOptions();
		theStationsOptions = populateDropdownsService.getStationsOptions();
		
		// Add the dropdown options to the model
		
	    theModel.addAttribute("usersOptions", theUsersOptions);
	    theModel.addAttribute("stationsOptions", theStationsOptions);
		
		boolean theSimulationStatus = false;
		session.setAttribute("theSimulationStatus", "false");
		
		// Get scheduler status
		if (schedulerFactory.isRunning()) {
			
			if (session.getAttribute("theServiceStatus") == "true") {
				
				// Set simulation status to false
				theSimulationStatus = false;
				
				// Add the simulation status to the current http session
				session.setAttribute("theSimulationStatus", "false");
				
			} else {
				
				// Set simulation status to true
				theSimulationStatus = true;
				
				// Add the simulation status to the current http session
				session.setAttribute("theSimulationStatus", "true");
			}
		}
		
		// Add status to the model
		theModel.addAttribute("simulationStatus", theSimulationStatus);
		
		// Add request statistics to the model
		addRequestStatistics(theModel);
		
		return "simulation";
	}
	
	@GetMapping("start-simulation/{mode}")
	@ResponseBody
	public String startSimulation(@PathVariable("mode") String mode, HttpSession session) {
		
		if (mode.equals("shortMode") && (session.getAttribute("theSimulationStatus").equals("false"))) {
			
			// Set process mode according to user selection
			processModeHelper.setMode("shortMode");
			
			// Set tasklet mode to simulation
			myTasklet.setMode("simulation");
			
			// If scheduler factory is not running, start it
			if (!schedulerFactory.isRunning()) schedulerFactory.start();
			
			// Add the simulation status to the current http session
			session.setAttribute("theSimulationStatus", "true");
			
		} else if (mode.equals("longMode") && (session.getAttribute("theSimulationStatus").equals("false"))) {
			
			// Set process mode according to user selection
			processModeHelper.setMode("longMode");
			
			// Set tasklet mode to simulation
			myTasklet.setMode("simulation");
						
			// If scheduler factory is not running, start it
			if (!schedulerFactory.isRunning()) schedulerFactory.start();
			
			// Add the simulation status to the current http session
			session.setAttribute("theSimulationStatus", "true");
		}
		
		return mode;
	}
	
	@GetMapping("stop-simulation")
	@ResponseBody
	public String stopSimulation(HttpSession session) {
		
		if (session.getAttribute("theSimulationStatus").equals("true")) {
		
			// Stop the tcp multithreaded server
			remoteConnectionService.stopServer();
			
			// If scheduler factory is running, stop it
			if (schedulerFactory.isRunning()) schedulerFactory.stop();
			
			// Add the simulation status to the current http session
			session.setAttribute("theSimulationStatus", "false");
		}
		
		return "done";
	}
	
	@GetMapping("select-user/{user}")
	@ResponseBody
	public String selectUser(@PathVariable("user") String userId) {
		
		// Send user id to the ajax script
		return userId;
	}
	
	@GetMapping("select-station/{station}")
	@ResponseBody
	public String selectStation(@PathVariable("station") String stationId) {
		
		// Send station id to the ajax script
		return stationId;
	}
	
	@GetMapping("/delete-simulation")
	public String deleteSimulation(@RequestParam("simulationId") int theId) {
		
		// Delete Simulation using the service
		simulationService.deleteSimulation(theId);
		
		// Redirect to list page
		return "redirect:/simulation/list";
	}
	
	@PostMapping("/upload-simulation")
	public String uploadSimulation(@RequestParam("file") MultipartFile theFile, Model theModel) {
		
		// Copy the uploaded xml file into default temp folder so we can access it
		
		try {
			FileCopyUtils.copy(theFile.getBytes(), new File("/opt/tomcat/tomcat-latest/" + theFile.getOriginalFilename()));
			
		} catch (IOException e) {
			System.err.println("Error copying the file" + e);
		}
		
		// The path of the copied xml file is its name, since it is in default temp folder
		String filePath = "/opt/tomcat/tomcat-latest/" + theFile.getOriginalFilename();
		
		// Save xml data to database with the service
		loadSimulationsService.saveSimulations(filePath);
		
		// Delete all current routes
        routeService.deleteAllRoutes();
		
		// Add multipart file and info message attributes to the model
        
		theModel.addAttribute("file", theFile);
		theModel.addAttribute("infoSimulationMessage", "Current simulation is deleted, new simulation was loaded");
		
		// Redirect to list page
		return "redirect:/simulation/list";
	}
	
	// Method to add request statistics to the model
	public void addRequestStatistics(Model theModel) {
		
		// Get all accepted routes from the service
        List<Route> theAcceptedRoutes = routeService.getRoutes();
        
        // Get all denied routes from the service
        List<Route> theDeniedRoutes = routeService.getRoutesByField("status", "Denied");
        
        double acceptedRoutes = theAcceptedRoutes.size();
        double deniedRoutes = theDeniedRoutes.size();
        double totalRoutes = acceptedRoutes + deniedRoutes;
        
        // Calculate efficiency ratio of requests
        double theEfficiencyRatio = (acceptedRoutes / totalRoutes) * 100;
        
        // Round efficiency ratio up to 2 decimal places
        theEfficiencyRatio = Math.round(theEfficiencyRatio * 100.0) / 100.0;
        
        // Add request statistics to the model
		
     	theModel.addAttribute("acceptedRequests", theAcceptedRoutes.size());
     	theModel.addAttribute("deniedRequests", theDeniedRoutes.size());
     	theModel.addAttribute("efficiencyRatio", theEfficiencyRatio);
	}
}