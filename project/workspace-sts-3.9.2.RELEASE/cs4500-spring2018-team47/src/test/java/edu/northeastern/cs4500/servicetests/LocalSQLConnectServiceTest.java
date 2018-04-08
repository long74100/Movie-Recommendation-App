package edu.northeastern.cs4500.servicetests;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;

public class LocalSQLConnectServiceTest {
	private static ILocalSQLConnectService localSQLConnectService;
	//mock movieID
	private static String movieID = "tt0096895";
	
	@Before
	public void init() {
		localSQLConnectService = new LocalSQLConnectServiceImpl();
	}
	
	@Test
	public void testCreateMovieList() {
		this.init();
		localSQLConnectService.createMovieList(32, "NewTestList+A");
	}
}
