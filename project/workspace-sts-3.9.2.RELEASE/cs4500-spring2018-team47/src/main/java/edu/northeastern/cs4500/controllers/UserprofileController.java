package edu.northeastern.cs4500.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.services.SystemRecommendationAlgo;

import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;

@Controller
public class UserprofileController {
	
	@Autowired
    private UserService userService;
	
	@Autowired
	private ILocalSQLConnectService localDbConnector;
	
	private static final Logger logger = LogManager.getLogger(UserprofileController.class);
	
	/**
	 * This is to return the profile page
	 * @return the model view of profile page
	 */
	@RequestMapping(value={"/profile"}, method = RequestMethod.GET)
	public ModelAndView getProfile() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		
		try {
		List<Movie> favorites = localDbConnector.getMovieFromUserMovieList(user.getId(), "Favorites");
		modelAndView.addObject("favorites", favorites);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("userProfile");
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		return modelAndView;
	}
	
	@RequestMapping(value={"/prodRepo"}, method = RequestMethod.GET) 
	public ModelAndView getProdList() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		modelAndView.setViewName("prods");
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
		List<String> movieListNames = null;
		try {
			movieListNames = localDbConnector.getMovieListForUser(user.getId());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
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
		List<Movie> favorites = null;
		try {
			favorites = localDbConnector.getMovieFromUserMovieList(profileUser.getId(), "Favorites");
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
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
		try {
			localDbConnector.sendFriendRequest(user.getId(), receiverUser.getId());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
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
		List<String> movieListNames = null;
		ArrayList<Movie> movies = null;
		try {
		movieListNames = localDbConnector.getMovieListForUser(user.getId());
		movies = localDbConnector.getMovieFromUserMovieList(user.getId(), listName);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
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
		List<User> friends = null;
		List<User> receivedRequest = null;
		List<User> sentRequest = null;
		try {
			friends = localDbConnector.getAllFriends(user.getId());
			sentRequest = localDbConnector.getAllSentFriendRequest(user.getId());
			receivedRequest = localDbConnector.getAllReceivedFriendRequest(user.getId());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
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
	    try {
			localDbConnector.acceptRequest(Integer.valueOf(httpServletRequest.getParameter("senderID")), user.getId());
		} catch (NumberFormatException e) {
			logger.error(e.getMessage());
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	
	@RequestMapping(value="/deleteFriend", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void deleteFriend(HttpServletRequest httpServletRequest) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userService.findUserByEmail(auth.getName());
	    int userId = user.getId();
	    int friendId = Integer.valueOf(httpServletRequest.getParameter("friendID"));
	    try {
			localDbConnector.deleteFriend(userId, friendId);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	    
	}
	
	@RequestMapping(value= "/createMovieList", method=RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void createNewMovieList(@RequestBody String listName, HttpServletRequest httpservletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String newListName = httpservletRequest.getParameter("listName");
    	try {
			localDbConnector.createMovieList(userId, newListName);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	
	
	@RequestMapping(value="/deleteMovie", method=RequestMethod.POST)
	@ResponseStatus(value= HttpStatus.OK)
	public void deleteMovieFromMovieList(@RequestBody String movieAndList, HttpServletRequest httpserveletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String movieList = httpserveletRequest.getParameter("listName");
    	String movieId = httpserveletRequest.getParameter("movieId");
    	try {
			localDbConnector.deleteMovieFromUserMovieList(userId, movieList, movieId);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	

	@RequestMapping(value="/deleteList", method=RequestMethod.POST)
	@ResponseStatus(value= HttpStatus.OK)
	public void deleteMovieList(@RequestBody String movieList, HttpServletRequest httpserveletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String movieListName = httpserveletRequest.getParameter("listName");
    	try {
			localDbConnector.deleteMovieList(userId, movieListName);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	
	
	@RequestMapping(value="/cleanList", method=RequestMethod.POST)
	@ResponseStatus(value= HttpStatus.OK)
	public void cleanMovieList(@RequestBody String movieList, HttpServletRequest httpserveletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String movieListName = httpserveletRequest.getParameter("listName");
    	try {
			localDbConnector.cleanMovieList(userId, movieListName);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}
	
	@RequestMapping(value="/systemRecommendation", method = RequestMethod.GET)
	public ModelAndView getSystemRecommendation() {
		 // this is my data base 
		  Map<String, Map<String, Double>> data = new HashMap<>(); 
		  // items 
		  String item_candy = "candy"; 
		  String item_dog = "dog"; 
		  String item_cat = "cat"; 
		  String item_war = "war"; 
		  String item_food = "strange food"; 
		 
		  //I'm going to fill it in 
		  HashMap<String, Double> user1 = new HashMap<>(); 
		  HashMap<String, Double> user2 = new HashMap<>(); 
		  HashMap<String, Double> user3 = new HashMap<>(); 
		  HashMap<String, Double> user4 = new HashMap<>(); 
		  user1.put(item_candy, 1.0); 
		  user1.put(item_dog, 0.5); 
		  user1.put(item_war, 0.1); 
		  data.put("Bob", user1); 
		  user2.put(item_candy, 1.0); 
		  user2.put(item_cat, 0.5); 
		  user2.put(item_war, 0.2); 
		  data.put("Jane", user2); 
		  user3.put(item_candy, 0.9); 
		  user3.put(item_dog, 0.4); 
		  user3.put(item_cat, 0.5); 
		  user3.put(item_war, 0.1); 
		  data.put("Jo", user3); 
		  user4.put(item_candy, 0.1); 
		  user4.put(item_war, 1.0); 
		  user4.put(item_food, 0.4); 
		  data.put("StrangeJo", user4); 
		  HashMap<String, Double> user10 = new HashMap<>(); 
		  user10.put(item_food, 0.4);
	      user10.put(item_war, 0.2);
	      data.put("ed", user10);
		  SystemRecommendationAlgo slopeOne = new SystemRecommendationAlgo(data);
		  // then, I'm going to test it out... 
	        System.out.println("Getting...");
	        
	        System.out.println(slopeOne.predict("ed"));
		
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		modelAndView.addObject("recommendation", slopeOne.predict("ed"));
		modelAndView.setViewName("systemRecommendation");
		return modelAndView;
	}
	
	
}
