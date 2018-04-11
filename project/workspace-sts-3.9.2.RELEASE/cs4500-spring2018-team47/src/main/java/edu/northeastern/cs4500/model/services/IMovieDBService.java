package edu.northeastern.cs4500.model.services;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

//Connects with the OMDB api
public interface IMovieDBService {

	/**
	 * Searches a movie by title
	 * @param title A string input of the title
	 * @return A jsonObject of the movie that matches the title
	 * @IOException thrown if there was an error to connect to the API
	 * @JSONException if creation of json object fails
	 */
	JSONObject searchMovieListByTitle(String title) throws IOException, JSONException;

	JSONObject searchMovieCast(int id) throws IOException, JSONException;

	JSONObject searchMovieDetails(int id) throws IOException, JSONException;
	
	/**
	 * Gathers movies that are in theaters
	 * @return A jsonObject of the movies that are in theaters
	 * @IOExceptionthrown thrown if there was an error to connect to the API
	 * @JSONException thrown if creation of json object fails
	 */
	JSONObject discoverInTheaterMovies() throws IOException, JSONException;
	
	/**
	 * Gathers movies that are most popular
	 * @return A jsonObject of the movies that are most popular
	 * @IOExceptionthrown thrown if there was an error to connect to the API
	 * @JSONException thrown if creation of json object fails
	 */
	JSONObject discoverPopularMovies() throws IOException, JSONException;
	
	/**
	 * Gathers movies that are coming soon
	 * @return A jsonObject of the movies that are coming soon
	 * @IOExceptionthrown thrown if there was an error to connect to the API
	 * @JSONException thrown if creation of json object fails
	 */
	JSONObject discoverMoviesComingSoon() throws IOException, JSONException;
}
