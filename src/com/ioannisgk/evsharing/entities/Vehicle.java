package com.ioannisgk.evsharing.entities;

import java.util.Comparator;

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
@Table(name="vehicle")
public class Vehicle {
	
	// Class attributes mapped to table columns in database
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="vehicle_id")
	private int id;
		
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=8, message="maximum chars 8")
	@Column(name="license_plates")
	private String licensePlates;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=45, message="maximum chars 45")
	@Column(name="model")
	private String model;
		
	@NotNull(message="is required")
	@Min(value=0, message="minimum is 0%")
	@Max(value=100, message="maximum is 100%")
	@Digits(integer=3, fraction=2, message="maximum 2 decimals")
	@Column(name="charge")
	private Double charge;
		
	@Column(name="available")
	private boolean available;
	
	@Column(name="station_id")
	private int stationId;
	
	@Transient
	private String currentStationName;
	
	@Transient
	private double futureCharge;
	
	@Transient
	private boolean used;

	// Class constructor
	public Vehicle() {
			
	}
		
	// Getters and setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getCharge() {
		return charge;
	}

	public void setCharge(Double charge) {
		this.charge = charge;
	}

	public boolean getAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
		
	public String getLicensePlates() {
		return licensePlates;
	}

	public void setLicensePlates(String licensePlates) {
		this.licensePlates = licensePlates;
	}
		
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	public int getStationId() {
		return stationId;
	}

	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	
	public String getCurrentStationName() {
		return currentStationName;
	}

	public void setCurrentStationName(String currentStationName) {
		this.currentStationName = currentStationName;
	}
	
	public double getFutureCharge() {
		return futureCharge;
	}

	public void setFutureCharge(double futureCharge) {
		this.futureCharge = futureCharge;
	}
	
	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	// Comparator for sorting the arraylist by future charge
	public static Comparator<Vehicle> VehicleCharge = new Comparator<Vehicle>() {

    	public int compare(Vehicle v1, Vehicle v2) {

    		double charge1 = v1.getFutureCharge();
    		double charge2 = v2.getFutureCharge();
    		
    		// Sort in descending order
    		return (int) (charge2 - charge1);
    }};
	
	// toString method for debugging

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", licensePlates=" + licensePlates + ", model=" + model + ", charge=" + charge
				+ ", available=" + available + ", stationId=" + stationId + ", currentStationName=" + currentStationName
				+ ", futureCharge=" + futureCharge + ", used=" + used + "]";
	}

}