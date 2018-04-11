package edu.northeastern.cs4500.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.movie.MovieReview;
import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.model.user.UserProfile;
import edu.northeastern.cs4500.prod.Prod;

@Controller
public class UserprofileController {
	
	@Autowired
    private UserService userService;
	private ILocalSQLConnectService sqlConnector = new LocalSQLConnectServiceImpl();
	
	/**
	 * This is to return the profile page
	 * @return the model view of profile page
	 */
	@RequestMapping(value={"/profile"}, method = RequestMethod.GET)
	public ModelAndView getProfile() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		
		List<Movie> favorites = sqlConnector.getMovieFromUserMovieList(user.getId(), "Favorites");
		modelAndView.addObject("favorites", favorites);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("userProfile");
		return modelAndView;
	}
	
	/**
	 * This is to return the user movie list page besides the profile management navigation bar
	 * @return the movie list page
	 */
	@RequestMapping(value={"/profile+to+movielist"}, method = RequestMethod.GET)
	public ModelAndView getMovieList() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		List<String> movieListNames = sqlConnector.getMovieListForUser(user.getId());
		modelAndView.addObject("usermovielist", movieListNames);
		modelAndView.addObject("currentMovies", new ArrayList<Movie>());
		modelAndView.setViewName("movielist");
		return modelAndView;
	}
	
	@RequestMapping(value="/view/{username}", method = RequestMethod.GET)
    public ModelAndView userProfile(@PathVariable String username) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		User profileUser = userService.findUserByUsername(username);
		List<Movie> favorites = sqlConnector.getMovieFromUserMovieList(profileUser.getId(), "Favorites");
		modelAndView.addObject("favorites", favorites);
		modelAndView.addObject("profileUser", profileUser);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("fragments/userProfile/profilePage");
		return modelAndView;
	}
	
	@RequestMapping(value="/view/{username}", method = RequestMethod.POST)
    public ModelAndView addFriend(@PathVariable String username) {
		
		ModelAndView modelAndView = new ModelAndView();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		User receiverUser = userService.findUserByUsername(username);
		sqlConnector.sendFriendRequest(user.getId(), receiverUser.getId());
		User profileUser = userService.findUserByUsername(username);
		
		modelAndView.addObject("user", user);
		modelAndView.addObject("profileUser", profileUser);
		modelAndView.setViewName("fragments/userProfile/profilePage");
		return modelAndView;
	}
	
	
	@RequestMapping(value={"/profile+to+movielist+{listName}"}, method = RequestMethod.GET)
	public ModelAndView getMovieItems(@PathVariable String listName) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		List<String> movieListNames = sqlConnector.getMovieListForUser(user.getId());
		List<User> friendList = sqlConnector.getAllFriends(user.getId());
		ArrayList<Movie> movies = sqlConnector.getMovieFromUserMovieList(user.getId(), listName);
		modelAndView.addObject("friendList", friendList);
		modelAndView.addObject("usermovielist", movieListNames);
		modelAndView.addObject("currentMovielist", listName);
		modelAndView.addObject("currentMovies", movies);
		modelAndView.setViewName("listMoviesItem");
		return modelAndView;
	}
	
	/*
	 * go to see who you are friends with and what friend requests you have
	 */
	@RequestMapping(value={"/profile+to+friendsAndRequests"}, method = RequestMethod.GET)
	public ModelAndView friendsAndRequests() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<User> friends = sqlConnector.getAllFriends(user.getId());
		List<User> receivedRequest = sqlConnector.getAllReceivedFriendRequest(user.getId());
		List<User> sentRequest = sqlConnector.getAllSentFriendRequest(user.getId());
		modelAndView.addObject("user", user);
		modelAndView.addObject("friends", friends);
		modelAndView.addObject("receivedRequest", receivedRequest);
		modelAndView.addObject("sentRequest", sentRequest);
		
		modelAndView.setViewName("friendsAndRequests");
		return modelAndView;
	}
	
	@RequestMapping(value="/acceptRequest", method=RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void acceptFriendRequest(HttpServletRequest httpServletRequest) {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	sqlConnector.acceptRequest(Integer.valueOf(httpServletRequest.getParameter("senderID")), user.getId());
    }
	
	
	@RequestMapping(value= "/createMovieList", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void createNewMovieList(@RequestBody String listName, HttpServletRequest httpservletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String movieListCreatedDate = formatter.format(new Date());
    	Integer userId = user.getId();
    	String newListName = httpservletRequest.getParameter("listName");
    	sqlConnector.createMovieList(userId, newListName, movieListCreatedDate);
	}
	
	
	@RequestMapping(value="/deleteMovie", method=RequestMethod.POST)
	@ResponseStatus(value= HttpStatus.OK)
	public void deleteMovieFromMovieList(@RequestBody String movieAndList, HttpServletRequest httpserveletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String movieList = httpserveletRequest.getParameter("listName");
    	String movieId = httpserveletRequest.getParameter("movieId");
    	sqlConnector.deleteMovieFromUserMovieList(userId, movieList, movieId);
	}
	

	@RequestMapping(value="/deleteList", method=RequestMethod.POST)
	@ResponseStatus(value= HttpStatus.OK)
	public void deleteMovieList(@RequestBody String movieList, HttpServletRequest httpserveletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String movieListName = httpserveletRequest.getParameter("listName");
    	sqlConnector.deleteMovieList(userId, movieListName);
	}
	
	
	@RequestMapping(value="/cleanList", method=RequestMethod.POST)
	@ResponseStatus(value= HttpStatus.OK)
	public void cleanMovieList(@RequestBody String movieList, HttpServletRequest httpserveletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String movieListName = httpserveletRequest.getParameter("listName");
    	sqlConnector.cleanMovieList(userId, movieListName);
	}
	
	/**
	 * This is to direct to the prod page
	 * @return prod page
	 */
	@RequestMapping(value={"/prodRepo"}, method = RequestMethod.GET) 
	public ModelAndView getProdList() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<User> allFriends = sqlConnector.getAllFriends(user.getId());
		modelAndView.addObject("friendList", allFriends);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("prods");
		return modelAndView;
	}
	
	
	// so needs to create three page also. or see if we can do something like display block and none.
	@RequestMapping(value={"/prodRepo+all+{repoName}"}, method = RequestMethod.GET)
	public ModelAndView getProdsItemsInProfileAll(@PathVariable String repoName) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<Prod> allProds = new ArrayList<>();
		String prodType = "";
		ModelAndView modelAndView = new ModelAndView();
		
		// get all sent out prod: can be all or individual friend
		
		if(repoName.equals("all")) {
			// get all prods in "all" section
			allProds = sqlConnector.extractAllProds(user.getId());
			prodType = "all";
		}
		else {
			allProds = sqlConnector.extractAllProdsForARecipient(user.getId(), repoName);
			prodType = repoName;
		}
		List<User> allFriends = sqlConnector.getAllFriends(user.getId());
		modelAndView.addObject("allProds", allProds);
		modelAndView.addObject("prodType", prodType);
		modelAndView.addObject("friendList", allFriends);
		modelAndView.addObject("user", user);
		modelAndView.addObject("repo", repoName);
		modelAndView.setViewName("allProdsSentAndReceived");
		System.out.println(allProds);
		return modelAndView;
	}
	
	@RequestMapping(value={"/prodRepo+allSent+{repoName}"}, method = RequestMethod.GET)
	public ModelAndView getProdsItemsInProfileAllSent(@PathVariable String repoName) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<Prod> allSentProds = new ArrayList<>();
		String prodType = "";
		ModelAndView modelAndView = new ModelAndView();
		
		if(repoName.equals("all")) {
			allSentProds = sqlConnector.extractAllSentProds(user.getId());
			prodType = "all";
		}
		else {
			allSentProds = sqlConnector.extractProdsSentToAFriend(user.getId(), repoName);
			prodType = repoName;
		}
		
		
		List<User> allFriends = sqlConnector.getAllFriends(user.getId());
		modelAndView.addObject("allProds", allSentProds);
		modelAndView.addObject("prodType", prodType);
		modelAndView.addObject("friendList", allFriends);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("allProdsSentAndReceived");
		return modelAndView;
	}
	
	@RequestMapping(value={"/prodRepo+allReceived+{repoName}"}, method = RequestMethod.GET)
	public ModelAndView getProdsItemsInProfileAllReceived(@PathVariable String repoName) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		String prodType = "";
		List<Prod> allReceivedProds = new ArrayList<>();
		
		if(repoName.equals("all")) {
			allReceivedProds = sqlConnector.extractAllFriendProds(user.getId());
			prodType = "all";
		}
		else {
			allReceivedProds = sqlConnector.extractProdsReceivedFromAFriend(user.getId(), repoName);
			prodType = repoName;
		}
		modelAndView.addObject("allProds", allReceivedProds);
		modelAndView.addObject("prodType", prodType);
		List<User> allFriends = sqlConnector.getAllFriends(user.getId());
		modelAndView.addObject("friendList", allFriends);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("allProdsSentAndReceived");
		return modelAndView;
	}
	
	
	
	/**
	 * This is to send the movie recommendations
	 * @param httpServletRequest
	 */
	@RequestMapping(value="/prodToFriends", method = RequestMethod.POST)
	@ResponseStatus(value= HttpStatus.OK)
	public void prodToFriends(HttpServletRequest httpServletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String userName = user.getUsername();
		String movieTitle = httpServletRequest.getParameter("movieName");
		String movieImdbId = httpServletRequest.getParameter("movieId");
		String movieDBId = httpServletRequest.getParameter("movieDBId");
		String recipientName = httpServletRequest.getParameter("allUserName");
		String recipientId = httpServletRequest.getParameter("allUserId");
		String senderComment = httpServletRequest.getParameter("comment");
		String poster = httpServletRequest.getParameter("poster");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	String prodSentDate = formatter.format(new Date());
		System.out.println(movieTitle); 
		System.out.println(movieImdbId);
		System.out.println(movieDBId);
		System.out.println(recipientName);
		System.out.println(recipientId);
		System.out.println(senderComment);
		if(recipientName.contains(",")) {
			String[] out = recipientName.split(",");
			String[] ids = recipientId.split(",");
			for(int i = 0; i < out.length; i++) {
				sqlConnector.sendProdToFriend(userId, Integer.parseInt(ids[i]), userName, out[i], 
						movieImdbId, movieTitle, prodSentDate, senderComment, movieDBId, poster);
			}
		}
		else {
			sqlConnector.sendProdToFriend(userId, Integer.parseInt(recipientId), userName, recipientName, 
					movieImdbId, movieTitle, prodSentDate, senderComment, movieDBId, poster);
		}
	}
}
