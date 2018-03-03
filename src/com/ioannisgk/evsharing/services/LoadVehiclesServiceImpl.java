package com.ioannisgk.evsharing.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.Vehicle;

@Service
public class LoadVehiclesServiceImpl implements LoadVehiclesService {
	
	// Inject the vehicle service
	@Autowired
	private VehicleService vehicleService;

	@Override
	public void saveVehicles(String filePath) {
		
		// Create document builder factory and document builder
		
	    File xmlFile = new File(filePath);
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;
	    
	    try {
	    	
	    	// Create document and get its elements
	    	
	        dBuilder = dbFactory.newDocumentBuilder();
	        Document doc = dBuilder.parse(xmlFile);
	        doc.getDocumentElement().normalize();
	        
	        // Get root element node name
	        String rootNodeName = doc.getDocumentElement().getNodeName();
	        
	        if (!(rootNodeName.equals("evsharing-vehicles"))) {
	        	
	        	// Throw new exception if the xml file is invalid
	        	throw new Exception();
	        
	        } else {
	        	
	        	// Load all vehicle nodes into memory
	        	NodeList nodeList = doc.getElementsByTagName("vehicle");
		        
		        // Create a list of vehicles objects
		        List<Vehicle> vehiclesList = new ArrayList<Vehicle>();
		       
		        for (int i = 0; i < nodeList.getLength(); i++) {
		        	
		        	// Get a vehicle from a node and add it to list
		        	vehiclesList.add(getVehicle(nodeList.item(i)));
		        }
		        
		        // Delete all current vehicles
		        vehicleService.deleteAllVehicles();
		               
		        for (Vehicle theVehicle : vehiclesList) {
		            
		            // Save all vehicle objects to the database
		        	vehicleService.saveVehicle(theVehicle);    
		        }
	        }
	        
	    } catch (SAXException | ParserConfigurationException | IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	    	System.err.println("Invalid XML file");
		}
	}
	
	// Method to get a vehicle object from each node
	
	private static Vehicle getVehicle(Node node) {
		
		Vehicle theVehicle = new Vehicle();
	    
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
	    	
	    	// Set the vehicle attributes according to element values
	    	
	        Element element = (Element) node;
	        theVehicle.setLicensePlates(getTagValue("licensePlates", element));
	        theVehicle.setModel(getTagValue("model", element));
	        theVehicle.setCharge(Double.parseDouble(getTagValue("charge", element)));
	        theVehicle.setAvailable(Boolean.parseBoolean(getTagValue("available", element)));
	    }

	    return theVehicle;
	}
	
	// Method to get the value of a specific tag in an element
	
	private static String getTagValue(String tag, Element element) {
	    NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
	    Node node = (Node) nodeList.item(0);
	    return node.getNodeValue();
	}
}