package edu.northeastern.cs4500.controllers;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;

/**
 * Spring MVC Controller to handle mappings for login and registration.
 *
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;
    
    private ILocalSQLConnectService localSQLConnector = new LocalSQLConnectServiceImpl();


    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public ModelAndView login() {
	ModelAndView modelAndView = new ModelAndView();
	modelAndView.setViewName("login");
	return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public ModelAndView registration() {
	ModelAndView modelAndView = new ModelAndView();
	User user = new User();
	modelAndView.addObject("user", user);
	modelAndView.setViewName("registration");
	return modelAndView;
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid User newUser, BindingResult bindingResult) {
	ModelAndView modelAndView = new ModelAndView();
	boolean emailExists = userService.existsByEmail(newUser.getEmail());
	boolean usernameExists = userService.existsByUsername(newUser.getUsername());

	if (emailExists) {
	    bindingResult.rejectValue("email", "error.user",
		    "There is already a user registered with provided email. Please try a new email.");
	}

	if (usernameExists) {
	    bindingResult.rejectValue("username", "error.user", "Username already exists. Please choose a new one.");
	}

	if (bindingResult.hasErrors()) {
	    modelAndView.setViewName("registration");
	} else {
	    userService.saveUser(newUser);
	    localSQLConnector.preloadMovieList(newUser.getId());
	    modelAndView.addObject("successMessage", "Registration successful!");
	    modelAndView.addObject("user", new User());
	    modelAndView.setViewName("registration");

	}
	return modelAndView;
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

}
