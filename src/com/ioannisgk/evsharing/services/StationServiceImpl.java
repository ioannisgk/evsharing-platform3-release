package com.ioannisgk.evsharing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ioannisgk.evsharing.entities.Station;
import com.ioannisgk.evsharing.repositories.StationDAO;

@Service
public class StationServiceImpl implements StationService {
	
	// Inject the administrator dao
	@Autowired
	private StationDAO stationDAO;

	@Override
	@Transactional
	public List<Station> getStations() {
		
		// Delegate call to dao
		return stationDAO.getStations();
	}
	
	@Override
	@Transactional
	public List<Station> getStationsAlphabetically() {
		
		// Delegate call to dao
		return stationDAO.getStationsAlphabetically();
	}

	@Override
	@Transactional
	public void saveStation(Station theStation) {
		
		// Delegate call to dao
		stationDAO.saveStation(theStation);
	}

	@Override
	@Transactional
	public Station getStation(int theId) {
		
		// Delegate call to dao
		return stationDAO.getStation(theId);
	}

	@Override
	@Transactional
	public void deleteStation(int theId) {

		// Delegate call to dao
		stationDAO.deleteStation(theId);
	}

	@Override
	@Transactional
	public void deleteAllStations() {

		// Delegate call to dao
		stationDAO.deleteAllStations();
	}
}