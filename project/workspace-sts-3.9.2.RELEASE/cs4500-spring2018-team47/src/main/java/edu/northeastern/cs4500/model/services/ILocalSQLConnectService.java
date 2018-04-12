package edu.northeastern.cs4500.model.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.movie.MovieRating;
import edu.northeastern.cs4500.model.movie.MovieReview;
import edu.northeastern.cs4500.model.user.User;

public interface ILocalSQLConnectService {

	/**
	 * To check if the given movie Id already exists in the database.
	 * 
	 * @param movieId the movieId of checked movie
	 * @return true if given movie exists in local database, else return false
	 * @throws SQLException 
	 */
	boolean containMovie(String movieId) throws SQLException;

	/**
	 * To insert the data into the given table.
	 * 
	 * @param data the data will be inserted
	 * @param tableName the destination table that the data will be inserted to
	 * @throws SQLException 
	 */
	void insertData(String data, String tableName) throws SQLException;

	/**
	 * To send friend request to receiver if there is non shown in relation list.
	 * 
	 * @param senderId the id for sender
	 * @param receiverId the id for receiver
	 * @throws SQLException 
	 */
	void sendFriendRequest(int senderId, int receiverId) throws SQLException;

	/**
	 * To accept the friend request from sender to receiver.
	 * 
	 * @param senderId sender who sent out the friend request
	 * @param receiverId receiver who received the friend request
	 */
	void acceptRequest(int senderId, int receiverId) throws SQLException;

	/**
	 * The receiver to reject the sender's friend request.
	 * 
	 * @param senderId the user to send friend request
	 * @param receiverId the user to receive friend request
	 */
	void rejectRequest(int senderId, int receiverId) throws SQLException;
	
	
	/**
	 * Removes a friend relationship.
	 * @param userId the user id
	 * @param friendId the user's friend's id
	 * @throws SQLException 
	 */
	void deleteFriend(int userId, int friendId) throws SQLException;

	/**
	 * receiver to block the sender.
	 * 
	 * @param senderId user who sent the friend request
	 * @param receiverId user who receives the friend request
	 */
	void blockSender(int senderId, int receiverId) throws SQLException;

	/**
	 * receiver to block the sender.
	 * 
	 * @param senderId user who sent the friend request
	 * @param receiverId user who receives the friend request
	 * 
	 */
	void blockReceiver(int senderId, int receiverId) throws SQLException;

	/**
	 * To add the review into local database.
	 * 
	 * @param mr Movie review for a movie
	 * 
	 */
	void addReviewToLocalDB(MovieReview mr) throws SQLException;
	
	/**
	 * Remove a review from the database.
	 * @throws SQLException 
	 */
	void removeReview(int reviewId) throws SQLException;

	/**
	 * To set up initial movie list for the new user.
	 * 
	 * @param userId the new user's id
	 */
	void preloadMovieList(int userId);

	/**
	 * To get list of users that match the given search name
	 * 
	 * @param username the user name that will be used as search keyword.
	 * @return list of users
	 */

	List<User> keywordSearchUser(String username) throws SQLException;


	/**
	 * To get the movie list name for all movie lists belonging to given user.
	 * 
	 * @param userId the id for user
	 * @return the list of movie name
	 */
	List<String> getMovieListForUser(int userId) throws SQLException;

