package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;

@SpringBootTest
public class LocalSQLConnectServiceTest {
	private static ILocalSQLConnectService localSQLConnectService;
	//mock movieID
	private static String movieID = "tt0096895";
	
	@Before
	public void init() {
		localSQLConnectService = new LocalSQLConnectServiceImpl();
	}
	
	@Test
	public void testContainMovie() {
		localSQLConnectService.deleteFromMovieTable("1");
		//does not contain movie
		boolean actual = localSQLConnectService.containMovie("1");
		assertFalse(actual);
		//contains movie
		Map<String, String> movieObject = new HashMap<>();
	    movieObject.put("imdbID", "1");
	    movieObject.put("title", "test");
	    movieObject.put("genre", "test");
	    movieObject.put("plot", "test");
	    movieObject.put("actors", "test");
	    movieObject.put("director", "test");
	    movieObject.put("released", "test");
	    movieObject.put("runtime", "test");
	    movieObject.put("country", "test");
	    movieObject.put("imdbRating", "0");
	    movieObject.put("poster", "test");
	    movieObject.put("language", "test");
	    movieObject.put("movieDBid", "test");
	    
		localSQLConnectService.loadMovieIntoLocalDB(movieObject);
		actual = localSQLConnectService.containMovie("1");
		assertTrue(actual);
		
		//Test Sql Exception
		actual = localSQLConnectService.containMovie("-1");
		assertFalse(actual);
	}
	
	@Test
	public void testCreateMovieList() {
		this.init();
		localSQLConnectService.createMovieList(32, "NewTestList+A");
	}
}
