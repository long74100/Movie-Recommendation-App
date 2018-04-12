package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import edu.northeastern.cs4500.Cs4500Spring2018Team47ApplicationTests;
import edu.northeastern.cs4500.model.services.IMovieDBService;
import edu.northeastern.cs4500.model.services.MovieDBServiceImpl;

public class MovieDBServiceImplTest {
	
	private static final Logger logger = LogManager.getLogger(MovieDBServiceImplTest.class);
	private static IMovieDBService movieDbService;
	
	
	@Before
	public void init() {
	movieDbService = new MovieDBServiceImpl();
	}
	
	
	@Test
	public void testSearchMovieListByTitle() throws JSONException, IOException {
		JSONObject batmanJson = movieDbService.searchMovieListByTitle("Batman");

		int actualPage = batmanJson.getInt("page");
		int actualTotalResults = batmanJson.getInt("total_results");
		int actualTotalPages = batmanJson.getInt("total_pages");
		int actualResultsSize = batmanJson.getJSONArray("results").length();
		
		int expectedPage = 1;
		int expectedTotalResults = 108;
		int expectedTotalpages = 6;
		int expectedResultsSize = 20;
		
		assertEquals(expectedPage, actualPage);
		assertEquals(expectedTotalResults, actualTotalResults);
		assertEquals(expectedTotalpages, actualTotalPages);
		assertEquals(expectedResultsSize, actualResultsSize);

		JSONObject nonsenseJSon = movieDbService.searchMovieListByTitle("sadfdsaf");
		
		actualPage = nonsenseJSon.getInt("page");
		actualTotalResults = nonsenseJSon.getInt("total_results");
		actualTotalPages = nonsenseJSon.getInt("total_pages");
		actualResultsSize = nonsenseJSon.getJSONArray("results").length();
		expectedPage = 1;
		expectedTotalResults = 0;
		expectedTotalpages = 1;
		expectedResultsSize = 0;
		
		assertEquals(expectedPage, actualPage);
		assertEquals(expectedTotalResults, actualTotalResults);
		assertEquals(expectedTotalpages, actualTotalPages);
		assertEquals(expectedResultsSize, actualResultsSize);
		
	}
	
	@Test
	public void testSearchMovieCast() throws JSONException, IOException {
		JSONObject batmanJson = movieDbService.searchMovieCast(268);
		int actualId = batmanJson.getInt("id");
		int actualResultLength = batmanJson.getJSONArray("cast").length();
		int expectedId = 268;
		int expectedResultLength = 20;
		assertEquals(expectedId, actualId);
		assertEquals(expectedResultLength, actualResultLength);
	}
	
	@Test
	public void testSearchMovieDetails() throws JSONException, IOException {
		JSONObject batmanJson = movieDbService.searchMovieDetails(268);
		String actualTitle = batmanJson.getString("original_title");
		String actualLanguage = batmanJson.getString("original_language");
		int actualId = batmanJson.getInt("id");
		String actualImdbId = batmanJson.getString("imdb_id");
		
		String expectedTitle = "Batman";
		String expectedLanguage = "en";
		int expectedId = 268;
		String expectedImdbId = "tt0096895";
		
		assertEquals(expectedTitle, actualTitle);
		assertEquals(expectedLanguage, actualLanguage);
		assertEquals(expectedId, actualId);
		assertEquals(expectedImdbId, actualImdbId);
	}
}