	/**
	 * To get movie from user movie list.
	 * 
	 * @param userId id of the user
	 * @param listname the name of the list
	 * @return list of movie names
	 */
	ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname) throws SQLException;

	/**
	 * To create movie list.
	 * 
	 * @param movieListName the name for the movie list
	 * @throws SQLException 
	 */
	void createMovieList(int userid, String movieListName) throws SQLException;

	/**
	 * To delete a movie from user's movie list.
	 * 
	 * @param userid the user who owns movie list
	 * @param movieList the movie list that will be deleted from
	 * @param movieId movie that will be deleted
	 */
	void deleteMovieFromUserMovieList(int userid, String movieList, String movieId) throws SQLException;

	/**
	 * To add the movie into the movie list with given name.
	 * 
	 * @param userId the user that this movie list belongs to
	 * @param listName the name of the movie list
	 * @param movieId id for movie that will be added to this list
	 * @param movieName name for movie that will be added to this list
	 */
	void addMovieIntoMovieList(int userId, String listName, String movieId, String movieName) throws SQLException;

	/**
	 * To get the status of the users relationship.
	 * 
	 * @param senderId the user who sent the request
	 * @param receiverId the user who is sent the request
	 * @return the relationship one of the following: "friend", "onHold"
	 */
	String getUserRelation(int senderId, int receiverId) throws SQLException;

	/**

	 * To get all friends.
	 * 
	 * @param userId the current user's id
	 * @return list of username
	 */
	List<User> getAllFriends(int userId) throws SQLException;

	/**
	 * To get all friend requests from other users.
	 * 
	 * @param userId the current user's id
	 * @return list of username who sent the friend request
	 */
	List<User> getAllReceivedFriendRequest(int userId) throws SQLException;

	/**
	 * To get all friend requests this user sent out.
	 * 
	 * @param userId the current user's id
	 * @return list of username who sent the friend request
	 */
	List<User> getAllSentFriendRequest(int userId) throws SQLException;

	/**
	 * To return the total number of friend request.
	 * 
	 * @param userId the current user's id
	 * @return the total number of friend request
	 */
	List<User> getAllFriendRequest(int userId) throws SQLException;

	/**
	 * Get a rating from movie ratings.
	 * 
	 * @param userId the user Id
	 * @param movieId the movie Id
	 */
	int getRating(int userId, String movieId) throws SQLException;
	

	/**
	 * Insert a rating into movie ratings.
	 * @param rating the movie rating
	 */
	public void insertRating(MovieRating movieRating) throws SQLException;

	/**
	 * To get all the reviews for the movie with given movieId.
	 * 
	 * @param movieId the movie that will be checked the reviews
	 * @return list of movie reviews
	 */
	List<MovieReview> getReviewsForMovie(String movieId) throws SQLException;

	/**
	 * To get review written by given user.
	 * 
	 * @param userId the user's id
	 * @return the list of movie
	 */
	List<MovieReview> getReviewForUser(String userId) throws SQLException;

	/**
	 * To delete the movie list also delete all the stored movies records.
	 * 
	 * @param userId user who owns the given list
	 * @param listName name of the movie list
	 */
	void deleteMovieList(int userId, String listName) throws SQLException;

	/**
	 * To clean the movie list with given list name.
	 * 
	 * @param userId the movie list's owner
	 * @param listName the name of movie list
	 */
	void cleanMovieList(int userId, String listName) throws SQLException;
	
	
	/**
	 * Add movie into the local Movie database.
	 * @param movie the movie information.
	 */
	void loadMovieIntoLocalDB(Map<String, String> movie) throws SQLException;
	
	/**
	 * Update a user's active status.
	 * @param userId for user to update and status to set
	 */
	 void updateUserStatus(int userId, int status) throws SQLException;
	
	
	/**
	 * Returns a list of banned users.
	 * @return banned users
	 * @throws SQLException 
	 */
	List<User> getBannedList() throws SQLException;

	/**
	 * Gets the user with the given user id.
	 * @param userId the user id
	 * @return the user with the given id
	 */
	User getUser(int userId);
	
	/**
	 * Inserts a user into the database.
	 */
	void insertUser(User user);
	
	/**
	 * Removes a user from the database.
	 * @param userId id of the user to remove
	 */
	void removeUser(int userId);

	/**
	 * Deletes a movie from the database
	 * @param id the id of the movie to remove
	 * @throws SQLException
	 */
	void deleteFromMovieTable(String id) throws SQLException;

	
}

