package com.ioannisgk.evsharing.services;

import java.util.List;
import com.ioannisgk.evsharing.entities.Route;

public interface RouteService {
	
	public List<Route> getRoutes();

	public void saveRoute(Route theRoute);

	public Route getRoute(int theId);

	public void deleteRoute(int theId);
	
	public void deleteAllRoutes();

	public List<Route> getRoutesByField(String field, int value);
	
	public List<Route> getRoutesByField(String field, String value);
	
	public List<Route> getRoutesWithFinishStationEarlierThanTime(int finishStationId, String time);
	
	public List<Route> getRoutesOfVehicleWithStartStationLaterThanTime(int vehicleId, int startStationId, String time);

}