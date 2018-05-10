package com.ioannisgk.evsharing.controllers;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.services.AdministratorService;
import com.ioannisgk.evsharing.services.LoadMapService;
import com.ioannisgk.evsharing.services.LoadVehiclesService;
import com.ioannisgk.evsharing.services.PopulateDropdownsService;
import com.ioannisgk.evsharing.services.RemoteConnectionService;
import com.ioannisgk.evsharing.services.RouteService;
import com.ioannisgk.evsharing.services.StationService;
import com.ioannisgk.evsharing.services.UserService;
import com.ioannisgk.evsharing.services.VehicleService;
import com.ioannisgk.evsharing.utils.MyTasklet;
import com.ioannisgk.evsharing.utils.ProcessModeHelper;
import com.ioannisgk.evsharing.utils.SocketConnectionHelper;
import com.ioannisgk.evsharing.utils.StrongTextEncryptorHelper;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.batch.core.Job; 
import org.springframework.batch.core.JobExecution; 
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/home")
public class HomeController {
	
	// Inject the administrator service
	@Autowired
	private AdministratorService administratorService;
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Inject the user service
	@Autowired
	private UserService userService;
	
	// Inject the vehicle service
	@Autowired
	private VehicleService vehicleService;
	
	// Inject the station service
	@Autowired
	private StationService stationService;
	
	// Inject the scheduler factory service
	@Autowired
	private SchedulerFactoryBean schedulerFactory;
	
	// Inject the remote connection service
	@Autowired
	private RemoteConnectionService remoteConnectionService;
	
	// Inject the process mode helper
	@Autowired
	private ProcessModeHelper processModeHelper;
	
	// Inject the my tasklet service
	@Autowired
	private MyTasklet myTasklet;
	
	// Inject the text encryptor helper
	@Autowired
	private StrongTextEncryptorHelper strongTextEncryptorHelper;
	
	// Inject the populate dropdowns service
	@Autowired
	private PopulateDropdownsService populateDropdownsService;
	
	private LinkedHashMap<String, String> theUsersOptions;
	private LinkedHashMap<String, String> theVehiclesOptions;
	private LinkedHashMap<String, String> theStationsOptions;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		// Trim input Strings to remove leading and trailing whitespace
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/login-page")
	public String showLogin(Model theModel) {
		
		// Create model attribute to bind form data
		Administrator theAdministrator = new Administrator();
		
		// Add attribute to the model
		theModel.addAttribute("administrator", theAdministrator);
		
		return "login";
	}
	
	// Authorization is handled in security configuration class by Spring Security
	
	@PostMapping("/login-process")
	public String loginProcess(
		@Valid @ModelAttribute("administrator") Administrator theAdministrator,
		BindingResult theBindingResult, HttpSession session, Model theModel) {
		
		boolean isValidUser = false;
		
		// If input data have errors redirect back to login form
		if (theBindingResult.hasErrors()) return "login";
		
		// If input data are valid save the administrator and redirect to list page

		else {
			
			// Get administrators that have the username entered in the login form
			List<Administrator> theAdministrators = administratorService.getAdministratorsByField("username", theAdministrator.getUsername());

			if (theAdministrators.size() == 1) {
				
				// Get current administrator encrypted password
				String currentPassword = theAdministrators.get(0).getPassword();
				
				// Decrypt current saved administrator password
				String decryptedPassword = strongTextEncryptorHelper.decryptPassword(currentPassword);
				
				// If the password entered equals the password retrieved from the database, then user is valid
				
				if (theAdministrator.getPassword().equals(decryptedPassword)) {
					isValidUser = true;
				}
			}
			
			// If the user is valid redirect to main page
			
			if (isValidUser == true) {
				
				// Redirect to main page
				return "redirect:/home/main";
				
			} else {
				
				// Add error message attribute to the model and redirect to login form
				
				theModel.addAttribute("errorMessage", "Invalid login details");
				return "login";
			}
		}
	}
	
