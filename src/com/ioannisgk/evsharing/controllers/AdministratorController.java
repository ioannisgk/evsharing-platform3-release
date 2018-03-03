package com.ioannisgk.evsharing.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
import com.ioannisgk.evsharing.repositories.AdministratorDAO;
import com.ioannisgk.evsharing.services.AdministratorService;
import com.ioannisgk.evsharing.utils.StrongTextEncryptorHelper;

@Controller
@RequestMapping("/admin")
public class AdministratorController {
	
	// Inject the administrator service
	@Autowired
	private AdministratorService administratorService;
	
	// Inject the text encryptor helper
	@Autowired
	private StrongTextEncryptorHelper strongTextEncryptorHelper;
	
	// Inject the in memory user details manager
	@Autowired
	private InMemoryUserDetailsManager inMemoryUserDetailsManager;
	
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		
		// Trim input Strings to remove leading and trailing whitespace
		
		StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
	}
	
	@GetMapping("/list")
	public String listAdministrators(Model theModel) {
		
		// Get administrators from the service
		List<Administrator> theAdministrators = administratorService.getAdministrators();
		
		// Add administrators to the model
		theModel.addAttribute("administrators", theAdministrators);
		
		// Add count variables to the model
		theModel.addAttribute("countAdmins", theAdministrators.size());
		
		return "admins";
	}
	
	@GetMapping("/add-new-admin")
	public String addNewAdmin(Model theModel) {
		
		// Create model attribute to bind form data
		Administrator theAdministrator = new Administrator();
		
		// Add attribute to the model
		theModel.addAttribute("administrator", theAdministrator);
		
		// Get administrators from the service
		List<Administrator> theAdministrators = administratorService.getAdministrators();
				
		// Add count variables to the model
		theModel.addAttribute("countAdmins", theAdministrators.size());
		
		return "admin-form";
	}
	
	@PostMapping("/save-admin")
	public String saveAdmin(
			@Valid @ModelAttribute("administrator") Administrator theAdministrator,
			BindingResult theBindingResult, Model theModel) {
		
		// If input data have errors redirect back to admin form
		
		if (theBindingResult.hasErrors()) {
			
			// Get administrators from the service
			List<Administrator> theAdministrators = administratorService.getAdministrators();
					
			// Add count variables to the model
			theModel.addAttribute("countAdmins", theAdministrators.size());
			
			return "admin-form";
		}
		
		// If input data are valid save the administrator and redirect to list page

		else {
			
			// Get current attributes from administrator object
			
			String currentUsername = theAdministrator.getUsername();
			String currentPassword = theAdministrator.getPassword();
			String currentRole = theAdministrator.getRole();
			
			// Get administrators from the database that have the username entered in the form
			List<Administrator> theAdministrators = administratorService.getAdministratorsByField("username", currentUsername);
			
			if (theAdministrators.size() == 0) {
				
				// Encrypt current password
				String encryptedPassword = strongTextEncryptorHelper.encryptPassword(currentPassword);
				
				// Save encrypted password to the object 
				theAdministrator.setPassword(encryptedPassword);
				
				// Save administrator to current authenticated users
				inMemoryUserDetailsManager.createUser(
						User
						.withUsername(currentUsername)
						.password(currentPassword)
						.roles(currentRole)
						.build());
				
			} else if (theAdministrators.size() == 1) {
				
				try {
					
					// Try to save administrator using the service
					administratorService.saveAdministrator(theAdministrator);
					
				} catch (Exception e) {
						
					// If username already exists, do not register again
					return "redirect:/admin/list";
				}
			}

			// Save administrator using the service
			administratorService.saveAdministrator(theAdministrator);
			
			return "redirect:/admin/list";
		}
	}
	
	@GetMapping("/update-admin")
	public String updateAdmin(@RequestParam("administratorId") int theId, Model theModel) {
		
		// Get administrator from the service
		Administrator theAdministrator = administratorService.getAdministrator(theId);
		
		// Add attribute to the model to prepopulate the form
		theModel.addAttribute("administrator", theAdministrator);
		
		// Get administrators from the service
		List<Administrator> theAdministrators = administratorService.getAdministrators();
							
		// Add count variables to the model
		theModel.addAttribute("countAdmins", theAdministrators.size());
		
		return "admin-form";
	}
	
	@GetMapping("/delete-admin")
	public String deleteAdmin(@RequestParam("administratorId") int theId) {
		
		// Get current administrator
		Administrator currentAdministrator = administratorService.getAdministrator(theId);
		
		// Get current administrator username
		String currentUsername = currentAdministrator.getUsername();
		
		// Delete administrator from current authenticated users
		inMemoryUserDetailsManager.deleteUser(currentUsername);
		
		// Delete administrator using the service
		administratorService.deleteAdministrator(theId);

		// Redirect to list page
		return "redirect:/admin/list";
	}
}