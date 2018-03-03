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
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;

@Repository
public class VehicleDAOImpl implements VehicleDAO {

	// Inject hibernate's session factory
	@Autowired
	private SessionFactory sessionFactory;		

	@Override
	public List<Vehicle> getVehicles() {
			
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query
		Query<Vehicle> theQuery = currentSession.createQuery(
				"from Vehicle where available = 1 order by license_plates ASC", Vehicle.class);
			
		// Execute query and get result list
		List<Vehicle> vehicles = theQuery.getResultList();
			
		// Return the results		
		return vehicles;
	}
	
	@Override
	public List<Vehicle> getAllVehicles() {
			
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query
		Query<Vehicle> theQuery = currentSession.createQuery(
				"from Vehicle order by license_plates ASC", Vehicle.class);
			
		// Execute query and get result list
		List<Vehicle> vehicles = theQuery.getResultList();
			
		// Return the results		
		return vehicles;
	}

	@Override
	public void saveVehicle(Vehicle theVehicle) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Save the vehicle to the database
		currentSession.saveOrUpdate(theVehicle);	
	}

	@Override
	public Vehicle getVehicle(int theId) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Retrieve the vehicle from the database with the primary key
		Vehicle theVehicle = currentSession.get(Vehicle.class, theId);
		
		// Return the result
		return theVehicle;
	}

	@Override
	public void deleteVehicle(int theId) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete the vehicle from the database with the primary key
		
		Query theQuery = currentSession.createQuery("delete from Vehicle where id=:vehicleId");
		theQuery.setParameter("vehicleId", theId);
		theQuery.executeUpdate();
	}

	@Override
	public void deleteAllVehicles() {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete all the vehicles from the database
		
		Query theQuery = currentSession.createQuery("delete from Vehicle");
		theQuery.executeUpdate();
	}
	
	@Override
	public List<Vehicle> getVehiclesByField(String field, int value) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Create the query with field and value
		Query<Vehicle> theQuery = currentSession.createQuery(
				"from Vehicle where available = 1 and " + field + "=:theValue", Vehicle.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theValue", value);
		List<Vehicle> vehicles = theQuery.getResultList();
		
		// Return the results		
		return vehicles;
	}
	
	@Override
	public List<Vehicle> getVehiclesByField(String field, String value) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Create the query with field and value
		Query<Vehicle> theQuery = currentSession.createQuery(
				"from Vehicle where " + field + "=:theValue", Vehicle.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theValue", value);
		List<Vehicle> vehicles = theQuery.getResultList();
		
		// Return the results		
		return vehicles;
	}
}