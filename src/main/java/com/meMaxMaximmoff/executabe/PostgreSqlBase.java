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


public class PostgreSqlBase {



	private String database_url = "jdbc:postgresql://192.168.1.41:5432/mydb";  // Default database address 
	private String user_login = "myuser"; 						// Default user login
	private String user_password = "123"; 						// Default database password

	private Connection connection = null; 
	private PreparedStatement preparedStatement = null;
	private Statement statement = null; // query statement
	private ResultSet resultSet = null; // manages results

	// Constructor of the SqlBase class with default parameters
	public PostgreSqlBase() {
		//System.out.println("Default constructor of the SQLBase class is created");
	}	

	// Constructor of the SQLBase class with the database name, username, and password
	public PostgreSqlBase(String database_url, String user_login, String user_password) {
		this.database_url = database_url;
		this.user_login = user_login;
		this.user_password = user_password;
		// System.out.println("Parameterized constructor of the SQLBase class is created");
	}

	public Connection createConnection() throws SQLException, ClassNotFoundException{

		try{
			Class.forName("org.postgresql.Driver");
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

	public void closeAll() throws SQLException {
		try{


			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}

			// release the connection statement
			if (statement != null) {
				try {
					statement.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}

			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
			}

			if(connection!=null)
				connection.close();
			System.out.println("Disconnected");
		}
		catch(SQLException e) {
			System.out.println("Unable close connection");
			System.out.println(e);
		}
	}

	public void createNewTableIfNoExists(String tableName) throws SQLException, NullPointerException{

		String newTable = String.format("CREATE TABLE IF NOT EXISTS %s (" 
				+ "\"tvchId\" INTEGER  not null PRIMARY KEY,"  
				+ "\"channelName\" CHARACTER VARYING(45) not null," 
				+ "\"groupTitle\" CHARACTER VARYING(45) not null,"  
				+ "\"channelUri\" CHARACTER VARYING(65) not null, "
				+ "\"providerName\" CHARACTER VARYING(45) not null)", tableName);  

		try {
			preparedStatement = connection.prepareStatement(newTable);
			preparedStatement.executeUpdate();
			System.out.println("Table created");
		}
		catch (SQLException e ) {
			System.out.println("An error has occured on Table Creation");
			e.printStackTrace();
		}
		catch (NullPointerException e ) {
			System.out.println("Connection does not exist");
			e.printStackTrace();
		}

	}

	public void deleteTableIfExists(String tableName) throws SQLException, NullPointerException{

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
		catch (NullPointerException e ) {
			System.out.println("Connection does not exist");
			e.printStackTrace();
		}

	}	
	// Method for deleting all records in a database table	   
	public void deleteAllPlistsInBase (String tableName) throws SQLException, NullPointerException {

		try
		{

			// creating a Statement to poll the database
			String query = String.format("TRUNCATE TABLE %s", tableName);
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();

			// Information message
			System.out.println("All entries in the database have been deleted.");

		}
		catch (SQLException e ) {
			System.out.println("An error has occured on Table Creation");
			e.printStackTrace();
		}
		catch (NullPointerException e ) {
			System.out.println("Connection does not exist");
			e.printStackTrace();
		}
	}	   

	//  Method for adding plist to database
	public void addPlistToBase (List<Entry> entries, String tableName) throws SQLException, NullPointerException {

		try
		{

			// creating a Statement to querying the database
			String query = String.format("INSERT INTO %s (\"channelName\", \"groupTitle\", \"channelUri\", \"providerName\", \"tvchId\")"
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
		catch (SQLException e ) {
			System.out.println("An error has occured on Table Creation");
			e.printStackTrace();
		}
		catch (NullPointerException e ) {
			System.out.println("Connection does not exist");
			e.printStackTrace();
		}

	}

	//  Method for updating the plist in the database
	public void updateBasePlist (List<Entry> entries, String tableName) throws SQLException, NullPointerException {

		try
		{

			// creating a Statement to querying the database
			String query = String.format("UPDATE %s SET \"channelName\"=?, \"groupTitle\"=?, \"channelUri\"=?, \"providerName\"=? WHERE \"tvchId\"=?"
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
		catch (SQLException e ) {
			System.out.println("An error has occured on Table Creation");
			e.printStackTrace();
		}
		catch (NullPointerException e ) {
			System.out.println("Connection does not exist");
			e.printStackTrace();
		}

	}



	// Method for reading data from a database table in a bean by SQL query
	public List<Entry> readDataFromBase (String query) throws SQLException, NullPointerException {

		List<Entry> entries = new ArrayList<Entry>();

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
		catch (SQLException e ) {
			System.out.println("An error has occured on Table Creation");
			e.printStackTrace();
		}
		catch (NullPointerException e ) {
			System.out.println("Connection does not exist");
			e.printStackTrace();
		}
		// returning the list
		return entries;

	}	

}
