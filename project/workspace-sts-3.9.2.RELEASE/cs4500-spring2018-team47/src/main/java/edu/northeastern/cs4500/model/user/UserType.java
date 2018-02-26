package edu.northeastern.cs4500.model.user;

/**
 * Types of Users.
 *
 */
public enum UserType {
	USER, ADMIN;
	
	@Override
	public String toString() {
		switch(this) {
			case USER: return "USER";
			case ADMIN: return "ADMIN";
			default: throw new IllegalArgumentException("No such user type");
		}
		
	}
}
