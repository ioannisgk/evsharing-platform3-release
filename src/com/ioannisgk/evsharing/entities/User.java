package com.ioannisgk.evsharing.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="user")
public class User {
	
	// Class attributes mapped to table columns in database
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private int id;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=45, message="maximum chars 45")
	@Column(name="username")
	private String username;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=45, message="maximum chars 45")
	@Column(name="password")
	private String password;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=255, message="maximum chars 255")
	@Column(name="name")
	private String name;
	
	@Column(name="gender")
	private String gender;
			
	@NotNull(message="is required")
	@Column(name="dob")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dob;
	
	@Transient
	private String requestStatus;
	
	@Transient
	private boolean used;
	
	// Class constructors
	public User() {
		
	}
	
	public User(String username, String password, String name, String gender, Date dob){
		this.username = username;
		this.password = password;
		this.name = name;
		this.gender = gender;
		this.dob = dob;
	}
	
	public User(String requestStatus){
		this.requestStatus = requestStatus;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	
	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	// toString method for debugging
	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", name=" + name + ", gender="
				+ gender + ", dob=" + dob + ", requestStatus=" + requestStatus + ", used=" + used + "]";
	}
}