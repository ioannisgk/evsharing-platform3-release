package com.ioannisgk.evsharing.services;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import com.ioannisgk.evsharing.utils.RemoteConnectionHandler;
import com.ioannisgk.evsharing.utils.SocketConnectionHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Thread;

@Component
public class RemoteConnectionServiceImpl implements RemoteConnectionService {
	
	private static final int LISTENING_PORT = 5566;
	
	// Class attributes
	private ServerSocket server;
	
	// Inject the thread task executor
	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	
	// Inject the socket connection helper
	@Autowired
	private SocketConnectionHelper socketConnectionHelper;
	
	// Inject the remote connection handler
	@Autowired
	private RemoteConnectionHandler remoteConnectionHandler;
	
	@Async
	public void startServer() {
		
		try {
			
			// Prepare server socket for reuse even if application may be terminated
			
			server = new ServerSocket();
			server.setReuseAddress(true);
			server.bind(new InetSocketAddress(LISTENING_PORT));
			
		    while (true) {
		    	
		    	// Always listening for incoming messages in server socket
		    	socketConnectionHelper.setClient(server.accept());
		    	
		    	// Start a new thread for processing messages with taskExecutor		    	
		        taskExecutor.execute(remoteConnectionHandler);
		    }
		    
		} catch (Exception e) {
			System.err.println("Exception caught:" + e);
		}
	}
	
	@Async
	public void stopServer() {
		
		try {
			
			// Close the connection and stop the tcp server
			
			socketConnectionHelper.getClient().close();
		    server.close();
		    
		} catch (Exception e) {
			System.err.println("Exception caught:" + e);
		}
	}
}