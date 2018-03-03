package com.ioannisgk.evsharing.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="administrator")
public class Administrator {
	
	// Class attributes mapped to table columns in database
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="administrator_id")
	private int id;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=45, message="maximum chars 45")
	@Column(name="username")
	private String username;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=255, message="maximum chars 255")
	@Column(name="password")
	private String password;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=255, message="maximum chars 255")
	@Column(name="name")
	private String name;
	
	@Column(name="role")
	private String role;
	
	// Class constructor
	public Administrator() {
		
	}

	// Getters and setters
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// toString method for debugging
	
	@Override
	public String toString() {
		return "Administrator [id=" + id + ", username=" + username + ", password=" + password + ", name=" + name
				+ ", role=" + role + "]";
	}
}