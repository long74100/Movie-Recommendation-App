package edu.northeastern.cs4500.model.user;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
}