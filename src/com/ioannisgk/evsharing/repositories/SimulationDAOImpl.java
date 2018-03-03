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
import com.ioannisgk.evsharing.entities.Simulation;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.entities.Vehicle;

@Repository
public class SimulationDAOImpl implements SimulationDAO {

	// Inject hibernate's session factory
	@Autowired
	private SessionFactory sessionFactory;		

	@Override
	public List<Simulation> getSimulations() {
			
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query
		Query<Simulation> theQuery = currentSession.createQuery("from Simulation", Simulation.class);
			
		// Execute query and get result list
		List<Simulation> simulations = theQuery.getResultList();
			
		// Return the results		
		return simulations;
	}

	@Override
	public void saveSimulation(Simulation theSimulation) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Save the simulation to the database
		currentSession.saveOrUpdate(theSimulation);	
	}

	@Override
	public Simulation getSimulation(int theId) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Retrieve the simulation from the database with the primary key
		Simulation theSimulation = currentSession.get(Simulation.class, theId);
		
		// Return the result
		return theSimulation;
	}

	@Override
	public void deleteSimulation(int theId) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete the simulation from the database with the primary key
		
		Query theQuery = currentSession.createQuery("delete from Simulation where id=:simulationId");
		theQuery.setParameter("simulationId", theId);
		theQuery.executeUpdate();
	}

	@Override
	public void deleteAllSimulations() {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete all the simulations from the database
		
		Query theQuery = currentSession.createQuery("delete from Simulation");
		theQuery.executeUpdate();
	}
}