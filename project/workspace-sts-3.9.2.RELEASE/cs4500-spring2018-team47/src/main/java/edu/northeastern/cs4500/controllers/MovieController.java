package edu.northeastern.cs4500.controllers;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.services.IOmdbService;
import edu.northeastern.cs4500.model.services.OmdbServiceImpl;

@Controller
public class MovieController {
	
	private IOmdbService omdbService = new OmdbServiceImpl();
	
	@RequestMapping(value={"/search"}, method = RequestMethod.GET)
	public ModelAndView searchResult(String searchString) {
		String plot = "";
		JSONObject movie = new JSONObject();
		try {
			movie = omdbService.searchMovieByTitle("Tropic Thunder");
			plot = movie.getString("Plot");
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject("movie", plot.toString() );
		modelAndView.setViewName("/movie");
		return modelAndView;
	}
	
	
}
