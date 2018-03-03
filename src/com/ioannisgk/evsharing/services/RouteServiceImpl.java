package com.ioannisgk.evsharing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.RouteDAO;

@Service
public class RouteServiceImpl implements RouteService {

	// Inject the route dao
	@Autowired
	private RouteDAO routeDAO;

	@Override
	@Transactional
	public List<Route> getRoutes() {
		
		// Delegate call to dao
		return routeDAO.getRoutes();
	}

	@Override
	@Transactional
	public void saveRoute(Route theRoute) {
		
		// Delegate call to dao
		routeDAO.saveRoute(theRoute);
	}

	@Override
	@Transactional
	public Route getRoute(int theId) {
		
		// Delegate call to dao
		return routeDAO.getRoute(theId);
	}

	@Override
	@Transactional
	public void deleteRoute(int theId) {

		// Delegate call to dao
		routeDAO.deleteRoute(theId);
	}

	@Override
	@Transactional
	public void deleteAllRoutes() {
		
		// Delegate call to dao
		routeDAO.deleteAllRoutes();		
	}
	
	@Override
	@Transactional
	public List<Route> getRoutesByField(String field, int value) {
		
		// Delegate call to dao
		return routeDAO.getRoutesByField(field, value);
	}
	
	@Override
	@Transactional
	public List<Route> getRoutesByField(String field, String value) {
		
		// Delegate call to dao
		return routeDAO.getRoutesByField(field, value);
	}
	
	@Override
	@Transactional
	public List<Route> getRoutesWithFinishStationEarlierThanTime(int finishStationId, String time) {
		
		// Delegate call to dao
		return routeDAO.getRoutesWithFinishStationEarlierThanTime(finishStationId, time);
	}
	
	@Override
	@Transactional
	public List<Route> getRoutesOfVehicleWithStartStationLaterThanTime(int vehicleId, int startStationId, String time) {
		
		// Delegate call to dao
		return routeDAO.getRoutesOfVehicleWithStartStationLaterThanTime(vehicleId, startStationId, time);
	}
}