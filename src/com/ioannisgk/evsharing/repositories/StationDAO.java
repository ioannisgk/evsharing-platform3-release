package com.ioannisgk.evsharing.repositories;

import java.util.List;

import com.ioannisgk.evsharing.entities.Station;

public interface StationDAO {
	
	public List<Station> getStations();
	
	public List<Station> getStationsAlphabetically();

	public void saveStation(Station theStation);

	public Station getStation(int theId);

	public void deleteStation(int theId);
	
	public void deleteAllStations();

}