package com.ioannisgk.evsharing.utils;

import java.net.Socket;

import org.springframework.stereotype.Component;

@Component
public class ProcessModeHelper {

	// Class attributes
	private String mode;
	
	// Getters and setters

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}