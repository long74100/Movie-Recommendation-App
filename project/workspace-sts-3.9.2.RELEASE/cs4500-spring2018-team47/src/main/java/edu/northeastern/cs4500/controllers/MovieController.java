package edu.northeastern.cs4500.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import edu.northeastern.cs4500.model.services.ILocalSQLConnectService;
import edu.northeastern.cs4500.model.services.IMovieDBService;
import edu.northeastern.cs4500.model.services.LocalSQLConnectServiceImpl;
import edu.northeastern.cs4500.model.services.MovieDBServiceImpl;
import edu.northeastern.cs4500.model.services.MovieRatingService;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;

@Controller
public class MovieController {

	private IMovieDBService movieDbService = new MovieDBServiceImpl();
	private ILocalSQLConnectService localDbConnector = new LocalSQLConnectServiceImpl();

	private ArrayList<String> movieIDs = new ArrayList<>();

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
			movieJSON = movieDbService.searchMovieListByTitle(searchParam);
			JSONArray movieJSONList = movieJSON.getJSONArray("results");

			int x = 0;
			while (x < 5) {
				Movie movie = new Movie();
				movieJSON = movieJSONList.getJSONObject(x);
				JSONObject movieCast = movieDbService.searchMovieCast(movieJSON.getInt("id"));
				JSONObject movieDetails = movieDbService.searchMovieDetails(movieJSON.getInt("id"));
				movie.setTitle(movieJSON.getString("title"));
				String actors = "";
				for (int y = 0; y < 5; y++) {
					actors += movieCast.getJSONArray("cast").getJSONObject(y).getString("name");
					if (y != 4) {
						actors += ", ";
					}
				}
				movie.setPlot(movieDetails.getString("overview"));
				movie.setActors(actors);
				movie.setReleased(movieJSON.getString("release_date"));
				movie.setImdbRating(String.valueOf(movieJSON.getDouble("vote_average")));
				movie.setImdbID(movieDetails.getString("imdb_id"));
				movie.setTheMovieDbID(String.valueOf(movieJSON.getInt("id")));
				movie.setPoster("http://image.tmdb.org/t/p/w185/" + movieJSON.getString("poster_path"));
				movieList.add(movie);
				movieIDs.add(movie.getTheMovieDbID());
				x++;
			}

//			 to add multiple movies from search results.
			 this.addMoviesIntoLocalDB();

		} catch (IOException | JSONException e) {
			// use logger
			logger.error(e.getMessage());
		}

		// get list of users
		userList = localDbConnector.keywordSearchUser(searchParam);

		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());

		modelAndView.addObject("user", user);
		modelAndView.addObject("movie", movieList);
		modelAndView.addObject("users", userList);
		modelAndView.setViewName("searchResult");
		return modelAndView;
	}

	@RequestMapping(value = "/movie/{id}", method = RequestMethod.GET)
	public ModelAndView movieResult(@PathVariable String id) {
		JSONObject movieJSON = new JSONObject();
		Map<String, String> movie = new HashMap<String, String>();
		List<MovieReview> reviews = null;

		try {
			movieJSON = movieDbService.searchMovieDetails(Integer.valueOf(id));
			JSONArray movieCast = movieDbService.searchMovieCast(movieJSON.getInt("id")).getJSONArray("cast");
			JSONArray movieCrew = movieDbService.searchMovieCast(movieJSON.getInt("id")).getJSONArray("crew");

			String actors = "";
			for (int y = 0; y < 5; y++) {
				actors += movieCast.getJSONObject(y).getString("name");
				if (y != 4) {
					actors += ", ";
				}
			}
			String director = "";
			for (int y = 0; y < movieCrew.length(); y++) {
				if (movieCrew.getJSONObject(y).getString("job").equals("Director")) {
					director = movieCrew.getJSONObject(y).getString("name");
				}
			}
			
			// add all the genres
			JSONArray genreList = movieJSON.getJSONArray("genres");
			StringBuilder genre = new StringBuilder();
			for(int i = 0; i < genreList.length(); i++) {
				if(i == genreList.length() - 1) {
					genre.append(genreList.getJSONObject(i).getString("name"));
				}
				else {
					genre.append(genreList.getJSONObject(i).getString("name") + ", ");
				}
			}
			
			// add all the countries
			JSONArray countries = movieJSON.getJSONArray("production_countries");
			StringBuilder contry = new StringBuilder();
			for(int i = 0; i < countries.length(); i++) {
				if(i == countries.length() - 1) {
					contry.append(countries.getJSONObject(i).getString("name"));
				}
				else {
					contry.append(countries.getJSONObject(i).getString("name") + ", ");
				}
			}
			
			// add all the language
			JSONArray languages = movieJSON.getJSONArray("spoken_languages");
			StringBuilder language = new StringBuilder();
			for(int i = 0; i < languages.length(); i++) {
				if(i == languages.length() - 1) {
					language.append(languages.getJSONObject(i).getString("name"));
				}
				else {
					language.append(languages.getJSONObject(i).getString("name") + ", ");
				}
			}
			
			movie.put("director", director);
			movie.put("title", movieJSON.getString("title"));
			movie.put("plot", movieJSON.getString("overview"));
			movie.put("genre", genre.toString());
			movie.put("released", movieJSON.getString("release_date"));
			movie.put("actors", actors);
			movie.put("runtime", String.valueOf(movieJSON.getInt("runtime")));
			movie.put("country", contry.toString());
			movie.put("imdbRating", String.valueOf(movieJSON.getInt("vote_average")));
			movie.put("imdbID", movieJSON.getString("imdb_id"));
			movie.put("poster", "http://image.tmdb.org/t/p/w185/" + movieJSON.getString("poster_path"));
			movie.put("language", language.toString());
			
			// load movie into local db when the user clicks into the movie pages.
			localDbConnector.loadMovieIntoLocalDB(movie);

			reviews = localDbConnector.getReviewsForMovie(movieJSON.getString("imdb_id"));
			

		} catch (IOException | JSONException e) {
			logger.error(e.getMessage());
		}

		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());

		modelAndView.addObject("reviews", reviews);
		modelAndView.addObject("user", user);
		modelAndView.addObject("movie", movie);

		if (user != null) {
			// To extract user movie list
			List<String> userMovieList = localDbConnector.getMovieListForUser(user.getId());
			modelAndView.addObject("userMVlist", userMovieList);
			int rating = localDbConnector.getRating(user.getId(), movie.get("imdbID"));
			modelAndView.addObject("rating", rating);
			modelAndView.addObject("userId", user.getId());
		} else {
			modelAndView.addObject("userMVlist", new ArrayList<String>());
		}

		modelAndView.setViewName("movie");
		return modelAndView;
	}

	@RequestMapping(value = "/movie/rating", method = RequestMethod.POST)
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

	@RequestMapping(value = "/writeReview", method = RequestMethod.POST)
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
		localDbConnector.addReviewToLocalDB(movieReview);
	}

	@RequestMapping(value = "/addMovieToList", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void addMovieToList(@RequestBody String movie, HttpServletRequest httpServletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		Integer userId = user.getId();
		String listname = httpServletRequest.getParameter("movieList");
		String movieId = httpServletRequest.getParameter("movieId");
		String movieName = httpServletRequest.getParameter("movie");
		localDbConnector.addMovieIntoMovieList(userId, listname, movieId, movieName);
	}
<<<<<<< HEAD
=======

	/**
	 * To add movie from searched result to local database
	 */
	private void addMoviesIntoLocalDB() {
		localDbConnector.addMultiMovies(movieIDs);
	}
>>>>>>> 6037e27a88cdfabe779409316a730641094dcb61
}
