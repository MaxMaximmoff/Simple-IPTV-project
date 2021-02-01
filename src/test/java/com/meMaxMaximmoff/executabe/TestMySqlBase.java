package com.meMaxMaximmoff.executabe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;


public class TestMySqlBase {
	
	   
	   final String database_url = "jdbc:mysql://localhost/plistsDB";  // Default atabase address
	   final String user_login = "user_login";                         // Default atabase login
	   final String user_password = "password";                        // Default atabase password
	   

	   // Testing a default constructor using Java Reflection
	   @Test
	   public void testConstructor() throws Exception {

		   MySqlBase mySqlBase = new MySqlBase();

		   Field field_database_url = mySqlBase.getClass().getDeclaredField("database_url");
		   Field field_user_login = mySqlBase.getClass().getDeclaredField("user_login");
		   Field field_user_password = mySqlBase.getClass().getDeclaredField("user_password");

		   //System.out.println(field_database_url.getName());

		   field_database_url.setAccessible(true);
		   field_user_login.setAccessible(true);
		   field_user_password.setAccessible(true);

		   //String obj_database_url = (String) field_database_url.get(sqlBase); //IllegalAccessException
		   //System.out.println(obj_database_url);

		   assertEquals(database_url, field_database_url.get(mySqlBase));
		   assertEquals(user_login, field_user_login.get(mySqlBase));
		   assertEquals(user_password, field_user_password.get(mySqlBase));

	   }	   

	   // Testing a parameterized constructor using Java Reflection
	   @Test
	   public void testParamConstructor() throws Exception {


		   String some_database_url = "jdbc:mysql://localhost/someDB";  // Some database address
		   String some_user_login = "login";                            // Some database login
		   String some_user_password = "123";                           // Some database password

		   MySqlBase mySqlBase = new MySqlBase(some_database_url, some_user_login, some_user_password);

		   Field field_database_url = mySqlBase.getClass().getDeclaredField("database_url");
		   Field field_user_login = mySqlBase.getClass().getDeclaredField("user_login");
		   Field field_user_password = mySqlBase.getClass().getDeclaredField("user_password");

		   //System.out.println(field_database_url.getName());

		   field_database_url.setAccessible(true);
		   field_user_login.setAccessible(true);
		   field_user_password.setAccessible(true);

		   //String obj_database_url = (String) field_database_url.get(sqlBase); //IllegalAccessException
		   //System.out.println(obj_database_url);

		   assertEquals(some_database_url, field_database_url.get(mySqlBase));
		   assertEquals(some_user_login, field_user_login.get(mySqlBase));
		   assertEquals(some_user_password, field_user_password.get(mySqlBase));

	   }
	   	   
	   
	   // Testing createConnection() method
	   @Test
	   public void testCreateConnection() throws Exception {

		   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);
		   
		   Connection conn = mySqlBase.createConnection();
		   
