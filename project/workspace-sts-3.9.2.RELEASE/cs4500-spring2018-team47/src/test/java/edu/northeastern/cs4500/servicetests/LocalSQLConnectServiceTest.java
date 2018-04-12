package edu.northeastern.cs4500.servicetests;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import edu.northeastern.cs4500.controllers.UserprofileController;
import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;

public class LocalSQLConnectServiceTest {
	private static ILocalSQLConnectService localSQLConnectService;
	//mock movieID
	private static String movieID = "tt0096895";
	private static final Logger logger = LogManager.getLogger(LocalSQLConnectServiceTest.class);
	
	@Before
	public void init() {
		localSQLConnectService = new LocalSQLConnectServiceImpl();
	}
	
	@Test
	public void testCreateMovieList() {
		this.init();
		try {
			localSQLConnectService.createMovieList(32, "NewTestList+A");
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
}
