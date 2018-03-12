package edu.northeastern.cs4500.model.session;

public interface SessionService {
	boolean isAuthenticated();
	String getCurrentUser();
}
