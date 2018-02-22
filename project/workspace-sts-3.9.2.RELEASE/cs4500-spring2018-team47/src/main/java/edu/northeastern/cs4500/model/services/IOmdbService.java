package edu.northeastern.cs4500.model.services;

import org.json.JSONObject;

//Connects with the OMDB api
public interface IOmdbService {

	/**
	 * Searches a movie by title
	 * @param title A string input of the title
	 * @return A jsonObject of the movie that matches the title
	 * @exception exception if the connection failed to connect
	 */
	JSONObject searchMovieByTitle(String title) throws Exception;
}
