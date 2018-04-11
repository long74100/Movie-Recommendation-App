package edu.northeastern.cs4500.model.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.northeastern.cs4500.model.movie.Movie;
import edu.northeastern.cs4500.model.user.User;

public class systemRecommendationAlgo {
	Map<User,Map<Movie, Double>> data;
	Map<Movie,Map<Movie, Double>> diff;
	Map<Movie,Map<Movie, Integer>> freq;
	
	public systemRecommendationAlgo(Map<Movie,Double> data) {
		this.data = data;
		buildDiffMatrix();
	}
	
	public void buildDiffMatrix() {
		diff = new HashMap<>();
		freq = new HashMap<>();
		
		for (Map<Movie, Double> user : data.values()) {
			
			for (Entry<Movie, Double> e : user.entrySet()) {
				if (!diff.containsKey(e.getKey())) {
					diff.put(e.getKey(), new HashMap<Movie, Double>());
					freq.put(e.getKey(), new HashMap<Movie, Integer>());
				}
				
				
			}
			
		}
		
	}

}