		   assertFalse(conn.isClosed());
		   
	   }
	   

	   // Testing closeConnection() method
	   @Test
	   public void testCloseConnection() throws Exception {

		   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);
		   
		   Connection conn = mySqlBase.createConnection();
		   
		   mySqlBase.closeAll();
		   
		   assertTrue(conn.isClosed());

	   }	

	   // Testing createNewTable() and deleteTable() methods
	   @Test
	   public void testCreateAndDeleteNewTable() throws Exception {

		   String tableName = "temp";
		   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);
		   boolean db_exist = false;
		   boolean tvchId_exist = false;
		   boolean channelName_exist = false;
		   boolean groupTitle_exist = false;
		   boolean channelUri_exist = false;
		   boolean providerName_exist = false;
		   
		   Connection conn = mySqlBase.createConnection();
		   
		   mySqlBase.createNewTableIfNoExists(tableName);
		   
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
		   
		   mySqlBase.deleteTableIfExists(tableName);
		   
		   rs = md.getTables(null, null, tableName, null);
		   if (!rs.next()) {
		     //Table Exist
			   db_exist = false;
		   }
		   
		   assertFalse(db_exist);
		   

	   }
	   
	   // Testing addPlistToBase() method
	   @Test
	   public void testAddPlistToBase() throws Exception {

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
			   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);

			   Connection conn = mySqlBase.createConnection();
			   
			   // deleting the temporary table if it exists
			   mySqlBase.deleteTableIfExists(tableName);
			   // creating a new temporary table
			   mySqlBase.createNewTableIfNoExists(tableName);
			   // writing prepared random data to the table
			   mySqlBase.addPlistToBase (expected_entries, tableName);
			   // place to receive data from the table
			   List<Entry> received_entries = new ArrayList<Entry>();

			   Statement statement = null; // query statement
			   ResultSet resultSet = null; // manages results

			   // creating Statement for querying database 
			   statement = conn.createStatement();
			   // querying the database on request and write the data to List<Entry> received_entries
			   for (int i=0; i <2; ++i) {

				   String query = String.format("SELECT * FROM %s WHERE tvchId = %s",
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
			   mySqlBase.deleteTableIfExists(tableName); 
			   conn.close();
		   } 
		   catch (Exception e) { 
			   e.printStackTrace(); 
		   } 

	   }	

	   
	   // Testing updatePlistToBase() method
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
			   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);

			   Connection conn = mySqlBase.createConnection();
			   
			   // deleting the temporary table if it exists
			   mySqlBase.deleteTableIfExists(tableName);
			   // creating a new temporary table
			   mySqlBase.createNewTableIfNoExists(tableName);
			   // writing prepared random data to the table
			   mySqlBase.addPlistToBase (entries, tableName);
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
			   mySqlBase.updateBasePlist(expected_entries, tableName);
			   // place to receive data from the table
			   List<Entry> received_entries = new ArrayList<Entry>();

			   Statement statement = null; // query statement
			   ResultSet resultSet = null; // manages results

			   // creating Statement for querying database 
			   statement = conn.createStatement();
			   // querying the database on request and write the data to List<Entry> received_entries
			   for (int i=0; i <2; ++i) {

				   String query = String.format("SELECT * FROM %s WHERE tvchId = %s",
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
			   mySqlBase.deleteTableIfExists(tableName); 
			   conn.close();

		   } 
		   catch (Exception e) { 
			   e.printStackTrace(); 
		   } 

	   }	
	   
	   // Testing readDataFromBase (String query) method
	   @Test
	   public void testReadDataFromBase() throws Exception {

		   String tableName = "temp"; // temporary table name
		   List <Entry> expected_entries = new ArrayList <Entry> (); // place to store data
		   try { 

			   // Adding random data to the List <Entry> expected_entries
			   for (int i=0; i <2; ++i) { 
				   Entry.Builder  entry = new Entry.Builder()
						   .tvchId(RandomString.getRandomIntString(5))
						   .channelName(RandomString.getRandomString(45-1))
						   .groupTitle(RandomString.getRandomString(45-1))
						   .channelUri(RandomString.getRandomString(65-1))
						   .providerName(RandomString.getRandomString(45-1));

				   expected_entries.add(entry.build());
			   }  

			   // connecting to the database
			   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);

			   Connection conn = mySqlBase.createConnection();
			   
			   // deleting the temporary table if it exists
			   mySqlBase.deleteTableIfExists(tableName);
			   // creating a new temporary table
			   mySqlBase.createNewTableIfNoExists(tableName);
			   // writing prepared random data to the table
			   mySqlBase.addPlistToBase (expected_entries, tableName);
			   
			   // reading the entire table using readDataFromBase(query)
			   String query = String.format("SELECT * FROM %s", tableName);
			   List <Entry> received_entries = mySqlBase.readDataFromBase(query);
			   
			   // compare the results obtained from the table with the sent ones (they must be the same)
			   for (int i=0; i <2; ++i) { 
				   for (int j=0; j <2; ++j) {
					   if(expected_entries.get(i).getTvchId()==received_entries.get(j).getTvchId()) {
						   
						   assertEquals((expected_entries.get(i).getChannelName()), (received_entries.get(j).getChannelName()));
						   assertEquals((expected_entries.get(i).getGroupTitle()), (received_entries.get(j).getGroupTitle()));
						   assertEquals((expected_entries.get(i).getChannelUri()), (received_entries.get(j).getChannelUri()));
						   assertEquals((expected_entries.get(i).getProviderName()), (received_entries.get(j).getProviderName()));
						   
					   }
					   
				   }
				   
			   }

			   // deleting the temporary table
			   mySqlBase.deleteTableIfExists(tableName); 
			   conn.close();

		   } 
		   catch (Exception e) { 
			   e.printStackTrace(); 
		   } 

	   }	

}
