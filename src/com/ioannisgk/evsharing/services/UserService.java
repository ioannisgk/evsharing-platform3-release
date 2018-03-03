package com.ioannisgk.evsharing.services;

import java.util.List;
import com.ioannisgk.evsharing.entities.User;

public interface UserService {
	
	public List<User> getUsers();

	public void saveUser(User theUser);

	public User getUser(int theId);

	public void deleteUser(int theId);
	
	public List<User> getUsersByField(String field, String value);

}