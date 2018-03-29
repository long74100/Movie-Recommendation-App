package edu.northeastern.cs4500.model.services;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.northeastern.cs4500.model.movie.MovieReview;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.user.User;

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
	private static final Logger logger = LogManager.getLogger(LocalSQLConnectService.class);
	
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
			logger.error(se.getMessage());
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
		logger.error(ep.getMessage());
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
		logger.error(ep.getMessage());
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
		logger.error(ep.getMessage());
    	}
    }
    
    // -----------------MOVIE OPERATOR---------------------------------
    
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
		logger.error(ep.getMessage());
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
		logger.error(ep.getMessage());
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
		logger.error(ep.getMessage());
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
		logger.error(ep.getMessage());
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
		logger.error(ec.getMessage());
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
		logger.error(ec.getMessage());
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
		logger.error(ec.getMessage());
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
		logger.error(ec.getMessage());
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
    		}
    	}
    	catch(Exception e) {
		logger.error(e.getMessage());
    	}
    }
    
    
    /**
     * To return the search result from local database
     * @return list of movies relevant to the search keyword.
     */
    public ArrayList<String> getSearchMovieResult() {
    	return this.movie;
    }
    
    
    // ----- user interaction in local database-----
    
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
			logger.error(se.getMessage());
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
		logger.error(ep.getMessage());
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
		logger.error(ep.getMessage());
    	}
    }
    
    /**
     * receiver to block the sender 
     * @param senderId user who sent the friend request
     * @param receiverId user who receives the friend request
     */
    public void blockSender(int senderId, int receiverId) {
    	try {
    		String value = "update userRelation set isSenderBlocked = " + 1 + " where senderId = " + senderId + " and "
    				+ "receiverId = " + receiverId;
    		connectStatement.executeUpdate(value);
    	}
    	catch(SQLException ep) {
		logger.error(ep.getMessage());
    	}
	}
    
    /**
     * receiver to block the sender 
     * @param senderId user who sent the friend request
     * @param receiverId user who receives the friend request
     */
    public void blockReceiver(int senderId, int receiverId) {
    	try {
    		String value = "update userRelation set isReceiverBlocked = " + 1 + " where senderId = " + senderId + " and "
    				+ "receiverId = " + receiverId;
    		connectStatement.executeUpdate(value);
    	}
    	catch(SQLException ep) {
		logger.error(ep.getMessage());
    	}
	}

    /**
     * To add the review into local database
     * @param mr Movie review for a movie
     */
    public void addReviewToLocalDB(MovieReview mr) {
		try {
			int reviewId = mr.getId();
			String reviewContent = mr.getReview();
			String movieId = mr.getMovie_id();
			String reviewer_id = mr.getUser_id();
			String date = mr.getDate();
			String query = 
			"insert into Review values (" + reviewId + ", \"" + movieId + "\", " + reviewer_id + ", \"" +  date + "\", \"" + reviewContent + "\")";
			
			connectStatement.executeUpdate(query);
			
		}
		catch(SQLException se){
			logger.error(se.getMessage());
		}

	}
    
    public void addMovieList(int userId, String movieList) {
	try {
	    String query = "insert into Movielist(user_id, list_name) values (" + userId + ", \"" + movieList + "\")";
	    connectStatement.executeUpdate(query);
	} catch(SQLException se) {
	    logger.error(se.getMessage());
	}
    }
    
    public void preloadMovieList(int userId) {
	addMovieList(userId, "Watched");
	addMovieList(userId, "Favorites");
	addMovieList(userId, "Recommended");

    }
    
    /**
     * To get list of users that match the given search name
     * @param username the user name that will be used as search keyword
     * @return list of users
     */
    public List<User> keywordSearchUser(String username) {
    	ArrayList<User> output = new ArrayList<>();
    	try {
    		String query = "select * from user where username like \"%" + username + "%\"";
    		myResult = connectStatement.executeQuery(query);
    		while(myResult.next()) {
    			User matched_user = new User();
    			int user_id = myResult.getInt("user_id");
    			String user_name = myResult.getString("username");
    			int user_role = myResult.getInt("role");
    			int user_active_status = myResult.getInt("active");
    			matched_user.setActive(user_active_status);
    			matched_user.setUsername(user_name);
    			matched_user.setRole(user_role);
    			matched_user.setId(user_id);
    			output.add(matched_user);
    		}
    		
    	}
    	catch(SQLException ep) {
		logger.error(ep.getMessage());
    	}
    	
    	return output;
    	
    }
    
    /**
     * To return all movies stored in user's movie list with the given user id
     * @param userId user who owns the movie list
     * @return list of movies
     */
    public HashMap<String, ArrayList<Movie>> getMovieFromMovieList(int userId) {
    	HashMap<String, ArrayList<Movie>> output = new HashMap<>();
    	List<String> movieListName = this.getMovieListForUser(userId);
    	for(int i = 0; i < movieListName.size(); i++) {
    		String listName = movieListName.get(i);
    		ArrayList<Movie> movies = this.getMovieFromUserMovieList(userId, listName);
    		output.put(listName, movies);
    	}
    	return output;
    }
    
    /**
     * To get the movie list name for all movie lists belonging to given user
     * @param userId the id for user
     * @return the list of movie name 
     */
    public List<String> getMovieListForUser(int userId) {
    	ArrayList<String> movieNames = new ArrayList<>();
    	try {
    		String query = "select list_name from Movielist where user_id = " + userId;
    		myResult = connectStatement.executeQuery(query);
    		while(myResult.next()) {
    			String listName = myResult.getString("list_name");
    			movieNames.add(listName);
    		}
    		
    	}
    	catch(SQLException sq) {
		logger.error(sq.getMessage());
    	}
    	
    	return movieNames;
    }
    
    /**
     * To get movie from user movie list
     * @return list of movie names 
     */
    public ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname) {
    	ArrayList<Movie> result = new ArrayList<>();
    	try {
    		String query = "select Movie.movie_id, Movie.movie_name, Movie.plot, Movie.actor from Movie join " + 
    				"(select movie_id from UserMovieList where user_id = " + userId + " and list_name = \"" + listname + 
    				"\") as comp on comp.movie_id = Movie.movie_id";
    		myResult = connectStatement.executeQuery(query);
    		while(myResult.next()) {
    			Movie element = new Movie();
    			String movieId = myResult.getString("movie_id");
    			String movieName = myResult.getString("movie_name");
    			String movieActor = myResult.getString("actor");
    			String moviePlot = myResult.getString("plot");
    			System.out.println(movieId + "+" + movieName + "+" + movieActor + "+" + moviePlot);
    			element.setImdbID(movieId);
    			element.setTitle(movieName);
    			element.setActors(movieActor);
    			element.setPlot(moviePlot);
    			// add to final result.
    			result.add(element);
    		}
    	}
    	catch(SQLException sq) {
		logger.error(sq.getMessage());
    	}
    	return result;
    }
    
    /**
     * To create movie list 
     * @param movieListName the name for the movie list
     */
    public void createMovieList(int userid, String movieListName) {
    	try {
    		String query = "select * from Movielist where user_id = " + userid + " and list_name = \"" + movieListName + "\"";
    		myResult = connectStatement.executeQuery(query);
    		if(myResult.next()) {
    			System.out.println("You already have this movielist");
    		}
    		else {
    			String addListQuery = "insert into Movielist values (" + userid + ", \"" + movieListName + "\")";
    			connectStatement.executeUpdate(addListQuery);
    			System.out.println("movielist has added");
    		}
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    }
    
    /**
     * To add the movie into the movie list with given name
     * @param userId the user that this movie list belongs to
     * @param listName the name of the movie list
     * @param movieId id for movie that will be added to this list
     * @param movieName name for movie that will be added to this list
     */
    public void addMovieIntoMovieList(int userId, String listName, String movieId, String movieName) {
    	try {
    		String query = "insert into UserMovieList vlaues (" + userId + ", \"" + listName + "\", \"" + 
    						movieId + "\", \"" + movieName + "\")";
    		connectStatement.executeUpdate(query);
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    }
    
    /**
     * To get the status of the users relationship
     * @param senderId the user who sent the request
     * @param receiverId the user who is sent the request
     * @return the relationship one of the following: "friend", "onHold"
     */
    public String getUserRelation(int senderId, int receiverId) {
    	StringBuilder status = new StringBuilder();
    	try {
    		String query = "select * from userRelation where senderId = " + senderId + " and receiverId " + receiverId;
    		myResult = connectStatement.executeQuery(query);
    		if(myResult.next()) {
    			String sta = myResult.getString("relationStatus");
    			status.append(sta);
    		}
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    	
    	return status.toString();
    }
    
    /**
     * To get all friend's userId as they are the request sender
     * @param userId current user's id
     * @return list of friend id
     */
    public List<User> getAllFriendsAsSender(int userId) {
    	ArrayList<User> output = new ArrayList<>();
    	try {
    		String query = "select * from user join "
    				+ "(select senderId from userRelation where receiverId = " + userId + " and relationStatus = \"" + "friend" + "\") as comp"
    						+ " on user.user_id = comp.senderId";
    		myResult = connectStatement.executeQuery(query);
    		while(myResult.next()) {
    			String friendUsername = myResult.getString("username");
    			Integer friendUserId = myResult.getInt("senderId");
    			User friend = new User();
    			friend.setId(friendUserId);
    			friend.setUsername(friendUsername);
    			output.add(friend);
    		}
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    	return output;
    }
    
    /**
     * To get all friend's userId as they are the request receiver
     * @param userId current user's id
     * @return list of friend Id
     */
    public List<User> getAllFriendAsReceiver(int userId) {
    	ArrayList<User> output = new ArrayList<>();
    	try {
    		String query = "select * from user join "
    				+ "(select receiverId from userRelation where senderId = " + userId + " and relationStatus = \"" + "friend" + "\") as comp "
    						+ "on user.user_id = comp.receiverId";
    		myResult = connectStatement.executeQuery(query);
    		while(myResult.next()) {
    			String friendUsername = myResult.getString("username");
    			Integer friendUserId = myResult.getInt("senderId");
    			User friend = new User();
    			friend.setId(friendUserId);
    			friend.setUsername(friendUsername);;
    			output.add(friend);
    		}
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    	return output;
    }
    
    
    /**
     * To get all friends 
     * @param userId the current user's id
     * @return list of username
     */
    public List<User> getAllFriends(int userId) {
    	ArrayList<User> output = new ArrayList<>();
    	output.addAll(this.getAllFriendAsReceiver(userId));
    	output.addAll(this.getAllFriendsAsSender(userId));
    	return output;
    }
    
   
    
    /**
     * To get all friend requests from other users
     * @param userId the current user's id
     * @return list of username who sent the friend request
     */
    public List<User> getAllReceivedFriendRequest(int userId) {
    	ArrayList<User> output = new ArrayList<>();
    	try {
    		String query =  "select * from user join "
    				+ "(select senderId from userRelation where receiverId = " + userId + " and relationStatus = \"" + "onHold" + "\") as comp "
					+ "on user.user_id = comp.receiverId";
    		myResult = connectStatement.executeQuery(query);
    		
    		while(myResult.next()) {
    			String  friendUserName = myResult.getString("username");
    			Integer friendUserId = myResult.getInt("senderId");
    			User friend = new User();
    			friend.setId(friendUserId);
    			friend.setUsername(friendUserName);
    			output.add(friend);
    		}
    		
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    	
    	return output;
    }
    
    /**
     * To get all friend requests this user sent out
     * @param userId the current user's id
     * @return list of username who sent the friend request
     */
    public List<User> getAllSentFriendRequest(int userId) {
    	ArrayList<User> output = new ArrayList<>();
    	try {
    		String query =  "select * from user join "
    				+ "(select receiverId from userRelation where senderId = " + userId + " and relationStatus = \"" + "onHold" + "\") as comp "
					+ "on user.user_id = comp.receiverId";
    		myResult = connectStatement.executeQuery(query);
    		
    		while(myResult.next()) {
    			String  friendUserName = myResult.getString("username");
    			Integer friendUserId = myResult.getInt("receiverId");
    			User friend = new User();
    			friend.setId(friendUserId);
    			friend.setUsername(friendUserName);
    			output.add(friend);
    		}
    		
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    	
    	return output;
    }
    
    
    
    /**
     * To return the total number of friend request 
     * @param userId the current user's id
     * @return the total number of friend request
     */
    public List<User> getAllFriendRequest(int userId) {
    	ArrayList<User> output = new ArrayList<>();
    	output.addAll(this.getAllReceivedFriendRequest(userId));
    	output.addAll(this.getAllSentFriendRequest(userId));
    	
    	return output;
    }
    
    
    
    /**
     * Get a rating from movie ratings.
     */
    public int getRating(int userId, String movieId) {
	try {
	    
	    String query = "select rating from rating"
	    	+ " where rating.user_id = " + userId
	    	+ " and rating.movie_id = '" + movieId + "'";
	    
	     myResult = connectStatement.executeQuery(query);
	     if(myResult.next()) {
		 return myResult.getInt("rating");
	     } 
	} catch(SQLException e) {
		logger.error(e.getMessage());
	}
	
	return -1;
    }

}
