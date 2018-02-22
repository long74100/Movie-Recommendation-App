package edu.northeastern.cs4500.model.services;

import edu.northeastern.cs4500.model.User;

public interface UserService {
	public User findUserByEmail(String email);
	public void saveUser(User user);
}