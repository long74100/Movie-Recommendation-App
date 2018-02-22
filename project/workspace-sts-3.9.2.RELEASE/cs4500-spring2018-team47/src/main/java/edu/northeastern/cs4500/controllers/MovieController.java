package edu.northeastern.cs4500.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.services.IOmdbService;
import edu.northeastern.cs4500.model.services.OmdbServiceImpl;

@Controller
public class MovieController {
	
	IOmdbService omdbService = new OmdbServiceImpl();
	
	@RequestMapping(value={"/search"}, method = RequestMethod.GET)
	public ModelAndView searchResult(){
		//only prints out the json for now
		try {
			System.out.println(omdbService.searchMovieByTitle("batman"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
