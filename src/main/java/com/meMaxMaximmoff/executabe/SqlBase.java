package com.meMaxMaximmoff.executabe;

/*
 * Created by Max Maximoff on 2/4/2021.
 * Class for working with playlist tables in the MySQL database
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public abstract class SqlBase {
	
	private String database_url;        // Database address 
	private String user_login; 			// User login
	private String user_password; 		// Database password
	
	
	public SqlBase(String database_url, String user_login, String user_password) {
		this.database_url = database_url;
		this.user_login = user_login;
		this.user_password = user_password;

	}
	
	   // set database_url
	   public void setDatabaseUrl( String database_url )
	   {
		   this.database_url = database_url; 
	   } 

	   // return database_url
	   public String getDatabaseUrl()
	   {
	      return this.database_url;
	   } 
	   
	   // set user_login
	   public void setUserLogin( String user_login )
	   {
		   this.user_login = user_login; 
	   } 

	   // return database_url
	   public String getUserLogin()
	   {
	      return this.user_login;
	   } 
	   
	   // set user_password
	   public void setUserPassword( String user_password )
	   {
		   this.user_password = user_password; 
	   } 

	   // return user_password
	   public String getUserPassword()
	   {
	      return this.user_password;
	   } 
	   
		public Connection createConnection(String driver) throws SQLException, ClassNotFoundException{
			
			Connection connection = null;

			try{
				Class.forName(driver);
				connection = DriverManager.getConnection(database_url, user_login, user_password);
				System.out.println("Connected");
			}catch(ClassNotFoundException e) {
				System.out.println("Connection Failed");
				System.out.println(e);
			}
			catch(SQLException e) {
				System.out.println("Connection Failed");
				System.out.println(e);
			}
		  return connection;
		}
		
		public void closeConnection(Connection connection) throws SQLException {
			try{

				if(connection!=null)
					connection.close();
				System.out.println("Disconnected");
			}
			catch(SQLException e) {
				System.out.println("Unable close connection");
				System.out.println(e);
			}
		}

        // Method for creating a new table in the database		
		public abstract void createNewTableIfNoExists(Connection connection, String tableName) throws SQLException;
		// Method for deleting a table in the database
		public abstract void deleteTableIfExists(Connection connection, String tableName) throws SQLException;
		// Method for deleting all records in a database table	
		public abstract void deleteAllPlistsInBaseTable (Connection connection, String tableName) throws SQLException;
    	//  Method for adding plist to database table
		public abstract void addPlistToBaseTable (Connection connection, List<Entry> entries, String tableName) throws Exception;
		//  Method for updating the plist in the database table
		public abstract void updatePlistInBaseTable (Connection connection, List<Entry> entries, String tableName) throws SQLException;
		// Method for reading data from a database table in a bean by SQL query
		public abstract List<Entry> readDataFromBaseTable (Connection connection, String tableName, String query) throws SQLException;

	   @Override
	   public String toString()
	   {
	      return String.format( "Database_url: %s User: %s Password: %s", 
	    		  getDatabaseUrl(), getUserLogin(), getUserPassword() );
	   } // end method toString
}
