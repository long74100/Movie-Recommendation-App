package edu.northeastern.cs4500.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;


/**
 * Spring MVC Controller to handle mappings for administrator abilities.

 */
@Controller
@RequestMapping("/admin")
public class AdminController {
	private ILocalSQLConnectService localDbConnector = new LocalSQLConnectServiceImpl();

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/ban", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void setRating(@RequestBody String userId, HttpServletRequest httpServletRequest) {
	    	
		int id = Integer.valueOf(httpServletRequest.getParameter("userId"));
		int status = Integer.valueOf(httpServletRequest.getParameter("userStatus"));
		
		if (status == 0) {
		    localDbConnector.updateUserStatus(id, 1);
		} else {
		    localDbConnector.updateUserStatus(id, 0);
		}
				
	}
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public ModelAndView home() {
	    ModelAndView modelAndView = new ModelAndView();
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userService.findUserByEmail(auth.getName());
	    modelAndView.addObject("user", user);
	    modelAndView.setViewName("home");
	    return modelAndView;
		
	}
	
	@RequestMapping(value = "/bannedlist", method = RequestMethod.GET)
	public ModelAndView bannedList() {
	    ModelAndView modelAndView = new ModelAndView();
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    User user = userService.findUserByEmail(auth.getName());
	    List<User> bannedList = localDbConnector.getBannedList();
	    modelAndView.addObject("banned", bannedList);
	    modelAndView.setViewName("banned");
	    return modelAndView;
		
	}



}