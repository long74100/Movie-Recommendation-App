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
	
	public systemRecommendationAlgo(Map<User,Map<Movie,Double>> data) {
		this.data = data;
		buildDiffMatrix();
	}
	
	public Map<Movie, Double> predict(Map<Movie,Double> user) {
		
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
				for (Entry<Movie, Double> e2 : user.entrySet()) {
					int oldCount = 0;
					if (freq.get(e.getKey()).containsKey(e2.getKey())) {
						oldCount = freq.get(e.getKey()).get(e2.getKey()).intValue();
					}
					double oldDiff = 0.0;
					if (diff.get(e.getKey()).containsKey(e2.getKey())) {
						oldDiff = diff.get(e.getKey()).get(e2.getKey()).doubleValue();
					}
					double observedDiff = e.getValue() - e2.getValue();
					freq.get(e.getKey()).put(e2.getKey(), oldCount+1);
					diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
				}
			}
			
		}
		
		for (Movie x : diff.keySet()) {
			for (Movie y :diff.get(x).keySet()) {
				double oldValue = diff.get(x).get(y).doubleValue();
				int count = freq.get(x).get(y).intValue();
				diff.get(x).put(y, oldValue / count);
			}
		}
	}
	


}
