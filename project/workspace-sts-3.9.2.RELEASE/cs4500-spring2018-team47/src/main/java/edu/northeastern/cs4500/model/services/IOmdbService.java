package edu.northeastern.cs4500.model.services;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

//Connects with the OMDB api
public interface IOmdbService {

	/**
	 * Searches a movie by title
	 * @param title A string input of the title
	 * @return A jsonObject of the movie that matches the title
	 * @IOException thrown if there was an error to connect to the API
	 * @JSONException if creation of json object fails
	 */
	JSONObject searchMovieByTitle(String title, String searchParam) throws IOException, JSONException;
}
