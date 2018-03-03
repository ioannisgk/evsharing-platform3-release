package com.ioannisgk.evsharing.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="route")
public class Route {
	
	// Class attributes mapped to table columns in database
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="route_id")
	private int id;
		
	@Column(name="start_station_id")
	private int startStationId;
		
	@Column(name="finish_station_id")
	private int finishStationId;
		
	@Column(name="user_id")
	private int userId;
		
	@Column(name="vehicle_id")
	private int vehicleId;
	
	@NotNull(message="is required")
	@Column(name="start_time")
	private String startTime;
	
	@NotNull(message="is required")
	@Column(name="end_time")
	private String endTime;
	
	@Column(name="status")
	private String status;
	
	@Transient
	private String currentStationStartName;
	
	@Transient
	private String currentStationFinishName;
		
	@Transient
	private String currentUsername;
		
	@Transient
	private String currentLicensePlates;
		
	// Class constructor
	public Route() {
		
	}
	
	// Getters and setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStartStationId() {
		return startStationId;
	}

	public void setStartStationId(int startStationId) {
		this.startStationId = startStationId;
	}

	public int getFinishStationId() {
		return finishStationId;
	}

	public void setFinishStationId(int finishStationId) {
		this.finishStationId = finishStationId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
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

	public String getCurrentLicensePlates() {
		return currentLicensePlates;
	}

	public void setCurrentLicensePlates(String currentLicensePlates) {
		this.currentLicensePlates = currentLicensePlates;
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	// toString method for debugging

	@Override
	public String toString() {
		return "Route [id=" + id + ", startStationId=" + startStationId + ", finishStationId=" + finishStationId
				+ ", userId=" + userId + ", vehicleId=" + vehicleId + ", startTime=" + startTime + ", endTime="
				+ endTime + ", status=" + status + ", currentStationStartName=" + currentStationStartName
				+ ", currentStationFinishName=" + currentStationFinishName + ", currentUsername=" + currentUsername
				+ ", currentLicensePlates=" + currentLicensePlates + "]";
	}

}