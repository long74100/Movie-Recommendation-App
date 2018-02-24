package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import edu.northeastern.cs4500.model.services.IOmdbService;
import edu.northeastern.cs4500.model.services.OmdbServiceImpl;
import edu.northeastern.cs4500.model.services.OmdbSQLconnectService;

public class LocalDBServiceTest {
	
	// the OMDB service 
	private static IOmdbService omdbService;
	// connection between OMDB and LOCALDB
	private static OmdbSQLconnectService sqlConnector;
	
	@Before
	public void init() {
	omdbService = new OmdbServiceImpl();
	sqlConnector = new OmdbSQLconnectService();
	}
	
	/**
	 * this is to test adding single movie to local database
	 */
	@Test
	public void testAddMovieToDatabaseOneByOne() {
		this.init();
		try {
			try {
				// check if the local database already has the given movie by searching movie id, 
				assertEquals(sqlConnector.hasMovie("tt0096895"), false);
				JSONObject job = omdbService.searchMovieByTitle("Batman");
				// to catch movie information(JSON File)
				sqlConnector.catchMovie(job);
				// import movie into local database
				sqlConnector.loadMovieToLocalDB();
				// test if the movie already exists in local database
				assertEquals(sqlConnector.hasMovie("tt0096895"), true);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Test to add list of movie into the database: basically operated by admin user.
	 */
	@Test
	public void testAddListOfMovieToDatabase() {
		init();
		// A sample list of movie names
		ArrayList<String> movieList = new ArrayList<>(Arrays.asList(
				"Avatar", "Pirates of the Caribbean: At World's End", "The Dark Knight Rises", "John Carter", 
				"Spider-Man 3", "Tangled", "Avengers: Age of Ultron", 
				"Batman v Superman: Dawn of Justice", "Superman Returns", "Quantum of Solace", 
				"Pirates of the Caribbean: Dead Man's Chest", "The Lone Ranger", "Man of Steel", "The Chronicles of Narnia: Prince Caspian", 
				"Pirates of the Caribbean: On Stranger Tides", "Men in Black 3", "The Hobbit: The Battle of the Five Armies", 
				"The Amazing Spider-Man", "Robin Hood", "The Hobbit: The Desolation of Smaug", "The Golden Compass", "King Kong", 
				"Titanic", "Captain America: Civil War", "Battleship", "Jurassic World", "Iron Man 3", "Skyfall", "Thor", "Hulk", 
				"Coach Carter", "2012", "Time", "8 Miles", "17 Again"));
		try {
			try {
				// to add the list of movie into local database.
				sqlConnector.addMultiMovies(movieList);
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
