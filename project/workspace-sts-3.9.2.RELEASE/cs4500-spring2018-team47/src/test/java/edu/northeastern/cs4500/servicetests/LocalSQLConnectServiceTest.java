package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.user.User;

@SpringBootTest
public class LocalSQLConnectServiceTest {
	private static ILocalSQLConnectService localSQLConnectService;
	//mock movieID
	private static String movieID = "tt0096895";
	private static String stub1Email = "teststub1@email.com"; // id 78
	private static String stub2Email = "teststub2@email.com"; // id 79
	private static int stub1Id = 78;
	private static int stub2Id = 79;
	private static int noSuchId = 832019;
	
	@Before
	public void init() {
		localSQLConnectService = new LocalSQLConnectServiceImpl();
	}
//		
	// this test is failing
	@Test
	public void testContainMovie() {
		localSQLConnectService.deleteFromMovieTable("1");
		//does not contain movie
		boolean actual = localSQLConnectService.containMovie("1");
		assertFalse(actual);
		//contains movie
		Map<String, String> movieObject = new HashMap<>();
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
		actual = localSQLConnectService.containMovie("1");
		assertTrue(actual);
		
		//Test Sql Exception
		actual = localSQLConnectService.containMovie("-1");
		assertFalse(actual);
	}
	
	@Test
	public void testLoadMovieIntoLocalDB() {
		
	}
	
//	@Test
//	public void testCreateMovieList() {
//		//NEED TO FIGURE OUT HOW TO SAVE USER
//		localSQLConnectService.createMovieList(32, "NewTestList+A");
//	}
	
	/**
	 * Test that getBannedList returns a list of banned users.
	 */
	@Test
	public void testGetBannedList() {
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
	    
	    localSQLConnectService.updateUserStatus(stub1Id, 1);
	   
	}
	
	/**
	 * Test that getUser fetches the correct user.
	 */
	@Test
	public void testGetUser() {
	    
	    User noSuchUser = localSQLConnectService.getUser(noSuchId);
	    assertNull(noSuchUser);
	    

	    User stub1 = localSQLConnectService.getUser(stub1Id);
	    assertNotNull(stub1);
	    
	    assertEquals(stub1.getActive(), 1);
	    assertEquals(stub1.getEmail(), stub1Email);
	    assertEquals(stub1.getFirstName(), "teststub");
	    assertEquals(stub1.getLastName(), "1");
	    assertEquals(stub1.getRole(), 1);
	    assertEquals(stub1.getUsername(), "teststub1");
	    
	}
	
	/**
	 * Test that updateUserStatus changes a user's active status.
	 */
	@Test
	public void testUpdateUserStatus() {
	    assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 1);
	    localSQLConnectService.updateUserStatus(stub1Id, 0);
	    
	    assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 0);
	    
	    localSQLConnectService.updateUserStatus(stub1Id, 1);
	    assertEquals(localSQLConnectService.getUser(stub1Id).getActive(), 1);

	}
	
	/**
	 * Test that delete friend removes a user relation.
	 */
	@Test
	public void testDeleteFriend() {
	    
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
	 */
	@Test
	public void testGetUserRelation() {
	    // ensure no relation exists
	    localSQLConnectService.deleteFriend(78, 79);

	    // no relation
	    assertEquals(localSQLConnectService.getUserRelation(78, 79), "");
	    
	    // request on hold
	    localSQLConnectService.sendFriendRequest(78, 79);
	    assertEquals(localSQLConnectService.getUserRelation(78, 79), "onHold");
	    
	    // accepted
	    localSQLConnectService.acceptRequest(78, 79);
	    assertEquals(localSQLConnectService.getUserRelation(78, 79), "friend");
	    
	    // rejected
	    localSQLConnectService.rejectRequest(78, 79);
	    assertEquals(localSQLConnectService.getUserRelation(78, 79), "");
	    
	}

	
}
