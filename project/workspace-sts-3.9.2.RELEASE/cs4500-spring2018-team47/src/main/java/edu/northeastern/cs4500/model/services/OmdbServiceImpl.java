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

public class OmdbServiceImpl implements IOmdbService{
	
	private static final String apiKey = "a15fa266";
	private static final String apiURL = "http://www.omdbapi.com/?apikey="+apiKey+"&";
	private URL url;
	
	public OmdbServiceImpl() {
	}

	public JSONObject searchMovieByTitle(String title) 
			throws IOException, JSONException {
		Map<String, String> params = new HashMap<>();
		params.put("t", title);
		
		String urlString = addParamsToUrl(apiURL, params);
		url = new URL(urlString);
		
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
		in.close();
		System.out.println(content.toString());
		return new JSONObject(content.toString());
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