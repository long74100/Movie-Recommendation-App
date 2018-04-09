package edu.northeastern.cs4500.servicetests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import edu.northeastern.cs4500.Cs4500Spring2018Team47ApplicationTests;
import edu.northeastern.cs4500.model.services.IMovieDBService;
import edu.northeastern.cs4500.model.services.MovieDBServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MovieDBServiceImplTest extends Cs4500Spring2018Team47ApplicationTests {
	
	private static final Logger logger = LogManager.getLogger(MovieDBServiceImplTest.class);
	private static IMovieDBService movieDbService;
	
	@Before
	public void init() {
	movieDbService = new MovieDBServiceImpl();
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
			+ "\"Awards\":\"Won 1 Oscar. Another 8 wins & 26 nominations.\","
			+ "\"Poster\":\"https://ia.media-imdb.com/images/M/MV5BMTYwNjAyODIyMF5BMl5BanBnXkFtZTYwNDMwMDk2._V1_SX300.jpg\","
			+ "\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.6/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"72%\"},{\"Source\":\"Metacritic\",\"Value\":\"69/100\"}],\"Metascore\":\"69\","
			+ "\"imdbRating\":\"7.6\","
			+ "\"imdbVotes\":\"296,001\","
			+ "\"imdbID\":\"tt0096895\","
			+ "\"Type\":\"movie\","
			+ "\"DVD\":\"25 Mar 1997\","
			+ "\"BoxOffice\":\"N/A\","
			+ "\"Production\":\"Warner Bros. Pictures\","
			+ "\"Website\":\"N/A\","
			+ "\"Response\":\"True\"}";
	
	@Test
	public void testSearchMovie() {
		init();
		try {
			movieDbService.searchMovieDetails(383498);
		}
		catch(Exception s) {
			s.printStackTrace();
		}
	}
//	public void testSearchMovieByTitle() {
//		init();
//		//title of existing movie
//		JSONObject expectedSearchResult = null;
//		JSONObject actualSearchResult = null;
//		try {
//			expectedSearchResult = new JSONObject(expectedSuccessfulSearchResultString);
//			actualSearchResult = movieDbService.searchMovieListByTitle("batman");
//			JSONAssert.assertEquals(expectedSearchResult, actualSearchResult, true);
//
//		} catch (JSONException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
//		}
//	
//		//title of non existing movie
//		try {
//			expectedSearchResult = new JSONObject(expectedFailedSearchResultString);
//			actualSearchResult = movieDbService.searchMovieListByTitle("a@@$!@.asd");
//			JSONAssert.assertEquals(expectedSearchResult, actualSearchResult, true);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
//		}
//		
//		//no title in search
//		try {
//			expectedSearchResult = new JSONObject(expectedErrorSearchResultString);
//			actualSearchResult = movieDbService.searchMovieListByTitle("");
//			JSONAssert.assertEquals(expectedSearchResult, actualSearchResult, true);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			logger.error(e.getMessage());
//		}
//	}
	
}
