package edu.northeastern.cs4500.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.movie.MovieRating;
import edu.northeastern.cs4500.model.movie.MovieReview;
import edu.northeastern.cs4500.model.services.IOmdbService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectService;
import edu.northeastern.cs4500.model.services.MovieRatingService;
import edu.northeastern.cs4500.model.services.OmdbSQLconnectService;
import edu.northeastern.cs4500.model.services.OmdbServiceImpl;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.session.SessionService;
import edu.northeastern.cs4500.model.session.SessionServiceImpl;
import edu.northeastern.cs4500.model.user.User;

@Controller
public class MovieController {

    private IOmdbService omdbService = new OmdbServiceImpl();
    private SessionService sessionService = new SessionServiceImpl();
    private OmdbSQLconnectService localDbConnector = new OmdbSQLconnectService();
    
    @Autowired
    private MovieRatingService movieRatingService;
    
    @Autowired
    private UserService userService;

    @RequestMapping(value = { "/search" }, method = RequestMethod.GET)
    public ModelAndView searchResult(@RequestParam("title") String searchParam) {
	JSONObject movieJSON = new JSONObject();
	List<Movie> movieList = new ArrayList<Movie>();
	List<User> userList = new ArrayList<User>();

	try {
	    movieJSON = omdbService.searchMovieByTitle(searchParam, "s");
	    
	    JSONArray movieJSONList = movieJSON.getJSONArray("Search");
	    
	    int x = 0;
	    while (x < 5) {
	    	Movie movie = new Movie();
		    movieJSON = omdbService.searchMovieByTitle(movieJSONList.getJSONObject(x).getString("Title"), "t");
	    	movie.setTitle(movieJSON.getString("Title"));
	    	movie.setActors(movieJSON.getString("Actors"));
	    	movie.setReleased(movieJSON.getString("Released"));
	    	movie.setImdbRating(movieJSON.getString("imdbRating"));
	    	movieList.add(movie);
	    	
	    	User user = new User();
	    	if(userService.findUserByUsername(searchParam) != null) {
		    	user = userService.findUserByUsername(searchParam);
		    	userList.add(user);
	    	}
	    	x++;
	    }
	    

	    
	} catch (IOException | JSONException e) {
	    // use logger
	    e.printStackTrace();
	}

	ModelAndView modelAndView = new ModelAndView();
	modelAndView.addObject("movie", movieList);
	modelAndView.addObject("user", userList);
	modelAndView.setViewName("searchResult");
	return modelAndView;
    }
    
    @RequestMapping(value="/movie/{title}", method = RequestMethod.GET)
    public ModelAndView movieResult(@PathVariable String title) {
	JSONObject movieJSON = new JSONObject();
	Map<String, String> movie = new HashMap<String, String>();

	try {
	    movieJSON = omdbService.searchMovieByTitle(title, "t");
	    movie.put("title", movieJSON.getString("Title"));
	    movie.put("plot", movieJSON.getString("Plot"));
	    movie.put("genre", movieJSON.getString("Genre"));
	    movie.put("released", movieJSON.getString("Released"));
	    movie.put("director", movieJSON.getString("Director"));
	    movie.put("actors", movieJSON.getString("Actors"));
	    movie.put("runtime", movieJSON.getString("Runtime"));
	    movie.put("country", movieJSON.getString("Country"));
	    movie.put("imdbRating", movieJSON.getString("imdbRating"));
	    movie.put("imdbId", movieJSON.getString("imdbID"));
	    
	    // to add seached movie into local database
	    localDbConnector.catchMovie(movieJSON);
	    localDbConnector.loadMovieToLocalDB();
	    
	} catch (IOException | JSONException e) {
	    // use logger
	    e.printStackTrace();
	}
	ModelAndView modelAndView = new ModelAndView();
	modelAndView.addObject("movie", movie);
	modelAndView.setViewName("movie");
	return modelAndView;
    }

    @RequestMapping(value= "/movie/rating",method=RequestMethod.POST)
    public @ResponseBody MovieRating setRating(@RequestBody String rating, HttpServletRequest httpServletRequest) {
	try {
	    JSONObject json = new JSONObject(rating);
		MovieRating movieRating = new MovieRating();
		movieRating.setMovieName(json.getString("movie"));
		movieRating.setRating(Double.valueOf(json.getString("rating")));
		Random rand = new Random();
		int  n = rand.nextInt(10) + 1;
		movieRating.setUserID(n);
		movieRatingService.saveMovieRating(movieRating);
	} catch (JSONException e) {
		
	    // use logger
	}
	return new MovieRating();
    }
    
    
    @RequestMapping(value="/writeReview", method=RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void writeReview(@RequestBody String review, HttpServletRequest httpServletRequest) {
    	MovieReview movieReview = new MovieReview();
    	movieReview.setMovie_id(httpServletRequest.getParameter("imdbID"));
    	movieReview.setReview(httpServletRequest.getParameter("review"));
    	movieReview.setDate(new Date());
    	//grab userid somehow
    	movieReview.setUser_id(-1);
    	LocalSQLConnectService db = new LocalSQLConnectService();
    	db.uploadReview(movieReview);
    }
    
    private void addMovieIntoLocalDB() {
    	
    }

}
