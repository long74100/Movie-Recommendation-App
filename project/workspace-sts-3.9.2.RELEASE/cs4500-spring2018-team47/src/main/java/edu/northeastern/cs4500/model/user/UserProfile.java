package edu.northeastern.cs4500.model.user;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.northeastern.cs4500.prod.Prod;

public class UserProfile {
	// userid
	private Integer userId;
	// username 
	private String userName;
	// the friend list contains the friend's userId and friend username
	private HashMap<Integer, String> friendList;
	// the prod list contains the Prod object
	private ArrayList<Prod> prodList;
	// the movieList of the current user： movie list contains a name and list of movies id
	private HashMap<String, ArrayList<String>> movieList;
	// the friend request list
	private HashMap<Integer, String> friendRequests;
	
	private static final Logger logger = LogManager.getLogger(UserProfile.class);
	
	
	/**
	 * Constructor
	 */
	public UserProfile(int userid) {
		this.userId = userid;
	}
	/**
	 * Get user name
	 * @return username of this user
	 */
	public String getUserName() {
		return this.userName;
	}
	
	/**
	 * Get friend list of this user
	 * @return friend list
	 */
	public HashMap<Integer, String> getFriendList() {
		return this.friendList;
	}
	
	/**
	 * To get the prod list
	 * @return prod list
	 */
	public ArrayList<Prod> getProdsList() {
		return this.prodList;
	}
	
	/**
	 * To get the movies inside the list with given list name
	 * @param listName name for the movie list
	 * @return list of movies belonging to movielist with given name
	 */
	public ArrayList<String> getMovieList(String listName) {
		ArrayList<String> con = new ArrayList<>();
		try {
			con = this.movieList.get(listName);
		}
		catch(NullPointerException ep) {
			logger.error(ep.getMessage());
		}
		return con;
	}
	
}
