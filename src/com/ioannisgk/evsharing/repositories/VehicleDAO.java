package com.ioannisgk.evsharing.repositories;

import java.util.List;
import com.ioannisgk.evsharing.entities.Vehicle;

public interface VehicleDAO {
	
	public List<Vehicle> getVehicles();
	
	public List<Vehicle> getAllVehicles();

	public void saveVehicle(Vehicle theVehicle);

	public Vehicle getVehicle(int theId);

	public void deleteVehicle(int theId);

	public void deleteAllVehicles();
	
	public List<Vehicle> getVehiclesByField(String field, int value);
	
	public List<Vehicle> getVehiclesByField(String field, String value);

}