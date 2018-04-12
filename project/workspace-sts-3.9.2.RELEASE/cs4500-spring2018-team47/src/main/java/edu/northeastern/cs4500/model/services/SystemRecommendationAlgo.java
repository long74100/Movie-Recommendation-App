package edu.northeastern.cs4500.model.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import edu.northeastern.cs4500.model.movie.Movie;

public class SystemRecommendationAlgo {
	Map<String,Map<Movie, Double>> data;
	Map<Movie,Map<Movie, Double>> diff;
	Map<Movie,Map<Movie, Integer>> freq;
	
	public SystemRecommendationAlgo(Map<String,Map<Movie,Double>> data) {
		this.data = data;
		buildDiffMatrix();
	}
	
	/*
	 * Takes in a user and returns a list of recommended movies based off of slopeOne.
	 */
	public Map<Movie, Double> predict(String user)
    {
		Map<Movie, Double> getUserData = data.get(user);
        HashMap<Movie, Double> predictions = new HashMap<>();
        HashMap<Movie, Integer> frequencies = new HashMap<>();
        for (Movie movieID : diff.keySet())
        {
            frequencies.put(movieID, 0);
            predictions.put(movieID, 0.0);
        }
        System.out.println("pred before: "+ predictions.values());
        try {
        for (Movie movieID : getUserData.keySet())
        {
            for (Movie username : diff.keySet())
            {
                try
                {
                    Double newval = (diff.get(username).get(movieID) + getUserData.get(movieID)) * freq.get(username).get(movieID).intValue();
                    predictions.put(username, predictions.get(username) + newval);
                    frequencies.put(username, frequencies.get(username) + freq.get(username).get(movieID).intValue());
                } catch (NullPointerException e)
                {
                	
                }
            }
        }
        }
        catch (Exception e) {
        	System.out.println("error 1");
        }
        System.out.println("predictions after: "+ predictions.values());
        HashMap<Movie, Double> cleanpredictions = new HashMap<>();
        for (Movie movieID : predictions.keySet())
        {
            if (frequencies.get(movieID) > 0)
            {
                cleanpredictions.put(movieID, predictions.get(movieID) / frequencies.get(movieID).intValue());
            }
        }
        try {
        for (Movie movieID : getUserData.keySet())
        {
        	//if (!predictions.containsKey(movieID)) {
                cleanpredictions.put(movieID, getUserData.get(movieID));
        	//}
        	//else {
        		//cleanpredictions.remove(movieID);
        	//}
        }
        }
        catch (Exception e) {
        	System.out.println("error 2");
        }
        System.out.println("cleanPredict after: "+ cleanpredictions);
        return cleanpredictions;
    }
	
	/*
	 * takes all the data and fills in difference and frequency matrix.
	 * Both matrices all movie's reviewed by all movies' reviewed.
	 * Difference matrix shows how different/similar a movie is to each other (same movie being 0)
	 * Frequency Matrix just shows the number of occurrences for that item
	 */
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
		for (Movie username : diff.keySet()) {
			for (Movie movieID : diff.get(username).keySet()) {
				double oldValue = diff.get(username).get(movieID).doubleValue();
				int count = freq.get(username).get(movieID).intValue();
				diff.get(username).put(movieID, oldValue / count);
			}
		}
		System.out.println("diff table: "+ diff.values());
		System.out.println("freq table: "+ freq.values());
	}
}
