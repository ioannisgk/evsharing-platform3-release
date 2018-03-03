package com.ioannisgk.evsharing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Simulation;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.SimulationDAO;
import com.ioannisgk.evsharing.repositories.VehicleDAO;

@Service
public class SimulationServiceImpl implements SimulationService {

	// Inject the simulation dao
	@Autowired
	private SimulationDAO simulationDAO;

	@Override
	@Transactional
	public List<Simulation> getSimulations() {
		
		// Delegate call to dao
		return simulationDAO.getSimulations();
	}

	@Override
	@Transactional
	public void saveSimulation(Simulation theSimulation) {

		// Delegate call to dao
		simulationDAO.saveSimulation(theSimulation);
	}

	@Override
	@Transactional
	public Simulation getSimulation(int theId) {
		
		// Delegate call to dao
		return simulationDAO.getSimulation(theId);
	}

	@Override
	@Transactional
	public void deleteSimulation(int theId) {

		// Delegate call to dao
		simulationDAO.deleteSimulation(theId);
	}

	@Override
	@Transactional
	public void deleteAllSimulations() {
		
		// Delegate call to dao
		simulationDAO.deleteAllSimulations();
	}
}