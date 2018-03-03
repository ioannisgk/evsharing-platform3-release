package com.ioannisgk.evsharing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Vehicle;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.VehicleDAO;

@Service
public class VehicleServiceImpl implements VehicleService {

	// Inject the vehicle dao
	@Autowired
	private VehicleDAO vehicleDAO;

	@Override
	@Transactional
	public List<Vehicle> getVehicles() {
		
		// Delegate call to dao
		return vehicleDAO.getVehicles();
	}
	
	@Override
	@Transactional
	public List<Vehicle> getAllVehicles() {
		
		// Delegate call to dao
		return vehicleDAO.getAllVehicles();
	}

	@Override
	@Transactional
	public void saveVehicle(Vehicle theVehicle) {

		// Delegate call to dao
		vehicleDAO.saveVehicle(theVehicle);
	}

	@Override
	@Transactional
	public Vehicle getVehicle(int theId) {
		
		// Delegate call to dao
		return vehicleDAO.getVehicle(theId);
	}

	@Override
	@Transactional
	public void deleteVehicle(int theId) {

		// Delegate call to dao
		vehicleDAO.deleteVehicle(theId);
	}

	@Override
	@Transactional
	public void deleteAllVehicles() {
		
		// Delegate call to dao
		vehicleDAO.deleteAllVehicles();
	}
	
	@Override
	@Transactional
	public List<Vehicle> getVehiclesByField(String field, int value) {
		
		// Delegate call to dao
		return vehicleDAO.getVehiclesByField(field, value);
	}
	
	@Override
	@Transactional
	public List<Vehicle> getVehiclesByField(String field, String value) {
		
		// Delegate call to dao
		return vehicleDAO.getVehiclesByField(field, value);
	}
}