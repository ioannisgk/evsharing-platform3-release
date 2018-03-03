package com.ioannisgk.evsharing.testdb;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class TestDbServlet
 */
@WebServlet("/TestDbServlet")
public class TestDbServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Setup database connection variables
		
		String user = "evsharingUser";
		String pass = "evsharingPass";
		String jdbcUrl = "jdbc:mysql://localhost:3306/evsharing_schema?useSSL=false";
		String driver = "com.mysql.jdbc.Driver";
				
		// Connect to the database and show result
		
		try {
			PrintWriter out = response.getWriter();
			out.println("Connecting to database: " + jdbcUrl);
			Class.forName(driver);
			Connection connection = DriverManager.getConnection(jdbcUrl, user, pass);
			out.println("SUCCESS!!!");
			connection.close();	
			
		} catch (Exception exc) {
			exc.printStackTrace();
			throw new ServletException(exc);
		}
	}
}