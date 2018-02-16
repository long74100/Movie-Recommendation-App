package edu.northeastern.cs4500.model;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
}