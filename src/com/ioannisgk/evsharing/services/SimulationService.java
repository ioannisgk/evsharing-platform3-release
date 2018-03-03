package com.ioannisgk.evsharing.services;

import java.util.List;

import com.ioannisgk.evsharing.entities.Simulation;
import com.ioannisgk.evsharing.entities.Vehicle;

public interface SimulationService {
	
	public List<Simulation> getSimulations();

	public void saveSimulation(Simulation theSimulation);

	public Simulation getSimulation(int theId);

	public void deleteSimulation(int theId);

	public void deleteAllSimulations();

}