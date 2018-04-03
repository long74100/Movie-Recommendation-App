package edu.northeastern.cs4500.model.services;

import java.io.IOException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import edu.northeastern.cs4500.model.movie.MovieReview;

import edu.northeastern.cs4500.model.movie.Movie;
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
	// this is the movie information fields for online movie database
	private final ArrayList<String> oldbfields = new ArrayList<>(
			Arrays.asList("imdbID", "Title", "Rated", "Runtime", "Year", "Released", "Genre", "Director", "Actors",
					"Plot", "Language", "Country", "Awards", "Poster", "Metascore", "imdbRating", "imdbVotes"));

	// this is the movie information fields for local movie database
	private final ArrayList<String> lcdbfields = new ArrayList<>(Arrays.asList("movie_id", "movie_name", "movie_rated",
			"runtime", "movie_year", "released_date", "genre", "director", "actor", "plot", "movie_language", "country",
			"awards", "poster", "metascore", "imdbRating", "imdbvotes"));

	// container for matching <local movie field> -> <online movie information>
	private HashMap<String, String> fieldToValue;

	/**
	 * The constructor The constructor will automatically create connection to local
	 * database
	 */
	public LocalSQLConnectServiceImpl() {
		this.fieldToValue = new HashMap<>();
		try {
			connector = DriverManager.getConnection(url, username, password);
			connectStatement = connector.createStatement();
		} catch (SQLException se) {
			logger.error(se.getMessage());
		}

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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#loadMovieToLocalDB(org.json.JSONObject)
	 */
	@Override
	public void loadMovieToLocalDB(JSONObject movieJSON) {
		catchMovie(movieJSON);
		if (!this.fieldToValue.isEmpty()) {
			try {
				String currentMovieId = this.fieldToValue.get("movie_id");
				if (!containMovie(currentMovieId)) {
					String expression = "";
					for (int i = 0; i < lcdbfields.size(); i++) {
						if (i == lcdbfields.size() - 1) {
							expression += "\"" + fieldToValue.get(lcdbfields.get(i)) + "\"";
						} else {
							expression += "\"" + fieldToValue.get(lcdbfields.get(i)) + "\", ";
						}
					}
					String result = "(" + expression + ", ratings" + ", votes" + ")";
					insertData(result, "Movie");
				}
			} catch (NullPointerException nl) {
				logger.error(nl.getMessage());
			}

		}
	}

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#containMovie(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#addMultiMovies(java.util.ArrayList)
	 */
	@Override
	public void addMultiMovies(ArrayList<String> ids) {
		IMovieDBService obj = new MovieDBServiceImpl();
		for (int i = 0; i < ids.size(); i++) {
			int item = Integer.valueOf(ids.get(i));
			try {
				try {
					JSONObject current = obj.searchMovieDetails(item);
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
	 * To check if two users have made the friend request to each other
	 * 
	 * @param senderId
	 *            id for the user who might send the friend request
	 * @param receiverId
	 *            id for the user who might receive the friend request
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#insertData(java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#deleteFromMovieTable(java.lang.String)
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#clearTable(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#searchKeyWordMovieTitle(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#searchKeyWordActorsName(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#searchKeyWordDirectorName(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#searchKeyWordGenre(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#searchKeyWordTime(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#searchByKeyWordInOne(java.lang.String)
	 */
	@Override
	public void searchByKeyWordInOne(String keyword) {
		String sqlcmd = "select * from Movie where movie_id like \"%\"?\"%\"" + " or movie_name like \"%\"?\"%\""
				+ " or movie_rated like \"%\"?\"%\"" + " or runtime like \"%\"?\"%\""
				+ " or movie_year like \"%\"?\"%\"" + " or release_date like \"%\"?\"%\"" + " or genre like \"%\"?\"%\""
				+ " or director like \"%\"?\"%\"" + " or actor like \"%\"?\"%\"" + " or plot like \"%\"?\"%\""
				+ " or movie_language like \"%\"?\"%\"" + " or country like \"%\"?\"%\""
				+ " or metascore like \"%\"?\"%\"" + " or imdbRating like \"%\"?\"%\"" + " or ratings like \"%\"?\"%\""
				+ " or production like \"%\"?\"%\"";
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
				output.append(movieId + "| " + movieName + "| " + movieRated + "| " + runtime + "| " + genre + "| "
						+ released_date + "| " + director + "| " + actors + "| " + plot + "| " + language + "| "
						+ country + "| " + poster + "| " + imdbRating + "| " + ratings);
				movie.add(output.toString());
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getSearchMovieResult()
	 */
	@Override
	public ArrayList<String> getSearchMovieResult() {
		return this.movie;
	}

	// ----- user interaction in local database-----

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#sendFriendRequest(int, int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#acceptRequest(int, int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#rejectRequest(int, int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#blockSender(int, int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#blockReceiver(int, int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#addReviewToLocalDB(edu.northeastern.cs4500.model.movie.MovieReview)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#preloadMovieList(int)
	 */
	@Override
	public void preloadMovieList(int userId) {
		createMovieList(userId, "Favorites");
	}

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#keywordSearchUser(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getMovieFromMovieList(int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getMovieListForUser(int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getMovieFromUserMovieList(int, java.lang.String)
	 */
	@Override
	public ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname) {
		ArrayList<Movie> result = new ArrayList<>();
		String sqlcmd = "select Movie.movie_id, Movie.movie_name, Movie.plot, Movie.actor, Movie.poster from Movie join "
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
				element.setImdbID(movieId);
				element.setTitle(movieName);
				element.setActors(movieActor);
				element.setPlot(moviePlot);
				element.setPoster(moviePoster);
				// add to final result.
				result.add(element);
			}
		} catch (SQLException sq) {
			logger.error(sq.getMessage());
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#createMovieList(int, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#deleteMovieFromUserMovieList(int, java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#addMovieIntoMovieList(int, java.lang.String, java.lang.String, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getUserRelation(int, int)
	 */
	@Override
	public String getUserRelation(int senderId, int receiverId) {
		StringBuilder status = new StringBuilder();
		String sqlcmd = "select * from userRelation where senderId = ? and receiverId ?";
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getAllFriendsAsSender(int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getAllFriendAsReceiver(int)
	 */
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
				Integer friendUserId = myResult.getInt("senderId");
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getAllFriends(int)
	 */
	@Override
	public List<User> getAllFriends(int userId) {
		ArrayList<User> output = new ArrayList<>();
		output.addAll(this.getAllFriendAsReceiver(userId));
		output.addAll(this.getAllFriendsAsSender(userId));
		return output;
	}

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getAllReceivedFriendRequest(int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getAllSentFriendRequest(int)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getAllFriendRequest(int)
	 */
	@Override
	public List<User> getAllFriendRequest(int userId) {
		ArrayList<User> output = new ArrayList<>();
		output.addAll(this.getAllReceivedFriendRequest(userId));
		output.addAll(this.getAllSentFriendRequest(userId));

		return output;
	}

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getRating(int, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getReviewsForMovie(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#getReviewForUser(java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#deleteMovieList(int, java.lang.String)
	 */
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

	/* (non-Javadoc)
	 * @see edu.northeastern.cs4500.model.services.ILocalSQLConnectService#cleanMovieList(int, java.lang.String)
	 */
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
			sq.printStackTrace();
		}
	}

}
