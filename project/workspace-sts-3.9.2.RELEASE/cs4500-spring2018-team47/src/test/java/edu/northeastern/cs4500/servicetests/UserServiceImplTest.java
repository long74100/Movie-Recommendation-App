//package edu.northeastern.cs4500.servicetests;
//
//import static org.junit.Assert.assertEquals;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import edu.northeastern.cs4500.model.services.UserService;
//import edu.northeastern.cs4500.model.services.UserServiceImpl;
//import edu.northeastern.cs4500.model.user.User;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest
//public class UserServiceImplTest {
//	
//	private static final Logger logger = LogManager.getLogger(MovieDBServiceImplTest.class);
//	private static UserService userServiceImpl;
//	
//	@Before
//	public void init() {
//		userServiceImpl = new UserServiceImpl();
//	}
//	
//	@Test(expected = NullPointerException.class)
//	public void testFindUserByEmailWithBadEmail() {
//		userServiceImpl.findUserByEmail("wrongemail@bademail.com");		
//	}
//	
//	@Test
//	public void testFindUserByEmailWithExistingEmail() {
//		User user = new User();
//		user.setEmail("testemail@test.com");
//		user.setFirstName("testFirst");
//		user.setLastName("testLast");
//		user.setId(123);
//		user.setPassword("password");
//		user.setUsername("username");
//		user.setRole(0);
//		user.setActive(0);
//		userServiceImpl.saveUser(user);
//		
//		User actualUser = userServiceImpl.findUserByEmail("testemail@test.com");
//
//		assertEquals("testemail@test.com", actualUser.getEmail());
//		assertEquals("testFirst", actualUser.getFirstName());
//		assertEquals("testLast", actualUser.getLastName());
//		assertEquals(123, actualUser.getId());
//		assertEquals("password", actualUser.getPassword());
//		assertEquals("username", actualUser.getUsername());
//		assertEquals("1", actualUser.getRole());
//		assertEquals("1", actualUser.getActive());
//
//		
//		
//	}
//}
