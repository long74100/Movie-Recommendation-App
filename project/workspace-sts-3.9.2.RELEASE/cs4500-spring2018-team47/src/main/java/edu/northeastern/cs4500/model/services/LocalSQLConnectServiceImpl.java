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
import edu.northeastern.cs4500.model.movie.MovieRating;
import edu.northeastern.cs4500.model.user.User;

/**
 * This class is used to connect to the local database. This tool builds a
 * connection between front end operation and back end database. Also, it will
 * get the online movie information to the local database. When user search a
 * movie online which already exists in local database, local database will
 * provide information without doing any API Call. Otherwise, local database
 * will store the movie which it does not contain but user search online.
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
    private enum Status {
	get, insert, delete, update
    };

    private static Status status = null;
    private static Connection connector = null;
    private static Statement connectStatement = null;
    private static ResultSet myResult = null;
    private ArrayList<String> movie = new ArrayList<>();
    private static final Logger logger = LogManager.getLogger(LocalSQLConnectServiceImpl.class);

    /**
     * The constructor The constructor will automatically create connection to local
     * database
     */
    public LocalSQLConnectServiceImpl() {
	try {
	    connector = DriverManager.getConnection(url, username, password);
	    connectStatement = connector.createStatement();
	} catch (SQLException se) {
	    logger.error(se.getMessage());
	}

    }

    @Override
    public boolean containMovie(String movieId) {
	String sqlcmd = "select * from Movie where movie_id =?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, movieId);
	    myResult = pstmt.executeQuery();
	    if (myResult.next()) {
		return true;
	    }
	} catch (SQLException ep) {
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
	    if (myResult.next()) {
		return true;
	    }
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
	return false;
    }

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
	    } catch (SQLException sq) {
		logger.error(sq.getMessage());
	    }
	} catch (Exception ep) {
	    logger.error(ep.getMessage());
	}

    }

    @Override
    public void insertData(String data, String tableName) {
	String sqlcmd = "insert into ? values ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, tableName);
	    pstmt.setString(2, data);
	    myResult = pstmt.executeQuery();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    /**
     * Movie Operators: This section is for user and system to operate on the movies
     */

    @Override
    public void deleteFromMovieTable(String id) {
	String sqlcmd = "delete from Movie where movie_id = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, id);
	    pstmt.executeUpdate();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void clearTable(String tableName) {
	String sqlcmd = "delete from ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, tableName);
	    pstmt.executeUpdate();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void searchKeyWordMovieTitle(String input) {
	String sqlcmd = "select from Movie where movie_name like \"%\"?\"%\"";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, input);
	    connectStatement.executeUpdate(sqlcmd);
	    myResult = pstmt.executeQuery();
	    execute();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void searchKeyWordActorsName(String actorsName) {
	String sqlcmd = "SELECT * from Movie where actor like \"%\"?\"%\"";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, actorsName);
	    myResult = pstmt.executeQuery();
	    execute();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void searchKeyWordDirectorName(String directorName) {
	String sqlcmd = "SELECT * from Movie where director like \"%\"?\"%\"";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, directorName);
	    myResult = pstmt.executeQuery();
	    execute();
	} catch (SQLException ec) {
	    logger.error(ec.getMessage());
	}
    }

    @Override
    public void searchKeyWordGenre(String genre) {
	String sqlcmd = "SELECT * from Movie where genre like \"%\"?\"%\"";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, genre);
	    myResult = pstmt.executeQuery();
	    execute();
	} catch (SQLException ec) {
	    logger.error(ec.getMessage());
	}
    }

    @Override
    public void searchKeyWordTime(String year) {
	String sqlcmd = "SELECT * from Movie where runtime like \"%\"?\"%\"";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, year);
	    myResult = pstmt.executeQuery();
	    execute();
	} catch (SQLException ec) {
	    logger.error(ec.getMessage());
	}
    }

    @Override
    public void searchByKeyWordInOne(String keyword) {
	String sqlcmd = "select * from Movie where movie_id like \"%\"?\"%\"" + " or movie_name like \"%\"?\"%\""
		+ " or runtime like \"%\"?\"%\"" + " or release_date like \"%\"?\"%\"" + " or genre like \"%\"?\"%\""
		+ " or director like \"%\"?\"%\"" + " or actor like \"%\"?\"%\"" + " or plot like \"%\"?\"%\""
		+ " or movie_language like \"%\"?\"%\"" + " or country like \"%\"?\"%\""
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
	} catch (SQLException ec) {
	    logger.error(ec.getMessage());
	}
    }

    /**
     * To execute the get movie from local database command
     */
    private void execute() {
	try {
	    while (myResult.next()) {
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
		output.append(movieId + "| " + movieName + "| " + runtime + "| " + genre + "| " + released_date + "| "
			+ director + "| " + actors + "| " + plot + "| " + language + "| " + country + "| " + poster
			+ "| " + imdbRating);
		movie.add(output.toString());
	    }
	} catch (Exception e) {
	    logger.error(e.getMessage());
	}
    }

    // ----- user interaction in local database-----

    @Override
    public void sendFriendRequest(int senderId, int receiverId) {
	String sqlcmd = "insert into userRelation values (?, ?, \"onHold\", \"0\", \"0\")";
	PreparedStatement pstmt = null;
	if (!this.hasMadeRequest(senderId, receiverId)) {
	    try {
		pstmt = connector.prepareStatement(sqlcmd);
		pstmt.setInt(1, senderId);
		pstmt.setInt(2, receiverId);
		pstmt.executeUpdate();
	    } catch (SQLException se) {
		logger.error(se.getMessage());
	    }
	}

    }

    @Override
    public void acceptRequest(int senderId, int receiverId) {
	String sqlcmd = "update userRelation set relationStatus = \"friend\" where senderId = ? and receiverId = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, senderId);
	    pstmt.setInt(2, receiverId);
	    pstmt.executeUpdate();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void rejectRequest(int senderId, int receiverId) {
	String sqlcmd = "delete from userRelation where senderId = ? and receiverId = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, senderId);
	    pstmt.setInt(2, receiverId);
	    pstmt.executeUpdate();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void blockSender(int senderId, int receiverId) {
	String sqlcmd = "update userRelation set isSenderBlocked = 1 where senderId = ? and receiverId = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, senderId);
	    pstmt.setInt(2, receiverId);
	    pstmt.executeUpdate();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void blockReceiver(int senderId, int receiverId) {
	String sqlcmd = "update userRelation set isReceiverBlocked = 1 where senderId = ? and receiverId = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, senderId);
	    pstmt.setInt(2, receiverId);
	    pstmt.executeUpdate();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
    }

    @Override
    public void addReviewToLocalDB(MovieReview mr) {
	try {
	    int reviewId = mr.getId();
	    String reviewContent = mr.getReview();
	    String movieId = mr.getMovie_id();
	    String reviewer_id = mr.getUser_id();
	    String date = mr.getDate();
	    String username = mr.getUsername();
	    String query = "insert into Review values (" + reviewId + ", \"" + movieId + "\", " + reviewer_id + ", \""
		    + username + "\", \"" + date + "\", \"" + reviewContent + "\")";

	    connectStatement.executeUpdate(query);

	} catch (SQLException se) {
	    logger.error(se.getMessage());
	}

    }

    @Override
    public void preloadMovieList(int userId) {
	createMovieList(userId, "Favorites");
    }

    @Override
    public List<User> keywordSearchUser(String username) {
	ArrayList<User> output = new ArrayList<>();
	String sqlcmd = "select * from user where username like \"%\"?\"%\"";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, username);
	    myResult = pstmt.executeQuery();
	    while (myResult.next()) {
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

	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
	return output;

    }

    @Override
    public HashMap<String, ArrayList<Movie>> getMovieFromMovieList(int userId) {
	HashMap<String, ArrayList<Movie>> output = new HashMap<>();
	List<String> movieListName = this.getMovieListForUser(userId);
	for (int i = 0; i < movieListName.size(); i++) {
	    String listName = movieListName.get(i);
	    ArrayList<Movie> movies = this.getMovieFromUserMovieList(userId, listName);
	    output.put(listName, movies);
	}
	return output;
    }

    @Override
    public List<String> getMovieListForUser(int userId) {
	ArrayList<String> movieListNames = new ArrayList<>();
	try {
	    String sqlcmd = "select * from Movielist where user_id = ?";
	    PreparedStatement pstmt = null;
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, userId);
	    myResult = pstmt.executeQuery();
	    while (myResult.next()) {
		String listName = myResult.getString("list_name");
		movieListNames.add(listName);
	    }
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}

	return movieListNames;
    }

    @Override
    public ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname) {
	ArrayList<Movie> result = new ArrayList<>();
	String sqlcmd = "select Movie.movie_id, Movie.movie_name, Movie.released_date, Movie.plot, Movie.actor, Movie.poster, Movie.movieDBid from Movie join "
		+ "(select movie_id from UserMovieList where user_id = ? and list_name = ?) as comp on comp.movie_id = Movie.movie_id";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, userId);
	    pstmt.setString(2, listname);
	    myResult = pstmt.executeQuery();
	    while (myResult.next()) {
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
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}
	return result;
    }

    @Override
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
	    if (!myResult.next()) {
		pstmt2 = connector.prepareStatement(addListQuery);
		pstmt2.setInt(1, userid);
		pstmt2.setString(2, movieListName);
		pstmt2.executeUpdate();
	    }
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}
    }

    @Override
    public void deleteMovieFromUserMovieList(int userid, String movieList, String movieId) {
	String sqlcmd = "delete from UserMovieList where user_id = ? and (list_name = ? and movie_id = ?)";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, userid);
	    pstmt.setString(2, movieList);
	    pstmt.setString(3, movieId);
	    pstmt.executeUpdate();
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}
    }

    @Override
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
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}
    }

    @Override
    public String getUserRelation(int senderId, int receiverId) {
	StringBuilder status = new StringBuilder();
	String sqlcmd = "select * from userRelation where senderId = ? and receiverId = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, senderId);
	    pstmt.setInt(2, receiverId);
	    myResult = pstmt.executeQuery();
	    if (myResult.next()) {
		String sta = myResult.getString("relationStatus");
		status.append(sta);
	    }
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}

	return status.toString();
    }

    @Override
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
	    while (myResult.next()) {
		String friendUsername = myResult.getString("username");
		Integer friendUserId = myResult.getInt("senderId");
		User friend = new User();
		friend.setId(friendUserId);
		friend.setUsername(friendUsername);
		output.add(friend);
	    }
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}
	return output;
    }

    @Override
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
	    while (myResult.next()) {
		String friendUsername = myResult.getString("username");
		Integer friendUserId = myResult.getInt("receiverId");
		User friend = new User();
		friend.setId(friendUserId);
		friend.setUsername(friendUsername);
		;
		output.add(friend);
	    }
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}
	return output;
    }

    @Override
    public List<User> getAllFriends(int userId) {
	ArrayList<User> output = new ArrayList<>();
	output.addAll(this.getAllFriendAsReceiver(userId));
	output.addAll(this.getAllFriendsAsSender(userId));
	return output;
    }

    @Override
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
	    while (myResult.next()) {
		String friendUserName = myResult.getString("username");
		Integer friendUserId = myResult.getInt("senderId");
		User friend = new User();
		friend.setId(friendUserId);
		friend.setUsername(friendUserName);
		output.add(friend);
	    }

	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}

	return output;
    }

    @Override
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
	    while (myResult.next()) {
		String friendUserName = myResult.getString("username");
		Integer friendUserId = myResult.getInt("receiverId");
		User friend = new User();
		friend.setId(friendUserId);
		friend.setUsername(friendUserName);
		output.add(friend);
	    }

	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}

	return output;
    }

    @Override
    public List<User> getAllFriendRequest(int userId) {
	ArrayList<User> output = new ArrayList<>();
	output.addAll(this.getAllReceivedFriendRequest(userId));
	output.addAll(this.getAllSentFriendRequest(userId));
	return output;
    }

    @Override
    public int getRating(int userId, String movieId) {
	String sqlcmd = "select rating from rating" + " where rating.user_id = ? and rating.movie_id = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, userId);
	    pstmt.setString(2, movieId);
	    myResult = pstmt.executeQuery();
	    if (myResult.next()) {
		return myResult.getInt("rating");
	    }

	} catch (SQLException e) {
	    logger.error(e.getMessage());
	}

	return -1;
    }

    @Override
    public void insertRating(MovieRating movieRating) {
	String sqlcmd1 = "insert into rating(movie_id, rating, user_id, review_date) values (?, ?, ?, ?)";
	String sqlcmd2 = "update rating set rating = ? where movie_id = ? and user_id = ?";
	PreparedStatement pstmt = null;
	
	int userId = movieRating.getUserID();
	String movieId = movieRating.getMovieId();
	double rating = movieRating.getRating();
	String date = movieRating.getDate();

	if (getRating(userId, movieId) == -1) {
	    try {
		pstmt = connector.prepareStatement(sqlcmd1);
		pstmt.setString(1, movieId);
		pstmt.setDouble(2, rating);
		pstmt.setInt(3, userId);
		pstmt.setString(4, date);
		pstmt.executeUpdate();
	    } catch (SQLException e) {
		logger.error(e.getMessage());
	    }
	    
	} else {
	    try {
		pstmt = connector.prepareStatement(sqlcmd2);
		pstmt.setDouble(1, rating);
		pstmt.setString(2, movieId);
		pstmt.setInt(3, userId);
		pstmt.executeUpdate();
	    } catch (SQLException e) {
		logger.error(e.getMessage());
	    }
	}
    }

    @Override
    public List<MovieReview> getReviewsForMovie(String movieId) {
	ArrayList<MovieReview> result = new ArrayList<>();
	String sqlcmd = "select * from Review where movie_id = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, movieId);
	    myResult = pstmt.executeQuery();
	    while (myResult.next()) {
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
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
	return result;
    }

    @Override
    public List<MovieReview> getReviewForUser(String userId) {
	ArrayList<MovieReview> output = new ArrayList<>();
	String sqlcmd = "select * from Review where reviewer_id = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, userId);
	    myResult = pstmt.executeQuery();
	    while (myResult.next()) {
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
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}

	return output;
    }

    @Override
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
	    if (deletedRow > 0) {
		pstmt2 = connector.prepareStatement(sqlcmdfollowing);
		pstmt2.setInt(1, userId);
		pstmt2.setString(2, listName);
		pstmt2.executeUpdate();
	    }
	} catch (SQLException sl) {
	    logger.error(sl.getMessage());
	}
    }

    @Override
    public void cleanMovieList(int userId, String listName) {
	String sqlcmd = "delete from UserMovieList where user_id = ? and list_name = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, userId);
	    pstmt.setString(2, listName);
	    pstmt.executeUpdate();
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
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
    public void updateUserStatus(int userId, int status) {
	String sqlcmd = "update user set active = ? where user_id = ?";
	PreparedStatement pstmt = null;
	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setInt(1, status);
	    pstmt.setInt(2, userId);
	    pstmt.executeUpdate();
	} catch (SQLException sq) {
	    logger.error(sq.getMessage());
	}

    }

    @Override
    public List<User> getBannedList() {
	ArrayList<User> output = new ArrayList<>();
	String sqlcmd = "select * from user where active = 0";
	PreparedStatement pstmt = null;

	try {
	    pstmt = connector.prepareStatement(sqlcmd);
	    myResult = pstmt.executeQuery();
	    while (myResult.next()) {
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

	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}
	return output;
    }

}
