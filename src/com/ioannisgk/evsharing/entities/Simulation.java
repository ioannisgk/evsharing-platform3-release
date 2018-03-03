package com.ioannisgk.evsharing.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name="simulation")
public class Simulation {
	
	// Class attributes mapped to table columns in database
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="simulation_id")
	private int id;
		
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=20, message="maximum chars 20")
	@Column(name="message")
	private String message;
	
	@Size(max=20, message="maximum chars 20")
	@Column(name="status")
	private String status;
	
	@Transient
	private String currentStationStartName;
	
	@Transient
	private String currentStationFinishName;
		
	@Transient
	private String currentUsername;
	
	@Transient
	private String currentStartTime;

	// Class constructor
	public Simulation() {
			
	}

	// Getters and setters
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCurrentStationStartName() {
		return currentStationStartName;
	}

	public void setCurrentStationStartName(String currentStationStartName) {
		this.currentStationStartName = currentStationStartName;
	}

	public String getCurrentStationFinishName() {
		return currentStationFinishName;
	}

	public void setCurrentStationFinishName(String currentStationFinishName) {
		this.currentStationFinishName = currentStationFinishName;
	}

	public String getCurrentUsername() {
		return currentUsername;
	}

	public void setCurrentUsername(String currentUsername) {
		this.currentUsername = currentUsername;
	}

	public String getCurrentStartTime() {
		return currentStartTime;
	}

	public void setCurrentStartTime(String currentStartTime) {
		this.currentStartTime = currentStartTime;
	}
	
	// toString method for debugging
	
	@Override
	public String toString() {
		return "Simulation [id=" + id + ", message=" + message + ", status=" + status + ", currentStationStartName="
				+ currentStationStartName + ", currentStationFinishName=" + currentStationFinishName
				+ ", currentUsername=" + currentUsername + ", currentStartTime=" + currentStartTime + "]";
	}

}