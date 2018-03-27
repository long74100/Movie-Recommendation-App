package edu.northeastern.cs4500.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring MVC Controller to handle mappings for default pages.
 *
 * Adding a comment for smart commits
 */
@Controller
public class PageController {
	@RequestMapping(value={"/"}, method = RequestMethod.GET)
	public ModelAndView login(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		return modelAndView;
	}
	
}
