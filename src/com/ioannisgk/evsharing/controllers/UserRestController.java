package com.ioannisgk.evsharing.controllers;

import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.services.UserService;
import com.ioannisgk.evsharing.utils.StrongTextEncryptorHelper;

@RestController
@RequestMapping("/api")
public class UserRestController {
	
	// Inject the user service
	@Autowired
	private UserService userService;
	
	// Inject the text encryptor helper
	@Autowired
	private StrongTextEncryptorHelper strongTextEncryptorHelper;
	
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LinkedHashMap<String, String> body) {
    	
    	User currentUser = new User("Could not query the database");
    	
    	// Get username and password from hash map
    	
    	String theUsername = body.get("username");
    	String thePassword = body.get("password");
    	
    	// Decrypt current password from Android app
    	String currentPassword = strongTextEncryptorHelper.decryptPasswordBasic(thePassword);
    	
    	// Get users with the same username from the service
        List<User> theUsers = userService.getUsersByField("username", theUsername);
        
        // If one record is found, check for password
        
        if (theUsers.size() == 1) {
        	
        	// Decrypt current password from database
        	
        	String password = theUsers.get(0).getPassword();
        	String decryptedPassword = strongTextEncryptorHelper.decryptPassword(password);
        	
        	if (decryptedPassword.equals(currentPassword)) {
        		
        		// If password is correct, get the user object and update request status
        		
        		currentUser = (User)theUsers.get(0);
        		currentUser.setRequestStatus("Success");
        		
        	} else {
        		
        		// If password is invalid, update request status
        		currentUser.setRequestStatus("Invalid login details");
        	}
        } else {
        	currentUser.setRequestStatus("Invalid login details");
        }
        
        // Return appropriate HTTP status
        
        if(theUsers.isEmpty()) return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<User>(currentUser, HttpStatus.OK);
    }
	
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
    	
    	// Get users from the service
        List<User> theUsers = userService.getUsers();
        
        // Return appropriate HTTP status
        
        if(theUsers.isEmpty()) return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
        else return new ResponseEntity<List<User>>(theUsers, HttpStatus.OK);
    }

    @GetMapping(value="/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> getUser(@PathVariable("id") int theId) {
    	
    	// Get user with specific id from the service
        User theUser = userService.getUser(theId);
        
        // Return appropriate HTTP status
        
        if (theUser == null) return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        else return new ResponseEntity<User>(theUser, HttpStatus.OK);
    }
 
    @PostMapping("/user")
    public ResponseEntity<Void> createUser(@RequestBody User theUser, UriComponentsBuilder ucBuilder) {
    	
    	// Get all current users from the service
    	List<User> currentUsers = userService.getUsers();
    			
    	for (int i = 0; i < currentUsers.size(); i++) {
    		
    		if (currentUsers.get(i).getUsername().equals(theUser.getUsername())) {
    			
    			// If the username is not unique return appropriate HTTP status
    			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
    		}
    	}
    	
    	// Decrypt current password from Android app
    	
    	String password = theUser.getPassword();
    	String currentPassword = strongTextEncryptorHelper.decryptPasswordBasic(password);
    	
    	// Encrypt current password using strong encryptor
    	String encryptedPassword = strongTextEncryptorHelper.encryptPassword(currentPassword);

    	// Save encrypted password to the object
    	theUser.setPassword(encryptedPassword);
    	
        // Save current user with the service
    	userService.saveUser(theUser);
 
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/user/{id}").buildAndExpand(theUser.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") int theId, @RequestBody User theUser) {
        
    	// Get current user from the service
        User currentUser = userService.getUser(theId);
        
        // Save current user and return appropriate HTTP status
        
        if (currentUser == null) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        
        } else {
        	
        	// Update current user with new data
        	
        	currentUser.setUsername(theUser.getUsername());
            currentUser.setPassword(theUser.getPassword());
            currentUser.setName(theUser.getName());
            currentUser.setGender(theUser.getGender());
            currentUser.setDob(theUser.getDob());
        	
        	userService.saveUser(currentUser);
        	return new ResponseEntity<User>(currentUser, HttpStatus.OK);
        }
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable("id") int theId) {
    	
    	// Get current user from the service
        User currentUser = userService.getUser(theId);
        
        // Delete current user and return appropriate HTTP status
        
        if (currentUser == null) {
            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
        
        } else {
        	
        	userService.deleteUser(theId);
        	return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        }
    }
}