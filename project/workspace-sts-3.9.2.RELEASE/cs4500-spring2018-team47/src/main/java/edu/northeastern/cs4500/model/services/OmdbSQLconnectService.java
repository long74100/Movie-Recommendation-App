package edu.northeastern.cs4500.model.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to connect to the online OMDB, extract movie information from it and store in
 * the local database.
 * @author lgj81
 */
public class OmdbSQLconnectService {
	// this is the movie information fields for online movie database
	private final ArrayList<String> oldbfields = new ArrayList<>(Arrays.asList(
			"imdbID", "Title", "Rated", "Runtime", "Year", "Released", "Genre", "Director", "Actors", "Plot", 
			"Language", "Country", "Awards", "Poster", "Metascore", "imdbRating", "imdbVotes", "Production"));

	// this is the movie information fields for local movie database
	private final ArrayList<String> lcdbfields = new ArrayList<>(Arrays.asList(
			"movie_id", "movie_name", "movie_rated", "runtime", "movie_year", "released_date", "genre", "director", "actor", "plot", 
			"movie_language", "country", "awards", "poster", "metascore", "imdbRating", "imdbvotes", "production"));
	
	// container for matching <local movie field> -> <online movie information>
	private HashMap<String, String> fieldToValue;
	
	// The connector to local database
	private final LocalSQLConnectService connector = new LocalSQLConnectService();
	
	// TODO: need to distribute the actors, directors,etc, to different tables.	
	
	/**
	 * The constructor
	 */
	public OmdbSQLconnectService() {
		this.fieldToValue = new HashMap<>();
	}
	
	/**
	 * To catch the movie information from online Movie JSON Object.
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
	public boolean hasMovie(String movieId) {
		if(this.connector.containMovie(movieId)) {
			System.out.println("Movie already exists in local database");
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * To insert the data into the given table
	 * @param data the data that will be inserted
	 * @param tableName the destination table for the given data
	 */
	private void insertToLocalDatabase(String data, String tableName) {
		this.connector.insertData(data, tableName);
		System.out.println("add movie successfully");
	}
	
	/**
	 * To delete information from local database， this will only allow admin user to operate.
	 * @param data the data(object id) stored in local database
	 * @param tableName the destination table that the given data is stored
	 */
	private void deleteFromLocalDatabase(String data, String tableName) {
		// TODO: this might be in different class for Admin user.
	}
	
	/**
	 * To clear the table with given table name
	 * @param tableName the name of table that will be clear all data from
	 */
	public void clearTable(String tableName) {
		this.connector.clearTable(tableName);
	}
	
	/**
	 * To add multiple movies into the local database instead letting system load one by one
	 * Note: (For admin user only) Might be in different class.
	 * @param movieNames the names for list of movie that would be added to local database.
	 */
	public void addMultiMovies(ArrayList<String> movieNames) {
		OmdbServiceImpl obj = new OmdbServiceImpl();
		for(int i = 0; i < movieNames.size(); i++) {
			String item = movieNames.get(i);
			try {
				try {
					JSONObject current = obj.searchMovieByTitle(item);
					catchMovie(current);
					loadMovieToLocalDB();
				}
				catch(JSONException je) {
					je.printStackTrace();
				}
			}
			catch(IOException io) {
				io.printStackTrace();
			}
		}
	}
	
	
	
	
	
}