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

import com.ioannisgk.evsharing.entities.Simulation;
import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.entities.Vehicle;

@Service
public class LoadSimulationsServiceImpl implements LoadSimulationsService {
	
	// Inject the simulation service
	@Autowired
	private SimulationService simulationService;

	@Override
	public void saveSimulations(String filePath) {
		
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
	        
	        if (!(rootNodeName.equals("evsharing-simulations"))) {
	        	
	        	// Throw new exception if the xml file is invalid
	        	throw new Exception();
	        
	        } else {
	        	
	        	// Load all request nodes into memory
	        	NodeList nodeList = doc.getElementsByTagName("request");
		        
		        // Create a list of simulations objects
		        List<Simulation> simulationsList = new ArrayList<Simulation>();
		       
		        for (int i = 0; i < nodeList.getLength(); i++) {
		        	
		        	// Get a simulation from a node and add it to list
		        	simulationsList.add(getSimulation(nodeList.item(i)));
		        }
		        
		        // Delete all current simulations
		        simulationService.deleteAllSimulations();
		               
		        for (Simulation theSimulation : simulationsList) {
		        	
		        	// Set default simulation request status
		        	theSimulation.setStatus("Ready");
		            
		            // Save all simulation objects to the database
		        	simulationService.saveSimulation(theSimulation);    
		        }
	        }
	        
	    } catch (SAXException | ParserConfigurationException | IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	System.err.println("Invalid XML file");
		}
	}
	
	// Method to get a simulation object from each node
	
	private static Simulation getSimulation(Node node) {
		
		Simulation theSimulation = new Simulation();
	    
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
	    	
	    	// Set the simulation attributes according to element values
	    	
	        Element element = (Element) node;
	        theSimulation.setMessage(getTagValue("message", element));
	    }

	    return theSimulation;
	}
	
	// Method to get the value of a specific tag in an element
	
	private static String getTagValue(String tag, Element element) {
	    NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
	    Node node = (Node) nodeList.item(0);
	    return node.getNodeValue();
	}
}