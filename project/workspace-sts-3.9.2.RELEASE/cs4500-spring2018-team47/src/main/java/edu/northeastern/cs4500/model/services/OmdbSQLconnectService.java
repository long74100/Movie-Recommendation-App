package edu.northeastern.cs4500.model.services;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to connect to the online OMDB, extract movie information
 * from it and store in the local database.
 * 
 * @author lgj81
 */
public class OmdbSQLconnectService {

    // this is the movie information fields for online movie database
    private final ArrayList<String> oldbfields = new ArrayList<>(Arrays.asList("imdbID", "Title", "Rated", "Runtime",
	    "Year", "Released", "Genre", "Director", "Actors", "Plot", "Language", "Country", "Awards", "Poster",
	    "Metascore", "imdbRating", "imdbVotes"));

    // this is the movie information fields for local movie database
    private final ArrayList<String> lcdbfields = new ArrayList<>(Arrays.asList("movie_id", "movie_name", "movie_rated",
	    "runtime", "movie_year", "released_date", "genre", "director", "actor", "plot", "movie_language", "country",
	    "awards", "poster", "metascore", "imdbRating", "imdbvotes"));

    // container for matching <local movie field> -> <online movie information>
    private HashMap<String, String> fieldToValue;

    // The connector to local database
    private final LocalSQLConnectService connector = new LocalSQLConnectService();
    private static final Logger logger = LogManager.getLogger(OmdbSQLconnectService.class);

    /**
     * The constructor
     */
    public OmdbSQLconnectService() {
	this.fieldToValue = new HashMap<>();
    }

    /**
     * To catch the movie information from online Movie JSON Object.
     * 
     * @param movieJsonString
     */
    private void catchMovie(JSONObject movieJSON) {
	try {
	    for (int i = 0; i < oldbfields.size(); i++) {
		String olField = oldbfields.get(i);
		String lcfields = lcdbfields.get(i);
		if (olField == null) {
		    fieldToValue.put(lcfields, "N/A");
		} else {
		    String value = (String) movieJSON.get(olField);
		    fieldToValue.put(lcfields, value);
		}
	    }
	} catch (JSONException e) {
	    logger.error(e.getMessage());
	}
    }

    /**
     * To load the new movie into the local database 1. To check if there is
     * existing id, which means the movie is already there. If there is one, don't
     * import 2. If there is no current movie id, load into the database.
     */
    public void loadMovieToLocalDB(JSONObject movieJSON) {
	catchMovie(movieJSON);
	if (!this.fieldToValue.isEmpty()) {
	    try {
		String currentMovieId = this.fieldToValue.get("movie_id");
		if (!movieAlreadyExists(currentMovieId)) {
		    String expression = "";
		    for (int i = 0; i < lcdbfields.size(); i++) {
			if (i == lcdbfields.size() - 1) {
			    expression += "\"" + fieldToValue.get(lcdbfields.get(i)) + "\"";
			} else {
			    expression += "\"" + fieldToValue.get(lcdbfields.get(i)) + "\", ";
			}
		    }
		    String result = "(" + expression + ", ratings" + ", votes" + ")";
		    this.insertToLocalDatabase(result, "Movie");
		}
	    } catch (NullPointerException nl) {
		logger.error(nl.getMessage());
	    }

	}
    }

    /**
     * Check if the movie is already in the local database
     * 
     * @param movieId
     *            the movieId of given movie that will be added
     * @return true if local database already contains the given movie
     */
    private boolean movieAlreadyExists(String movieId) {
	return this.connector.containMovie(movieId);
    }

    /**
     * This is for test
     * 
     * @param movieId
     *            the movieId of given movie that will be added
     * @return true if local database already contains the given movie
     */
    public boolean hasMovie(String movieId) {
	return this.connector.containMovie(movieId);
    }

    /**
     * To insert the data into the given table
     * 
     * @param data
     *            the data that will be inserted
     * @param tableName
     *            the destination table for the given data
     */
    private void insertToLocalDatabase(String data, String tableName) {
	this.connector.insertData(data, tableName);
    }

    /**
     * To delete information from local database， this will only allow admin user to
     * operate.
     * 
     * @param data
     *            the data(object id) stored in local database
     * @param tableName
     *            the destination table that the given data is stored
     */
    private void deleteFromLocalDatabase(String data, String tableName) {
	// TODO: this might be in different class for Admin user.
    }

    /**
     * To add multiple movies into the local database instead letting system load
     * one by one Note: (For admin user only) Might be in different class.
     * 
     * @param movieNames
     *            the names for list of movie that would be added to local database.
     */
    public void addMultiMovies(ArrayList<String> movieNames) {
	OmdbServiceImpl obj = new OmdbServiceImpl();
	for (int i = 0; i < movieNames.size(); i++) {
	    String item = movieNames.get(i);
	    try {
		try {
		    JSONObject current = obj.searchMovieByTitle(item, "t");
		    loadMovieToLocalDB(current);
		} catch (JSONException je) {
		    logger.error(je.getMessage());
		}
	    } catch (IOException io) {
		logger.error(io.getMessage());
	    }
	}
    }

    /**
     * To search movies which the actor with given name act on
     * 
     * @param actorName
     *            the name of actors
     * @return the list of movie information
     */
    public ArrayList<String> searchMovieByActors(String actorName) {
	this.connector.searchKeyWordActorsName(actorName);
	return this.connector.getSearchMovieResult();
    }

    /**
     * To search movie based on genre
     * 
     * @param genre
     *            the movie genre
     * @return the list of movie information
     */
    public ArrayList<String> searchMovieByGenre(String genre) {
	this.connector.searchKeyWordGenre(genre);
	return this.connector.getSearchMovieResult();
    }

    /**
     * To search the movies that are held by the director with given name
     * 
     * @param director
     *            the name of director
     * @return the list of movie information
     */
    public ArrayList<String> searchMovieByDirector(String director) {
	this.connector.searchKeyWordDirectorName(director);
	return this.connector.getSearchMovieResult();
    }

    /**
     * To search movie based on given keyword
     * 
     * @param mvTitle
     *            the given searched string used for movie title
     * @return list of movie information
     */
    public ArrayList<String> searchMovieByTitleKeyWord(String mvTitle) {
	this.connector.searchKeyWordMovieTitle(mvTitle);
	return this.connector.getSearchMovieResult();
    }

    /**
     * To search the movie by release time(date)
     * 
     * @param time
     *            the year that movie was released
     * @return list of movie information
     */
    public ArrayList<String> searchMovieByTime(String time) {
	this.connector.searchKeyWordTime(time);
	return this.connector.getSearchMovieResult();
    }

    /**
     * To search movie based on a keyword
     * 
     * @param keyword
     *            the keyword that is used to search for movie
     * @return all movies that contain the keyword as a text information.
     */
    public ArrayList<String> searchMovieByAll(String keyword) {
	this.connector.searchByKeyWordInOne(keyword);
	return this.connector.getSearchMovieResult();
    }
}
