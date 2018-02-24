package edu.northeastern.cs4500.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
		JSONObject movieJSON = new JSONObject();
		Map<String, String> movie = new HashMap<String, String>();
		
		try {
			movieJSON = omdbService.searchMovieByTitle("Tropic Thunder");
			
			
			movie.put("title", movieJSON.getString("Title"));
			movie.put("plot", movieJSON.getString("Plot"));
			movie.put("genre", movieJSON.getString("Genre"));
			movie.put("released", movieJSON.getString("Released"));
			movie.put("director", movieJSON.getString("Director"));
			movie.put("actors", movieJSON.getString("Actors"));
			movie.put("runtime", movieJSON.getString("Runtime"));
			movie.put("country", movieJSON.getString("Country"));
			movie.put("imdbRating", movieJSON.getString("imdbRating"));
		} catch (IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.addObject("movie", movie);
		modelAndView.setViewName("/movie");
		return modelAndView;
	}
	
	
}
