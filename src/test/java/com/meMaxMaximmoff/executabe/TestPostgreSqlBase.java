package com.meMaxMaximmoff.executabe;

/*
 * Created by Max Maximoff on 02/05/2021.
 * Class for working with playlist tables in the PostgreSql database
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class TestPostgreSqlBase {
	
	final String pgDbUrl = "jdbc:postgresql://192.168.1.41:5432/mydb";  // Default database address 
	final String pgDbLogin = "myuser"; 						// Default user login
	final String pgDbPass = "123"; 						// Default database password
	
	final String pgSqlDriver = "org.postgresql.Driver"; 	// Driver PostgreSql


	   // Testing a constructor using Java Reflection
	   @Test
	   @Category({MainTestsCategory.class, ExcludedTestsCategory.class})
	   public void testParamConstructor() throws Exception {


		   String some_database_url = "jdbc:postgresql://localhost/someDB";  // Some database address
		   String some_user_login = "login";                                 // Some database login
		   String some_user_password = "123";                                // Some database password

		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(some_database_url, some_user_login, some_user_password);

		   Field field_database_url = postgreSqlBase.getClass().getSuperclass().getDeclaredField("database_url");
		   Field field_user_login = postgreSqlBase.getClass().getSuperclass().getDeclaredField("user_login");
		   Field field_user_password = postgreSqlBase.getClass().getSuperclass().getDeclaredField("user_password");

		   field_database_url.setAccessible(true);
		   field_user_login.setAccessible(true);
		   field_user_password.setAccessible(true);

		   assertEquals(some_database_url, field_database_url.get(postgreSqlBase));
		   assertEquals(some_user_login, field_user_login.get(postgreSqlBase));
		   assertEquals(some_user_password, field_user_password.get(postgreSqlBase));

	   }
	   
	   // Testing createConnection(String driver) method
	   @Test
	   @Category(MainTestsCategory.class)
	   public void testCreateConnection() throws Exception {
		   
		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
		   
		   Connection conn = postgreSqlBase.createConnection(pgSqlDriver);
		   
		   assertFalse(conn.isClosed());
		   
	   }
	   
	   // Testing closeConnection(Connection connection) method
	   @Test
	   @Category(MainTestsCategory.class)
	   public void testCloseConnection() throws Exception {

		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
		   
		   Connection conn = postgreSqlBase.createConnection(pgSqlDriver);
		   
		   postgreSqlBase.closeConnection(conn);
		   
		   assertTrue(conn.isClosed());

	   }
	   
	   // Testing setDatabaseUrl( String database_url ) and getDatabaseUrl() methods
	   @Test
	   public void testSetGetDatabaseUrl() throws Exception {

		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
		   
		   String new_database_url = "jdbc:postgresql://127.0.0.1/plistsDB";
		   
		   postgreSqlBase.setDatabaseUrl(new_database_url);
		   
 		   assertTrue(new_database_url==postgreSqlBase.getDatabaseUrl());

	   }
	   
	   // Testing setUserLogin( String database_url ) and getUserLogin() methods
	   @Test
	   public void testSetGetUserLogin() throws Exception {

		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
		   
		   String new_user_login = "new_user_login";
		   
		   postgreSqlBase.setUserLogin(new_user_login);
		   
 		   assertTrue(new_user_login==postgreSqlBase.getUserLogin());

	   }
	   
	   // Testing setUserPassword( String database_url ) and getUserPassword() methods
	   @Test
	   public void testSetGetUserPassword() throws Exception {

		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
		   
		   String new_user_password = "new_password";
		   
		   postgreSqlBase.setUserPassword(new_user_password);
		   
 		   assertTrue(new_user_password==postgreSqlBase.getUserPassword());

	   }
	   
	   // Testing createNewTableIfNoExists(Connection connection, String tableName)
	   // and deleteTableIfExists(Connection connection, String tableName) methods

	   @Test
	   public void testCreateAndDeleteNewTable() throws Exception {

		   String tableName = "temp";
		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
		   boolean db_exist = false;
		   boolean tvchId_exist = false;
		   boolean channelName_exist = false;
		   boolean groupTitle_exist = false;
		   boolean channelUri_exist = false;
		   boolean providerName_exist = false;
		   
		   Connection conn = postgreSqlBase.createConnection(pgSqlDriver);
		   
		   postgreSqlBase.createNewTableIfNoExists(conn, tableName);
		   
		   DatabaseMetaData md = conn.getMetaData();

		   ResultSet rs = md.getColumns(null, null, tableName, "tvchId");
		   if (rs.next()) {
		        //Column tvchId_exist in the table exist
			   tvchId_exist = true;
		      }
		   rs = md.getColumns(null, null, tableName, "channelName");
		   if (rs.next()) {
		        //Column channelName in the table exist
			   channelName_exist = true;
		      }
		   rs = md.getColumns(null, null, tableName, "groupTitle");
		   if (rs.next()) {
		        //Column groupTitle in the table exist
			   groupTitle_exist = true;
		      }
		   rs = md.getColumns(null, null, tableName, "channelUri");
		   if (rs.next()) {
		        //Column channelUri in the table exist
			   channelUri_exist = true;
		      }
		   rs = md.getColumns(null, null, tableName, "providerName");
		   if (rs.next()) {
		        //Column providerName in the table exist
			   providerName_exist = true;
		      }

		   boolean db_ok = tvchId_exist&&channelName_exist&&groupTitle_exist&&channelUri_exist&&providerName_exist;

		   assertTrue(db_ok);
		   
		   postgreSqlBase.deleteTableIfExists(conn, tableName);
		   
		   rs = md.getTables(null, null, tableName, null);
		   if (!rs.next()) {
		     //Table Exist
			   db_exist = false;
		   }
		   
		   assertFalse(db_exist);

	   }

	   
	   // Testing addPlistToBaseTable(Connection connection, List<Entry> entries, String tableName) method
	   @Test
	   @Category(MainTestsCategory.class)
	   public void testAddPlistToBaseTable() throws Exception {

		   String tableName = "temp"; // temporary table name
		   List <Entry> expected_entries = new ArrayList <Entry> (); // place to store data
		   try { 
			   Entry.Builder entry = null;

			   // Adding random data to the List <Entry> expected_entries
			   for (int i=0; i <2; ++i) { 
				   entry = new Entry.Builder()
						   .tvchId(RandomString.getRandomIntString(5))
						   .channelName(RandomString.getRandomString(45-1))
						   .groupTitle(RandomString.getRandomString(45-1))
						   .channelUri(RandomString.getRandomString(65-1))
						   .providerName(RandomString.getRandomString(45-1));

				   expected_entries.add(entry.build());
			   }  

			   // connecting to the database
			   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);

			   Connection conn = postgreSqlBase.createConnection(pgSqlDriver);
			   
			   // deleting the temporary table if it exists
			   postgreSqlBase.deleteTableIfExists(conn, tableName);
			   // creating a new temporary table
			   postgreSqlBase.createNewTableIfNoExists(conn, tableName);
			   // writing prepared random data to the table
			   postgreSqlBase.addPlistToBaseTable (conn, expected_entries, tableName);
			   // place to receive data from the table
			   List<Entry> received_entries = new ArrayList<Entry>();

			   Statement statement = null; // query statement
			   ResultSet resultSet = null; // manages results

			   // creating Statement for querying database 
			   statement = conn.createStatement();
			   // querying the database on request and write the data to List<Entry> received_entries
			   for (int i=0; i <2; ++i) {

				   String query = String.format("SELECT * FROM %s WHERE \"tvchId\" = %s",
						                                 tableName, Integer.parseInt(expected_entries.get(i).getTvchId()));

				   // Preparing a ResultSet that depends on the query
				   resultSet = statement.executeQuery(query);
				   Entry.Builder received_entry = null;
				   // Writing a row of a table in received_entry
				   while(resultSet.next()){
					   received_entry = new Entry.Builder()
							   .tvchId(resultSet.getString(1))
							   .channelName(resultSet.getString(2))
							   .groupTitle(resultSet.getString(3))
							   .channelUri(resultSet.getString(4))
							   .providerName(resultSet.getString(5));
					// adding received_entry to the List
					   received_entries.add(received_entry.build());

				   }
				   // compare the results obtained from the table with the sent ones (they must be the same)
				   assertEquals((expected_entries.get(i).getChannelName()), (received_entries.get(i).getChannelName()));
				   assertEquals((expected_entries.get(i).getGroupTitle()), (received_entries.get(i).getGroupTitle()));
				   assertEquals((expected_entries.get(i).getChannelUri()), (received_entries.get(i).getChannelUri()));
				   assertEquals((expected_entries.get(i).getProviderName()), (received_entries.get(i).getProviderName()));

			   } 
			   // deleting the temporary table
			   postgreSqlBase.deleteTableIfExists(conn, tableName); 

		   } 
		   catch (Exception e) { 
			   e.printStackTrace(); 
		   } 

	   }
	   
	   // Testing addPlistToBaseTable(Connection connection, List<Entry> entries, String tableName) method
	   // The test version.
	   @Ignore
	   @Test
	   public void testAddPlistToBaseTable2() throws Exception {

		   String tableName = "temp"; // temporary table name
		   // place to store data
		   List <Entry> expected_entries = new ArrayList <Entry> (); 
		   // place to receive data from the table
		   List<Entry> received_entries = new ArrayList<Entry>();
		   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
		   Connection conn = null;
		   Statement statement = null; // query statement
		   ResultSet resultSet = null; // manages results

		   try {
			   Entry.Builder entry = null;
			   // Adding random data to the List <Entry> expected_entries
			   for (int i=0; i <2; ++i) { 
				   entry = new Entry.Builder()
						   .tvchId(RandomString.getRandomIntString(5))
						   .channelName(RandomString.getRandomString(45-1))
						   .groupTitle(RandomString.getRandomString(45-1))
						   .channelUri(RandomString.getRandomString(65-1))
						   .providerName(RandomString.getRandomString(45-1));

				   expected_entries.add(entry.build());
			   } 

			   // connecting to the database
			   conn = postgreSqlBase.createConnection(pgSqlDriver);

			   // deleting the temporary table if it exists
			   postgreSqlBase.deleteTableIfExists(conn, tableName);
			   // creating a new temporary table
			   postgreSqlBase.createNewTableIfNoExists(conn, tableName);
			   // writing prepared random data to the table
			   postgreSqlBase.addPlistToBaseTable (conn, expected_entries, tableName);

			   // creating Statement for querying database 
			   statement = conn.createStatement();
			   // querying the database on request and write the data to List<Entry> received_entries
			   for (int i=0; i <2; ++i) {

				   String query = String.format("SELECT * FROM %s WHERE \"tvchId\" = %s",
						   tableName, Integer.parseInt(expected_entries.get(i).getTvchId()));

				   // Preparing a ResultSet that depends on the query
				   resultSet = statement.executeQuery(query);
				   Entry.Builder received_entry = null;
				   // Writing a row of a table in received_entry
				   while(resultSet.next()){
					   received_entry = new Entry.Builder()
							   .tvchId(resultSet.getString(1))
							   .channelName(resultSet.getString(2))
							   .groupTitle(resultSet.getString(3))
							   .channelUri(resultSet.getString(4))
							   .providerName(resultSet.getString(5));
					   // adding received_entry to the List
					   received_entries.add(received_entry.build());
				   }
			   }

		   }catch (Exception e) { 
			   assertNotNull(resultSet);
			   //  e.printStackTrace(); 
		   } finally {
			   for (int i=0; i <2; ++i) {
				   // compare the results obtained from the table with the sent ones (they must be the same)
				   assertEquals((expected_entries.get(i).getChannelName()), (received_entries.get(i).getChannelName()));
				   assertEquals((expected_entries.get(i).getGroupTitle()), (received_entries.get(i).getGroupTitle()));
				   assertEquals((expected_entries.get(i).getChannelUri()), (received_entries.get(i).getChannelUri()));
				   assertEquals((expected_entries.get(i).getProviderName()), (received_entries.get(i).getProviderName()));
			   }
			   postgreSqlBase.deleteTableIfExists(conn, tableName); 
		   }

	   }  
	   
	   
	   // Testing updatePlistInBaseTable(Connection connection, List<Entry> entries, String tableName) method
	   @Test
	   public void testUpdatePlistToBase() throws Exception {

		   String tableName = "temp"; // temporary table name
		   List <Entry> entries = new ArrayList <Entry> (); // place to store initial data
		   List <Entry> expected_entries = new ArrayList <Entry> (); // place to store updated data
		   try { 

			   // Adding random data to the List <Entry> expected_entries
			   for (int i=0; i <2; ++i) { 
				   Entry.Builder entry = new Entry.Builder()
						   .tvchId(RandomString.getRandomIntString(5))
						   .channelName(RandomString.getRandomString(45-1))
						   .groupTitle(RandomString.getRandomString(45-1))
						   .channelUri(RandomString.getRandomString(65-1))
						   .providerName(RandomString.getRandomString(45-1));

				   entries.add(entry.build());
			   }  

			   // connecting to the database
			   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);

			   Connection conn = postgreSqlBase.createConnection(pgSqlDriver);
			   
			   // deleting the temporary table if it exists
			   postgreSqlBase.deleteTableIfExists(conn, tableName);
			   // creating a new temporary table
			   postgreSqlBase.createNewTableIfNoExists(conn, tableName);
			   // writing prepared random data to the table
			   postgreSqlBase.addPlistToBaseTable(conn, entries, tableName);
			   // preparing updated data			   
			   for (int i=0; i <2; ++i) { 
				   Entry.Builder expected_entry = new Entry.Builder()
						   .tvchId(entries.get(i).getTvchId())
						   .channelName(RandomString.getRandomString(45-1))
						   .groupTitle(RandomString.getRandomString(45-1))
						   .channelUri(RandomString.getRandomString(65-1))
						   .providerName(RandomString.getRandomString(45-1));

				   expected_entries.add(expected_entry.build());
			   } 			   
			   // updating using method updatePlistToBase()
			   postgreSqlBase.updatePlistInBaseTable(conn, expected_entries, tableName);
			   // place to receive data from the table
			   List<Entry> received_entries = new ArrayList<Entry>();

			   Statement statement = null; // query statement
			   ResultSet resultSet = null; // manages results

			   // creating Statement for querying database 
			   statement = conn.createStatement();
			   // querying the database on request and write the data to List<Entry> received_entries
			   for (int i=0; i <2; ++i) {

				   String query = String.format("SELECT * FROM %s WHERE \"tvchId\" = %s",
						                                 tableName, Integer.parseInt(expected_entries.get(i).getTvchId()));

				   // Preparing a ResultSet that depends on the query
				   resultSet = statement.executeQuery(query);
				   Entry.Builder received_entry = null;
				   // Writing a row of a table in received_entry
				   while(resultSet.next()){
					   received_entry = new Entry.Builder()
							   .tvchId(resultSet.getString(1))
							   .channelName(resultSet.getString(2))
							   .groupTitle(resultSet.getString(3))
							   .channelUri(resultSet.getString(4))
							   .providerName(resultSet.getString(5));
					// adding received_entry to the List
					   received_entries.add(received_entry.build());

				   }
				   // compare the results obtained from the table with the sent ones (they must be the same)
				   assertEquals((expected_entries.get(i).getChannelName()), (received_entries.get(i).getChannelName()));
				   assertEquals((expected_entries.get(i).getGroupTitle()), (received_entries.get(i).getGroupTitle()));
				   assertEquals((expected_entries.get(i).getChannelUri()), (received_entries.get(i).getChannelUri()));
				   assertEquals((expected_entries.get(i).getProviderName()), (received_entries.get(i).getProviderName()));

			   } 
			   // deleting the temporary table
			   postgreSqlBase.deleteTableIfExists(conn, tableName); 

		   } 
		   catch (Exception e) { 
			   e.printStackTrace(); 
		   } 

	   }
	   
	   // Testing readDataFromBaseTable(Connection connection, String tableName, String query) method
	   @Test
	   @Category(MainTestsCategory.class)
	   public void testReadDataFromBase() throws Exception {

		   String tableName = "temp"; // temporary table name
		   List <Entry> expected_entries = new ArrayList <Entry> (); // place to store data
		   // number of rows in the table
		   int rowsNumber = 2;
		   try { 

			   // Adding random data to the List <Entry> expected_entries
			   for (int i=0; i <rowsNumber; ++i) { 
				   Entry.Builder  entry = new Entry.Builder()
						   .tvchId(RandomString.getRandomIntString(5))
						   .channelName(RandomString.getRandomString(45-1))
						   .groupTitle(RandomString.getRandomString(45-1))
						   .channelUri(RandomString.getRandomString(65-1))
						   .providerName(RandomString.getRandomString(45-1));

				   expected_entries.add(entry.build());
			   }  

			   // connecting to the database
			   PostgreSqlBase postgreSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);

			   Connection conn = postgreSqlBase.createConnection(pgSqlDriver);
			   
			   // deleting the temporary table if it exists
			   postgreSqlBase.deleteTableIfExists(conn, tableName);
			   // creating a new temporary table
			   postgreSqlBase.createNewTableIfNoExists(conn, tableName);
			   // writing prepared random data to the table
			   postgreSqlBase.addPlistToBaseTable(conn, expected_entries, tableName);
			   
			   // reading the entire table using readDataFromBase(query)
			   String query = String.format("SELECT * FROM %s", tableName);
			   List <Entry> received_entries = postgreSqlBase.readDataFromBaseTable(conn, tableName, query);
			   
			   int expecTvchId;
			   int receivedTvchId;
			   // compare the results obtained from the table with the sent ones (they must be the same)
			   for (Entry expected_entry : expected_entries) { 
				   for (Entry received_entry : received_entries) {
					   
					   expecTvchId = Integer.parseInt(expected_entry.getTvchId());
					   receivedTvchId = Integer.parseInt(received_entry.getTvchId());
					   
                       if(expecTvchId==receivedTvchId) {
						   assertEquals((expected_entry.getChannelName()), (received_entry.getChannelName()));
						   assertEquals((expected_entry.getGroupTitle()), (received_entry.getGroupTitle()));
						   assertEquals((expected_entry.getChannelUri()), (received_entry.getChannelUri()));
						   assertEquals((expected_entry.getProviderName()), (received_entry.getProviderName()));
                    	   
                       }
					   
				   }
				   
			   }

			   // deleting the temporary table
			   postgreSqlBase.deleteTableIfExists(conn, tableName); 

		   } 
		   catch (Exception e) { 
			   e.printStackTrace(); 
		   } 

	   }
	   

}
