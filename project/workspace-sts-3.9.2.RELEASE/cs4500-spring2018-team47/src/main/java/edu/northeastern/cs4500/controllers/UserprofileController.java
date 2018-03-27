package edu.northeastern.cs4500.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;
import edu.northeastern.cs4500.model.user.UserProfile;

@Controller
public class UserprofileController {
	
	@Autowired
    private UserService userService;
	
	@RequestMapping(value={"/profile"}, method = RequestMethod.GET)
	public ModelAndView getProfile() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userInfo", "User: " + user.getUsername());
		modelAndView.addObject("username", user.getUsername());
		modelAndView.addObject("userid", user.getId());
		modelAndView.setViewName("userProfile");
		return modelAndView;
	}
	
	@RequestMapping(value={"/profile+to+movielist"}, method = RequestMethod.GET)
	public ModelAndView getMovieList() {
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("username", user.getUsername());
		modelAndView.addObject("userid", user.getId());
		modelAndView.addObject("Owner", "MovieList Owner: " + user.getUsername());
		modelAndView.setViewName("movieList");
		return modelAndView;
	}
	
	
}
