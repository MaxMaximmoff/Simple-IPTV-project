package com.meMaxMaximmoff.executabe;

/*
 * Created by Max Maximoff on 11/07/2020.
 * Class for working with playlist tables in the MySQL database
 */
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class SqlBase {



	private String database_url = "jdbc:mysql://localhost/plistsDB";  // Default database address 
	private String user_login = "user_login"; 						// Default user login
	private String user_password = "password"; 						// Default database password

	private static Connection connection = null; 
	private PreparedStatement preparedStatement = null;

	// Constructor of the SqlBase class with default parameters
	public SqlBase() {
		//System.out.println("Default constructor of the SQLBase class is created");
	}	

	// Constructor of the SQLBase class with the database name, username, and password
	public SqlBase(String database_url, String user_login, String user_password) {
		this.database_url = database_url;
		this.user_login = user_login;
		this.user_password = user_password;
		// System.out.println("Parameterized constructor of the SQLBase class is created");
	}

	public Connection createConnection() throws SQLException, ClassNotFoundException{

		try{
			Class.forName("com.mysql.cj.jdbc.Driver");
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

	public void closeConnection(Connection conn) throws SQLException {
		try{
			connection.close();
			System.out.println("Disconnected");

		}
		catch(SQLException e) {
			System.out.println("Unable close connection");
			System.out.println(e);
		}
	}

	public void createNewTableIfNoExists(String tableName) {

		String newTable = String.format("CREATE TABLE IF NOT EXISTS %s (" 
				+ "tvchId int auto_increment not null,"  
				+ "channelName varchar(45) not null," 
				+ "groupTitle varchar(45) not null,"  
				+ "channelUri varchar(65) not null, "
				+ "providerName varchar(45) not null, "
				+ "PRIMARY KEY(tvchId)) engine innodb default charset=utf8", tableName);  

		try {
			preparedStatement = connection.prepareStatement(newTable);
			preparedStatement.executeUpdate();
			System.out.println("Table created");
		}
		catch (SQLException e ) {
			System.out.println("An error has occured on Table Creation");
			e.printStackTrace();
		}

	}

	public void deleteTableIfExists(String tableName) {

		String newTable = String.format("DROP TABLE IF EXISTS %s", tableName);  

		try {
			preparedStatement = connection.prepareStatement(newTable);
			preparedStatement.executeUpdate();
			System.out.println("Table deleted");
		}
		catch (SQLException e ) {
			System.out.println("An error has occured on Table Creation");
			e.printStackTrace();
		}

	}	
	// Method for deleting all records in a database table	   
	public void deleteAllPlistsInBase (String tableName) throws Exception {

		try
		{

			// creating a Statement to poll the database
			String query = String.format("TRUNCATE TABLE %s", tableName);
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();

			// Information message
			System.out.println("All entries in the database have been deleted.");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}	   

	//  Method for adding plist to database
	public void addPlistToBase (List<Entry> entries, String tableName) throws Exception {

		try
		{

			// creating a Statement to querying the database
			String query = String.format("INSERT INTO %s (channelName, groupTitle, channelUri, providerName, tvchId)"
					+ "  VALUES (?, ?, ?, ?, ?)", tableName);

			preparedStatement = connection.prepareStatement(query);
			// adding an element-by-element string from entries to Statement
			for (int i=0; i < entries.size(); ++i) {
				String chId = entries.get(i).getTvchId();

				preparedStatement.setString(1, entries.get(i).getChannelName());
				preparedStatement.setString(2, entries.get(i).getGroupTitle());
				preparedStatement.setString(3, entries.get(i).getChannelUri());
				preparedStatement.setString(4, entries.get(i).getProviderName());
				preparedStatement.setInt(5, Integer.parseInt(chId.trim()));
				preparedStatement.executeUpdate();

			}

			// Information message
			System.out.println("Data added to the database.");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	//  Method for updating the plist in the database
	public void updateBasePlist (List<Entry> entries, String tableName) throws Exception {

		try
		{

			// creating a Statement to querying the database
			String query = String.format("UPDATE %s SET channelName=?, groupTitle=?, channelUri=?, providerName=? WHERE tvchId=?"
					,tableName);
			preparedStatement = connection.prepareStatement(query);
			// adding an element-by-element string from entries to Statement
			for (int i=0; i < entries.size(); ++i) {
				String chId = entries.get(i).getTvchId();

				preparedStatement.setString(1, entries.get(i).getChannelName());
				preparedStatement.setString(2, entries.get(i).getGroupTitle());
				preparedStatement.setString(3, entries.get(i).getChannelUri());
				preparedStatement.setString(4, entries.get(i).getProviderName());
				preparedStatement.setInt(5, Integer.parseInt(chId.trim()));
				preparedStatement.executeUpdate();

			}

			// Information message
			System.out.println("Data in the database has been updated.");

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}



	// Method for reading data from a database table in a bean by SQL query
	public List<Entry> readDataFromBase (String query) throws Exception {

		List<Entry> entries = new ArrayList<Entry>();

		Statement statement = null; // query statement
		ResultSet resultSet = null; // manages results

		try
		{

			// create Statement for querying database 
			statement = connection.createStatement();
			// Preparing a ResultSet that depends on the query
			resultSet = statement.executeQuery(query);
			Entry.Builder entry = null;
			// Writing each row of the table to entry
			while(resultSet.next()){
				entry = new Entry.Builder()
						.tvchId(resultSet.getString(1))
						.channelName(resultSet.getString(2))
						.groupTitle(resultSet.getString(3))
						.channelUri(resultSet.getString(4))
						.providerName(resultSet.getString(5));
				// adding entry to the List
				entries.add(entry.build());

			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		// returning the list
		return entries;

	}	

}
