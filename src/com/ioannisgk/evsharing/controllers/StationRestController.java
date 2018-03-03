package com.ioannisgk.evsharing.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.services.StationService;
import com.ioannisgk.evsharing.services.UserService;
import com.ioannisgk.evsharing.utils.StrongTextEncryptorHelper;

@RestController
@RequestMapping("/api")
public class StationRestController {
	
	// Inject the station service
	@Autowired
	private StationService stationService;
	
    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getStations() {
    	
    	// Get stations from the service
        List<Station> theStations = stationService.getStationsAlphabetically();
        
        // Return appropriate HTTP status
        
        if(theStations.isEmpty()) return new ResponseEntity<List<Station>>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<List<Station>>(theStations, HttpStatus.OK);
    }
}