package com.ioannisgk.evsharing.utils;

import java.net.Socket;

import org.springframework.stereotype.Component;

@Component
public class SocketConnectionHelper {

	// Class attributes
	private Socket client;

	// Getters and setters
	
	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}
	
}