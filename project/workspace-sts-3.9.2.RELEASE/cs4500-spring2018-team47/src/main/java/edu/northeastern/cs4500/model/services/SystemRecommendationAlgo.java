package edu.northeastern.cs4500.model.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class SystemRecommendationAlgo {
	Map<String,Map<String, Double>> data;
	Map<String,Map<String, Double>> diff;
	Map<String,Map<String, Integer>> freq;
	
	public SystemRecommendationAlgo(Map<String,Map<String,Double>> data) {
		this.data = data;
		buildDiffMatrix();
	}
	
	/*
	 * Takes in a user and returns a list of recommended movies based off of slopeOne.
	 */
	public List<String> predict(String user)
    {
		Map<String, Double> getUserData = data.get(user);
        HashMap<String, Double> predictions = new HashMap<>();
        HashMap<String, Integer> frequencies = new HashMap<>();
        for (String movieID : diff.keySet())
        {
            frequencies.put(movieID, 0);
            predictions.put(movieID, 0.0);
        }
        System.out.println("pred before: "+ predictions.values());
        try {
        for (String movieID : getUserData.keySet())
        {
            for (String username : diff.keySet())
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
        }
        System.out.println("predictions after: "+ predictions.values());
        List<String> cleanPredictions = new ArrayList<>();
        for (String movieID : predictions.keySet())
        {
            if (frequencies.get(movieID) > 0)
            {
            	cleanPredictions.add(movieID);
            }
        }
        try {
        for (String movieID : getUserData.keySet())
        {
        	if (!predictions.containsKey(movieID)) {
               cleanPredictions.add(movieID);
        	}
        	else {
        		cleanPredictions.remove(movieID);
        	}
        }
        }
        catch (Exception e) {
        }
        
        
        return cleanPredictions;
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
		
		for (Map<String, Double> user : data.values()) {
			
			for (Entry<String, Double> e : user.entrySet()) {
				if (!diff.containsKey(e.getKey())) {
					diff.put(e.getKey(), new HashMap<String, Double>());
					freq.put(e.getKey(), new HashMap<String, Integer>());
				}
				for (Entry<String, Double> e2 : user.entrySet()) {
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
		for (String username : diff.keySet()) {
			for (String movieID : diff.get(username).keySet()) {
				double oldValue = diff.get(username).get(movieID).doubleValue();
				int count = freq.get(username).get(movieID).intValue();
				diff.get(username).put(movieID, oldValue / count);
			}
		}
	}
}
