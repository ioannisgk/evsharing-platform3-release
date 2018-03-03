package com.ioannisgk.evsharing.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.UserDAO;

@Service
public class UserServiceImpl implements UserService {

	// Inject the user dao
	@Autowired
	private UserDAO userDAO;

	@Override
	@Transactional
	public List<User> getUsers() {
		
		// Delegate call to dao
		return userDAO.getUsers();
	}

	@Override
	@Transactional
	public void saveUser(User theUser) {
		
		// Delegate call to dao
		userDAO.saveUser(theUser);
	}

	@Override
	@Transactional
	public User getUser(int theId) {
		
		// Delegate call to dao
		return userDAO.getUser(theId);
	}

	@Override
	@Transactional
	public void deleteUser(int theId) {

		// Delegate call to dao
		userDAO.deleteUser(theId);
	}

	@Override
	@Transactional
	public List<User> getUsersByField(String field, String value) {
		
		// Delegate call to dao
		return userDAO.getUsersByField(field, value);
	}
}