package com.ioannisgk.evsharing.repositories;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.ioannisgk.evsharing.entities.Administrator;

@Repository
public class AdministratorDAOImpl implements AdministratorDAO {
	
	// Inject hibernate's session factory
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public List<Administrator> getAdministrators() {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Create the query
		Query<Administrator> theQuery = currentSession.createQuery("from Administrator", Administrator.class);
		
		// Execute query and get result list
		List<Administrator> administrators = theQuery.getResultList();
		
		// Return the results		
		return administrators;
	}

	@Override
	public void saveAdministrator(Administrator theAdministrator) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Save the administrator to the database
		currentSession.saveOrUpdate(theAdministrator);
	}

	@Override
	public Administrator getAdministrator(int theId) {

		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Retrieve the administrator from the database with the primary key
		Administrator theAdministrator = currentSession.get(Administrator.class, theId);
		
		// Return the result
		return theAdministrator;
	}

	@Override
	public void deleteAdministrator(int theId) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Delete the administrator from the database with the primary key
		
		Query theQuery = currentSession.createQuery("delete from Administrator where id=:administratorId");
		theQuery.setParameter("administratorId", theId);
		theQuery.executeUpdate();
	}

	@Override
	public List<Administrator> getAdministratorsByField(String field, String value) {
		
		// Get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// Create the query with field and value
		Query<Administrator> theQuery = currentSession.createQuery(
				"from Administrator where " + field + "=:theValue", Administrator.class);
		
		// Execute query while passing the value as a parameter and get result list
		
		theQuery.setParameter("theValue", value);
		List<Administrator> administrators = theQuery.getResultList();
		
		// Return the results		
		return administrators;
	}
}