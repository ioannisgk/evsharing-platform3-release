package com.ioannisgk.evsharing.controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.VehicleDAO;
import com.ioannisgk.evsharing.services.LoadVehiclesService;
import com.ioannisgk.evsharing.services.RouteService;
import com.ioannisgk.evsharing.services.StationService;
import com.ioannisgk.evsharing.services.VehicleService;

@Controller
@RequestMapping("/vehicle")
public class VehicleController {
	
	// Inject the vehicle service
	@Autowired
	private VehicleService vehicleService;
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Inject the station service
	@Autowired
	private StationService stationService;
	
	// Inject the load vehicles service
	@Autowired
	private LoadVehiclesService loadVehiclesService;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		// Trim input Strings to remove leading and trailing whitespace
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/list")
	public String listVehicles(Model theModel) {
		
		// Get vehicles from the service
		List<Vehicle> theVehicles = vehicleService.getAllVehicles();
		
		// Get routes from the service
		List<Route> theRoutes = routeService.getRoutes();
		
		// Iterate list and save current helper values to transient attributes in entity class
		
		for (int i = 0; i < theVehicles.size(); i++) {
			
			// Get current station id
			int currentStationId = theVehicles.get(i).getStationId();
			
			if (currentStationId != 0) {
				
				// Get current station object with this id
				Station currentStation = stationService.getStation(currentStationId);
					
				// Get current station name from this object
				String currentStationName = currentStation.getName();
	
				// Save current station name to the list
				theVehicles.get(i).setCurrentStationName(currentStationName);
			}
			
			// Get current vehicle id
			int currentVehicleId = theVehicles.get(i).getId();
			
			for (int j = 0; j < theRoutes.size(); j++) {
				
				// Get vehicle ids that are used in routes
				int routeVehicleId = theRoutes.get(j).getVehicleId();
				
				if (currentVehicleId == routeVehicleId) {
					
					// Save current used status to the list
					theVehicles.get(i).setUsed(true);
				}
			}
		}
		
		// Add vehicles to the model
		theModel.addAttribute("vehicles", theVehicles);
		
		// Add count variables to the model
		theModel.addAttribute("countVehicles", theVehicles.size());
		
		return "vehicles";
	}
	
	@GetMapping("/add-new-vehicle")
	public String addNewVehicle(Model theModel) {
		
		// Create model attribute to bind form data
		Vehicle theVehicle = new Vehicle();
		
		// Add attribute to the model
		theModel.addAttribute("vehicle", theVehicle);
		
		// Get vehicles from the service
		List<Vehicle> theVehicles = vehicleService.getAllVehicles();
				
		// Add count variables to the model
		theModel.addAttribute("countVehicles", theVehicles.size());
		
		return "vehicle-form";
	}
	
	@PostMapping("/save-vehicle")
	public String saveVehicle(
			@Valid @ModelAttribute("vehicle") Vehicle theVehicle,
			BindingResult theBindingResult, Model theModel) {
		
		// If input data have errors redirect back to vehicle form
		
		if (theBindingResult.hasErrors()) {
			
			// Get vehicles from the service
			List<Vehicle> theVehicles = vehicleService.getAllVehicles();
					
			// Add count variables to the model
			theModel.addAttribute("countVehicles", theVehicles.size());
			
			return "vehicle-form";
		}
		
		// If input data are valid save the vehicle and redirect to list page

		else {
			
			// Get current license plates from vehicle object
			String currentLicensePlates = theVehicle.getLicensePlates();
			
			// Get vehicles from the database that have the license plates entered in the form
			List<Vehicle> theVehicles = vehicleService.getVehiclesByField("license_plates", currentLicensePlates);

			if (theVehicles.size() == 1) {

				try {
					
					// Try to save vehicle using the service
					vehicleService.saveVehicle(theVehicle);
					
				} catch (Exception e) {
						
					// If license plates already exist, do not save it again
					return "redirect:/vehicle/list";
				}
			}
			
			// Try to save vehicle using the service
			vehicleService.saveVehicle(theVehicle);
			
			return "redirect:/vehicle/list";
		}
	}
	
	@GetMapping("/update-vehicle")
	public String updateVehicle(@RequestParam("vehicleId") int theId, Model theModel) {
		
		// Get vehicle from the service
		Vehicle theVehicle = vehicleService.getVehicle(theId);
		
		// Add attribute to the model to prepopulate the form
		theModel.addAttribute("vehicle", theVehicle);
		
		// Get vehicles from the service
		List<Vehicle> theVehicles = vehicleService.getAllVehicles();
						
		// Add count variables to the model
		theModel.addAttribute("countVehicles", theVehicles.size());
		
		return "vehicle-form";
	}
	
	@GetMapping("/delete-vehicle")
	public String deleteVehicle(@RequestParam("vehicleId") int theId) {
		
		// Delete vehicle using the service
		vehicleService.deleteVehicle(theId);
		
		// Redirect to list page
		return "redirect:/vehicle/list";
	}
	
	@PostMapping("/upload-vehicles")
	public String uploadVehicles(@RequestParam("file") MultipartFile theFile, Model theModel) {
		
		// Copy the uploaded xml file into default temp folder so we can access it
		
		try {
			FileCopyUtils.copy(theFile.getBytes(), new File("/opt/tomcat/tomcat-latest/" + theFile.getOriginalFilename()));
			
		} catch (IOException e) {
			System.err.println("Error copying the file" + e);
		}
		
		// The path of the copied xml file is its name, since it is in default temp folder
		String filePath = "/opt/tomcat/tomcat-latest/" + theFile.getOriginalFilename();
		
		// Save xml data to database with the service
		loadVehiclesService.saveVehicles(filePath);
		
		// Delete all current routes
        routeService.deleteAllRoutes();
		
		// Add multipart file and info message attributes to the model
        
		theModel.addAttribute("file", theFile);
		theModel.addAttribute("infoVehicleMessage", "Current routes are deleted, new set of vehicles was loaded");
		
		// Redirect to list page
		return "redirect:/vehicle/list";
	}
}