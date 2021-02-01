package com.meMaxMaximmoff.executabe;

import static org.junit.Assert.*;


import org.junit.Test;

public class TestSqlQueries {

	final String database_url = "jdbc:mysql://localhost/plistsDB";  // Default atabase address
	final String user_login = "user_login";                         // Default atabase login
	final String user_password = "password";                        // Default atabase password
	final String tableName = "plistsdb.plists";
	final String nameFile1 = "./src/test/resources/ProxyBot.m3u";//имя файла плейлиста

	@Test
	public void testQuery1() throws Exception {
		
		   // connecting to the database
		   MySqlBase mySqlBase = new MySqlBase(database_url, user_login, user_password);

		   mySqlBase.createConnection();
		   
		   // reading the entire table using readDataFromBase(query)
		   String query = String.format("SELECT * FROM %s WHERE groupTitle = 'ЭТНИЧЕСКИЕ';", tableName);
		   
		   RobotIptv robotIptv = new RobotIptv(nameFile1);
		   
		   robotIptv.createM3uNoCheck(mySqlBase.readDataFromBase(query));
		   
		   mySqlBase.closeAll();
	}

}
