package edu.northeastern.cs4500.model.services;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.movie.MovieRating;
import edu.northeastern.cs4500.model.movie.MovieReview;
import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.prod.Prod;


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
	void createMovieList(int userid, String movieListName, String date) throws SQLException;

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
	void addMovieIntoMovieList(int userId, String listName, String movieId, String movieName,String date) throws SQLException;

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
	 * @param userId the user Id
	 * @param movieId the movie Id
	 */
	MovieRating getRating(int userId, String movieId) throws SQLException;

	/**
	 * Insert a rating into movie ratings.
	 * @param rating the movie rating
	 */
	void insertRating(MovieRating movieRating) throws SQLException;
	
	/**
	 * Removes a rating.
	 * @param ratingId id of the rating
	 * @throws SQLException
	 */
	void removeRating(int ratingId) throws SQLException;

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
	 * @throws SQLException 
	 */
	User getUser(int userId) throws SQLException;
	
	/**
	 * Inserts a user into the database.
	 * @throws SQLException 
	 */
	void insertUser(User user) throws SQLException;
	
	/**
	 * Removes a user from the database.
	 * @param userId id of the user to remove
	 * @throws SQLException 
	 */
	void removeUser(int userId) throws SQLException;

	/**
	 * Deletes a movie from the database
	 * @param id the id of the movie to remove
	 * @throws SQLException
	 */
	void deleteFromMovieTable(String id) throws SQLException;

	

	/**
	 * To send prod to a friend
	 * @param senderId the prod sender
	 * @param receiverId the prod receiver
	 * @param senderName the name of sender
	 * @param receiverName name of receiver
	 * @param movieId prod movie id
	 * @param movieName prod movie name
	 * @param date sent date
	 * @param comment sender's comment
	 * @param movieDBId the movie id in movie db
	 * @param moviePoster the movie's poster
	 */
	void sendProdToFriend(int senderId, int receiverId, String senderName, 
			String receiverName, String movieId, String movieName, String date, String comment, String movieDBId, String moviePoster) throws SQLException;
	
	/**
	 * To extract all prods from friends
	 * @param userId the user who receives the prods 
	 */
	List<Prod> extractAllFriendProds(int userId) throws SQLException;
	
	/**
	 * To extract all prods sent to friends
	 * @param userId the user who sent the prods
	 */
	List<Prod> extractAllSentProds(int userId) throws SQLException;
	
	/**
	 * To get all prods sent to a friend
	 * @param userId the user who sent the prods
	 * @param friendId the user who received the prods
	 * @return list of prods the user sent to receiver user
	 */
	List<Prod> extractProdsSentToAFriend(int userId, String friendName) throws SQLException;
	
	/**
	 * To get all the prods received from a user
	 * @param userId the user who received the prod
	 * @param friendId the user who sent the prod
	 * @return
	 */
	List<Prod> extractProdsReceivedFromAFriend(int userId, String friendName) throws SQLException;
	
	/**
	 * To extract all prods for the user with given user id
	 * @param userId the user that all prods belongs to
	 * @return list of all prods
	 */
	List<Prod> extractAllProds(int userId) throws SQLException;
	
	/**
	 * To extract all prods towards a specific friend with given friend Id
	 * @param userId the prod list own
	 * @param friendName friend of prod list owner
	 * @return
	 */
	List<Prod> extractAllProdsForARecipient(int userId, String friendName) throws SQLException;
	
	/**
	 * Get all users with all the movie they already rated
	 * output format is: Map<UserName, Map<Movie, rating>> 
	 * @return map containing all users as well as corresponding movie they rated
	 */

	Map<String, Map<Movie, Double>> getSlopeOneData() throws SQLException;
	/**
	 * To get all movies based on the given genre
	 * @param genre movie genre
	 * @return all movies matching the given genre
	 * @throws SQLException
	 */
	List<Movie> extractMoviesByGenre(String genre) throws SQLException;
	
	/**
	 * Get movie by imdb id
	 * @param imdbId imdb id for the movie
	 * @return movie object which has the given imdb id
	 * @throws SQLException
	 */
	Movie findMovieByImdbId(String imdbId) throws SQLException;
	
	/**
	 * Get movie by MovieDBId
	 * @param moviedbId the moviedbId for the movie
	 * @return movie object which hsa the given moviedb id
	 * @throws SQLException
	 */
	Movie findMovieByMovieDBId(String moviedbId) throws SQLException;
	
	/**
	 * To delete the prod record in user's prod list, but will not the same prod in other user side.
	 * @param senderId the user who sent out this prod
	 * @param receiverId the user who received this prod
	 * @param movieId the movie inside this prod
	 * @param byWhom who deletes it, can be one of "sender" and "receiver"
	 * @throws SQLException
	 */
	void deleteProdOnOneSide(int senderId, int receiverId, String movieId, String byWhom) throws SQLException;
	
	/**
	 * to delete the prod on both sender side and both receiver side.
	 * @param senderId the prod sender
	 * @param receiverId the prod receiver
	 * @param movieId the prod movie
	 * @throws SQLException
	 */
	void deleteProdOnBothSide(int senderId, int receiverId, String movieId) throws SQLException;
	
	
	/**
	 * To get the prod delete status 
	 * @param senderId prod sender
	 * @param receiverId prod receiver
	 * @param movieId prod movie
	 * @param byWhom the user who will be checked on the delete prod status
	 * @return delete status 0 (No) or 1 (Yes)
	 * @throws SQLException
	 */
	boolean getProdDeleteStatus(int senderId, int receiverId, String movieId, String byWhom) throws SQLException;
	
	/**
	 * To update the movie community rating for movies
	 * @param movieId the movie that is rated
	 */
	Integer getCommnunityMovieRating(String movieId) throws SQLException;
}

