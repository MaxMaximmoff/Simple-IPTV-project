package com.meMaxMaximmoff.executabe;

// examples of using SQL to prepare playlists
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestSqlQueries {
	
	   final static String database_url = "jdbc:mysql://localhost/plistsDB";  // Default atabase address
	   final static String user_login = "user_login";                         // Default atabase login
	   final static String user_password = "password";                        // Default atabase password
	   final static String mySqlDriver = "com.mysql.cj.jdbc.Driver";          // Driver MySQL
	   final static String mySqlTableName = "plists"; // Name of the table in the MySQL database

	// 
	
//	@BeforeClass
	@Ignore
	public static void prepTempTable() throws Exception{
		
		   final String tempTableName = "temp"; // temporary table name
		   // connecting to the database
		   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);
		   
		   Connection conn = null;

		   try {
			   
			     // connecting to the base
			     conn = mySqlBase.createConnection(mySqlDriver);

		         // deleting the temporary table if it exists
			     mySqlBase.deleteTableIfExists(conn, tempTableName);
			
			     // creating a new temporary table
			     mySqlBase.createNewTableIfNoExists(conn, tempTableName);
			     
			     String coppyAllQuery = String.format("SELECT * FROM %s", mySqlTableName);
			     
			     // Copy all the data from the mySqlTableName table and paste it into the tempTableName table
			     mySqlBase.addPlistToBaseTable(
			    		                       conn,
			    		                       mySqlBase.readDataFromBaseTable(conn, mySqlTableName, coppyAllQuery), 
			    		                       tempTableName);
			     System.out.println("A temporary table is prepared");
			     System.out.println();
			     
			} catch (Exception e) {
				fail();
				e.printStackTrace();
			}

	}

	// 
	@Test
	public void testQuery1() throws Exception{
		
		   final String tempTableName = "temp"; // temporary table name
		   // connecting to the database
		   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);
		   
		   Connection conn = null;

		   try {
			   
			     // connecting to the base
			     conn = mySqlBase.createConnection(mySqlDriver);

			     String extractNovotel = String.format("SELECT * FROM %s WHERE providerName = 'Novotel'", tempTableName);
			     
			     List <Entry> entries = mySqlBase.readDataFromBaseTable(conn, tempTableName, extractNovotel);
			     
			     System.out.println();
			     
			     for(Entry entry : entries) {
			    	 
			    	 System.out.println(String.format("%-15s %-35s %-25s %-55s %-15s", 
			    			                          entry.getTvchId(), 
			    			                          entry.getChannelName(), 
			    			                          entry.getGroupTitle(), 
			    			                          entry.getChannelUri(), 
			    			                          entry.getProviderName()));
			     }
			     System.out.println();
			     
			     
			     
			} catch (Exception e) {
				fail();
				e.printStackTrace();
			}

	}
	
	// 
	@Test
	public void testQuery2() throws Exception{
		
		   final String tempTableName = "temp"; // temporary table name
		   final String groupTitleTipe = "СПОРТИВНЫЕ"; // 
		   // connecting to the database
		   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);
		   
		   Connection conn = null;

		   try {
			   
			     // connecting to the base
			     conn = mySqlBase.createConnection(mySqlDriver);

			     String extractNovotel = String.format("SELECT * FROM %s WHERE providerName = 'Novotel' "
			     		                                                + "AND groupTitle = '%s' "
			     		                                                + "AND tvchId BETWEEN '3110' AND '3193'", 
			     		                                                tempTableName, 
			     		                                                groupTitleTipe);
			     
			     List <Entry> entries = mySqlBase.readDataFromBaseTable(conn, tempTableName, extractNovotel);
			     
			     System.out.println();
			     
			     for(Entry entry : entries) {
			    	 
			    	 System.out.println(String.format("%-15s %-35s %-25s %-55s %-15s", 
			    			                          entry.getTvchId(), 
			    			                          entry.getChannelName(), 
			    			                          entry.getGroupTitle(), 
			    			                          entry.getChannelUri(), 
			    			                          entry.getProviderName()));
			     }
			     System.out.println();
			     
			     
			     
			} catch (Exception e) {
				fail();
				e.printStackTrace();
			}

	}
	
	// 
	@Test
	public void testQuery3() throws Exception{

		final String tempTableName = "temp"; // temporary table name
		// connecting to the database
		MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);

		Connection conn = null;
		Statement statement; // query statement
		ResultSet resultSet; // manages results

		List<Entry> entries = new ArrayList<Entry>();

		try {

			// connecting to the base
			conn = mySqlBase.createConnection(mySqlDriver);

			String extractQuantity = String.format("SELECT groupTitle, COUNT(*) AS quantity FROM %s GROUP BY groupTitle", 
					tempTableName);

			// create Statement for querying database 
			statement = conn.createStatement();
			// Preparing a ResultSet that depends on the query
			resultSet = statement.executeQuery(extractQuantity);

			//Entry.Builder entry = null;
			// Writing each row of the table to entry
			while(resultSet.next()){
				Entry.Builder entry = new Entry.Builder()
						.groupTitle(resultSet.getString(1))
						.tvgLogo(resultSet.getString(2));
				// adding entry to the List
				entries.add(entry.build());
				// Information message
			}

			System.out.println();
			System.out.println(String.format("%-25s %-25s", "Название группы", "Количество каналов"));
			System.out.println();

			for(Entry entry : entries) {

				System.out.println(String.format("%-25s %-25s", 
						entry.getGroupTitle(), 
						entry.getTvgLogo()));
			}
			System.out.println();

			System.out.println();

		} catch (Exception e) {
			fail();
			e.printStackTrace();
		}

	}

}
