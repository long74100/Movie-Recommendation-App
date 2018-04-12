package edu.northeastern.cs4500.servicetests;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.services.UserServiceImpl;
import edu.northeastern.cs4500.model.user.User;

@SpringBootTest
public class UserServiceImplTest {
	
	private static UserService userServiceImpl;
	private static LocalSQLConnectServiceTest connector = new LocalSQLConnectServiceTest();
	
	@Before
	public void init() throws SQLException {
		userServiceImpl = new UserServiceImpl();
		connector.setUpConnection();
	}
	
	/**
	 * Test findsUserByEmail finds a user by email.
	 * @throws SQLException 
	 */
	@Test
	public void testFindUserByEmail() throws SQLException {
	
	}
	
	/**
	 * Test findUserByUsername finds a user by username.
	 */
	@Test
	public void testFindUserByUsername() {
	    
	}
	
	/**
	 * Test savesUser saves a new User to the repository.
	 * @param user User
	 */
	@Test
	public void testSaveUser() {
	    
	}
	
	/**
	 * Test existsByEmail Checks if there is a User with the given email in the repository.
	 */
	@Test
	public void testExistsByEmail() {
	    
	}
	
	/**
	 * Test existsByUsername Checks if there is a User with the given username in the repository.
	 */
	@Test
	public void testExistsByUsername() {
	    
	}
	

}
