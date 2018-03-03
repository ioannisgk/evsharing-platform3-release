package com.ioannisgk.evsharing.services;

import java.util.List;
import com.ioannisgk.evsharing.entities.Station;

public interface StationService {
	
	public List<Station> getStations();
	
	public List<Station> getStationsAlphabetically();

	public void saveStation(Station theStation);

	public Station getStation(int theId);

	public void deleteStation(int theId);
	
	public void deleteAllStations();

}