package edu.northeastern.cs4500.model.services;

//Connects with the OMDB api
public interface IOmdbService {

	/**
	 * Searches a movie by title
	 * @param title A string input of the title
	 * @return A string json list of movies that match the title
	 * @exception exception if the connection failed to connect
	 */
	String searchMovieByTitle(String title) throws Exception;
}
