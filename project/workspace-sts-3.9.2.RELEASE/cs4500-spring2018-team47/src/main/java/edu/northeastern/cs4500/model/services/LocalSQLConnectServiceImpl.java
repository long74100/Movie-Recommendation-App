package edu.northeastern.cs4500.model.services;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

@Service("localDbConnector")
public class LocalSQLConnectServiceImpl implements ILocalSQLConnectService {
	
    // the local database URL
    private static String url = "jdbc:mysql://team-47-dev-db.cllrg7hgpqkh.us-east-2.rds.amazonaws.com/"
	    + "cs4500_spring2018_team47_dev";
    
    // database username
    private static String username = "RuairiMSmillie";
    
    // database password
    @Value("${spring.datasource.password}")
    private String password;

    private static Connection connector = null;
    private static ResultSet myResult = null;
    private static final Logger logger = LogManager.getLogger(LocalSQLConnectServiceImpl.class);
    
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
	public void loadMovieIntoLocalDB(Map<String, String> movieObject) {
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
		} catch (Exception ep) {
			logger.error(ep.getMessage());
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
		String sqlcmd = "update userRelation set isSenderBlocked = 1 where senderId = ? and receiverId = ?";
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
		String sqlcmd = "update userRelation set isReceiverBlocked = 1 where senderId = ? and receiverId = ?";
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

	@Override
	public void preloadMovieList(int userId) {
		try {
			createMovieList(userId, "Favorites");
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
	public List<String> getMovieListForUser(int userId) throws SQLException {
		ArrayList<String> movieListNames = new ArrayList<>();
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			String sqlcmd = "select * from Movielist where user_id = ?";
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			myResult = pstmt.executeQuery();
			while (myResult.next()) {
				String listName = myResult.getString("list_name");
				movieListNames.add(listName);
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

		return movieListNames;
	}

	@Override
	public ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname) throws SQLException {
		ArrayList<Movie> result = new ArrayList<>();
		String sqlcmd = "select Movie.movie_id, Movie.movie_name, Movie.released_date, Movie.plot, Movie.actor, Movie.poster, Movie.movieDBid from Movie join "
				+ "(select movie_id from UserMovieList where user_id = ? and list_name = ?) as comp on comp.movie_id = Movie.movie_id";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
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
	public void createMovieList(int userid, String movieListName) throws SQLException {
		String sqlcmd = "select * from Movielist where user_id = ? and list_name = ?";
		String addListQuery = "insert into Movielist values (?, ?)";
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		try {
			openConToDatabase();
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
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (pstmt2 != null) {
				pstmt2.close();
			}
			if (connector != null) {
				closeConToDatabase();
			}
		}
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
	public void addMovieIntoMovieList(int userId, String listName, String movieId, String movieName)
			throws SQLException {
		String sqlcmd = "insert into UserMovieList values (?, ?, ?, ?)";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setString(2, listName);
			pstmt.setString(3, movieId);
			pstmt.setString(4, movieName);
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
	public int getRating(int userId, String movieId) throws SQLException {
		String sqlcmd = "select rating from rating" + " where rating.user_id = ? and rating.movie_id = ?";
		PreparedStatement pstmt = null;
		try {
			openConToDatabase();
			pstmt = connector.prepareStatement(sqlcmd);
			pstmt.setInt(1, userId);
			pstmt.setString(2, movieId);
			myResult = pstmt.executeQuery();
			if (myResult.next()) {
				return myResult.getInt("rating");
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

		return -1;
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

		if (getRating(userId, movieId) == -1) {
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
				pstmt2 = connector.prepareStatement(sqlcmdfollowing);
				pstmt2.setInt(1, userId);
				pstmt2.setString(2, listName);
				pstmt2.executeUpdate();
			}
		} catch (SQLException sl) {
			logger.error(sl.getMessage());
		} finally {
			if (pstmt != null) {
				pstmt.close();
			}
			if (pstmt2 != null) {
				pstmt2.close();
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
	public void deleteFromMovieTable(String id) throws SQLException{
	String sqlcmd = "delete from Movie where movie_id = ?";
	PreparedStatement pstmt = null;
	try {
		openConToDatabase();
	    pstmt = connector.prepareStatement(sqlcmd);
	    pstmt.setString(1, id);
	    pstmt.executeUpdate();
	} catch (SQLException ep) {
	    logger.error(ep.getMessage());
	}finally {
        if (pstmt != null) {
        	pstmt.close();
        }
        if (connector != null) {
			closeConToDatabase();
		}
	}
    }

}
