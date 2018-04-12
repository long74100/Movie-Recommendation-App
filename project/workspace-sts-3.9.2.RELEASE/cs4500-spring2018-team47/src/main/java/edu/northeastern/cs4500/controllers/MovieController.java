package edu.northeastern.cs4500.controllers;

import java.io.IOException;
import java.sql.SQLException;
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
import edu.northeastern.cs4500.model.services.MovieDBServiceImpl;
import edu.northeastern.cs4500.model.services.UserService;
import edu.northeastern.cs4500.model.user.User;

@Controller
public class MovieController {
	
	@Autowired
	private ILocalSQLConnectService localDbConnector;

	private IMovieDBService movieDbService = new MovieDBServiceImpl();
	

	private ArrayList<String> movieIDs = new ArrayList<>();

	private static final Logger logger = LogManager.getLogger(MovieController.class);

	
	private static final String TITLE = "title";
	private static final String IMDBID = "imdb_id";
	private static final String MOVIE = "movie";
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
				movie.setTitle(movieJSON.getString(TITLE));
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
				movie.setImdbID(movieDetails.getString(IMDBID));
				movie.setTheMovieDbID(String.valueOf(movieJSON.getInt("id")));
				movie.setPoster("http://image.tmdb.org/t/p/original/" + movieJSON.getString("poster_path"));
				movieList.add(movie);
				movieIDs.add(movie.getTheMovieDbID());
				x++;
			}


		} catch (IOException | JSONException e) {
			// use logger
			logger.error(e.getMessage());
		}
		
		

		// get list of users
		try {
			userList = localDbConnector.keywordSearchUser(searchParam);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		List<User> firendList = new ArrayList<>();
		
		if(user != null) {
			// get User's friends:
			try {
				firendList = localDbConnector.getAllFriends(user.getId());
			}
			catch(SQLException sq) {
				logger.error(sq.getMessage());
			}
			modelAndView.addObject("user", user);
			modelAndView.addObject("friendList", firendList);
		}
		else {
			modelAndView.addObject("friendList", new ArrayList<User>());
		}
		
		modelAndView.addObject("movie", movieList);
		modelAndView.addObject("user", user);
		modelAndView.addObject(MOVIE, movieList);
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
			String director = "";
			try {
				for (int y = 0; y < 5; y++) {
					actors += movieCast.getJSONObject(y).getString("name");
					if (y != 4) {
						actors += ", ";
					}
				}
				
				for (int y = 0; y < movieCrew.length(); y++) {
					if (movieCrew.getJSONObject(y).getString("job").equals("Director")) {
						director = movieCrew.getJSONObject(y).getString("name");
					}
				}
			}
			catch(NullPointerException np) {
				logger.error(np.getMessage());
			}
			
			
			// add all the genres
			JSONArray genreList = movieJSON.getJSONArray("genres");
			StringBuilder genre = new StringBuilder();
			try {
				for(int i = 0; i < genreList.length(); i++) {
					if(i == genreList.length() - 1) {
						genre.append(genreList.getJSONObject(i).getString("name"));
					}
					else {
						genre.append(genreList.getJSONObject(i).getString("name") + ", ");
					}
				}
			}
			catch(NullPointerException np) {
				logger.error(np.getMessage());
			}
			
			
			// add all the countries
			JSONArray countries = movieJSON.getJSONArray("production_countries");
			StringBuilder contry = new StringBuilder();
			try {
				for(int i = 0; i < countries.length(); i++) {
					if(i == countries.length() - 1) {
						contry.append(countries.getJSONObject(i).getString("name"));
					}
					else {
						contry.append(countries.getJSONObject(i).getString("name") + ", ");
					}
				}
			}
			catch(NullPointerException np) {
				logger.error(np.getMessage());
			}
			
			
			// add all the language
			JSONArray languages = movieJSON.getJSONArray("spoken_languages");
			StringBuilder language = new StringBuilder();
			try {
				for(int i = 0; i < languages.length(); i++) {
					if(i == languages.length() - 1) {
						language.append(languages.getJSONObject(i).getString("name"));
					}
					else {
						language.append(languages.getJSONObject(i).getString("name") + ", ");
					}
				}
			}
			catch (NullPointerException np) {
				logger.error(np.getMessage());
			}
			
			try {
				movie.put("director", director);
				movie.put(TITLE, movieJSON.getString(TITLE));
				movie.put("plot", movieJSON.getString("overview"));
				movie.put("genre", genre.toString());
				movie.put("released", movieJSON.getString("release_date"));
				movie.put("actors", actors);
				Object runtime = movieJSON.get("runtime");
				if (runtime.toString().equals("null")) {
					movie.put("runtime", "N/A");
				} else {
					movie.put("runtime", runtime.toString() + " Minutes");
				}
				movie.put("country", contry.toString());
				movie.put("imdbRating", String.valueOf(movieJSON.getInt("vote_average")));
				movie.put("imdbID", movieJSON.getString(IMDBID));
				movie.put("poster", "http://image.tmdb.org/t/p/original/" + movieJSON.getString("poster_path"));
				movie.put("language", language.toString());
				movie.put("movieDBid", id);
			}
			catch(Exception ep) {
				logger.error(ep.getMessage());
			}
			
			// to update the browse history
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			User user = userService.findUserByEmail(auth.getName());
			if(user != null) {
				Integer userId = user.getId();
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					localDbConnector.addMovieIntoMovieList(userId, "Browse History", 
							movie.get("imdbID"), movie.get("title"), formatter.format(new Date()));
				}
				catch(SQLException sq) {
					logger.error(sq.getMessage());
				}
				
			}
			try {
				localDbConnector.loadMovieIntoLocalDB(movie);
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}

			try {
				reviews = localDbConnector.getReviewsForMovie(movieJSON.getString(IMDBID));
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
			

		} catch (IOException | JSONException e) {
			logger.error(e.getMessage());
		}

		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());

		modelAndView.addObject("reviews", reviews);
		modelAndView.addObject("user", user);
		modelAndView.addObject(MOVIE, movie);

		if (user != null) {
			// to get user's friend List
			
			List<User> friendList = new ArrayList<>();
			try {
				friendList = localDbConnector.getAllFriends(user.getId());
			}
			catch(SQLException sq) {
				logger.error(sq.getMessage());
			}
			modelAndView.addObject("friendList", friendList);

			try {
			// To extract user movie list
			List<String> userMovieList = localDbConnector.getMovieListForUser(user.getId());
			modelAndView.addObject("userMVlist", userMovieList);
			int rating = localDbConnector.getRating(user.getId(), movie.get("imdbID"));
			modelAndView.addObject("rating", rating);
			modelAndView.addObject("userId", user.getId());
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		} else {
			modelAndView.addObject("userMVlist", new ArrayList<String>());
		}

		modelAndView.setViewName(MOVIE);
		return modelAndView;
	}

	@RequestMapping(value = "/movie/rating", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void setRating(@RequestBody String rating, HttpServletRequest httpServletRequest) {
		MovieRating movieRating = new MovieRating();
		movieRating.setMovieId(httpServletRequest.getParameter("movieId"));
		movieRating.setRating(Double.valueOf(httpServletRequest.getParameter("rating")));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		movieRating.setDate(formatter.format(new Date()));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		
		if (user != null) {
			movieRating.setUserID(user.getId());
			try {
				localDbConnector.insertRating(movieRating);
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}
	

	@RequestMapping(value = "/movie/removeReview", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void removeReview(@RequestBody String review, HttpServletRequest httpServletRequest) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		
		String userId = httpServletRequest.getParameter("userId");
		String reviewId = httpServletRequest.getParameter("reviewId");
		
		if (user != null && ((Integer.valueOf(userId) == user.getId()) || user.getRole() == 2)) {
		    try {
				localDbConnector.removeReview(Integer.valueOf(reviewId));
			} catch (NumberFormatException e) {
				logger.error(e.getMessage());
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}

	@RequestMapping(value = "/writeReview", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void writeReview(@RequestBody String review, HttpServletRequest httpServletRequest) {
		MovieReview movieReview = new MovieReview();
		movieReview.setMovie_id(httpServletRequest.getParameter("movieId"));
		movieReview.setReview(httpServletRequest.getParameter("review"));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		movieReview.setDate(formatter.format(new Date()));
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		movieReview.setUser_id(String.valueOf(user.getId()));
		movieReview.setUsername(user.getUsername());
		try {
			localDbConnector.addReviewToLocalDB(movieReview);
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
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
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			localDbConnector.addMovieIntoMovieList(userId, listname, movieId, movieName, formatter.format(new Date()));
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
	}

	
}
