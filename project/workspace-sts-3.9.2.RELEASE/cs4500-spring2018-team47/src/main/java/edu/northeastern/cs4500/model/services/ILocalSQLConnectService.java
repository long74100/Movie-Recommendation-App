package edu.northeastern.cs4500.model.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

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
	 */
	boolean containMovie(String movieId);

	/**
	 * To insert the data into the given table.
	 * 
	 * @param data the data will be inserted
	 * @param tableName the destination table that the data will be inserted to
	 */
	void insertData(String data, String tableName);

	/**
	 * To delete the given movie from the local database
	 * 
	 * @param id the given movie id
	 */
	void deleteFromMovieTable(String id);

	/**
	 * Note: this is more for testing only, use it carefully. To clear the table
	 * with given tableName.
	 * 
	 * @param tableName the given table that will be cleaned up
	 */
	void clearTable(String tableName);

	/**
	 * To search movie based on given keyword -- first start with the movie title.
	 * 
	 * @param input the given searched string used for movie title
	 */
	void searchKeyWordMovieTitle(String input);

	/**
	 * To search movies which the actor with given name.
	 * 
	 * @param actorsName the name of actors
	 */
	void searchKeyWordActorsName(String actorsName);

	/**
	 * To search movies which the director with given name director.
	 * 
	 * @param directorName the name of director
	 */
	void searchKeyWordDirectorName(String directorName);

	/**
	 * To search movies with given genre.
	 * 
	 * @param genre the movie genre
	 */
	void searchKeyWordGenre(String genre);

	/**
	 * To search movies with given year of publication.
	 * 
	 * @param year the publication year period
	 */
	void searchKeyWordTime(String year);

	/**
	 * Search movie by keyword
	 * 
	 * @param keyword only
	 */
	void searchByKeyWordInOne(String keyword);

	/**
	 * To send friend request to receiver if there is non shown in relation list.
	 * 
	 * @param senderId the id for sender
	 * @param receiverId the id for receiver
	 */
	void sendFriendRequest(int senderId, int receiverId);

	/**
	 * To accept the friend request from sender to receiver.
	 * 
	 * @param senderId sender who sent out the friend request
	 * @param receiverId receiver who received the friend request
	 */
	void acceptRequest(int senderId, int receiverId);

	/**
	 * The receiver to reject the sender's friend request.
	 * 
	 * @param senderId the user to send friend request
	 * @param receiverId the user to receive friend request
	 */
	void rejectRequest(int senderId, int receiverId);
	
	
	/**
	 * Removes a friend relationship.
	 * @param userId the user id
	 * @param friendId the user's friend's id
	 */
	void deleteFriend(int userId, int friendId);

	/**
	 * receiver to block the sender.
	 * 
	 * @param senderId user who sent the friend request
	 * @param receiverId user who receives the friend request
	 */
	void blockSender(int senderId, int receiverId);

	/**
	 * receiver to block the sender.
	 * 
	 * @param senderId user who sent the friend request
	 * @param receiverId user who receives the friend request
	 * 
	 */
	void blockReceiver(int senderId, int receiverId);

	/**
	 * To add the review into local database.
	 * 
	 * @param mr Movie review for a movie
	 * 
	 */
	void addReviewToLocalDB(MovieReview mr);
	
	/**
	 * Remove a review from the database.
	 */
	void removeReview(int reviewId);

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
	List<User> keywordSearchUser(String username);

	/**
	 * To return all movies stored in user's movie list with the given user id.
	 * 
	 * @param userId user who owns the movie list
	 * @return list of movies
	 */
	HashMap<String, ArrayList<Movie>> getMovieFromMovieList(int userId);

	/**
	 * To get the movie list name for all movie lists belonging to given user.
	 * 
	 * @param userId the id for user
	 * @return the list of movie name
	 */
	List<String> getMovieListForUser(int userId);

	/**
	 * To get movie from user movie list.
	 * 
	 * @param userId id of the user
	 * @param listname the name of the list
	 * @return list of movie names
	 */
	ArrayList<Movie> getMovieFromUserMovieList(int userId, String listname);

	/**
	 * To create movie list.
	 * 
	 * @param movieListName the name for the movie list
	 */
	void createMovieList(int userid, String movieListName);

	/**
	 * To delete a movie from user's movie list.
	 * 
	 * @param userid the user who owns movie list
	 * @param movieList the movie list that will be deleted from
	 * @param movieId movie that will be deleted
	 */
	void deleteMovieFromUserMovieList(int userid, String movieList, String movieId);

	/**
	 * To add the movie into the movie list with given name.
	 * 
	 * @param userId the user that this movie list belongs to
	 * @param listName the name of the movie list
	 * @param movieId id for movie that will be added to this list
	 * @param movieName name for movie that will be added to this list
	 */
	void addMovieIntoMovieList(int userId, String listName, String movieId, String movieName);

	/**
	 * To get the status of the users relationship.
	 * 
	 * @param senderId the user who sent the request
	 * @param receiverId the user who is sent the request
	 * @return the relationship one of the following: "friend", "onHold"
	 */
	String getUserRelation(int senderId, int receiverId);

	/**
	 * To get all friend's userId as they are the request sender.
	 * 
	 * @param userId current user's id
	 * @return list of friend id
	 */
	List<User> getAllFriendsAsSender(int userId);

	/**
	 * To get all friend's userId as they are the request receiver.
	 * 
	 * @param userId current user's id
	 * @return list of friend Id
	 */
	List<User> getAllFriendAsReceiver(int userId);

	/**
	 * To get all friends.
	 * 
	 * @param userId the current user's id
	 * @return list of username
	 */
	List<User> getAllFriends(int userId);

	/**
	 * To get all friend requests from other users.
	 * 
	 * @param userId the current user's id
	 * @return list of username who sent the friend request
	 */
	List<User> getAllReceivedFriendRequest(int userId);

	/**
	 * To get all friend requests this user sent out.
	 * 
	 * @param userId the current user's id
	 * @return list of username who sent the friend request
	 */
	List<User> getAllSentFriendRequest(int userId);

	/**
	 * To return the total number of friend request.
	 * 
	 * @param userId the current user's id
	 * @return the total number of friend request
	 */
	List<User> getAllFriendRequest(int userId);

	/**
	 * Get a rating from movie ratings.
	 * 
	 * @param userId the user Id
	 * @param movieId the movie Id
	 */
	int getRating(int userId, String movieId);
	

	/**
	 * Insert a rating into movie ratings.
	 * @param rating the movie rating
	 */
	public void insertRating(MovieRating movieRating);

	/**
	 * To get all the reviews for the movie with given movieId.
	 * 
	 * @param movieId the movie that will be checked the reviews
	 * @return list of movie reviews
	 */
	List<MovieReview> getReviewsForMovie(String movieId);

	/**
	 * To get review written by given user.
	 * 
	 * @param userId the user's id
	 * @return the list of movie
	 */
	List<MovieReview> getReviewForUser(String userId);

	/**
	 * To delete the movie list also delete all the stored movies records.
	 * 
	 * @param userId user who owns the given list
	 * @param listName name of the movie list
	 */
	void deleteMovieList(int userId, String listName);

	/**
	 * To clean the movie list with given list name.
	 * 
	 * @param userId the movie list's owner
	 * @param listName the name of movie list
	 */
	void cleanMovieList(int userId, String listName);
	
	
	/**
	 * Add movie into the local Movie database.
	 * @param movie the movie information.
	 */
	void loadMovieIntoLocalDB(Map<String, String> movie);
	
	/**
	 * Update a user's active status.
	 * @param userId for user to update and status to set
	 */
	 void updateUserStatus(int userId, int status);
	
	
	/**
	 * Returns a list of banned users.
	 * @return banned users
	 */
	List<User> getBannedList();
	
	/**
	 * Gets the user with the given user id.
	 * @param userId the user's id
	 * 
	 * @return the user with the corresponding id
	 */
	User getUser(int userId);

	
}

