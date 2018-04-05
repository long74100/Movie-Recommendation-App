package edu.northeastern.cs4500.model.services;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import edu.northeastern.cs4500.model.movie.MovieReview;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.prod.Prod;

/**
 * This class is used to connect to the local database. This tool builds a connection between front end 
 * operation and back end database. Also, it will get the online movie information to the local database. 
 * When user search a movie online which already exists in local database, local database will provide information 
 * without doing any API Call. Otherwise, local database will store the movie which it does not contain but user search online.
 * 
 * @author lgj81
 */
public class LocalSQLConnectServiceImpl implements ILocalSQLConnectService {
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
	private static final Logger logger = LogManager.getLogger(LocalSQLConnectServiceImpl.class);
	
	/**
	 * The constructor
	 * The constructor will automatically create connection to local database
	 */
	public LocalSQLConnectServiceImpl() {
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
    	String sqlcmd = "select * from Movie where movie_id =?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, movieId);
    		myResult = pstmt.executeQuery();
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
    	String sqlcmd = "select * from userRelation where (senderId = ? and receiverId = ?) or (senderId = ? and receiverId = ?)";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, senderId);
    		pstmt.setInt(2, receiverId);
    		pstmt.setInt(3, receiverId);
    		pstmt.setInt(4, senderId);
    		myResult = pstmt.executeQuery();
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
     * Store movie into local db when user search and click to view the movie profile
     * @param movieObject the object containing all movie information
     */
    @Override
    public void loadMovieIntoLocalDB(Map<String, String> movieObject) {
    	System.out.println("Start loading movie....");
    	try {
    		String sqlcmd = "insert into Movie values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
        	String movie_id = movieObject.get("imdbID");
        	String movie_name = movieObject.get("title");
        	String genre = movieObject.get("genre");
        	String plot = movieObject.get("plot");
        	String actors = movieObject.get("actors");
        	String directors = movieObject.get("director");
        	String released = movieObject.get("released");
        	String runtime = movieObject.get("runtime");
        	String country = movieObject.get("country");
        	String imdbRating = movieObject.get("imdbRating");
        	String poster = movieObject.get("poster");
        	String language = movieObject.get("language");
        	String movieDBid = movieObject.get("movieDBid");
        	PreparedStatement pstmt = null;
        	
        	try {
        		pstmt = connector.prepareStatement(sqlcmd);
        		pstmt.setString(1, movie_id);
        		pstmt.setString(2, movie_name);
        		pstmt.setString(3, runtime);
        		pstmt.setString(4, released);
        		pstmt.setString(5, genre);
        		pstmt.setString(6, directors);
        		pstmt.setString(7, actors);
        		pstmt.setString(8, plot);
        		pstmt.setString(9, language);
        		pstmt.setString(10, country);
        		pstmt.setString(11, poster);
        		pstmt.setString(12, imdbRating);
        		pstmt.setString(13, movieDBid);
        		pstmt.executeUpdate();
        		System.out.println("loading movie finished");
        	}
        	catch(SQLException sq) {
        		logger.error(sq.getMessage());
        	}
    	}
    	catch(Exception ep) {
    		logger.error(ep.getMessage());
    	}
    	
    }
    
