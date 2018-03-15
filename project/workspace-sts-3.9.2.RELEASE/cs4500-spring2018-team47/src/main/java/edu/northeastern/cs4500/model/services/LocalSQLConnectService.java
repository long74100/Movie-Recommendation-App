package edu.northeastern.cs4500.model.services;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * This class is used to connect to the local database. This tool builds a connection between front end 
 * operation and back end database. Also, it will get the online movie information to the local database. 
 * When user search a movie online which already exists in local database, local database will provide information 
 * without doing any API Call. Otherwise, local database will store the movie which it does not contain but user search online.
 * 
 * @author lgj81
 */
public class LocalSQLConnectService {
	// the local database URL
	private static String url = "jdbc:mysql://team-47-dev-db.cllrg7hgpqkh.us-east-2.rds.amazonaws.com/"
			+ "cs4500_spring2018_team47_dev";
	// database username
	private static String username = "RuairiMSmillie";
	// database password
	private static String password = "TbthaGCmiimWrtayxr4MBEcD3tVB3sY";
	// this will be used to contain the Query command
	private static String command = "";
	// this is the operation status on database
	private enum Status {get, insert, delete, update};
	private static Status status = null;
	private static Connection connector = null;
	private static Statement connectStatement = null;
	private static ResultSet myResult = null;
	private ArrayList<String> movie = new ArrayList<>();
	/**
	 * The constructor
	 * The constructor will automatically create connection to local database
	 */
	public LocalSQLConnectService() {
		try {
			connector = DriverManager.getConnection(url, username, password);
			connectStatement = connector.createStatement();
		}
		catch(SQLException se) {
			se.printStackTrace();
		}
		
	}
	
    
    /**
     * To check if the given movie Id already exists in the database
     * @param movieId the movieId of checked movie
     * @return true if given movie exists in local database, else return false
     */
    public boolean containMovie(String movieId) {
    	try {
    		String sqlcmd = "select * from Movie where movie_id = '" + movieId + "'";
    		myResult = connectStatement.executeQuery(sqlcmd);
    		if(myResult.next()) {
    			return true;
    		}
    	}
    	catch (SQLException ep) {
    		ep.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * To check if two users have made the friend request to each other
     * @param senderId id for the user who might send the friend request
     * @param receiverId id for the user who might receive the friend request
     * @return true if they sent request to each other.
     */
    private boolean hasMadeRequest(int senderId, int receiverId) {
    	try {
    		String query = "select * from userRelation where (senderId = " + senderId + 
    				" and receiverId = " + receiverId + ") or (senderId = " 
    				+ receiverId + " and receiverId = " + senderId + ")";
    		myResult = connectStatement.executeQuery(query);
    		if(myResult.next()) {
    			return true;
    		}
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    	return false;
    }
    
    /**
     * To insert the data into the given table
     * @param data the data will be inserted
     * @param tableName the destination table that the data will be inserted to
     */
    public void insertData(String data, String tableName) {
    	try {
    		String query = "insert into " + tableName + " values" + data;
    		connectStatement.executeUpdate(query);
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    /**
     * To delete the given movie from the local database
     * @param id the given movie id
     */
    public void deleteFromMovieTable(String id) {
    	try {
    		String query = "delete from Movie where movie_id = \"" + id + "\"";
    		int row = connectStatement.executeUpdate(query);
    		System.out.println("delete row " + row + " id: " + id);
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    /**
     * Note: this is more for testing only, use it carefully.
     * To clear the table with given tableName
     * @param tableName the given table that will be cleaned up
     */
    public void clearTable(String tableName) {
    	try {
    		String query = "delete from " + tableName;
    		connectStatement.executeUpdate(query);
    		System.out.println("Clear Table for test insert");
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    /**
     * To search movie based on given keyword -- first start with the movie title
     * @param input the given searched string used for movie title
     */
    public void searchKeyWordMovieTitle(String input) {
    	try {
    		String query = "select from Movie where movie_name like \"%" + input + "%\"";
    		myResult = connectStatement.executeQuery(query);
    		execute();
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    /**
     * To search movies which the actor with given name act on
     * @param actorsName the name of actors
     */
    public void searchKeyWordActorsName(String actorsName) {
    	try {
    		String query = "SELECT * from Movie where actor like \"%" + actorsName + "%\"";
    		myResult = connectStatement.executeQuery(query);
    		execute();
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    /**
     * To search movies which the director with given name direct
     * @param directorName the name of director
     */
    public void searchKeyWordDirectorName(String directorName) {
    	try {
    		String query = "select * from Movie where director like \"%" + directorName + "%\"";
    		myResult = connectStatement.executeQuery(query);
    		execute();
    	}
    	catch(SQLException ec) {
    		ec.printStackTrace();
    	}
    }
    
    /**
     * To search movies with given genre
     * @param genre the movie genre
     */
    public void searchKeyWordGenre(String genre) {
    	try {
    		String query = "select * from Movie where genre like \"%" + genre + "%\"";
    		myResult = connectStatement.executeQuery(query);
    		execute();
    	}
    	catch(SQLException ec) {
    		ec.printStackTrace();
    	}
    }
    
    /**
     * To search movies with given year of publication
     * @param year the publication year period
     */
    public void searchKeyWordTime(String year) {
    	try {
    		String query = "select * from Movie where runtime like \"%" + year + "%\"";
    		myResult = connectStatement.executeQuery(query);
    		execute();
    	}
    	catch(SQLException ec) {
    		ec.printStackTrace();
    	}
    }
    
    /**
     * Search movie by keyword
     * @param keyword only 
     */
    public void searchByKeyWordInOne(String keyword) {
    	try {
    		String query = 
    				"select * from Movie where movie_id like \"%" + keyword + "%\""
    						+ " or movie_name like \"%" + keyword + "%\"" 
    						+ " or movie_rated like \"%" + keyword + "%\""
    						+ " or runtime like \"%" + keyword + "%\"" 
    						+ " or movie_year like \"%" + keyword + "%\""
    						+ " or release_date like \"%" + keyword + "%\""
    						+ " or genre like \"%" + keyword + "%\""
    						+ " or director like \"%" + keyword + "%\""
    						+ " or actor like \"%" + keyword + "%\""
    						+ " or plot like \"%" + keyword + "%\""
    						+ " or movie_language like \"%" + keyword + "%\""
    						+ " or country like \"%" + keyword + "%\""
    						+ " or metascore like \"%" + keyword + "%\""
    						+ " or imdbRating like \"%" + keyword + "%\""
    						+ " or ratings like \"%" + keyword + "%\""
    						+ " or production like \"%" + keyword + "%\"";
    		myResult = connectStatement.executeQuery(query);
    		execute();
    	}
    	catch(SQLException ec) {
    		ec.printStackTrace();
    	}
    }
    
    /**
     * To execute the get movie from local database command
     */
    private void execute() {
    	try {
    		while(myResult.next()) {
    			String movieId = myResult.getString("movie_id");
    			String movieName = myResult.getString("movie_name");
    			String movieRated = myResult.getString("movie_rated");
    			String runtime = myResult.getString("runtime");
    			String genre = myResult.getString("genre");
    			String released_date = myResult.getString("released_date");
    			String director = myResult.getString("director");
    			String actors = myResult.getString("actor");
    			String plot = myResult.getString("plot");
    			String language = myResult.getString("movie_language");
    			String country = myResult.getString("country");
    			String poster = myResult.getString("poster");
    			String imdbRating = myResult.getString("imdbRating");
    			String ratings = myResult.getString("ratings");
    			StringBuilder output = new StringBuilder();
    			output.append(movieId + "| " + movieName + "| " + movieRated + "| " 
    					+ runtime + "| " + genre + "| " + released_date + "| " + director + "| " + 
    					actors + "| " + plot + "| " + language + "| " + country + "| " + poster + "| "
    					+ imdbRating + "| " + ratings);
    			movie.add(output.toString());
    			//System.out.println(movieName + " --> " + actors); 
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    /**
     * To return the search result from local database
     * @return list of movies relevant to the search keyword.
     */
    public ArrayList<String> getSearchMovieResult() {
    	return this.movie;
    }
    
    /**
     * To send friend request to receiver if there is non shown in relation list.
     * @param senderId the id for sender
     * @param receiverId the id for receiver
     */
    public void sendFriendRequest(int senderId, int receiverId) {
    	if(!this.hasMadeRequest(senderId, receiverId)) {
    		try {
    			String query = "insert into userRelation values (" + senderId + ", " + receiverId + ", " 
    						+ "\"onHold\", " + 0 + ", " + 0 + ")";
    			connectStatement.executeUpdate(query);
    		}
    		catch(SQLException se) {
    			se.printStackTrace();
    		}
    	}
    	else {
    		System.out.println("friend request has made.");
    	}
    }
    
    /**
     * To accept the friend request from sender to receiver
     * @param senderId sender who sent out the friend request
     * @param receiverId receiver who received the friend request
     */
    public void acceptRequest(int senderId, int receiverId) {
    	try {
    		String query = "update userRelation set relationStatus = \"" + "friend\"" + " where senderId = " + senderId + 
    				" and receiverId = " + receiverId;
    		connectStatement.executeUpdate(query);
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    /**
     * The receiver to reject the sender's friend request
     * @param senderId the user to send friend request
     * @param receiverId the user to receive friend request
     */
    public void rejectRequest(int senderId, int receiverId) {
    	try {
    		String query = "delete from userRelation where senderId = " + senderId + " and receiverId = " + receiverId;
    		connectStatement.executeUpdate(query);
    	}
    	catch(SQLException ep) {
    		ep.printStackTrace();
    	}
    }
    
    
}
