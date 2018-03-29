package edu.northeastern.cs4500.controllers;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
import edu.northeastern.cs4500.model.movie.MovieRating;
import edu.northeastern.cs4500.model.movie.MovieReview;
import edu.northeastern.cs4500.model.services.IOmdbService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectService;
import edu.northeastern.cs4500.model.services.MovieRatingService;
import edu.northeastern.cs4500.model.services.OmdbSQLconnectService;
import edu.northeastern.cs4500.model.services.OmdbServiceImpl;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Controller
public class MovieController {

    private LocalSQLConnectService localSQLConnector = new LocalSQLConnectService();
    private IOmdbService omdbService = new OmdbServiceImpl();
    private OmdbSQLconnectService localDbConnector = new OmdbSQLconnectService();
    private ArrayList<String> movieNames = new ArrayList<>();
    
    private static final Logger logger = LogManager.getLogger(MovieController.class);

    @Autowired
    private MovieRatingService movieRatingService;
    
    @Autowired
    private UserService userService;

    @RequestMapping(value = { "/search" }, method = RequestMethod.GET)
    public ModelAndView searchResult(@RequestParam("title") String searchParam) {
	JSONObject movieJSON = new JSONObject();
	List<Movie> movieList = new ArrayList<Movie>();
	List<User> userList = new ArrayList<User>();

	// get list of movies, only 5
	try {
	    movieJSON = omdbService.searchMovieByTitle(searchParam, "s");
	    
	    JSONArray movieJSONList = movieJSON.getJSONArray("Search");
	    
	    int x = 0;
	    while (x < 5) {
	    	Movie movie = new Movie();
		    movieJSON = omdbService.searchMovieByTitle(movieJSONList.getJSONObject(x).getString("Title"), "t");
	    	movie.setTitle(movieJSON.getString("Title"));
	    	movie.setPoster(movieJSON.getString("Poster"));
	    	movie.setActors(movieJSON.getString("Actors"));
	    	movie.setReleased(movieJSON.getString("Released"));
	    	movie.setImdbRating(movieJSON.getString("imdbRating"));
	    	movieList.add(movie);
	    	movieNames.add(movie.getTitle());
	    	System.out.println("Poster"+ movie.getPoster());
	    	x++;
	    }
	    
	    // to add multiple movies from search results.
	    this.addMoviesIntoLocalDB();
	    

	    
	} catch (IOException | JSONException e) {
	    // use logger
	    e.printStackTrace();
	}
	
	// get list of users
	userList = localSQLConnector.keywordSearchUser(searchParam);
	
	ModelAndView modelAndView = new ModelAndView();
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	User user = userService.findUserByEmail(auth.getName());
	modelAndView.addObject("user", user);
	modelAndView.addObject("movie", movieList);
	modelAndView.addObject("users", userList);
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
	    movie.put("poster", movieJSON.getString("Poster"));
	    movie.put("imdbRating", movieJSON.getString("imdbRating"));
	    movie.put("imdbID", movieJSON.getString("imdbID"));
	    
	    // to add seached movie into local database
	    localDbConnector.catchMovie(movieJSON);
	    localDbConnector.loadMovieToLocalDB();
	    
	} catch (IOException | JSONException e) {
	    logger.error(e.getMessage());
	}
	
	ModelAndView modelAndView = new ModelAndView();
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	User user = userService.findUserByEmail(auth.getName());
	modelAndView.addObject("user", user);
	modelAndView.addObject("movie", movie);
	
	if (user != null) {
	    int rating = localSQLConnector.getRating(user.getId(), movie.get("imdbID"));
	    modelAndView.addObject("rating", rating);
	    modelAndView.addObject("userId", user.getId());
	}
	
	modelAndView.setViewName("movie");
	return modelAndView;
    }
    
    @RequestMapping(value= "/movie/rating",method=RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
        public void setRating(@RequestBody String rating, HttpServletRequest httpServletRequest) {
    	MovieRating movieRating = new MovieRating();	
        	movieRating.setMovieId(httpServletRequest.getParameter("movieId"));
        	movieRating.setRating(Double.valueOf(httpServletRequest.getParameter("rating")));
        	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        movieRating.setDate(formatter.format(new Date()));
        	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        	User user = userService.findUserByEmail(auth.getName());
        	
        	if (user != null) {
        	    movieRating.setUserID(user.getId());
        	    movieRatingService.saveMovieRating(movieRating);
        	} 
        	
    }
        
    @RequestMapping(value="/writeReview", method=RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void writeReview(@RequestBody String review, HttpServletRequest httpServletRequest) {
    	MovieReview movieReview = new MovieReview();	
    	movieReview.setMovie_id(httpServletRequest.getParameter("movieId"));
    	movieReview.setReview(httpServletRequest.getParameter("review"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	movieReview.setDate(formatter.format(new Date()));
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	User user = userService.findUserByEmail(auth.getName());
    	movieReview.setUser_id(String.valueOf(user.getId()));
    	movieReview.setUsername(user.getUsername());
    	LocalSQLConnectService db = new LocalSQLConnectService();
    	db.addReviewToLocalDB(movieReview);
    }
    
    /**
     * To add movie from searched result to local database
     */
    private void addMoviesIntoLocalDB() {
    	localDbConnector.addMultiMovies(movieNames);
    }
}
