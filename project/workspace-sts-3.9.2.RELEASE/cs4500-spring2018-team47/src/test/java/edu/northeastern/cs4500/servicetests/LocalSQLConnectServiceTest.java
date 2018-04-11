package edu.northeastern.cs4500.servicetests;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.user.User;

public class LocalSQLConnectServiceTest {
	private static ILocalSQLConnectService localSQLConnectService;
	//mock movieID
	private static String movieID = "tt0096895";
	private static String stub1Email = "teststub1@email.com"; // id 78
	private static String stub2Email = "teststub2@email.com"; // id 79
	private static int stub1Id = 78;
	private static int stub2Id = 79;
	
	@Before
	public void init() {
		localSQLConnectService = new LocalSQLConnectServiceImpl();
	}
		
	// this test is failing
	@Test
	public void testCreateMovieList() {
		this.init();
		localSQLConnectService.createMovieList(32, "NewTestList+A");
	}
	
	/**
	 * Test that getBannedList returns a list of banned users.
	 */
	@Test
	public void testGetBannedList() {
	    List<User> preBanList = localSQLConnectService.getBannedList();
	    localSQLConnectService.updateUserStatus(stub1Id, 0);
	    List<User> postBanList = localSQLConnectService.getBannedList();

	    assertEquals(postBanList.size(), preBanList.size() + 1);
	    localSQLConnectService.updateUserStatus(stub1Id, 1);
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
	 * Test that getUserRelation fetchs the correct relation.
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
