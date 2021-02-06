package com.meMaxMaximmoff.executabe;

/*
 * Created by Max Maximoff on 02/06/2021.
 * 
 * Suite for running main tests
 * 
*/
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.experimental.categories.Categories.IncludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@IncludeCategory(MainTestsCategory.class)
@ExcludeCategory(ExcludedTestsCategory.class)
@Suite.SuiteClasses({
	TestMySqlBase.class,
	TestPostgreSqlBase.class
})
public class MainTestsSuite {

}
