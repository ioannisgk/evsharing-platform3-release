package com.ioannisgk.evsharing.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ioannisgk.evsharing.entities.Administrator;
import com.ioannisgk.evsharing.entities.Route;
import com.ioannisgk.evsharing.entities.User;
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.repositories.UserDAO;
import com.ioannisgk.evsharing.services.RouteService;
import com.ioannisgk.evsharing.services.UserService;
import com.ioannisgk.evsharing.utils.StrongTextEncryptorHelper;

@Controller
@RequestMapping("/user")
public class UserController {
	
	// Inject the user service
	@Autowired
	private UserService userService;
	
	// Inject the route service
	@Autowired
	private RouteService routeService;
	
	// Inject the text encryptor helper
	@Autowired
	private StrongTextEncryptorHelper strongTextEncryptorHelper;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		// Trim input Strings to remove leading and trailing whitespace
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/list")
	public String listUsers(Model theModel) {
		
		// Get users from the service
		List<User> theUsers = userService.getUsers();
		
		// Get routes from the service
		List<Route> theRoutes = routeService.getRoutes();
		
		// Iterate list and save current helper values to transient attributes in entity class
		
		for (int i = 0; i < theUsers.size(); i++) {
			
			// Get current user id
			int currentUserId = theUsers.get(i).getId();
			
			for (int j = 0; j < theRoutes.size(); j++) {
				
				// Get user ids that are used in routes
				int routeUserId = theRoutes.get(j).getUserId();
				
				if (currentUserId == routeUserId) {
					
					// Save current used status to the list
					theUsers.get(i).setUsed(true);
				}
			}
		}
		
		// Add users to the model
		theModel.addAttribute("users", theUsers);
		
		// Add count variables to the model
	 	theModel.addAttribute("countUsers", theUsers.size());
		
		return "users";
	}
	
	@GetMapping("/add-new-user")
	public String addNewUser(Model theModel) {
		
		// Create model attribute to bind form data
		User theUser = new User();
		
		// Add attribute to the model
		theModel.addAttribute("user", theUser);
		
		// Get users from the service
		List<User> theUsers = userService.getUsers();
				
		// Add count variables to the model
		theModel.addAttribute("countUsers", theUsers.size());
		
		return "user-form";
	}
	
	@PostMapping("/save-user")
	public String saveUser(
			@Valid @ModelAttribute("user") User theUser,
			BindingResult theBindingResult, Model theModel) {
		
		// If input data have errors redirect back to user form
		
		if (theBindingResult.hasErrors()) {
			
			// Get users from the service
			List<User> theUsers = userService.getUsers();
					
			// Add count variables to the model
			theModel.addAttribute("countUsers", theUsers.size());
			
			return "user-form";
		}
		
		// If input data are valid save the user and redirect to list page

		else {
			
			// Get current attributes from user object
			
			String currentUsername = theUser.getUsername();
			String currentPassword = theUser.getPassword();
			
			// Get users from the database that have the username entered in the form
			List<User> theUsers = userService.getUsersByField("username", currentUsername);
			
			if (theUsers.size() == 0) {
				
				// Encrypt current password
				String encryptedPassword = strongTextEncryptorHelper.encryptPassword(currentPassword);
				
				// Save encrypted password to the object 
				theUser.setPassword(encryptedPassword);
				
			} else if (theUsers.size() == 1) {
				
				try {
					
					// Try to save user using the service
					userService.saveUser(theUser);
					
				} catch (Exception e) {
						
					// If username already exists, do not register again
					return "redirect:/user/list";
				}
			}
			
			// Save user using the service
			userService.saveUser(theUser);
			
			return "redirect:/user/list";
		}
	}
	
	@GetMapping("/update-user")
	public String updateUser(@RequestParam("userId") int theId, Model theModel) {
		
		// Get user from the service
		User theUser = userService.getUser(theId);
		
		// Add attribute to the model to prepopulate the form
		theModel.addAttribute("user", theUser);
		
		// Get users from the service
		List<User> theUsers = userService.getUsers();
							
		// Add count variables to the model
		theModel.addAttribute("countUsers", theUsers.size());
		
		return "user-form";
	}
	
	@GetMapping("/delete-user")
	public String deleteUser(@RequestParam("userId") int theId) {
		
		// Delete user using the service
		userService.deleteUser(theId);
		
		// Redirect to list page
		return "redirect:/user/list";
	}
}