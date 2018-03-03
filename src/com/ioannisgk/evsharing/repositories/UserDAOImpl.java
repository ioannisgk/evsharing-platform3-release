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
import com.ioannisgk.evsharing.entities.User;

@Repository
public class UserDAOImpl implements UserDAO {

	// Inject hibernate's session factory
	@Autowired
	private SessionFactory sessionFactory;		

	@Override
	public List<User> getUsers() {
			
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
			
		// Create the query
		Query<User> theQuery = currentSession.createQuery("from User order by username ASC", User.class);
			
		// Execute query and get result list
		List<User> users = theQuery.getResultList();
			
		// Return the results		
		return users;
	}

	@Override
	public void saveUser(User theUser) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Save the user to the database
		currentSession.saveOrUpdate(theUser);	
	}

	@Override
	public User getUser(int theId) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Retrieve the user from the database with the primary key
		User theUser = currentSession.get(User.class, theId);
		
		// Return the result
		return theUser;
	}

	@Override
	public void deleteUser(int theId) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete the user from the database with the primary key
		
		Query theQuery = currentSession.createQuery("delete from User where id=:userId");
		theQuery.setParameter("userId", theId);
		theQuery.executeUpdate();
	}
	
	@Override
	public List<User> getUsersByField(String field, String value) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Create the query with field and value
		Query<User> theQuery = currentSession.createQuery(
				"from User where " + field + "=:theValue", User.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theValue", value);
		List<User> users = theQuery.getResultList();
		
		// Return the results		
		return users;
	}
}