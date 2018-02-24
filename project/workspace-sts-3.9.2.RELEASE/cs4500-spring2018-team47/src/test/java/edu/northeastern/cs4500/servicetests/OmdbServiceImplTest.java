package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import edu.northeastern.cs4500.model.services.IOmdbService;
import edu.northeastern.cs4500.model.services.OmdbServiceImpl;
import edu.northeastern.cs4500.model.services.SQLConnection;
import edu.northeastern.cs4500.model.services.SQLConnector;

public class OmdbServiceImplTest {
	
	private static IOmdbService omdbServiceImpl;
	
	@Before
	public void init() {
	omdbServiceImpl = new OmdbServiceImpl();
	}
	private static String expectedErrorSearchResultString = "{\"Response\":\"False\",\"Error\":\"Something went wrong.\"}";
	private static String expectedFailedSearchResultString = "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";
	private static String  expectedSuccessfulSearchResultString = "{\"Title\":\"Batman\","
			+ "\"Year\":\"1989\","
			+ "\"Rated\":\"PG-13\","
			+ "\"Released\":\"23 Jun 1989\","
			+ "\"Runtime\":\"126 min\","
			+ "\"Genre\":\"Action, Adventure\","
			+ "\"Director\":\"Tim Burton\","
			+ "\"Writer\":\"Bob Kane (Batman characters), Sam Hamm (story), Sam Hamm (screenplay), Warren Skaaren (screenplay)\","
			+ "\"Actors\":\"Michael Keaton, Jack Nicholson, Kim Basinger, Robert Wuhl\","
			+ "\"Plot\":\"The Dark Knight of Gotham City begins his war on crime with his first major enemy being the clownishly homicidal Joker.\","
			+ "\"Language\":\"English, French, Spanish\","
			+ "\"Country\":\"USA, UK\","
			+ "\"Awards\":\"Won 1 Oscar. Another 9 wins & 26 nominations.\","
			+ "\"Poster\":\"https://images-na.ssl-images-amazon.com/images/M/MV5BMTYwNjAyODIyMF5BMl5BanBnXkFtZTYwNDMwMDk2._V1_SX300.jpg\","
			+ "\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.6/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"72%\"},{\"Source\":\"Metacritic\",\"Value\":\"69/100\"}],\"Metascore\":\"69\","
			+ "\"imdbRating\":\"7.6\","
			+ "\"imdbVotes\":\"294,261\","
			+ "\"imdbID\":\"tt0096895\","
			+ "\"Type\":\"movie\","
			+ "\"DVD\":\"25 Mar 1997\","
			+ "\"BoxOffice\":\"N/A\","
			+ "\"Production\":\"Warner Bros. Pictures\","
			+ "\"Website\":\"N/A\","
			+ "\"Response\":\"True\"}";
	
	@Test
	public void testSearchMovieByTitle() {
		init();
		//title of existing movie
		JSONObject expectedSearchResult = null;
		JSONObject actualSearchResult = null;
		try {
			expectedSearchResult = new JSONObject(expectedSuccessfulSearchResultString);
			actualSearchResult = omdbServiceImpl.searchMovieByTitle("batman");
			JSONAssert.assertEquals(expectedSearchResult, actualSearchResult, true);

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//title of non existing movie
		try {
			expectedSearchResult = new JSONObject(expectedFailedSearchResultString);
			actualSearchResult = omdbServiceImpl.searchMovieByTitle("a@@$!@.asd");
			JSONAssert.assertEquals(expectedSearchResult, actualSearchResult, true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//no title in search
		try {
			expectedSearchResult = new JSONObject(expectedErrorSearchResultString);
			actualSearchResult = omdbServiceImpl.searchMovieByTitle("");
			JSONAssert.assertEquals(expectedSearchResult, actualSearchResult, true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testAddMovieToDatabase() {
		try {
			try {
				IOmdbService ob = new OmdbServiceImpl();
				SQLConnection sqlConnector = new SQLConnection();
				assertEquals(sqlConnector.testPurpose("tt0096895"), false);
				JSONObject job = ob.searchMovieByTitle("Batman");
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
