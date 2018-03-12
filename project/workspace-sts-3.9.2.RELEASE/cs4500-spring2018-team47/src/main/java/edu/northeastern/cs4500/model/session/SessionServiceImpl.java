package edu.northeastern.cs4500.model.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;

public class SessionServiceImpl implements SessionService {

	@Override
	public boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return !auth.getName().equals("anonymousUser");
	}
	
	
}
