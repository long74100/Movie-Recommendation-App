package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import edu.northeastern.cs4500.model.movie.MovieReview;
import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.user.User;

@SpringBootTest
public class LocalSQLConnectServiceTest {
    
    	@Value("${spring.datasource.password}")
    	private static String dbPassword = "TbthaGCmiimWrtayxr4MBEcD3tVB3sY";
    	
	private static ILocalSQLConnectService localSQLConnectService;
	
	// mock movie
	private static String movieID = "tt0096895";
	private static Map<String, String> movieObject;
	
	// mock user
	private static User stub1;
	private static User stub2;

	private static String stub1Email = "teststub1@email.com"; // id 78
	private static String stub2Email = "teststub2@email.com"; // id 79
	private static int stub1Id = 983721;
	private static int stub2Id = 983722;
	private static String stub1Username = "stub1username";
	private static String stub2Username = "stub2username";
	private static String stub1LastName = "stub1lastname";
	private static String stub2lastName = "stub2lastname";
	private static String stub1FirstName = "stub1FirstName";
	private static String stub2FirstName = "stub2FirstName";
	private static String password = "password";

	private static User noSuchUser;
	private static int noSuchId = 983729;
	
	// mock review
	private static MovieReview mockReview;
	private static int mockReviewId = 983730;

	@Before
	public void setUpConnection() throws SQLException {
		localSQLConnectService = new LocalSQLConnectServiceImpl(dbPassword);
		cleanMockUsers();
		cleanMockMovie();
		cleanMockReview();
	}
	
	/**
	 * Initialize mock users for testing.
	 * @throws SQLException 
	 */
	private void initMockUsers() throws SQLException {

		// set up mock user 1
		stub1 = new User();
		stub1.setId(stub1Id);
		stub1.setActive(1);
		stub1.setEmail(stub1Email);
		stub1.setUsername(stub1Username);
		stub1.setLastName(stub1LastName);
		stub1.setFirstName(stub1FirstName);
		stub1.setPassword(dbPassword);
		stub1.setRole(1);

		// set up mock user 2
		stub2 = new User();
		stub2.setId(stub2Id);
		stub2.setActive(1);
		stub2.setEmail(stub2Email);
		stub2.setUsername(stub2Username);
		stub2.setLastName(stub2lastName);
		stub2.setFirstName(stub2FirstName);
		stub2.setPassword(dbPassword);
		stub2.setRole(1);

		localSQLConnectService.insertUser(stub1);
		localSQLConnectService.insertUser(stub2);

	}
	
	/**
	 * Clean up mock users.
	 * @throws SQLException 
	 */
	private void cleanMockUsers() throws SQLException {
		localSQLConnectService.removeUser(stub1Id);
		localSQLConnectService.removeUser(stub2Id);

	}
	
	/**
	 * Initialize mock movie for testing.
	 * @throws SQLException 
	 */
	private void initMockMovie() throws SQLException {
		movieObject = new HashMap<>();
		movieObject.put("imdbID", "1");
		movieObject.put("title", "test");
		movieObject.put("genre", "test");
		movieObject.put("plot", "test");
		movieObject.put("actors", "test");
		movieObject.put("director", "test");
		movieObject.put("released", "test");
		movieObject.put("runtime", "test");
		movieObject.put("country", "test");
		movieObject.put("imdbRating", "0");
		movieObject.put("poster", "test");
		movieObject.put("language", "test");
		movieObject.put("movieDBid", "test");
		localSQLConnectService.loadMovieIntoLocalDB(movieObject);
	}
	
	/**
	 * Clean up mock movie.
	 * @throws SQLException 
	 */
	private void cleanMockMovie() throws SQLException {
		localSQLConnectService.deleteFromMovieTable("1");
	}
	
	/**
	 * Creates a mock review for testing.
	 * @throws SQLException 
	 */
	private void initMockReview() throws SQLException {
	    initMockMovie();
	    initMockUsers();
	    
	    mockReview = new MovieReview();
	    mockReview.setId(mockReviewId);
	    mockReview.setMovie_id("1");
	    mockReview.setUser_id(String.valueOf(stub1Id));
	    mockReview.setUsername(stub1Username);
	    mockReview.setDate("2018-04-11 00:00:00");
	    mockReview.setReview("this review is fake");
	    localSQLConnectService.addReviewToLocalDB(mockReview);

	}
	
	/**
	 * clean the mock review.
	 * @throws SQLException 
	 */
	private void cleanMockReview() throws SQLException {
	    localSQLConnectService.removeReview(mockReviewId);
	    cleanMockMovie();
	    cleanMockUsers();
	}
	

	@Test
	public void testContainMovieAndLoadMovie() throws SQLException {
		// does not contain movie
		boolean actual = localSQLConnectService.containMovie("1");
		assertFalse(actual);
		// contains movie
		initMockMovie();

		actual = localSQLConnectService.containMovie("1");
		assertTrue(actual);

		// Test Sql Exception
		actual = localSQLConnectService.containMovie("-1");
		assertFalse(actual);

	}

	// @Test
	// public void testCreateMovieList() {
	// //NEED TO FIGURE OUT HOW TO SAVE USER
	// localSQLConnectService.createMovieList(32, "NewTestList+A");
	// }

	/**
	 * Test that getBannedList returns a list of banned users.
	 * @throws SQLException 
	 */
	@Test
	public void testGetBannedList() throws SQLException {
		initMockUsers();

		List<User> preBanList = localSQLConnectService.getBannedList();
		localSQLConnectService.updateUserStatus(stub1Id, 0);

		List<User> postBanList = localSQLConnectService.getBannedList();

		boolean stub1IsBanned = false;

		for (User user : postBanList) {
			if (user.getActive() == 0) {
				stub1IsBanned = true;
				break;
			}
		}

		// check that banned list increased
		assertEquals(postBanList.size(), preBanList.size() + 1);

		// check that banned list contains banned user
		assertTrue(stub1IsBanned);


	}

