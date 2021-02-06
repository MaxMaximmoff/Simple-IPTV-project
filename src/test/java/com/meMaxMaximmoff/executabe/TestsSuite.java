package com.meMaxMaximmoff.executabe;
/*
 * Created by Max Maximoff on 02/05/2021.
 * 
 * Suite for running tests
 * 
*/
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestMySqlBase.class,
	TestPostgreSqlBase.class
})
public class TestsSuite {

}
