package com.meMaxMaximmoff.executabe;
/*
 * Created by Max Maximoff on 02/01/2021.
 * 
 * Script for creating m3u playlists
 * based on the data received from the site http://proxytv.ru/
 * Demonstration of creating such playlists by reading data from a csv file
 * and MySQL databases
 * 
 * Full list of providers on proxytv.ru :
 * Optik Redkom Novotel Lada Zapsib Aist Tauer Elite RostNSK Bes Utelekom Post Lanta Perspektiv Corbina Citilink Yarnet Skaynet Tenet
*/


import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws Exception{

		final String M3UNAMEFILE1 = "./src/main/resources/plist_Novotel.m3u";//playlist file name
		final String M3UNAMEFILE2 = "./src/main/resources/plist_sport.m3u";//playlist file name
		final String CSVNAMEFILE = "./src/main/resources/Novotel.csv";//csv file name
		final String DRIVERNAME = "./src/main/resources/BrowserDrivers"
				+ "/Chrome/ChromeDriver 88.0.4324.96/chromedriver.exe";//path to ChromeDriver
		final char DELIMITER = ';'; // delimiter in the csv file
		final int TIMEOUT = 3*1000;// 3 seconds
		final int LENDATA = 8192; // the length of the buffer to check for the presence of a stream
		final int MAXATTEMPT = 3; //  number of attempts to check
		final String tableName = "plists";
		

        // a line with a list of all providers	
		final String allProviders = "Aist,Elite,Lada,Optik,Lanta,Novotel,"
				+ "Perspektiv,Post,Redkom,RostNSK,Skaynet,Citilink,Tenet,Tauer,Zapsib,Utelekom,Corbina,Yarnet";
		
		
		
		  try {
		  
			  // the list of beans where we will save data about channels
			   List<Entry> entries =  new ArrayList<Entry>();
			  
			  // constructor for the RobotIptv class
			   RobotIptv robotIptv1 = new RobotIptv();
			  
			  // parsing website http://proxytv.ru/ and write the channel data to the bean list
			  entries = robotIptv1.getProviderPlist(allProviders, DRIVERNAME);
			  
			  
			  // constructor of the MySqlBase class
			  MySqlBase mySqlBase = new MySqlBase();
			  
			  mySqlBase.createConnection();
			  
			  // deleting all entries in the database table
			  mySqlBase.deleteAllPlistsInBase(tableName);
			  
			  // adding data about channels to the database
			  mySqlBase.addPlistToBase(entries, tableName);
			  
			  // SQL query for the Novotel provider
			  String query1 = "SELECT * FROM plists WHERE providerName = 'Novotel' ";
			  
			  // getting data from an SQL query
			  entries = mySqlBase.readDataFromBase(query1);
			  
			  // constructor for the CsvFile class
			  CsvFile newCsvFile = new CsvFile(CSVNAMEFILE, DELIMITER);
			  
			  // writing data in csv
			   newCsvFile.writePlistToCSV(entries);
			   
			  // constructor for the RobotIptv class with the file name specified
			   RobotIptv robotIptv2 = new RobotIptv(M3UNAMEFILE1);	
			   
			  // reading data from csv
			   entries = newCsvFile.readDataFromCSV();
			   
			  // creating an m3u based on data from csv
			   robotIptv2.createM3uNoCheck(entries);

			  // constructor for the RobotIptv class, specifying the file name, timeout, and length of the test buffer
			  // and the maximum number of attempts
			   RobotIptv robotIptv3 = new RobotIptv(M3UNAMEFILE2, TIMEOUT, LENDATA, MAXATTEMPT);	
			   
			   // SQL query from the database for selecting channels СПОРТИВНЫЕ
			   String query2 = "SELECT * FROM plists WHERE (providerName = 'Novotel' OR providerName = 'Lanta') AND groupTitle = 'СПОРТИВНЫЕ'"; 
			   
			   // getting data from an SQL query
			   entries = mySqlBase.readDataFromBase(query2);
			   
			   // creating an m3u based on data from the database with checking and replacing the stream if necessary
			   robotIptv3.createM3uCheck(entries);
			   

		  
		  } catch(Exception e) {
			  	e.printStackTrace(); 
		  }
		 
	}

}
