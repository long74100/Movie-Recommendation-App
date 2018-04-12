package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.user.User;

@SpringBootTest
public class LocalSQLConnectServiceTest {
	private static ILocalSQLConnectService localSQLConnectService;
	// mock movieID
	private static String movieID = "tt0096895";
	private static Map<String, String> movieObject;

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

	@Before
	public void setUpConnection() {
		localSQLConnectService = new LocalSQLConnectServiceImpl();
	}

	private void initMockUsers() {

		// set up mock user 1
		stub1 = new User();
		stub1.setId(stub1Id);
		stub1.setActive(1);
		stub1.setEmail(stub1Email);
		stub1.setUsername(stub1Username);
		stub1.setLastName(stub1LastName);
		stub1.setFirstName(stub1FirstName);
		stub1.setPassword(password);
		stub1.setRole(1);

		// set up mock user 2
		stub2 = new User();
		stub2.setId(stub2Id);
		stub2.setActive(1);
		stub2.setEmail(stub2Email);
		stub2.setUsername(stub2Username);
		stub2.setLastName(stub2lastName);
		stub2.setFirstName(stub2FirstName);
		stub2.setPassword(password);
		stub2.setRole(1);

		localSQLConnectService.insertUser(stub1);
		localSQLConnectService.insertUser(stub2);

	}

	private void cleanMockUsers() {
		localSQLConnectService.removeUser(stub1Id);
		localSQLConnectService.removeUser(stub2Id);

	}

	private void initMockMovie() {
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

	private void cleanMockMovie() {
		localSQLConnectService.deleteFromMovieTable("1");
	}

	@Test
	public void testContainMovie() {
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

		cleanMockMovie();
	}

	@Test
	public void testLoadMovieIntoLocalDB() {

	}

	// @Test
	// public void testCreateMovieList() {
	// //NEED TO FIGURE OUT HOW TO SAVE USER
	// localSQLConnectService.createMovieList(32, "NewTestList+A");
	// }

	/**
	 * Test that getBannedList returns a list of banned users.
	 */
	@Test
	public void testGetBannedList() {
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

		cleanMockUsers();

	}

	/**
	 * Test that getUser fetches the correct user.
	 */
	@Test
	public void testGetUser() {
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

		cleanMockUsers();
	}

	/**
	 * Test that updateUserStatus changes a user's active status.
	 */
	@Test
	public void testUpdateUserStatus() {
		initMockUsers();

		assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 1);
		localSQLConnectService.updateUserStatus(stub1Id, 0);

		assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 0);

		localSQLConnectService.updateUserStatus(stub1Id, 1);
		assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 1);

		cleanMockUsers();
	}

	/**
	 * Test that delete friend removes a user relation.
	 */
	@Test
	public void testDeleteFriend() {
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

		cleanMockUsers();
	}

	/**
	 * Test that getUserRelation fetches the correct relation.
	 */
	@Test
	public void testGetUserRelation() {
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

		cleanMockUsers();
	}

	/**
	 * Test that insertUser inserts a user into the database.
	 */
	@Test
	public void testInsertUser() {
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

		cleanMockUsers();
	}

	/**
	 * Test that remove user removes the user from the database.
	 */
	@Test
	public void testRemoveUser() {
		initMockUsers();

		assertNotNull(localSQLConnectService.getUser(stub1Id));
		localSQLConnectService.removeUser(stub1Id);
		assertNull(localSQLConnectService.getUser(stub1Id));

		cleanMockUsers();
	}

	/**
	 * Test that remove review removes a review from the database.
	 */
	@Test
	public void testRemoveReview() {

	}

}