    /**
     * To insert the data into the given table
     * @param data the data will be inserted
     * @param tableName the destination table that the data will be inserted to
     */
    public void insertData(String data, String tableName) {
    	String sqlcmd = "insert into ? values ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, tableName);
    		pstmt.setString(2, data);
    		myResult = pstmt.executeQuery();
    	}
    	catch(SQLException ep) {
		logger.error(ep.getMessage());
    	}
    }
    
    /**
     * Movie Operators:
     * This section is for user and system to operate on the movies
     */
    
    /**
     * To delete the given movie from the local database
     * @param id the given movie id
     */
    public void deleteFromMovieTable(String id) {
    	String sqlcmd = "delete from Movie where movie_id = ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, id);
        	pstmt.executeUpdate();
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
    	String sqlcmd = "delete from ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, tableName);
    		pstmt.executeUpdate();
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
    	String sqlcmd = "select from Movie where movie_name like \"%\"?\"%\"";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, input);
    		connectStatement.executeUpdate(sqlcmd);
    		myResult = pstmt.executeQuery();
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
    	String sqlcmd = "SELECT * from Movie where actor like \"%\"?\"%\"";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, actorsName);
    		myResult = pstmt.executeQuery();
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
    	String sqlcmd = "SELECT * from Movie where director like \"%\"?\"%\"";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, directorName);
    		myResult = pstmt.executeQuery();
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
    	String sqlcmd = "SELECT * from Movie where genre like \"%\"?\"%\"";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, genre);
    		myResult = pstmt.executeQuery();
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
    	String sqlcmd = "SELECT * from Movie where runtime like \"%\"?\"%\"";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, year);
    		myResult = pstmt.executeQuery();
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
    	String sqlcmd = "select * from Movie where movie_id like \"%\"?\"%\""
				+ " or movie_name like \"%\"?\"%\"" 
				+ " or runtime like \"%\"?\"%\""
				+ " or release_date like \"%\"?\"%\"" 
				+ " or genre like \"%\"?\"%\""
				+ " or director like \"%\"?\"%\""
				+ " or actor like \"%\"?\"%\""
				+ " or plot like \"%\"?\"%\""
				+ " or movie_language like \"%\"?\"%\""
				+ " or country like \"%\"?\"%\""
				+ " or imdbRating like \"%\"?\"%\"";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, keyword);
    		pstmt.setString(2, keyword);
    		pstmt.setString(3, keyword);
    		pstmt.setString(4, keyword);
    		pstmt.setString(5, keyword);
    		pstmt.setString(6, keyword);
    		pstmt.setString(7, keyword);
    		pstmt.setString(8, keyword);
    		pstmt.setString(9, keyword);
    		pstmt.setString(10, keyword);
    		pstmt.setString(11, keyword);
    		pstmt.setString(12, keyword);
    		pstmt.setString(13, keyword);
    		pstmt.setString(14, keyword);
    		pstmt.setString(15, keyword);
    		pstmt.setString(16, keyword);
    		myResult = pstmt.executeQuery();
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
    			StringBuilder output = new StringBuilder();
    			output.append(movieId + "| " + movieName + "| " 
    					+ runtime + "| " + genre + "| " + released_date + "| " + director + "| " + 
    					actors + "| " + plot + "| " + language + "| " + country + "| " + poster + "| "
    					+ imdbRating);
    			movie.add(output.toString());
    		}
    	}
    	catch(Exception e) {
		logger.error(e.getMessage());
    	}
    }
    
    
    // ----- user interaction in local database-----
    
    /**
     * To send friend request to receiver if there is non shown in relation list.
     * @param senderId the id for sender
     * @param receiverId the id for receiver
     */
    public void sendFriendRequest(int senderId, int receiverId) {
    	String sqlcmd = "insert into userRelation values (?, ?, \"onHold\", \"0\", \"0\")";
    	PreparedStatement pstmt = null;
    	if(!this.hasMadeRequest(senderId, receiverId)) {
    		try {
    			pstmt = connector.prepareStatement(sqlcmd);
        		pstmt.setInt(1, senderId);
        		pstmt.setInt(2, receiverId);
    			pstmt.executeUpdate();
    		}
    		catch(SQLException se) {
			logger.error(se.getMessage());
    		}
    	}
    
    }
    
    /**
     * To accept the friend request from sender to receiver
     * @param senderId sender who sent out the friend request
     * @param receiverId receiver who received the friend request
     */
    public void acceptRequest(int senderId, int receiverId) {
    	String sqlcmd = "update userRelation set relationStatus = \"friend\" where senderId = ? and receiverId = ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, senderId);
    		pstmt.setInt(2, receiverId);
    		pstmt.executeUpdate();
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
    	String sqlcmd = "delete from userRelation where senderId = ? and receiverId = ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, senderId);
    		pstmt.setInt(2, receiverId);
    		pstmt.executeUpdate();
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
    	String sqlcmd = "update userRelation set isSenderBlocked = 1 where senderId = ? and receiverId = ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, senderId);
    		pstmt.setInt(2, receiverId);
    		pstmt.executeUpdate();
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
    	String sqlcmd = "update userRelation set isReceiverBlocked = 1 where senderId = ? and receiverId = ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, senderId);
    		pstmt.setInt(2, receiverId);
    		pstmt.executeUpdate();
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
			String username = mr.getUsername();
			String query = 
			"insert into Review values (" + reviewId + ", \"" + movieId + "\", " 
			+ reviewer_id + ", \"" +  username + "\", \"" + date + "\", \"" + reviewContent + "\")";
			
			connectStatement.executeUpdate(query);
			
		}
		catch(SQLException se){
			logger.error(se.getMessage());
		}

	}

    
    /**
     * To set up initial movie list for the new user 
     * @param userId the new user's id
     */
    public void preloadMovieList(int userId) {
    	createMovieList(userId, "Favorites");
    }
    
    /**
     * To get list of users that match the given search name
     * @param username the user name that will be used as search keyword
     * @return list of users
     */
    public List<User> keywordSearchUser(String username) {
    	ArrayList<User> output = new ArrayList<>();
    	String sqlcmd = "select * from user where username like \"%\"?\"%\"";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setString(1, username);
    		myResult = pstmt.executeQuery();
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
    	ArrayList<String> movieListNames = new ArrayList<>();
    	try {
    		String sqlcmd = "select * from Movielist where user_id = ?";
        	PreparedStatement pstmt = null;
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userId);
    		myResult = pstmt.executeQuery();
    		while(myResult.next()) {
    			String listName = myResult.getString("list_name");
    			movieListNames.add(listName);
    		}	
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
    	}
    	
    	return movieListNames;
    }
    
    /**
     * To get movie from user movie list
     * @return list of movie names 
     */
    public ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname) {
    	ArrayList<Movie> result = new ArrayList<>();
    	String sqlcmd = "select Movie.movie_id, Movie.movie_name, Movie.released_date, Movie.plot, Movie.actor, Movie.poster, Movie.movieDBid from Movie join " + 
				"(select movie_id from UserMovieList where user_id = ? and list_name = ?) as comp on comp.movie_id = Movie.movie_id";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userId);
    		pstmt.setString(2, listname);
    		myResult = pstmt.executeQuery();
    		while(myResult.next()) {
    			Movie element = new Movie();
    			String movieId = myResult.getString("movie_id");
    			String movieName = myResult.getString("movie_name");
    			String movieActor = myResult.getString("actor");
    			String moviePlot = myResult.getString("plot");
    			String moviePoster = myResult.getString("poster");
    			String movieDBId = myResult.getString("movieDBid");
    			String release = myResult.getString("released_date");
    			element.setImdbID(movieId);
    			element.setTitle(movieName);
    			element.setActors(movieActor);
    			element.setPlot(moviePlot);
    			element.setPoster(moviePoster);
    			element.setTheMovieDbID(movieDBId);
    			element.setReleased(release);
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
    	String sqlcmd = "select * from Movielist where user_id = ? and list_name = ?";
    	String addListQuery = "insert into Movielist values (?, ?)";
    	PreparedStatement pstmt = null;
    	PreparedStatement pstmt2 = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userid);
    		pstmt.setString(2, movieListName);
    		myResult = pstmt.executeQuery();
    		if(!myResult.next()) {
    		pstmt2 = connector.prepareStatement(addListQuery);
        		pstmt2.setInt(1, userid);
        		pstmt2.setString(2, movieListName);
        		pstmt2.executeUpdate();
    		}
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
    	}
    }
    
    /**
     * To delete a movie from user's movie list
     * @param userid the user who owns movie list
     * @param movieList the movie list that will be deleted from 
     * @param movieId movie that will be deleted
     */
    public void deleteMovieFromUserMovieList(int userid, String movieList, String movieId) {
    	String sqlcmd = "delete from UserMovieList where user_id = ? and (list_name = ? and movie_id = ?)";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userid);
    		pstmt.setString(2, movieList);
    		pstmt.setString(3, movieId);
    		pstmt.executeUpdate();
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
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
    	String sqlcmd = "insert into UserMovieList values (?, ?, ?, ?)";
    	PreparedStatement pstmt = null;
    	try {

    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userId);
    		pstmt.setString(2, listName);
    		pstmt.setString(3, movieId);
    		pstmt.setString(4, movieName);
    		pstmt.executeUpdate();
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
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
    	String sqlcmd = "select * from userRelation where senderId = ? and receiverId = ?";
	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, senderId);
    		pstmt.setInt(2, receiverId);
    		myResult = pstmt.executeQuery();
    		if(myResult.next()) {
    			String sta = myResult.getString("relationStatus");
    			status.append(sta);
    		}
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
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
    	String sqlcmd = "select * from user join "
				+ "(select senderId from userRelation where receiverId = ? and relationStatus = \"friend\") as comp"
				+ " on user.user_id = comp.senderId";
    	PreparedStatement pstmt = null;
    try {
        	pstmt = connector.prepareStatement(sqlcmd);
        	pstmt.setInt(1, userId);
        	myResult = pstmt.executeQuery();
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
    		logger.error(sq.getMessage());
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
    	String sqlcmd = "select * from user join "
				+ "(select receiverId from userRelation where senderId = ? and relationStatus = \"friend\") as comp "
				+ "on user.user_id = comp.receiverId";
    	PreparedStatement pstmt = null;
    try {
        	pstmt = connector.prepareStatement(sqlcmd);
        	pstmt.setInt(1, userId);
        	myResult = pstmt.executeQuery();
    		while(myResult.next()) {
    			String friendUsername = myResult.getString("username");
    			Integer friendUserId = myResult.getInt("receiverId");
    			User friend = new User();
    			friend.setId(friendUserId);
    			friend.setUsername(friendUsername);;
    			output.add(friend);
    		}
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
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
    	String sqlcmd = "select * from user join "
				+ "(select senderId from userRelation where receiverId = ? and relationStatus = \"onHold\") as comp "
				+ "on user.user_id = comp.senderId";
    	PreparedStatement pstmt = null;
    try {
        	pstmt = connector.prepareStatement(sqlcmd);
        	pstmt.setInt(1, userId);
        	myResult = pstmt.executeQuery();
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
    		logger.error(sq.getMessage());
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
    	String sqlcmd = "select * from user join "
				+ "(select receiverId from userRelation where senderId = ? and relationStatus = \"onHold\") as comp "
				+ "on user.user_id = comp.receiverId";
    	PreparedStatement pstmt = null;
    try {
        	pstmt = connector.prepareStatement(sqlcmd);
        	pstmt.setInt(1, userId);
        	myResult = pstmt.executeQuery();
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
    		logger.error(sq.getMessage());
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
     * @param userId the user Id
     * @param movieId the movie Id
     */
    public int getRating(int userId, String movieId) {
    	String sqlcmd = "select rating from rating"
    	    	+ " where rating.user_id = ? and rating.movie_id = ?";
    	PreparedStatement pstmt = null;
    try {
        	pstmt = connector.prepareStatement(sqlcmd);
        	pstmt.setInt(1, userId);
        	pstmt.setString(2, movieId);
        	myResult = pstmt.executeQuery();
        	if(myResult.next()) {
        		return myResult.getInt("rating");
        		} 
    } 
	catch(SQLException e) {
		logger.error(e.getMessage());
	}
	
		return -1;
    }
    
    
    /**
     * To get all the reviews for the movie with given movieId
     * @param movieId the movie that will be checked the reviews
     * @return list of movie reviews
     */
    public List<MovieReview> getReviewsForMovie(String movieId) {
    	ArrayList<MovieReview> result = new ArrayList<>();
    	String sqlcmd = "select * from Review where movie_id = ?";
    	PreparedStatement pstmt = null;
    try {
        	pstmt = connector.prepareStatement(sqlcmd);
        	pstmt.setString(1, movieId);
        	myResult = pstmt.executeQuery();
    		while(myResult.next()) {
    			MovieReview output = new MovieReview();
    			String username = myResult.getString("reviewer_name");
    			String userid = myResult.getString("reviewer_id");
    			Integer reviewId = myResult.getInt("review_id");
    			String reviewDate = myResult.getString("review_date");
    			String description = myResult.getString("description");
    			output.setDate(reviewDate);
    			output.setMovie_id(movieId);
    			output.setReview(description);
    			output.setUser_id(userid);
    			output.setUsername(username);
    			result.add(output);
    		}
    	}
    	catch(SQLException ep) {
	    logger.error(ep.getMessage());
    	}
    	return result;
    }
    
    
    /**
     * To get review written by given user
     * @param userId the user's id
     * @return the list of movie
     */
    public List<MovieReview> getReviewForUser(String userId) {
    	ArrayList<MovieReview> output = new ArrayList<>();
    	String sqlcmd = "select * from Review where reviewer_id = ?";
    	PreparedStatement pstmt = null;
    try {
        	pstmt = connector.prepareStatement(sqlcmd);
        	pstmt.setString(1, userId);
        	myResult = pstmt.executeQuery();
    		while(myResult.next()) {
    			MovieReview item = new MovieReview();
    			String username = myResult.getString("reviewer_name");
    			String movieid = myResult.getString("movie_id");
    			Integer reviewId = myResult.getInt("review_id");
    			String reviewDate = myResult.getString("review_date");
    			String description = myResult.getString("description");
    			item.setDate(reviewDate);
    			item.setMovie_id(movieid);
    			item.setReview(description);
    			item.setUser_id(userId);
    			item.setUsername(username);
    			output.add(item);
    		}
    	}
    	catch(SQLException sq) {
	    logger.error(sq.getMessage());
    	}
    	
    	return output;
    }
    
    /**
     * To delete the movie list also delete all the stored movies records
     * @param userId user who owns the given list
     * @param listName name of the movie list
     */
    public void deleteMovieList(int userId, String listName) {
    	String sqlcmd = "delete from Movielist where user_id = ? and list_name = ?";
    	String sqlcmdfollowing = "delete from UserMovieList where user_id = ? and list_name = ?";
    	PreparedStatement pstmt = null;
    	PreparedStatement pstmt2 = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userId);
    		pstmt.setString(2, listName);
    		int deletedRow = pstmt.executeUpdate();
    		if(deletedRow > 0) {
    			pstmt2 = connector.prepareStatement(sqlcmdfollowing);
    			pstmt2.setInt(1, userId);
    			pstmt2.setString(2, listName);
    			pstmt2.executeUpdate();
    		}
    	}
    	catch(SQLException sl) {
    		logger.error(sl.getMessage());
    	}
    }
    
    /**
     * To clean the movie list with given list name
     * @param userId the movie list's owner
     * @param listName the name of movie list
     */
    public void cleanMovieList(int userId, String listName) {
    	String sqlcmd = "delete from UserMovieList where user_id = ? and list_name = ?";
    	PreparedStatement pstmt = null;
    	try {
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userId);
    		pstmt.setString(2, listName);
    		pstmt.executeUpdate();
    	}
    	catch(SQLException sq) {
    		sq.printStackTrace();
    	}
    }


	@Override
	public void loadMovieToLocalDB(JSONObject movieJSON) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addMultiMovies(ArrayList<String> ids) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public ArrayList<String> getSearchMovieResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void sendProdToFriend(int senderId, int receiverId, String senderName, 
			String receiverName, String movieId, String movieName, String date, String comment) {
		String sqlcmd = "insert into Prod values (?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, senderId);
			pstmt.setInt(2, receiverId);
			pstmt.setString(3, senderName);
			pstmt.setString(4, receiverName);
			pstmt.setString(5,  movieId);
			pstmt.setString(6, movieName);
			pstmt.setString(7, date);
			pstmt.setString(8, comment);
			pstmt.executeUpdate();
		}
		catch (SQLException sq) {
			logger.error(sq.getMessage());
		}
	}
	
	@Override
	public List<Prod> extractAllFriendProds(int userId) {
		String sqlcmd = "select * from Prod where receiverId = ?";
		PreparedStatement pstmt = null;
		ArrayList<Prod> friendsProds = new ArrayList<>();
		try {
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				prod.setSender(myResult.getInt("senderId"));
				prod.setSenderName(myResult.getString("sender_name"));
				prod.setReceiver(userId);
				prod.setReceiverName(myResult.getString("receiver_name"));
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				friendsProds.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		
		return friendsProds;
	}
	
	@Override
	public List<Prod> extractAllSentProds(int userId) {
		String sqlcmd = "select * from Prod where senderId = ?";
		PreparedStatement pstmt = null;
		ArrayList<Prod> sentOutProds = new ArrayList<>();
		try {
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				prod.setSender(myResult.getInt(userId));
				prod.setSenderName(myResult.getString("sender_name"));
				prod.setReceiver(myResult.getInt("receiverId"));
				prod.setReceiverName(myResult.getString("receiver_name"));
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				sentOutProds.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		return sentOutProds;
	}
	
	@Override
	public List<Prod> extractProdsSentToAFriend(int userId, int friendId) {
		String sqlcmd = "select * from Prod where senderId = ? and receiverId = ?";
		ArrayList<Prod> sentToThisFriend = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, friendId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				prod.setSender(userId);
				prod.setSenderName(myResult.getString("sender_name"));
				prod.setReceiver(friendId);
				prod.setReceiverName(myResult.getString("receiver_name"));
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				sentToThisFriend.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		
		return sentToThisFriend;
	}
	
	@Override
	public List<Prod> extractProdsReceivedFromAFriend(int userId, int friendId) {
		String sqlcmd = "select * from Prod where senderId = ? and receiverId = ?";
		ArrayList<Prod> sentToThisFriend = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, friendId);
			pstmt.setInt(2, userId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				prod.setSender(friendId);
				prod.setSenderName(myResult.getString("sender_name"));
				prod.setReceiver(userId);
				prod.setReceiverName(myResult.getString("receiver_name"));
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				sentToThisFriend.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		
		return sentToThisFriend;
	}

}
