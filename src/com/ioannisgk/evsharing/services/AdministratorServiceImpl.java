package com.ioannisgk.evsharing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;

@Service
public class AdministratorServiceImpl implements AdministratorService {
	
	// Inject the administrator dao
	@Autowired
	private AdministratorDAO administratorDAO;

	@Override
	@Transactional
	public List<Administrator> getAdministrators() {
		
		// Delegate call to dao
		return administratorDAO.getAdministrators();
	}

	@Override
	@Transactional
	public void saveAdministrator(Administrator theAdministrator) {
		
		// Delegate call to dao
		administratorDAO.saveAdministrator(theAdministrator);
	}

	@Override
	@Transactional
	public Administrator getAdministrator(int theId) {
		
		// Delegate call to dao
		return administratorDAO.getAdministrator(theId);
	}

	@Override
	@Transactional
	public void deleteAdministrator(int theId) {

		// Delegate call to dao
		administratorDAO.deleteAdministrator(theId);
	}

	@Override
	@Transactional
	public List<Administrator> getAdministratorsByField(String field, String value) {
		
		// Delegate call to dao
		return administratorDAO.getAdministratorsByField(field, value);
	}
}