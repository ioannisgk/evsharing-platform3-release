package com.ioannisgk.evsharing.services;

import java.util.LinkedHashMap;

public interface PopulateDropdownsService {
	
	public LinkedHashMap<String, String> getUsersOptions();
	
	public LinkedHashMap<String, String> getVehiclesOptions();
	
	public LinkedHashMap<String, String> getStationsOptions();
	
	public LinkedHashMap<String, String> getTimeOptions();

}