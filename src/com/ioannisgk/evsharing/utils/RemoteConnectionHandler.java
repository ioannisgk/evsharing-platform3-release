package com.ioannisgk.evsharing.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.services.AdministratorService;
import com.ioannisgk.evsharing.services.ProcessRequestService;
import com.ioannisgk.evsharing.services.ProcessRequestServiceShortImpl;
import com.ioannisgk.evsharing.services.RouteService;
import com.ioannisgk.evsharing.services.VehicleService;

@Component
@Scope("prototype")
public class RemoteConnectionHandler implements Runnable {
	
	// Inject the socket connection helper 
	@Autowired
	private SocketConnectionHelper socketConnectionHelper;
	
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
	
	// Inject the text encryptor helper
	@Autowired
	private StrongTextEncryptorHelper strongTextEncryptorHelper;
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Inject the vehicle service
	@Autowired
	private VehicleService vehicleService;
	
	public void run () {                                                                                                                                              
		
		try {
			
			// Get a BufferedReader object to read incoming messages
			// Get a PrintWriter object for sending messages to remote client
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(socketConnectionHelper.getClient().getInputStream()));
			PrintWriter writer = new PrintWriter(socketConnectionHelper.getClient().getOutputStream(), true);

			while (true) {
				String result = "";
				String message = reader.readLine();
				
				// If the client message is "bye", disconnect
				
				if (message.trim().equals("bye")) {
					break;
				}
				
				// Decrypt current vehicle request from Android app
				String currentMessage = strongTextEncryptorHelper.decryptPasswordBasic(message);
				
				// Get process mode according to user selection
				String currentMode = processModeHelper.getMode();
				
				if (currentMode.equals("shortMode")) {
					
					// Process request and get result
					result = processRequestServiceShortImpl.requestResult(currentMessage);
					
				} else if (currentMode.equals("longMode")) {
					
					// Process request and get result
					result = processRequestServiceLongImpl.requestResult(currentMessage);
					
				}
				
				System.out.println("Mode:" + currentMode);
				System.out.println("Result:" + result);
				
				// Send request result to client
				writer.println(result);
				
			}
			
	    } catch (Exception e) {
	      System.err.println("Exception caught: client disconnected.");
	      
	    } finally {
	    	try {
	    		socketConnectionHelper.getClient().close();
	    	} catch (Exception e ) {
	    		
	    	}
	    }
	}
}