	@GetMapping("/main")
	public String mainPage(HttpSession session, Model theModel) {
		
		String theLoggedInUsername = "";
		
		// If the administrator has just logged in, loggedInUsername equals to null
		
		if (session.getAttribute("loggedInUsername") == null) {
			
			// When using Spring Security get principal object
			Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

			if (principal instanceof UserDetails) {
				
				// Get logged-in username from principal object
				theLoggedInUsername = ((UserDetails) principal).getUsername();
				
				// Add logged-in username to the current http session
				session.setAttribute("loggedInUsername", theLoggedInUsername);
			}
			
			// Get administrators that have the logged-in username
			List<Administrator> theAdministrators = administratorService.getAdministratorsByField("username", theLoggedInUsername);
			
			// Get current administrator role
			String theLoggedInRole = theAdministrators.get(0).getRole();
			
			// Add logged-in role to the current http session
			session.setAttribute("loggedInRole", theLoggedInRole);
		}
		
		// Get routes from the service
		List<Route> theRoutes = routeService.getRoutes();
		
		// Iterate list and save current helper values to transient attributes in entity class
		
		for (int i = 0; i < theRoutes.size(); i++) {
			
			// Get current user id, vehicle id, start station id, finish station id
			
			int currentUserId = theRoutes.get(i).getUserId();
			int currentVehicleId = theRoutes.get(i).getVehicleId();
			int currentStationStartId = theRoutes.get(i).getStartStationId();
			int currentStationFinishId = theRoutes.get(i).getFinishStationId();
			
			// Get current user, vehicle, start station, finish station objects with those ids
			
			User currentUser = userService.getUser(currentUserId);
			Vehicle currentVehicle = vehicleService.getVehicle(currentVehicleId);
			Station currentStationStart = stationService.getStation(currentStationStartId);
			Station currentStationFinish = stationService.getStation(currentStationFinishId);
			
			// Get current username, license plates, start station name, finish station name from those objects
			
			String currentUsername = currentUser.getUsername();
			String currentLicensePlates = currentVehicle.getLicensePlates();
			String currentStationStartName = currentStationStart.getName();
			String currentStationFinishName = currentStationFinish.getName();
			
			// Save current username, license plates, start station name, finish station name to the list
			
			theRoutes.get(i).setCurrentUsername(currentUsername);
			theRoutes.get(i).setCurrentLicensePlates(currentLicensePlates);
			theRoutes.get(i).setCurrentStationStartName(currentStationStartName);
			theRoutes.get(i).setCurrentStationFinishName(currentStationFinishName);
		}
		
		// Add enhanced routes list to the model
		theModel.addAttribute("routes", theRoutes);
		
		// Get stations from the service
		List<Station> theStations = stationService.getStations();
		
		// Create stations array to be used for storing station data and display them to map using javascript
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
		
		// Get users from the service
		List<User> theUsers = userService.getUsers();
		
		// Get vehicles from the service
		List<Vehicle> theVehicles = vehicleService.getAllVehicles();
		
		// Add count variables to the model
		
		theModel.addAttribute("countRoutes", theRoutes.size());
		theModel.addAttribute("countStations", theStations.size());
		theModel.addAttribute("countUsers", theUsers.size());
		theModel.addAttribute("countVehicles", theVehicles.size());
		
		boolean theServiceStatus = false;
		session.setAttribute("theServiceStatus", "false");
		
		// Get scheduler status
		if (schedulerFactory.isRunning()) {
			
			if (session.getAttribute("theSimulationStatus") == "true") {
				
				// Set service status to false
				theServiceStatus = false;
				
				// Add the service status to the current http session
				session.setAttribute("theServiceStatus", "false");
				
			} else {

				// Set service status to true
				theServiceStatus = true;
				
				// Add the service status to the current http session
				session.setAttribute("theServiceStatus", "true");
			}
		}
		
		// Add status to the model
		theModel.addAttribute("serviceStatus", theServiceStatus);
		
		// Add request statistics to the model
		addRequestStatistics(theModel);
		
		
		
		
		
		int totalMinutes = 0;
        
        // Get all routes from the service
        List<Route> acceptedRoutes = routeService.getRoutes();
        
        for (int i = 0; i < acceptedRoutes.size(); i++) {
        	
        	totalMinutes = totalMinutes +
        							differenceInMinutes(acceptedRoutes.get(i).getStartTime(), acceptedRoutes.get(i).getEndTime());
        	
        }
        
        System.out.println("\nTOTAL TIME IN MINUTES: " + totalMinutes);
		
		
		
		
		// Load main jsp page after login
		return "dashboard";
	}
	
	@GetMapping("start-service/{mode}")
	@ResponseBody
	public String startService(@PathVariable("mode") String mode, HttpSession session) {
		
		if (mode.equals("shortMode") && (session.getAttribute("theServiceStatus").equals("false"))) {
			
			// Set process mode according to user selection
			processModeHelper.setMode("shortMode");
			
			// Start multithreaded server asynchronously
			remoteConnectionService.startServer();
			
			// Set tasklet mode to service
			myTasklet.setMode("service");
			
			// If scheduler factory is not running, start it
			if (!schedulerFactory.isRunning()) schedulerFactory.start();
			
			// Add the service status to the current http session
			session.setAttribute("theServiceStatus", "true");
			
		} else if (mode.equals("longMode") && (session.getAttribute("theServiceStatus").equals("false"))) {
			
			// Set process mode according to user selection
			processModeHelper.setMode("longMode");
			
			// Start multithreaded server asynchronously
			remoteConnectionService.startServer();
			
			// Set tasklet mode to service
			myTasklet.setMode("service");
						
			// If scheduler factory is not running, start it
			if (!schedulerFactory.isRunning()) schedulerFactory.start();
			
			// Add the service status to the current http session
			session.setAttribute("theServiceStatus", "true");
		}
		
		return mode;
	}
	
	@GetMapping("stop-service")
	@ResponseBody
	public String stopService(HttpSession session) {
		
		if (session.getAttribute("theServiceStatus").equals("true")) {
		
			// Stop the tcp multithreaded server
			remoteConnectionService.stopServer();
			
			// If scheduler factory is running, stop it
			if (schedulerFactory.isRunning()) schedulerFactory.stop();
			
			// Add the service status to the current http session
			session.setAttribute("theServiceStatus", "false");
		}
		
		return "done";
	}
	
