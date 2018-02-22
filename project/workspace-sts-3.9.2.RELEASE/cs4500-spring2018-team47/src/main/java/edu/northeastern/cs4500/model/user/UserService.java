package edu.northeastern.cs4500.model.user;

public interface UserService {
	/**
	 * Finds a user by email.
	 * @param email email
	 * @return User associated with given email.
	 */
	public User findUserByEmail(String email);
	
	/**
	 * Finds a user by username.
	 * @param username
	 * @return User associated with given username.
	 */
	public User findUserByUsername(String username);
	
	/**
	 * Saves a new User to the repository.
	 * @param user User
	 */
	public void saveUser(User user);
	
	/**
	 * Checks if there is a User with the given email in the repository.
	 * @param email 
	 * @return if User exists.
	 */
	public boolean existsByEmail(String email);
	
	/**
	 * Checks if there is a User with the given username in the repository.
	 * @param username
	 * @return if User exists.
	 */
	public boolean existsByUsername(String username);
}