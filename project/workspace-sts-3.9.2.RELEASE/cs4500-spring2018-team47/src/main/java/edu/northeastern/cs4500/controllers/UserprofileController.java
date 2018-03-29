package edu.northeastern.cs4500.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.services.LocalSQLConnectService;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.model.user.UserProfile;

@Controller
public class UserprofileController {
	
	@Autowired
    private UserService userService;
	
	/**
	 * This is to return the profile page
	 * @return the model view of profile page
	 */
	@RequestMapping(value={"/profile"}, method = RequestMethod.GET)
	public ModelAndView getProfile() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
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
		LocalSQLConnectService sqlConnector = new LocalSQLConnectService();
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		List<String> movieListNames = sqlConnector.getMovieListForUser(user.getId());
		modelAndView.addObject("usermovielist", movieListNames);
		modelAndView.addObject("currentMovies", new ArrayList<Movie>());
//		modelAndView.addObject("newListName", "");
		modelAndView.setViewName("movielist");
		return modelAndView;
	}
	
	@RequestMapping(value="/view/{username}", method = RequestMethod.GET)
    public ModelAndView userProfile(@PathVariable String username) {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		User profileUser = userService.findUserByUsername(username);
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
		
		LocalSQLConnectService localSQLConnectService = new LocalSQLConnectService();
		localSQLConnectService.sendFriendRequest(user.getId(), receiverUser.getId());
		User profileUser = userService.findUserByUsername(username);
		
		modelAndView.addObject("user", user);
		modelAndView.addObject("profileUser", profileUser);
		modelAndView.setViewName("fragments/userProfile/profilePage");
		return modelAndView;
	}
	
	
	@RequestMapping(value={"/profile+to+movielist+{listName}"}, method = RequestMethod.GET)
	public ModelAndView getMovieItems(@PathVariable String listName) {
		ModelAndView modelAndView = new ModelAndView();
		LocalSQLConnectService sqlConnector = new LocalSQLConnectService();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		List<String> movieListNames = sqlConnector.getMovieListForUser(user.getId());
		ArrayList<Movie> movies = sqlConnector.getMovieFromUserMovieList(user.getId(), listName);
		modelAndView.addObject("usermovielist", movieListNames);
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
		
		LocalSQLConnectService sqlConnector = new LocalSQLConnectService();
		List<User> friends = sqlConnector.getAllFriends(user.getId());
		List<User> receivedRequest = sqlConnector.getAllReceivedFriendRequest(user.getId());
		List<User> sentRequest = sqlConnector.getAllSentFriendRequest(user.getId());
		modelAndView.addObject("user", user);
		modelAndView.addObject("friends", friends);
		modelAndView.addObject("receivedRequest", receivedRequest);
		modelAndView.addObject("sendRequest", sentRequest);
		
		modelAndView.setViewName("friendsAndRequests");
		return modelAndView;
	}
	
	
	
}