	@GetMapping("/logout")
	public String logoutProcess(HttpSession session, HttpServletRequest request, HttpServletResponse response, Model theModel) {
		
		// Remove logged-in username, role, service status and simulation status from the current http session
		
		session.removeAttribute("loggedInUsername");
		session.removeAttribute("loggedInRole");
		session.removeAttribute("theServiceStatus");
		session.removeAttribute("theSimulationStatus");
		
		// When using Spring Security
		// Get authentication from context and set it to null
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
        if (auth != null){    
            new SecurityContextLogoutHandler().logout(request, response, auth);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
		
		// Add logout message attribute to the model
		theModel.addAttribute("logoutMessage", "You have been logged out");
		
		// Create model attribute to bind form data
		Administrator theAdministrator = new Administrator();
				
		// Add attribute to the model
		theModel.addAttribute("administrator", theAdministrator);
				
		return "login";
	}
	
	@GetMapping("/route/add-new-route")
	public String addNewRoute(Model theModel) {
		
		// Create model attribute to bind form data
		Route theRoute = new Route();
		
		// Add attribute to the model
		theModel.addAttribute("route", theRoute);
		
		// Get the dropdown options hashmaps from the service
		
		theUsersOptions = populateDropdownsService.getUsersOptions();
		theVehiclesOptions = populateDropdownsService.getVehiclesOptions();
		theStationsOptions = populateDropdownsService.getStationsOptions();
		
		// Add the dropdown options to the model
		
	    theModel.addAttribute("usersOptions", theUsersOptions);
	    theModel.addAttribute("vehiclesOptions", theVehiclesOptions);
	    theModel.addAttribute("stationsOptions", theStationsOptions);
	    
	    // Get routes from the service
	 	List<Route> theRoutes = routeService.getRoutes();
	 	
	 	// Add count variables to the model
	 	theModel.addAttribute("countRoutes", theRoutes.size());
		
		return "route-form";
	}
	
	@PostMapping("/route/save-route")
	public String saveRoute(
			@Valid @ModelAttribute("route") Route theRoute,
			BindingResult theBindingResult, Model theModel) {
		
		// Get the dropdown options hashmaps from the service
		
		theUsersOptions = populateDropdownsService.getUsersOptions();
		theVehiclesOptions = populateDropdownsService.getVehiclesOptions();
		theStationsOptions = populateDropdownsService.getStationsOptions();
		
		// Add the dropdown options to the model
		
	    theModel.addAttribute("usersOptions", theUsersOptions);
	    theModel.addAttribute("vehiclesOptions", theVehiclesOptions);
	    theModel.addAttribute("stationsOptions", theStationsOptions);
		
		// If input data have errors or user has selected the same stations redirect back to route form
		
		if (theBindingResult.hasErrors() || (theRoute.getStartStationId() == theRoute.getFinishStationId())) {
			
			if (theRoute.getStartStationId() == theRoute.getFinishStationId()) {
				
				// Add attribute to the model
				theModel.addAttribute("errorMessage", "You can not select the same station");
			}
			
		    // Get routes from the service
		 	List<Route> theRoutes = routeService.getRoutes();
		 	
		 	// Add count variables to the model
		 	theModel.addAttribute("countRoutes", theRoutes.size());
			
			return "route-form";
		}
		
		// If input data are valid save the route and redirect to list page

		else {
			
			// The manual added route is marked as "Accepted"
			theRoute.setStatus("Accepted");
			
			// Save route using the service
			routeService.saveRoute(theRoute);
			
			return "redirect:/home/main";
		}
	}
	
	@GetMapping("/route/update-route")
	public String updateRoute(@RequestParam("routeId") int theId, Model theModel) {
		
		// Get route from the service
		Route theRoute = routeService.getRoute(theId);
		
		// Add attribute to the model to prepopulate the form
		theModel.addAttribute("route", theRoute);
		
		// Get the dropdown options hashmaps from the service
		
		theUsersOptions = populateDropdownsService.getUsersOptions();
		theVehiclesOptions = populateDropdownsService.getVehiclesOptions();
		theStationsOptions = populateDropdownsService.getStationsOptions();
		
		// Add the dropdown options to the model
		
	    theModel.addAttribute("usersOptions", theUsersOptions);
	    theModel.addAttribute("vehiclesOptions", theVehiclesOptions);
	    theModel.addAttribute("stationsOptions", theStationsOptions);
	    
	    // Get routes from the service
	 	List<Route> theRoutes = routeService.getRoutes();
	 	
	 	// Add count variables to the model
	 	theModel.addAttribute("countRoutes", theRoutes.size());
		
		return "route-form";
	}
	
	@GetMapping("/route/delete-route")
	public String deleteRoute(@RequestParam("routeId") int theId) {
		
		// Delete route using the service
		routeService.deleteRoute(theId);
		
		// Redirect to list page
		return "redirect:/home/main";
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
}