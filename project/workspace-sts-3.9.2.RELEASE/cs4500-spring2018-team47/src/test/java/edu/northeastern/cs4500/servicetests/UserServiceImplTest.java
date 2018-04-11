package edu.northeastern.cs4500.servicetests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.services.UserServiceImpl;
import edu.northeastern.cs4500.model.user.User;

public class UserServiceImplTest {
	
	private static final Logger logger = LogManager.getLogger(MovieDBServiceImplTest.class);
	private static UserService userServiceImpl;
	
	@Before
	public void init() {
		userServiceImpl = new UserServiceImpl();
	}
	
	@Test
	public void testFindUserByEmail() {
		User badUser = userServiceImpl.findUserByEmail("wrong email");
	}
}
