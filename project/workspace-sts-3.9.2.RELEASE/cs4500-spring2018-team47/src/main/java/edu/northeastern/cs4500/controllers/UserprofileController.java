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
import edu.northeastern.cs4500.model.services.LocalSQLConnectService;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.model.user.UserProfile;

@Controller
public class UserprofileController {
	
	@Autowired
    private UserService userService;
	private LocalSQLConnectService sqlConnector = new LocalSQLConnectService();
	
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
		LocalSQLConnectService db = new LocalSQLConnectService();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	Integer userId = user.getId();
    	String newListName = httpservletRequest.getParameter("listName");
    	db.createMovieList(userId, newListName);
	}
}
