package com.ioannisgk.evsharing.repositories;

import java.util.List;
import com.ioannisgk.evsharing.entities.User;

public interface UserDAO {
	
	public List<User> getUsers();

	public void saveUser(User theUser);

	public User getUser(int theId);

	public void deleteUser(int theId);

	public List<User> getUsersByField(String field, String value);

}