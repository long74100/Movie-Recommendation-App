package session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.model.user.UserService;

public class SessionServiceImpl implements SessionService {

	@Autowired
	private UserService userService;
	
	@Override
	public boolean isAuthenticated() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth.getName() != "anonymousUser";
	}
	
	
}
