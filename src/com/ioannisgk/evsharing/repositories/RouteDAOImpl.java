package com.ioannisgk.evsharing.repositories;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Route;

@Repository
public class RouteDAOImpl implements RouteDAO {
	
	// Inject hibernate's session factory
	@Autowired
	private SessionFactory sessionFactory;		

	@Override
	public List<Route> getRoutes() {
			
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query
		Query<Route> theQuery = currentSession.createQuery("from Route where status = 'Accepted' order by start_time ASC", Route.class);
			
		// Execute query and get result list
		List<Route> routes = theQuery.getResultList();
			
		// Return the results		
		return routes;
	}

	@Override
	public void saveRoute(Route theRoute) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Save the route to the database
		currentSession.saveOrUpdate(theRoute);	
	}

	@Override
	public Route getRoute(int theId) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Retrieve the route from the database with the primary key
		Route theRoute = currentSession.get(Route.class, theId);
		
		// Return the result
		return theRoute;
	}

	@Override
	public void deleteRoute(int theId) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete the route from the database with the primary key
		
		Query theQuery = currentSession.createQuery("delete from Route where id=:routeId");
		theQuery.setParameter("routeId", theId);
		theQuery.executeUpdate();
	}

	@Override
	public void deleteAllRoutes() {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete all the routes from the database
		
		Query theQuery = currentSession.createQuery("delete from Route");
		theQuery.executeUpdate();
	}
	
	@Override
	public List<Route> getRoutesByField(String field, int value) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Create the query with field and value
		Query<Route> theQuery = currentSession.createQuery(
				"from Route where status = 'Accepted' and " + field + "=:theValue", Route.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theValue", value);
		List<Route> routes = theQuery.getResultList();
		
		// Return the results		
		return routes;
	}
	
	@Override
	public List<Route> getRoutesByField(String field, String value) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Create the query with field and value
		Query<Route> theQuery = currentSession.createQuery(
				"from Route where " + field + "=:theValue", Route.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theValue", value);
		List<Route> routes = theQuery.getResultList();
		
		// Return the results		
		return routes;
	}
	
	@Override
	public List<Route> getRoutesWithFinishStationEarlierThanTime(int finishStationId, String time) {
			
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query with field and value
		Query<Route> theQuery = currentSession.createQuery(
				"from Route where status = 'Accepted' and finish_station_id=:theFinishStationId and ('" + time + "' > end_time)", Route.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theFinishStationId", finishStationId);
		List<Route> routes = theQuery.getResultList();
		
		// Return the results		
		return routes;
	}
	
	@Override
	public List<Route> getRoutesOfVehicleWithStartStationLaterThanTime(int vehicleId, int startStationId, String time) {
			
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		
		// Create the query with fields and values
		Query<Route> theQuery = currentSession.createQuery(
				"from Route where status = 'Accepted' and vehicle_id=:theVehicleId"
				+ " and start_station_id=:theStartStationId"
				+ " and ('" + time + "' < end_time) order by start_time ASC", Route.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theVehicleId", vehicleId);
		theQuery.setParameter("theStartStationId", startStationId);
		List<Route> routes = theQuery.getResultList();
		
		// Return the results		
		return routes;
	}
}