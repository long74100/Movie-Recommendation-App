package edu.northeastern.cs4500.model.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import edu.northeastern.cs4500.model.movie.MovieReview;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.movie.MovieRating;
import edu.northeastern.cs4500.model.movie.MovieReview;
import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.prod.Prod;

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

@Service("localDbConnector")
public class LocalSQLConnectServiceImpl implements ILocalSQLConnectService {

	// the local database URL
	private static String url = "jdbc:mysql://team-47-dev-db.cllrg7hgpqkh.us-east-2.rds.amazonaws.com/"
			+ "cs4500_spring2018_team47_dev";

	// database username
	private static String username = "RuairiMSmillie";

	// database password
	private String password;

	private static Connection connector = null;
	private static ResultSet myResult = null;
	private static final Logger logger = LogManager.getLogger(LocalSQLConnectServiceImpl.class);

	public LocalSQLConnectServiceImpl(@Value("${spring.datasource.password}") String password) {
		this.password = password;
	}

	private void openConToDatabase() {
		try {
			connector = DriverManager.getConnection(url, username, password);
		} catch (SQLException se) {
			logger.error(se.getMessage());
		}
	}

	private void closeConToDatabase() {
		try {
			connector.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public boolean containMovie(String movieId) throws SQLException {
		String sqlcmd = "select * from Movie where movie_id =?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, movieId);
			myResult = pstmt.executeQuery();
			if (myResult.next()) {
				return true;
			}
			closeConToDatabase();
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return false;
	}

	/**
	 * To check if two users have made the friend request to each other
	 * 
	 * @param senderId
	 *            id for the user who might send the friend request
	 * @param receiverId
	 *            id for the user who might receive the friend request
	 * @return true if they sent request to each other.
	 * @throws SQLException
	 */
	private boolean hasMadeRequest(int senderId, int receiverId) throws SQLException {
		String sqlcmd = "select * from userRelation where (senderId = ? and receiverId = ?) or (senderId = ? and receiverId = ?)";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return false;
	}

	@Override
	public void loadMovieIntoLocalDB(Map<String, String> movieObject) throws SQLException {
		String sqlcmd = "insert into Movie values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
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
		try {
			openConToDatabase();
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
		} catch (SQLException sq) {
			logger.error(sq.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

	}

	@Override
	public void insertData(String data, String tableName) throws SQLException {
		String sqlcmd = "insert into ? values ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, tableName);
			pstmt.setString(2, data);
			myResult = pstmt.executeQuery();
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	/**
	 * Movie Operators: This section is for user and system to operate on the movies
	 */

	// ----- user interaction in local database-----

	@Override
	public void sendFriendRequest(int senderId, int receiverId) throws SQLException {
		String sqlcmd = "insert into userRelation values (?, ?, \"onHold\", \"0\", \"0\")";
		PreparedStatement pstmt = null;
		if (!this.hasMadeRequest(senderId, receiverId)) {
			try {
				openConToDatabase();
				pstmt = connector.prepareStatement(sqlcmd);
				pstmt.setInt(1, senderId);
				pstmt.setInt(2, receiverId);
				pstmt.executeUpdate();
			} catch (SQLException se) {
				logger.error(se.getMessage());
			} finally {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connector != null) {
					closeConToDatabase();
				}
			}
		}
	}

	@Override
	public void acceptRequest(int senderId, int receiverId) throws SQLException {
		String sqlcmd = "update userRelation set relationStatus = \"friend\" where senderId = ? and receiverId = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, senderId);
			pstmt.setInt(2, receiverId);
			pstmt.executeUpdate();
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void rejectRequest(int senderId, int receiverId) throws SQLException {
		String sqlcmd = "delete from userRelation where senderId = ? and receiverId = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, senderId);
			pstmt.setInt(2, receiverId);
			pstmt.executeUpdate();
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void blockSender(int senderId, int receiverId) throws SQLException {
		String sqlcmd = "update userRelation set isSenderBlocked = 1, relationStatus = \"senderBlocked\" where senderId = ? and receiverId = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, senderId);
			pstmt.setInt(2, receiverId);
			pstmt.executeUpdate();
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void blockReceiver(int senderId, int receiverId) throws SQLException {
		String sqlcmd = "update userRelation set isReceiverBlocked = 1, relationStatus = \"receiverBlocked\" where senderId = ? and receiverId = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, senderId);
			pstmt.setInt(2, receiverId);
			pstmt.executeUpdate();
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void addReviewToLocalDB(MovieReview mr) throws SQLException {
		String sqlcmd = "insert into Review(movie_id, reviewer_id, reviewer_name, review_date, description) values(?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, mr.getMovie_id());
			pstmt.setString(2, mr.getUser_id());
			pstmt.setString(3, mr.getUsername());
			pstmt.setString(4, mr.getDate());
			pstmt.setString(5, mr.getReview());
			pstmt.executeUpdate();
		} catch (SQLException se) {
			logger.error(se.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

	}

    /**
     * To get the movie list name for all movie lists belonging to given user
     * @param userId the id for user
     * @return the list of movie name 
     */
    public List<String> getMovieListForUser(int userId) throws SQLException {
    	ArrayList<String> movieListNames = new ArrayList<>();
    	PreparedStatement pstmt = null;
    	try {
    		openConToDatabase();
    		String sqlcmd = "select * from Movielist where user_id = ? order by created_date asc";
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
    	finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
    	}
    	
    	return movieListNames;
    }
    
    /**
     * To get movie from user movie list
     * @return list of movie names 
     */
    public ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname) throws SQLException{
    	ArrayList<Movie> result = new ArrayList<>();
    	String sqlcmd = "select Movie.movie_id, Movie.movie_name, Movie.released_date, Movie.plot, Movie.actor, Movie.poster, Movie.movieDBid from Movie join " + 
    			"(select movie_id from UserMovieList where user_id = ? and list_name = ? order by add_date desc) as comp on comp.movie_id = Movie.movie_id";
    	PreparedStatement pstmt = null;
    	try {
    		openConToDatabase();
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
    	}finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
    	return result;
    }
    
    /**
     * To create movie list 
     * @param movieListName the name for the movie list
     */
    @Override
    public void createMovieList(int userid, String movieListName, String date) throws SQLException {
    	String sqlcmd = "select * from Movielist where user_id = ? and list_name = ?";
    	String addListQuery = "insert into Movielist values (?, ?, ?)";
    	PreparedStatement pstmt = null;
    	PreparedStatement pstmt2 = null;
    	try {
    		openConToDatabase();
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userid);
    		pstmt.setString(2, movieListName);
    		myResult = pstmt.executeQuery();
    		if(!myResult.next()) {
    			try {
    				pstmt2 = connector.prepareStatement(addListQuery);
            		pstmt2.setInt(1, userid);
            		pstmt2.setString(2, movieListName);
            		pstmt2.setString(3, date);
            		pstmt2.executeUpdate();
    			}
    			catch(SQLException sq) {
    				logger.error(sq.getMessage());
    			}finally {
    				if(pstmt2 != null) {
    					pstmt2.close();
    				}
    			}
    		}
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
    	}finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
    }
    
    
    /**
     * To add the movie into the movie list with given name
     * @param userId the user that this movie list belongs to
     * @param listName the name of the movie list
     * @param movieId id for movie that will be added to this list
     * @param movieName name for movie that will be added to this list
     */
    @Override
    public void addMovieIntoMovieList(int userId, String listName, String movieId, String movieName, String add_date) throws SQLException {
    	String sqlcmd = "insert into UserMovieList values (?, ?, ?, ?, ?)";
    	PreparedStatement pstmt = null;
    	try {
    		openConToDatabase();
    		pstmt = connector.prepareStatement(sqlcmd);
    		pstmt.setInt(1, userId);
    		pstmt.setString(2, listName);
    		pstmt.setString(3, movieId);
    		pstmt.setString(4, movieName);
    		pstmt.setString(5, add_date);
    		pstmt.executeUpdate();
    	}
    	catch(SQLException sq) {
    		logger.error(sq.getMessage());
    	}finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
    }
    
   
	@Override
	public void preloadMovieList(int userId) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	createMovieList(userId, "Browse History", formatter.format(new Date()));
	    	createMovieList(userId, "Favorites", formatter.format(new Date()));
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public List<User> keywordSearchUser(String username) throws SQLException {
		ArrayList<User> output = new ArrayList<>();
		String sqlcmd = "select * from user where username like \"%\"?\"%\"";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return output;

	}


	

	

	@Override
	public void deleteMovieFromUserMovieList(int userid, String movieList, String movieId) throws SQLException {
		String sqlcmd = "delete from UserMovieList where user_id = ? and (list_name = ? and movie_id = ?)";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userid);
			pstmt.setString(2, movieList);
			pstmt.setString(3, movieId);
			pstmt.executeUpdate();
		} catch (SQLException sq) {
			logger.error(sq.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}


	@Override
	public String getUserRelation(int senderId, int receiverId) throws SQLException {
		StringBuilder status = new StringBuilder();
		String sqlcmd = "select * from userRelation where senderId = ? and receiverId = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

		return status.toString();
	}

	private List<User> getAllFriendsAsSender(int userId) throws SQLException {
		ArrayList<User> output = new ArrayList<>();
		String sqlcmd = "select * from user join "
				+ "(select senderId from userRelation where receiverId = ? and relationStatus = \"friend\") as comp"
				+ " on user.user_id = comp.senderId";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return output;
	}

	private List<User> getAllFriendAsReceiver(int userId) throws SQLException {
		ArrayList<User> output = new ArrayList<>();
		String sqlcmd = "select * from user join "
				+ "(select receiverId from userRelation where senderId = ? and relationStatus = \"friend\") as comp "
				+ "on user.user_id = comp.receiverId";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return output;
	}

	@Override
	public List<User> getAllFriends(int userId) {
		ArrayList<User> output = new ArrayList<>();
		try {
			output.addAll(this.getAllFriendAsReceiver(userId));
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		try {
			output.addAll(this.getAllFriendsAsSender(userId));
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return output;
	}

	@Override
	public List<User> getAllReceivedFriendRequest(int userId) throws SQLException {
		ArrayList<User> output = new ArrayList<>();
		String sqlcmd = "select * from user join "
				+ "(select senderId from userRelation where receiverId = ? and relationStatus = \"onHold\") as comp "
				+ "on user.user_id = comp.senderId";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

		return output;
	}

	@Override
	public List<User> getAllSentFriendRequest(int userId) throws SQLException {
		ArrayList<User> output = new ArrayList<>();
		String sqlcmd = "select * from user join "
				+ "(select receiverId from userRelation where senderId = ? and relationStatus = \"onHold\") as comp "
				+ "on user.user_id = comp.receiverId";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

		return output;
	}

	@Override
	public List<User> getAllFriendRequest(int userId) {
		ArrayList<User> output = new ArrayList<>();
		try {
			output.addAll(this.getAllReceivedFriendRequest(userId));
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		try {
			output.addAll(this.getAllSentFriendRequest(userId));
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return output;
	}

	@Override
	public MovieRating getRating(int userId, String movieId) throws SQLException {
		String sqlcmd = "select * from rating" + " where rating.user_id = ? and rating.movie_id = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setString(2, movieId);
			myResult = pstmt.executeQuery();
			if (myResult.next()) {
				MovieRating rating = new MovieRating();
				rating.setRatingId(myResult.getInt("rating_id"));
				rating.setMovieId(movieId);
				rating.setUserID(userId);
				rating.setRating(myResult.getInt("rating"));
				rating.setDate(myResult.getString("review_date"));
				return rating;
			} else {
				return null;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

		return null;
	}

	@Override
	public void insertRating(MovieRating movieRating) throws SQLException {
		String sqlcmd1 = "insert into rating(movie_id, rating, user_id, review_date) values (?, ?, ?, ?)";
		String sqlcmd2 = "update rating set rating = ? where movie_id = ? and user_id = ?";
		PreparedStatement pstmt = null;

		int userId = movieRating.getUserID();
		String movieId = movieRating.getMovieId();
		double rating = movieRating.getRating();
		String date = movieRating.getDate();

		if (getRating(userId, movieId) == null) {
			try {
				openConToDatabase();
				pstmt = connector.prepareStatement(sqlcmd1);
				pstmt.setString(1, movieId);
				pstmt.setDouble(2, rating);
				pstmt.setInt(3, userId);
				pstmt.setString(4, date);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			} finally {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connector != null) {
					closeConToDatabase();
				}
			}

		} else {
			try {
				openConToDatabase();
				pstmt = connector.prepareStatement(sqlcmd2);
				pstmt.setDouble(1, rating);
				pstmt.setString(2, movieId);
				pstmt.setInt(3, userId);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			} finally {
				if (pstmt != null) {
					pstmt.close();
				}
				if (connector != null) {
					closeConToDatabase();
				}
			}
		}
	}

	@Override
	public void removeRating(int ratingId) throws SQLException {
		String sqlcmd = "delete from rating where rating_id = ?";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, ratingId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

	}

	@Override
	public List<MovieReview> getReviewsForMovie(String movieId) throws SQLException {
		ArrayList<MovieReview> result = new ArrayList<>();
		String sqlcmd = "select * from Review where movie_id = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
				output.setId(reviewId);
				output.setDate(reviewDate);
				output.setMovie_id(movieId);
				output.setReview(description);
				output.setUser_id(userid);
				output.setUsername(username);
				result.add(output);
			}
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return result;
	}

	@Override
	public List<MovieReview> getReviewForUser(String userId) throws SQLException {
		ArrayList<MovieReview> output = new ArrayList<>();
		String sqlcmd = "select * from Review where reviewer_id = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, userId);
			myResult = pstmt.executeQuery();
			while (myResult.next()) {
				MovieReview item = new MovieReview();
				String username = myResult.getString("reviewer_name");
				String movieid = myResult.getString("movie_id");
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

		return output;
	}

	@Override
	public void deleteMovieList(int userId, String listName) throws SQLException {
		String sqlcmd = "delete from Movielist where user_id = ? and list_name = ?";
		String sqlcmdfollowing = "delete from UserMovieList where user_id = ? and list_name = ?";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setString(2, listName);
			int deletedRow = pstmt.executeUpdate();
			if (deletedRow > 0) {
				try {
					pstmt2 = connector.prepareStatement(sqlcmdfollowing);
					pstmt2.setInt(1, userId);
					pstmt2.setString(2, listName);
					pstmt2.executeUpdate();
				} catch (SQLException sl) {
					logger.error(sl.getMessage());
				} finally {
					if (pstmt2 != null) {
						pstmt2.close();
					}
				}
			}
		} catch (SQLException sl) {
			logger.error(sl.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void cleanMovieList(int userId, String listName) throws SQLException {
		String sqlcmd = "delete from UserMovieList where user_id = ? and list_name = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setString(2, listName);
			pstmt.executeUpdate();
		} catch (SQLException sq) {
			logger.error(sq.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void updateUserStatus(int userId, int status) throws SQLException {
		String sqlcmd = "update user set active = ? where user_id = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, status);
			pstmt.setInt(2, userId);
			pstmt.executeUpdate();
		} catch (SQLException sq) {
			logger.error(sq.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

	}

	@Override
	public List<User> getBannedList() throws SQLException {
		ArrayList<User> output = new ArrayList<>();
		String sqlcmd = "select * from user where active = 0";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return output;
	}


	@Override
	public void removeReview(int reviewId) throws SQLException {
		String sqlcmd = "delete from Review where review_id = ?";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, reviewId);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void deleteFriend(int userId, int friendId) throws SQLException {
		String sqlcmd = "delete from userRelation where senderId = ? and receiverId = ?";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, friendId);
			if (pstmt.executeUpdate() == 0) {
				pstmt.clearParameters();
				pstmt.setInt(1, friendId);
				pstmt.setInt(2, userId);
				pstmt.executeUpdate();
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

	}

	@Override
	public User getUser(int userId) throws SQLException {
		String sqlcmd = "select * from user where user_id = ?";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			myResult = pstmt.executeQuery();
			if (myResult.next()) {
				User user = new User();
				user.setId(userId);
				user.setEmail(myResult.getString("email"));
				user.setFirstName(myResult.getString("first_name"));
				user.setLastName(myResult.getString("last_name"));
				user.setActive(myResult.getInt("active"));
				user.setUsername(myResult.getString("username"));
				user.setRole(myResult.getInt("role"));
				return user;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

		return null;
	}
	
	
	/**
	 * *********************************************************
	 * 														   *
	 * 				   FRIEND PROD OPERATOR                    *
	 *														   *
	 * *********************************************************
	 */
	@Override
	public void sendProdToFriend(int senderId, int receiverId, String senderName, 
			String receiverName, String movieId, String movieName, String date, String comment, String movieDBId, String moviePoster) 
					throws SQLException {
		String sqlcmd = "insert into Prod values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, senderId);
			pstmt.setString(2, senderName);
			pstmt.setInt(3, receiverId);
			pstmt.setString(4, receiverName);
			pstmt.setString(5,  movieId);
			pstmt.setString(6, movieName);
			pstmt.setString(7, date);
			pstmt.setString(8, comment);
			pstmt.setString(9, movieDBId);
			pstmt.setString(10, moviePoster);
			pstmt.executeUpdate();
		}
		catch (SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}
	
	@Override
	public List<Prod> extractAllFriendProds(int userId) throws SQLException {
		String sqlcmd = "select * from Prod where receiverId = ? order by sent_date desc";
		PreparedStatement pstmt = null;
		ArrayList<Prod> friendsProds = new ArrayList<>();
		try {
			openConToDatabase();
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
				prod.setMovieDBId(myResult.getString("movieDBId"));
				prod.setMoviePoster(myResult.getString("moviePoster"));
				friendsProds.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		
		return friendsProds;
	}
	
	@Override
	public List<Prod> extractAllSentProds(int userId) throws SQLException {
		String sqlcmd = "select * from Prod where senderId = ? order by sent_date desc";
		PreparedStatement pstmt = null;
		ArrayList<Prod> sentOutProds = new ArrayList<>();
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				prod.setSender(userId);
				prod.setSenderName(myResult.getString("sender_name"));
				prod.setReceiver(myResult.getInt("receiverId"));
				prod.setReceiverName(myResult.getString("receiver_name"));
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				prod.setMovieDBId(myResult.getString("movieDBId"));
				prod.setMoviePoster(myResult.getString("moviePoster"));
				sentOutProds.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		
		return sentOutProds;
	}
	
	@Override
	public List<Prod> extractProdsSentToAFriend(int userId, String friendName) throws SQLException {
		String sqlcmd = "select * from Prod where senderId = ? and receiver_name = ? order by sent_date desc";
		ArrayList<Prod> sentToThisFriend = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setString(2, friendName);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				prod.setSender(userId);
				prod.setSenderName(myResult.getString("sender_name"));
				prod.setReceiver(myResult.getInt("receiverId"));
				prod.setReceiverName(friendName);
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				prod.setMovieDBId(myResult.getString("movieDBId"));
				prod.setMoviePoster(myResult.getString("moviePoster"));
				sentToThisFriend.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		
		return sentToThisFriend;
	}
	
	@Override
	public List<Prod> extractProdsReceivedFromAFriend(int userId, String friendName) throws SQLException {
		String sqlcmd = "select * from Prod where sender_name = ? and receiverId = ? order by sent_date desc";
		ArrayList<Prod> sentToThisFriend = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, friendName);
			pstmt.setInt(2, userId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				prod.setSender(myResult.getInt("receiverId"));
				prod.setSenderName(friendName);
				prod.setReceiver(userId);
				prod.setReceiverName(myResult.getString("receiver_name"));
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				prod.setMovieDBId(myResult.getString("movieDBId"));
				prod.setMoviePoster(myResult.getString("moviePoster"));
				sentToThisFriend.add(prod);
			}
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return sentToThisFriend;
	}
	
	@Override
	public List<Prod> extractAllProds(int userId) throws SQLException {
		ArrayList<Prod> output = new ArrayList<Prod>();
		String sqlcmd = "select * from Prod where senderId = ? or receiverId = ? order by sent_date desc";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, userId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				if(myResult.getInt("senderId") == userId) {
					prod.setSender(userId);
					prod.setSenderName(myResult.getString("sender_name"));
					prod.setReceiver(myResult.getInt("receiverId"));
					prod.setReceiverName(myResult.getString("receiver_name"));
				}
				else {
					prod.setSender(myResult.getInt("senderId"));
					prod.setSenderName(myResult.getString("sender_name"));
					prod.setReceiver(userId);
					prod.setReceiverName(myResult.getString("receiver_name"));
				}
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				prod.setMovieDBId(myResult.getString("movieDBId"));
				prod.setMoviePoster(myResult.getString("moviePoster"));
				output.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		
		return output;
	}
	
	
	@Override
	public List<Prod> extractAllProdsForARecipient(int userId, String friendName) throws SQLException {
		ArrayList<Prod> output = new ArrayList<Prod>();
		String sqlcmd = "select * from Prod where (senderId = ? and receiver_name = ?) or (receiverId = ? and sender_name = ? ) order by sent_date desc";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setString(2, friendName);
			pstmt.setInt(3, userId);
			pstmt.setString(4, friendName);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Prod prod = new Prod();
				if(myResult.getInt("senderId") == userId) {
					prod.setSender(userId);
					prod.setSenderName(myResult.getString("sender_name"));
					prod.setReceiver(myResult.getInt("receiverId"));
					prod.setReceiverName(friendName);
				}
				else {
					prod.setSender(myResult.getInt("senderId"));
					prod.setSenderName(friendName);
					prod.setReceiver(userId);
					prod.setReceiverName(myResult.getString("receiver_name"));
				}
				prod.setMovieId(myResult.getString("movieId"));
				prod.setMovieName(myResult.getString("movieName"));
				prod.setTime(myResult.getString("sent_date"));
				prod.setComment(myResult.getString("senderComment"));
				prod.setMovieDBId(myResult.getString("movieDBId"));
				prod.setMoviePoster(myResult.getString("moviePoster"));
				output.add(prod);
			}
			
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		
		return output;
	}
	
	
	@Override
	public HashMap<String, HashMap<Movie, Double>> getSlopeOneDate() throws SQLException {
		HashMap<String, HashMap<Movie, Double>> output = new HashMap<>();
		String sqlcmd = "select user.username, comp.movie_id, comp.movieDBid, comp.movie_name, comp.poster, comp.rating from user join " + 
				"(select rating.user_id, Movie.movie_id, Movie.movieDBid, Movie.movie_name, Movie.poster, rating.rating from Movie join rating where Movie.movie_id = rating.movie_id) as comp " + 
				"where user.user_id = comp.user_id order by username";
		PreparedStatement pstmt = null;
		String tempUser = "";
		HashMap<Movie, Double> tempMap = new HashMap<>();
		
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				if(myResult.getString("username") == tempUser) {
					Movie movie = new Movie();
					movie.setImdbID(myResult.getString("movie_id"));
					movie.setTheMovieDbID(myResult.getString("movieDBid"));
					movie.setTitle(myResult.getString("movie_name"));
					movie.setPoster(myResult.getString("poster"));
					Double rating = myResult.getDouble("rating");
					tempMap.put(movie, rating);
				}
				else {
					if(!tempUser.isEmpty()) {
						output.put(tempUser, tempMap);
					}
					tempUser = myResult.getString("username");
					tempMap = new HashMap<>();
					Movie movie = new Movie();
					movie.setImdbID(myResult.getString("movie_id"));
					movie.setTheMovieDbID(myResult.getString("movieDBid"));
					movie.setTitle(myResult.getString("movie_name"));
					movie.setPoster(myResult.getString("poster"));
					Double rating = myResult.getDouble("rating");
					tempMap.put(movie, rating);
				}
			}
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
		return output;
	}

	@Override
	public void insertUser(User user) throws SQLException {
		String sqlcmd = "insert into user values(?, ?, ?, ?, ?, ?, ? ,?)";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, user.getId());
			pstmt.setInt(2, user.getActive());
			pstmt.setString(3, user.getEmail());
			pstmt.setString(4, user.getUsername());
			pstmt.setString(5, user.getLastName());
			pstmt.setString(6, user.getFirstName());
			pstmt.setString(7, user.getPassword());
			pstmt.setInt(8, user.getRole());

			pstmt.executeUpdate();

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}

	}

	@Override
	public void removeUser(int userId) throws SQLException {
		String sqlcmd = "delete from user where user_id = ?";
		PreparedStatement pstmt = null;

		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}

	@Override
	public void deleteFromMovieTable(String id) throws SQLException {
		String sqlcmd = "delete from Movie where movie_id = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException ep) {
			logger.error(ep.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
	}
	
	@Override
	public List<Movie> extractMoviesByGenre(String genre) throws SQLException {
		ArrayList<Movie> output = new ArrayList<>();
		String sqlcmd = "select * from Movie where genre like \"%?%\"";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, genre);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				Movie movie = new Movie();
				String imdbId = myResult.getString("movie_id");
				String movieName = myResult.getString("movie_name");
				String movieDBId = myResult.getString("movieDBid");
				String moviePoster = myResult.getString("poster");
				movie.setTitle(movieName);
				movie.setImdbID(imdbId);
				movie.setPoster(moviePoster);
				movie.setTheMovieDbID(movieDBId);
				output.add(movie);
			}
			
		}
		catch(SQLException ep) {
			logger.error(ep.getMessage());
		}finally {
	        if (pstmt != null) {
	        	pstmt.close();
	        }
	        if (connector != null) {
				closeConToDatabase();
			}
		}
		
		return output;
	}
	
	@Override
	public Movie findMovieByImdbId(String imdbId) throws SQLException {
		String sqlcmd = "select * from Movie where movie_id = ?";
		PreparedStatement pstmt = null;
		Movie movie = new Movie();
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, imdbId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				movie.setImdbID(imdbId);
				movie.setTheMovieDbID(myResult.getString("movieDBid"));
				movie.setTitle(myResult.getString("movie_name"));
				movie.setPoster(myResult.getString("poster"));
				Double rating = myResult.getDouble("rating");
				movie.setImdbRating(rating.toString());
			}
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
	        	pstmt.close();
	        }
	        if (connector != null) {
				closeConToDatabase();
			}
		}
		
		return movie;
	}
	
	
	@Override
	public Movie findMovieByMovieDBId(String moviedbId) throws SQLException {
		String sqlcmd = "select * from Movie where movieDBid = ?";
		PreparedStatement pstmt = null;
		Movie movie = new Movie();
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setString(1, moviedbId);
			myResult = pstmt.executeQuery();
			while(myResult.next()) {
				movie.setImdbID(moviedbId);
				movie.setTheMovieDbID(myResult.getString("movieDBid"));
				movie.setTitle(myResult.getString("movie_name"));
				movie.setPoster(myResult.getString("poster"));
				Double rating = myResult.getDouble("rating");
				movie.setImdbRating(rating.toString());
			}
		}
		catch(SQLException sq) {
			logger.error(sq.getMessage());
		}
		finally {
			if (pstmt != null) {
	        	pstmt.close();
	        }
	        if (connector != null) {
				closeConToDatabase();
			}
		}
		return movie;
	}

}
