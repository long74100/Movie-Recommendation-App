package edu.northeastern.cs4500.servicetests;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.cs4500.model.services.LocalSQLConnectService;

public class LocalSQLConnectServiceTest {
	private static LocalSQLConnectService localSQLConnectService;
	//mock movieID
	private static String movieID = "tt0096895";
	
	@Before
	public void init() {
		localSQLConnectService = new LocalSQLConnectService();
	}
	
	@Test
	public void testContainMovie() {
		
	}
}
