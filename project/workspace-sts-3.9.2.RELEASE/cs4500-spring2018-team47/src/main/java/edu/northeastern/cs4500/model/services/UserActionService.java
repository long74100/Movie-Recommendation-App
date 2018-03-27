package edu.northeastern.cs4500.model.services;

/**
 * The actions between users
 * @author lgj81
 *
 */
public class UserActionService {
	
	// The connector to local database
	private final LocalSQLConnectService connector = new LocalSQLConnectService();
	
	/**
	 * Default constructor.
	 */
	public UserActionService() {
		
	}
	
	/**
	 * To add the movie into user movie list
	 * 
	 * @param userId id for the user
	 * @param listName movie list name
	 * @param movieId id for the movie that is added into the movieList
	 * @param movieName name for the movie that is added into the movieList
	 */
	public void addMovieIntoUserMovieList(int userId, String listName, String movieId, String movieName) {
		String values = "(" + userId + ", \"" + listName + "\", \"" + movieId + "\", \"" + movieName + "\")";
		this.connector.insertData(values, "UserMovieList");
	}

	/**
	 * Receiver to block the sender
	 * @param senderId user who sent the friend request
	 * @param receiverId user who received the friend request
	 */
	public void blockSender(int senderId, int receiverId) {
		this.connector.blockSender(senderId, receiverId);
	}
	
	/**
	 * Sender to block the receiver
	 * @param senderId the user who sent the friend request
	 * @param receiverId the user who received the friend request
	 */
	public void blockReceiver(int senderId, int receiverId) {
		this.connector.blockReceiver(senderId, receiverId);
	}

	/**
	 * To send friend request from sender to receiver
	 * 
	 * @param senderId id for sender
	 * @param receiverId id for receiver
	 */
	public void sendFriendRequest(int senderId, int receiverId) {
		this.connector.sendFriendRequest(senderId, receiverId);
	}

	/**
	 * To accept the request from sender
	 * 
	 * @param senderId id for sender
	 * @param receiverId id for receiver
	 */
	public void acceptFriendRequest(int senderId, int receiverId) {
		this.connector.acceptRequest(senderId, receiverId);
	}

	/**
	 * receiver to reject the sender's friend request
	 * 
	 * @param senderId user to send out friend request
	 * @param receiverId user to receive friend request
	 */
	public void rejectFriendRequest(int senderId, int receiverId) {
		this.connector.rejectRequest(senderId, receiverId);
	}
		

}
