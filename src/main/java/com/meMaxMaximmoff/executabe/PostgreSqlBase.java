package com.meMaxMaximmoff.executabe;

/*
 * Created by Max Maximoff on 02/01/2021.
 * Class for working with playlist tables in the PostgreSql database
 */
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class PostgreSqlBase extends SqlBase{

	// Constructor of the PostgreSqlBase class with the database name, username, and password
	public PostgreSqlBase(String database_url, String user_login, String user_password) {

		super(database_url, user_login, user_password);
	}

	// Method for creating a new table in the PostgreSQL database
	public void createNewTableIfNoExists(Connection connection, String tableName) throws SQLException{
		
		PreparedStatement preparedStatement;

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
			System.out.println("An error has occurred on table creation");
			e.printStackTrace();
		}
	}

	// Method for deleting a table in the PostgreSQL database
	public void deleteTableIfExists(Connection connection, String tableName) throws SQLException{
		
		PreparedStatement preparedStatement;

		String newTable = String.format("DROP TABLE IF EXISTS %s", tableName);  

		try {
			preparedStatement = connection.prepareStatement(newTable);
			preparedStatement.executeUpdate();
			System.out.println("Table deleted");
		}
		catch (SQLException e ) {
			System.out.println("An error has occurred on table deleting");
			e.printStackTrace();
		}
	}	
	
	// Method for deleting all records in a PostgreSQL database table	   
	public void deleteAllPlistsInBaseTable(Connection connection, String tableName) throws SQLException{
		
		PreparedStatement preparedStatement;

		try
		{
			// creating a Statement to query the database
			String query = String.format("TRUNCATE TABLE %s", tableName);
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.executeUpdate();

			// Information message
			String message = String.format( "All entries in the %s table have been deleted.", tableName);
			System.out.println(message);

		}
		catch (SQLException e ) {
			System.out.println("An error has occured when accessing the database");
			e.printStackTrace();
		}
	}	   

	//  Method for adding plist to a database table
	public void addPlistToBaseTable(Connection connection, List<Entry> entries, String tableName) throws Exception{
		
		PreparedStatement preparedStatement = null;

		try
		{

			// creating a Statement to querying the database
			String query = String.format("INSERT INTO %s (\"channelName\", \"groupTitle\", \"channelUri\", \"providerName\", \"tvchId\")"
					+ "  VALUES (?, ?, ?, ?, ?)", tableName);

			preparedStatement = connection.prepareStatement(query);

		}
		catch (SQLException e ) {
			System.out.println("An error has occured when accessing the database");
			e.printStackTrace();
		}finally {
			try {
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
			catch (Exception e ) {
				System.out.println("Adding to the database failed!!!\nSuch records exists. You should use the update method");
				//	e.printStackTrace();
			}
		}
	}

	//  Method for updating the plist in the database
	public void updatePlistInBaseTable(Connection connection, List<Entry> entries, String tableName) throws SQLException{
		
		PreparedStatement preparedStatement;

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
			System.out.println("An error has occured when accessing the database");
			e.printStackTrace();
		}
	}



	// Method for reading data from a database table in a bean by SQL query
	public List<Entry> readDataFromBaseTable(Connection connection,String tableName, String query) throws SQLException{
		
		Statement statement; // query statement
		ResultSet resultSet; // manages results

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
			String message = String.format( "The query data from the \"%s\" table is generated.", tableName);
			System.out.println(message);

		}
		catch (SQLException e ) {
			System.out.println("An error has occured when accessing the database");
			e.printStackTrace();
		}
		// returning the list
	return entries;

	}	

}
