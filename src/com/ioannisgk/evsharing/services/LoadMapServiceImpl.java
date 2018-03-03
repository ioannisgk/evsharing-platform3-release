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

@Service
public class LoadMapServiceImpl implements LoadMapService {
	
	// Inject the station service
	@Autowired
	private StationService stationService;

	@Override
	public void saveMap(String filePath) {
		
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
	        
	        if (!(rootNodeName.equals("evsharing-maps"))) {
	        	
	        	// Throw new exception if the xml file is invalid
	        	throw new Exception();
	        
	        } else {
	        	
	        	// Load all map nodes into memory
	        	NodeList nodeList = doc.getElementsByTagName("station");
		        
		        // Create a list of station objects
		        List<Station> stationsList = new ArrayList<Station>();
		       
		        for (int i = 0; i < nodeList.getLength(); i++) {
		        	
		        	// Get a station from a node and add it to list
		        	stationsList.add(getStation(nodeList.item(i)));
		        }
		        
		        // Delete all current stations
		        stationService.deleteAllStations();
		               
		        for (Station theStation : stationsList) {
		            
		            // Save all station objects to the database
		        	stationService.saveStation(theStation);    
		        }
	        }
	        
	    } catch (SAXException | ParserConfigurationException | IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	    	System.err.println("Invalid XML file");
		}
	}
	
	// Method to get a station object from each node
	
	private static Station getStation(Node node) {
		
		Station theStation = new Station();
	    
	    if (node.getNodeType() == Node.ELEMENT_NODE) {
	    	
	    	// Set the station attributes according to element values
	    	
	        Element element = (Element) node;
	        theStation.setName(getTagValue("name", element));
	        theStation.setLatitude(Double.parseDouble(getTagValue("latitude", element)));
	        theStation.setLongitude(Double.parseDouble(getTagValue("longitude", element)));
	        theStation.setTrafficLevel(Integer.parseInt(getTagValue("trafficLevel", element)));
	    }

	    return theStation;
	}
	
	// Method to get the value of a specific tag in an element
	
	private static String getTagValue(String tag, Element element) {
	    NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
	    Node node = (Node) nodeList.item(0);
	    return node.getNodeValue();
	}
}