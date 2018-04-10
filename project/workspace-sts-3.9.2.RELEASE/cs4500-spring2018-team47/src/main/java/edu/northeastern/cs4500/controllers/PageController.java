package edu.northeastern.cs4500.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.IMovieDBService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.services.MovieDBServiceImpl;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;

/**
 * Spring MVC Controller to handle mappings for default pages.
 *
 * Adding a comment for smart commits
 */
@Controller
public class PageController {
	
	private IMovieDBService movieDbService = new MovieDBServiceImpl();
	private ILocalSQLConnectService localDbConnector = new LocalSQLConnectServiceImpl();
	
	private static final Logger logger = LogManager.getLogger(PageController.class);
	
	@Autowired
    private UserService userService;
	
	@RequestMapping(value={"/"}, method = RequestMethod.GET)
	public ModelAndView login(){
		JSONObject movieJSON = new JSONObject();
		List<Movie> movieList = new ArrayList<Movie>();

		// get list of movies, only 10
		try {
			movieJSON = movieDbService.discoverInTheaterMovies();
			JSONArray movieJSONList = movieJSON.getJSONArray("results");

			int x = 0;
			while (x < 10) {
				Movie movie = new Movie();
				movieJSON = movieJSONList.getJSONObject(x);
				movie.setTitle(movieJSON.getString("title"));
				movie.setPlot(movieJSON.getString("overview"));
				movie.setReleased(movieJSON.getString("release_date"));
				movie.setImdbRating(String.valueOf(movieJSON.getDouble("vote_average")));
				movie.setTheMovieDbID(String.valueOf(movieJSON.getInt("id")));
				movie.setPoster("http://image.tmdb.org/t/p/original/" + movieJSON.getString("poster_path"));
				movieList.add(movie);
				x++;
			}


		} catch (IOException | JSONException e) {
			// use logger
			logger.error(e.getMessage());
		}
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		modelAndView.addObject("movie", movieList);
		modelAndView.setViewName("index");
		return modelAndView;
	}
	
}
