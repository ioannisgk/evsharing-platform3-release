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
@Table(name="station")
public class Station {
	
	// Class attributes mapped to table columns in database
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="station_id")
	private int id;
	
	@NotNull(message="is required")
	@Size(min=6, message="minimum chars 6")
	@Size(max=45, message="maximum chars 45")
	@Column(name="name")
	private String name;
		
	@NotNull(message="is required")
	@Column(name="latitude")
	private Double latitude;
	
	@NotNull(message="is required")
	@Column(name="longitude")
	private Double longitude;
		
	@Column(name="traffic_level")
	private Integer trafficLevel;
	
	@Transient
	private boolean used;

	// Class constructor
	public Station() {
			
	}
	
	// Getters and setters

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getTrafficLevel() {
		return trafficLevel;
	}

	public void setTrafficLevel(Integer trafficLevel) {
		this.trafficLevel = trafficLevel;
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
		return "Station [id=" + id + ", name=" + name + ", latitude=" + latitude + ", longitude=" + longitude
				+ ", trafficLevel=" + trafficLevel + ", used=" + used + "]";
	}
}