package edu.northeastern.cs4500.model.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class MovieDBServiceImpl implements IMovieDBService{
	
	private static final String apiKey = "005e91dcdf4c4742c228833ea398ff7e";
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
		System.out.println(content.toString());
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
}