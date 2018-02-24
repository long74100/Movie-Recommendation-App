package edu.northeastern.cs4500.model.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

public class SQLConnection {
	private final ArrayList<String> oldbfields = new ArrayList<>(Arrays.asList(
			"imdbID", "Title", "Rated", "Runtime", "Year", "Released", "Genre", "Director", "Actors", "Plot", 
			"Language", "Country", "Awards", "Poster", "Metascore", "imdbRating", "imdbVotes", "Production"));

	private final ArrayList<String> lcdbfields = new ArrayList<>(Arrays.asList(
			"movie_id", "movie_name", "movie_rated", "runtime", "movie_year", "released_date", "genre", "director", "actor", "plot", 
			"movie_language", "country", "awards", "poster", "metascore", "imdbRating", "imdbvotes", "production"));
	
	private HashMap<String, String> fieldToValue;
	
	private final SQLConnector connector = new SQLConnector();
	
	
	
	/**
	 * The constructor
	 */
	public SQLConnection() {
		this.fieldToValue = new HashMap<>();
	}
	
	/**
	 * To catch the movie information and store into the local container.
	 * @param movieJsonString
	 */
	public void catchMovie(JSONObject movieJSON) {
		try {
			for(int i = 0; i < oldbfields.size(); i++) {
				String olField = oldbfields.get(i);
				String lcfields = lcdbfields.get(i);
				if(olField == null) {
					fieldToValue.put(lcfields, "N/A");
				}
				else {
					String value = (String)movieJSON.get(olField);
					fieldToValue.put(lcfields, value);
				}
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * To load the new movie into the local database
	 * 1. To check if there is existing id, which means the movie is already there. If there is one, don't import
	 * 2. If there is no current movie id, load into the database.
	 */
	public void loadMovieToLocalDB() {
		if(this.fieldToValue.isEmpty()) {
			System.out.println("There is nothing to load to database");
		}
		else {
			try {
				String currentMovieId = this.fieldToValue.get("movie_id");
				if(movieAlreadyExists(currentMovieId)) {
					System.out.println("Movie already exists");
				}
				else {
					// insert movie into local database
					String expression = "";
					for(int i = 0; i < lcdbfields.size(); i++) {
						if(i == lcdbfields.size() - 1) {
							expression += "\"" + fieldToValue.get(lcdbfields.get(i)) + "\"";
						}
						else {
							expression += "\"" + fieldToValue.get(lcdbfields.get(i)) + "\", ";
						}
					}
					String result = "(" + expression + ", ratings" + ", votes" + ")";
					this.insertToLocalDatabase(result, "Movie");
				}
			}
			catch (NullPointerException nl) {
				nl.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Check if the movie is already in the local database
	 * @param movieId the movieId of given movie that will be added
	 * @return true if local database already contains the given movie
	 */
	private boolean movieAlreadyExists(String movieId) {
		return this.connector.containMovie(movieId);
	}
	
	/**
	 * This is for test
	 * @param movieId the movieId of given movie that will be added
	 * @return true if local database already contains the given movie
	 */
	public boolean testPurpose(String movieId) {
		return this.connector.containMovie(movieId);
	}
	
	/**
	 * To insert the data into the given table
	 * @param data the data that will be inserted
	 * @param tableName the destination table for the given data
	 */
	private void insertToLocalDatabase(String data, String tableName) {
		this.connector.insertData(data, tableName);
	}
	
	
	
	
	
}
