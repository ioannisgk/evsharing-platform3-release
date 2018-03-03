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

import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.services.LoadMapService;
import com.ioannisgk.evsharing.services.RouteService;
import com.ioannisgk.evsharing.services.StationService;

@Controller
@RequestMapping("/station")
public class StationController {
	
	// Inject the station service
	@Autowired
	private StationService stationService;
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Inject the load map service
	@Autowired
	private LoadMapService loadMapService;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		// Trim input Strings to remove leading and trailing whitespace
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/list")
	public String listStations(Model theModel) {
		
		// Get stations from the service
		List<Station> theStations = stationService.getStationsAlphabetically();
		
		// Get routes from the service
		List<Route> theRoutes = routeService.getRoutes();
		
		// Iterate list and save current helper values to transient attributes in entity class
		
		for (int i = 0; i < theStations.size(); i++) {
			
			// Get current station id
			int currentStationId = theStations.get(i).getId();
			
			for (int j = 0; j < theRoutes.size(); j++) {
				
				// Get station ids that are used in routes
				
				int routeStartStationId = theRoutes.get(j).getStartStationId();
				int routeFinishStationId = theRoutes.get(j).getFinishStationId();
				
				if ((currentStationId == routeStartStationId) || (currentStationId == routeFinishStationId)) {
					
					// Save current used status to the list
					theStations.get(i).setUsed(true);
				}
			}
		}
		
		// Add stations to the model
		theModel.addAttribute("stations", theStations);
		
		// Add count variables to the model
		theModel.addAttribute("countStations", theStations.size());
		
		return "stations";
	}
	
	@GetMapping("/add-new-station")
	public String addNewStation(Model theModel) {
		
		// Create model attribute to bind form data
		Station theStation = new Station();
		
		// Add attribute to the model
		theModel.addAttribute("station", theStation);
		
		// Get stations from the service
		List<Station> theStations = stationService.getStations();
					
		// Add count variables to the model
		theModel.addAttribute("countStations", theStations.size());
		
		return "station-form";
	}
	
	@PostMapping("/save-station")
	public String saveStation(
			@Valid @ModelAttribute("station") Station theStation,
			BindingResult theBindingResult, Model theModel) {
		
		// If input data have errors redirect back to admin form
		
		if (theBindingResult.hasErrors()) {
			
			// Get stations from the service
			List<Station> theStations = stationService.getStations();
			
			// Add count variables to the model
			theModel.addAttribute("countStations", theStations.size());
			
			return "station-form";
		}
		
		// If input data are valid save the station and redirect to list page

		else {
			stationService.saveStation(theStation);
			return "redirect:/station/list";
		}
	}
	
	@GetMapping("/update-station")
	public String updateStation(@RequestParam("stationId") int theId, Model theModel) {
		
		// Get station from the service
		Station theStation = stationService.getStation(theId);
		
		// Add attribute to the model to prepopulate the form
		theModel.addAttribute("station", theStation);
		
		// Get stations from the service
		List<Station> theStations = stationService.getStations();
							
		// Add count variables to the model
		theModel.addAttribute("countStations", theStations.size());
		
		return "station-form";
	}
	
	@GetMapping("/delete-station")
	public String deleteStation(@RequestParam("stationId") int theId) {
		
		// Delete station using the service
		stationService.deleteStation(theId);
		
		// Redirect to list page
		return "redirect:/station/list";
	}
	
	@PostMapping("/upload-map")
	public String uploadFile(@RequestParam("file") MultipartFile theFile, Model theModel) {
		
		// Copy the uploaded xml file into default temp folder so we can access it
		
		try {
			FileCopyUtils.copy(theFile.getBytes(), new File("/opt/tomcat/tomcat-latest/" + theFile.getOriginalFilename()));
			
		} catch (IOException e) {
			System.err.println("Error copying the file" + e);
		}
		
		// The path of the copied xml file is its name, since it is in default temp folder
		String filePath = "/opt/tomcat/tomcat-latest/" + theFile.getOriginalFilename();
		
		// Save xml data to database with the service
		loadMapService.saveMap(filePath);
		
		// Delete all current routes
        routeService.deleteAllRoutes();
		
		// Add multipart file and info message attributes to the model
        
		theModel.addAttribute("file", theFile);
		theModel.addAttribute("infoMapMessage", "Current routes are deleted, new Map was loaded");
		
		// Redirect to list page
		return "redirect:/station/list";
	}
}