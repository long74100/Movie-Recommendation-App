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

	/**
	 * Searches a cast of a movie
	 * @param id a movie id within themoviedb
	 * @return a JSONObject of the movie cast
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject searchMovieCast(int id) throws IOException, JSONException;

	/**
	 * Do a detailed search of a movie
	 * @param id a movie id within themoviedb
	 * @return A JSONObject of the movie details
	 * @throws IOException
	 * @throws JSONException
	 */
	JSONObject searchMovieDetails(int id) throws IOException, JSONException;
}
