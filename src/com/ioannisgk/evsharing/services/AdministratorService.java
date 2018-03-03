package com.ioannisgk.evsharing.services;

import java.util.List;

import com.ioannisgk.evsharing.entities.Administrator;

public interface AdministratorService {
	
	public List<Administrator> getAdministrators();

	public void saveAdministrator(Administrator theAdministrator);

	public Administrator getAdministrator(int theId);

	public void deleteAdministrator(int theId);
	
	public List<Administrator> getAdministratorsByField(String field, String value);

}