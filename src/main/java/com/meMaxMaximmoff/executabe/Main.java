package com.meMaxMaximmoff.executabe;
/*
 * Created by Max Maximoff on 02/01/2021.
 * 
 * Script for creating m3u playlists
 * based on the data received from the site http://proxytv.ru/
 * Demonstration of creating such playlists by reading data from a csv file, MySQL PostgreSQL and  databases
 * 
 * 
 * Full list of providers on proxytv.ru :
 * Optik Redkom Novotel Lada Zapsib Aist Tauer Elite RostNSK Bes Utelekom Post Lanta Perspektiv Corbina Citilink Yarnet Skaynet Tenet
*/

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception{

		final String m3uFile1 = "./src/main/resources/plist_Novotel.m3u";//playlist file name
		final String m3uFile2 = "./src/main/resources/plist_sport.m3u";//playlist file name
		final String csvFile = "./src/main/resources/Novotel.csv";//csv file name
		final String chromeDriver = "./src/main/resources/BrowserDrivers"
				+ "/Chrome/ChromeDriver 88.0.4324.96/chromedriver.exe";//path to ChromeDriver
		final char csvDelimiter = ';'; // delimiter in the csv file
		final int timeout = 3*1000;// 3 seconds
		final int lenData = 8192; // the length of the buffer to check for the presence of a stream
		final int maxAttempt = 3; //  number of attempts to check
		
		final String mySqlTableName = "plists"; // Name of the table in the MySQL database
		final String pgSqlTableName = "plists"; // Name of the table in the PostgreSql database
		
		final String mySqlDbUrl = "jdbc:mysql://localhost/plistsDB";  // Default database address 
		final String mySqlDbLogin = "user_login"; 						// Default user login
		final String mySqlDbPass = "password"; 						// Default database password
		
		final String pgDbUrl = "jdbc:postgresql://192.168.1.41:5432/mydb";  // Default database address 
		final String pgDbLogin = "myuser"; 						// Default user login
		final String pgDbPass = "123"; 						// Default database password
		
		final String mySqlDriver = "com.mysql.cj.jdbc.Driver";  // Driver MySQL
		final String pgSqlDriver = "org.postgresql.Driver"; 	// Driver PostgreSql
		

        // a line with a list of all providers	
		final String allProviders = "Aist,Elite,Lada,Optik,Lanta,Novotel,"
				+ "Perspektiv,Post,Redkom,RostNSK,Skaynet,Citilink,Tenet,Tauer,Zapsib,Utelekom,Corbina,Yarnet";
		
		Connection mySqlBaseConn = null;
		Connection pgSqlBaseConn = null;
		
		
		  try {
		  
			  // the list of beans where we will save data about channels
			   List<Entry> entries =  new ArrayList<Entry>();
			  
			  // constructor for the RobotIptv class
			   RobotIptv robotIptv1 = new RobotIptv();
			  
			  // parsing website http://proxytv.ru/ and write the channel data to the bean list
			  entries = robotIptv1.getProviderPlist(allProviders, chromeDriver);
			  
			  
			  // constructor of the MySqlBase class
			  MySqlBase mySqlBase = new MySqlBase(mySqlDbUrl, mySqlDbLogin, mySqlDbPass);
			  
			  mySqlBaseConn = mySqlBase.createConnection(mySqlDriver);
			  
			  // deleting all entries in the database table
			  mySqlBase.deleteAllPlistsInBaseTable(mySqlBaseConn, mySqlTableName);
			  
			  // adding data about channels to the database
			  mySqlBase.addPlistToBaseTable(mySqlBaseConn, entries, mySqlTableName);
			  
			  // SQL query for the Novotel provider
			  String query1 = "SELECT * FROM plists WHERE providerName = 'Novotel' ";
			  
			  // getting data from an SQL query
			  entries = mySqlBase.readDataFromBaseTable(mySqlBaseConn, mySqlTableName, query1);
			  
			  // constructor for the CsvFile class
			  CsvFile newCsvFile = new CsvFile(csvFile, csvDelimiter);
			  
			  // writing data in csv
			   newCsvFile.writePlistToCSV(entries);
			   
			  // constructor for the RobotIptv class with the file name specified
			   RobotIptv robotIptv2 = new RobotIptv(m3uFile1);	
			   
			  // reading data from csv
			   entries = newCsvFile.readDataFromCSV();
			   
			  // creating an m3u based on data from csv
			   robotIptv2.createM3uNoCheck(entries);

			  // constructor for the RobotIptv class, specifying the file name, timeout, and length of the test buffer
			  // and the maximum number of attempts
			   RobotIptv robotIptv3 = new RobotIptv(m3uFile2, timeout, lenData, maxAttempt);	
			   
			   // SQL query from the database for selecting channels СПОРТИВНЫЕ
			   String query2 = "SELECT * FROM plists WHERE (providerName = 'Novotel' OR providerName = 'Lanta') AND groupTitle = 'СПОРТИВНЫЕ'"; 
			   
			   // getting data from an SQL query
			   entries = mySqlBase.readDataFromBaseTable(mySqlBaseConn, mySqlTableName, query2);
			   
			   // creating an m3u based on data from the database with checking and replacing the stream if necessary
			   robotIptv3.createM3uCheck(entries);
			   
			   // constructor of the PostgreSqlBase class
			   PostgreSqlBase pgSqlBase = new PostgreSqlBase(pgDbUrl, pgDbLogin, pgDbPass);
			   
			   // Connecting to the PostgreSQL database
			   pgSqlBaseConn = pgSqlBase.createConnection(pgSqlDriver);
			   
			   // Deleting the old table
			   pgSqlBase.deleteTableIfExists(pgSqlBaseConn, pgSqlTableName);
			   
			   // Creating a pgSqlTableName table in the PostgreSQL database			   
			   pgSqlBase.createNewTableIfNoExists(pgSqlBaseConn, pgSqlTableName);
			   
			   // Adding the data obtained from the query2 query to the pgSqlTableName table
			   pgSqlBase.addPlistToBaseTable(pgSqlBaseConn, entries, pgSqlTableName);
			   
			   // Closing all connections
			   pgSqlBase.closeConnection(pgSqlBaseConn);
			   mySqlBase.closeConnection(mySqlBaseConn);

		  
		  } catch(Exception e) {
			  	e.printStackTrace(); 
		  }
		 
	}

}
