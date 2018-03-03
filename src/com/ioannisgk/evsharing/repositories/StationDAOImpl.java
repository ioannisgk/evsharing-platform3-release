package com.ioannisgk.evsharing.repositories;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ioannisgk.evsharing.entities.Station;

@Repository
public class StationDAOImpl implements StationDAO {
	
	// Inject hibernate's session factory
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Station> getStations() {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query
		Query<Station> theQuery = currentSession.createQuery("from Station order by traffic_level DESC", Station.class);
			
		// Execute query and get result list
		List<Station> stations = theQuery.getResultList();
			
		// Return the results		
		return stations;
	}
	
	@Override
	public List<Station> getStationsAlphabetically() {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query
		Query<Station> theQuery = currentSession.createQuery("from Station order by name ASC", Station.class);
			
		// Execute query and get result list
		List<Station> stations = theQuery.getResultList();
			
		// Return the results		
		return stations;
	}

	@Override
	public void saveStation(Station theStation) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Save the station to the database
		currentSession.saveOrUpdate(theStation);
	}

	@Override
	public Station getStation(int theId) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Retrieve the station from the database with the primary key
		Station theStation = currentSession.get(Station.class, theId);
		
		// Return the result
		return theStation;
	}

	@Override
	public void deleteStation(int theId) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete the station from the database with the primary key
		
		Query theQuery = currentSession.createQuery("delete from Station where id=:stationId");
		theQuery.setParameter("stationId", theId);
		theQuery.executeUpdate();
	}

	@Override
	public void deleteAllStations() {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete all the stations from the database
		
		Query theQuery = currentSession.createQuery("delete from Station");
		theQuery.executeUpdate();
	}
}