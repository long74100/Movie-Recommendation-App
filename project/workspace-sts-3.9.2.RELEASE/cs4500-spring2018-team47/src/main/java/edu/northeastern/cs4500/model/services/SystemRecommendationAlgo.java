package edu.northeastern.cs4500.model.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import edu.northeastern.cs4500.model.user.User;

public class SystemRecommendationAlgo {
	Map<String,Map<String, Double>> data;
	Map<String,Map<String, Double>> diff;
	Map<String,Map<String, Integer>> freq;
	
	public SystemRecommendationAlgo(Map<String,Map<String,Double>> data) {
		this.data = data;
		buildDiffMatrix();
	}
	
	public Map<String, Double> predict(String user)
    {
		Map<String, Double> getUserData = data.get(user);
        HashMap<String, Double> predictions = new HashMap<>();
        HashMap<String, Integer> frequencies = new HashMap<>();
        for (String x : diff.keySet())
        {
            frequencies.put(x, 0);
            predictions.put(x, 0.0);
        }
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
        HashMap<String, Double> cleanpredictions = new HashMap<>();
        for (String movieID : predictions.keySet())
        {
            if (frequencies.get(movieID) > 0)
            {
                cleanpredictions.put(movieID, predictions.get(movieID) / frequencies.get(movieID).intValue());
            }
        }
        for (String x : getUserData.keySet())
        {
            cleanpredictions.put(x, getUserData.get(x));
        }
        return cleanpredictions;
    }
	
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
		
		for (String x : diff.keySet()) {
			for (String y :diff.get(x).keySet()) {
				double oldValue = diff.get(x).get(y).doubleValue();
				int count = freq.get(x).get(y).intValue();
				diff.get(x).put(y, oldValue / count);
			}
		}
	}
}