	/**
	 * Test that getUser fetches the correct user.
	 * @throws SQLException 
	 */
	@Test
	public void testGetUser() throws SQLException {
		initMockUsers();

		noSuchUser = localSQLConnectService.getUser(noSuchId);
		assertNull(noSuchUser);

		User stub1 = localSQLConnectService.getUser(stub1Id);
		assertNotNull(stub1);

		assertEquals(stub1.getActive(), 1);
		assertEquals(stub1.getEmail(), stub1Email);
		assertEquals(stub1.getFirstName(), stub1FirstName);
		assertEquals(stub1.getLastName(), stub1LastName);
		assertEquals(stub1.getRole(), 1);
		assertEquals(stub1.getUsername(), stub1Username);

	}

	/**
	 * Test that updateUserStatus changes a user's active status.
	 * @throws SQLException 
	 */
	@Test
	public void testUpdateUserStatus() throws SQLException {
		initMockUsers();

		assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 1);
		localSQLConnectService.updateUserStatus(stub1Id, 0);

		assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 0);

		localSQLConnectService.updateUserStatus(stub1Id, 1);
		assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 1);

	}

	/**
	 * Test that delete friend removes a user relation.
	 * @throws SQLException 
	 */
	@Test
	public void testDeleteFriend() throws SQLException {
		initMockUsers();

		localSQLConnectService.sendFriendRequest(stub1Id, stub2Id);
		assertEquals(localSQLConnectService.getUserRelation(stub1Id, stub2Id), "onHold");
		localSQLConnectService.deleteFriend(stub1Id, stub2Id);
		assertEquals(localSQLConnectService.getUserRelation(stub1Id, stub2Id), "");

		// reverse sender and receiver
		localSQLConnectService.sendFriendRequest(stub2Id, stub1Id);
		assertEquals(localSQLConnectService.getUserRelation(stub2Id, stub1Id), "onHold");
		localSQLConnectService.deleteFriend(stub1Id, stub2Id);
		assertEquals(localSQLConnectService.getUserRelation(stub2Id, stub1Id), "");

	}

	/**
	 * Test that getUserRelation fetches the correct relation.
	 * @throws SQLException 
	 */
	@Test
	public void testGetUserRelation() throws SQLException {
		initMockUsers();

		// ensure no relation exists
		localSQLConnectService.deleteFriend(stub1Id, stub2Id);

		// no relation
		assertEquals(localSQLConnectService.getUserRelation(stub1Id, stub2Id), "");

		// request on hold
		localSQLConnectService.sendFriendRequest(stub1Id, stub2Id);
		assertEquals(localSQLConnectService.getUserRelation(stub1Id, stub2Id), "onHold");

		// accepted
		localSQLConnectService.acceptRequest(stub1Id, stub2Id);
		assertEquals(localSQLConnectService.getUserRelation(stub1Id, stub2Id), "friend");

		// rejected
		localSQLConnectService.rejectRequest(stub1Id, stub2Id);
		assertEquals(localSQLConnectService.getUserRelation(stub1Id, stub2Id), "");

	}

	/**
	 * Test that insertUser inserts a user into the database.
	 * @throws SQLException 
	 */
	@Test
	public void testInsertUser() throws SQLException {
		initMockUsers();

		noSuchUser = localSQLConnectService.getUser(noSuchId);
		assertNull(noSuchUser);
		noSuchUser = new User();
		noSuchUser.setId(noSuchId);
		noSuchUser.setEmail("thisemailshouldntexist@fakeemail.com");
		noSuchUser.setActive(1);
		noSuchUser.setRole(1);
		noSuchUser.setUsername("fake username");
		noSuchUser.setLastName("fake last Name");
		noSuchUser.setFirstName("fake first Name");
		noSuchUser.setPassword("fake password");
		noSuchUser.setRole(1);

		localSQLConnectService.insertUser(noSuchUser);

		User nowExistingUser = localSQLConnectService.getUser(noSuchId);
		assertNotNull(nowExistingUser);
		localSQLConnectService.removeUser(noSuchId);

	}

	/**
	 * Test that remove user removes the user from the database.
	 * @throws SQLException 
	 */
	@Test
	public void testRemoveUser() throws SQLException {
		initMockUsers();

		assertNotNull(localSQLConnectService.getUser(stub1Id));
		localSQLConnectService.removeUser(stub1Id);
		assertNull(localSQLConnectService.getUser(stub1Id));

	}

	/**
	 * Test that remove review removes a review from the database.
	 * @throws SQLException 
	 */
	@Test
	public void testRemoveReview() throws SQLException {
	    initMockReview();
	    List<MovieReview> reviews = localSQLConnectService.getReviewsForMovie("1");
	    assertEquals(reviews.size(), 1);
	    
	    localSQLConnectService.removeReview(reviews.get(0).getId());
	    	    
	    reviews = localSQLConnectService.getReviewsForMovie("1");
	    assertEquals(reviews.size(), 0);
	    
	}
	
	/**
	 * Test that addReviewToLocalDb adds a review to the database
	 * @throws SQLException
	 */
	@Test
	public void testAddReviewToLocalDb() throws SQLException{
	    List<MovieReview> reviews = localSQLConnectService.getReviewsForMovie("1");
	    assertEquals(reviews.size(), 0);
	    initMockReview();
	    reviews = localSQLConnectService.getReviewsForMovie("1");
	    assertEquals(reviews.size(), 1);
	    
	}

}

