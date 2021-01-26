package com.meMaxMaximmoff.executabe;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class SqlBaseTests {
	
	   
	   static final String database_url = "jdbc:mysql://localhost/plistsDB";  // Default database address
	   static final String user_login = "user_login";                         // Default database login
	   static final String user_password = "password";                        // Default database password
	   
	   static final String tableName = "temp"; 
	   
	   private  Connection connection = null;
	   
	   SqlBase sqlBase = new SqlBase(database_url, user_login, user_password);
	
	@Before
	public void setUp() throws ClassNotFoundException, SQLException {
		connection = sqlBase.createConnection();
	}
	
	@After
	public void setOut() throws ClassNotFoundException, SQLException {
		sqlBase.closeConnection(connection);
	}
	
	@Test
    public void test1() throws ClassNotFoundException, SQLException {
    	
    	sqlBase.createNewTable(tableName);
    }

	@Ignore
	@Test
    public void test2() throws ClassNotFoundException, SQLException {

    	sqlBase.deleteTable(tableName);

    }
    
}
