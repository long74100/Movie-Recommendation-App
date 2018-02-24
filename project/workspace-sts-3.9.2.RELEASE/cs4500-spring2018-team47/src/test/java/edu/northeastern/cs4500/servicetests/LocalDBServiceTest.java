package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import edu.northeastern.cs4500.model.services.IOmdbService;
import edu.northeastern.cs4500.model.services.OmdbServiceImpl;
import edu.northeastern.cs4500.model.services.OmdbSQLconnectService;

public class LocalDBServiceTest {
	
	private static IOmdbService omdbService;
	
	@Before
	public void init() {
	omdbService = new OmdbServiceImpl();
	}
	
	@Test
	public void testAddMovieToDatabase() {
		init();
		try {
			try {
				OmdbSQLconnectService sqlConnector = new OmdbSQLconnectService();
				assertEquals(sqlConnector.testPurpose("tt0096895"), false);
				JSONObject job = omdbService.searchMovieByTitle("Batman");
				sqlConnector.catchMovie(job);
				sqlConnector.loadMovieToLocalDB();
				assertEquals(sqlConnector.testPurpose("tt0096895"), true);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		catch (Exception io) {
			io.printStackTrace();
		}
	}
	
	
	

	
	
}
