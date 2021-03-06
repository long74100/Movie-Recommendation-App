package edu.northeastern.cs4500.model.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDBServiceImpl implements IMovieDBService{
	
	private static final String apiKey = "005e91dcdf4c4742c228833ea398ff7e";
	private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final String apiURL = "https://api.themoviedb.org/3/search/movie?api_key=" + apiKey + "&";
	
	public MovieDBServiceImpl() {
	}
	
	private JSONObject makeRequest(URL url) throws IOException, JSONException {	
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		

		connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		
		connection.connect();
		
		BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		//System.out.println(content.toString());
		in.close();
		return new JSONObject(content.toString());
	}

	@Override
	public JSONObject searchMovieListByTitle(String title) throws IOException, JSONException {
		Map<String, String> params = new HashMap<>();
		params.put("query", title);
		String urlString = addParamsToUrl(apiURL, params);
		URL url = new URL(urlString);
		return makeRequest(url);
	}

	@Override
	public JSONObject searchMovieCast(int movieID) throws IOException, JSONException {
		String urlString = "https://api.themoviedb.org/3/movie/"+ movieID +"/credits?api_key=" + apiKey;
		URL url = new URL(urlString);
		return makeRequest(url);
	}

	@Override
	public JSONObject searchMovieDetails(int movieID) throws IOException, JSONException {
		String urlString = "https://api.themoviedb.org/3/movie/"+ movieID +"?api_key=" + apiKey;
		URL url = new URL(urlString);
		return makeRequest(url); 
	}
	
	//Helper method that adds the parameters to the url correctly
	//Encodes the paramaters for security
	private String addParamsToUrl(String url, Map<String, String> params) throws UnsupportedEncodingException {
		 for (Map.Entry<String, String> entry : params.entrySet()) {
			  String searchType = URLEncoder.encode(entry.getKey(), "UTF-8");
			  String searchValue = URLEncoder.encode(entry.getValue(), "UTF-8");
	          url = url + searchType + "=" + searchValue + "&";
	        }
		 return url;
	}

	@Override
	public JSONObject discoverInTheaterMovies() throws IOException, JSONException {
		Calendar cal = Calendar.getInstance();
		Date date = new Date();
		cal.setTime(date);
		cal.add(Calendar.DATE, -30);
		Date dateBefore30Days = cal.getTime();
		String sdate = sdf.format(dateBefore30Days);
		String edate = sdf.format(date);
		String urlString = "https://api.themoviedb.org/3/discover/movie?page=1&region=US&include_video=false&include_adult=false&sort_by=popularity.desc&language=en-US&primary_release_date.gte="+ sdate +"&primary_release_date.lte=" + edate + "&api_key=" + apiKey;
		URL url = new URL(urlString);
		return makeRequest(url);
	}

	@Override
	public JSONObject discoverPopularMovies() throws IOException, JSONException {
		String urlString = "https://api.themoviedb.org/3/discover/movie?page=1&include_video=false&include_adult=false&sort_by=popularity.desc&language=en-US&" + "&api_key=" + apiKey;
		URL url = new URL(urlString);
		return makeRequest(url);
	}

	@Override
	public JSONObject discoverMoviesComingSoon() throws IOException, JSONException {
		Date date = new Date();
		String edate = sdf.format(date);
		String urlString = "https://api.themoviedb.org/3/discover/movie?language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1&primary_release_date.gte=" + edate + "&api_key=" + apiKey;
		URL url = new URL(urlString);
		return makeRequest(url);
	}
}