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
	
	public Map<String, Double> predict(Map<String, Double> user)
    {
        HashMap<String, Double> predictions = new HashMap<>();
        HashMap<String, Integer> frequencies = new HashMap<>();
        for (String x : diff.keySet())
        {
            frequencies.put(x, 0);
            predictions.put(x, 0.0);
        }
        for (String x : user.keySet())
        {
            for (String y : diff.keySet())
            {
                try
                {
                    Double newval = (diff.get(x).get(x) + user.get(x)) * freq.get(y).get(x).intValue();
                    predictions.put(y, predictions.get(y) + newval);
                    frequencies.put(y, frequencies.get(y) + freq.get(y).get(x).intValue());
                } catch (NullPointerException e)
                {}
            }
        }
        HashMap<String, Double> cleanpredictions = new HashMap<>();
        for (String x : predictions.keySet())
        {
            if (frequencies.get(x) > 0)
            {
                cleanpredictions.put(x, predictions.get(x) / frequencies.get(x).intValue());
            }
        }
        for (String x : user.keySet())
        {
            cleanpredictions.put(x, user.get(x));
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
