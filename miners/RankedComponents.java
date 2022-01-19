package miners;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import datatypes.*;

/*
 * Return top-N recommended components. 
 * Use as input for the constructor a Map containing the components and their corresponding scores
 * Use as input for the method getTopComponents the number n of the top components to return
 * 
 */
public class RankedComponents {
	
	Map<Component, Double> componentScores;
	

	public RankedComponents(Map<Component, Double> scores) {
		this.componentScores = scores;
		
	}
	
	// return top-n libraries
	public Map<Component, Double> getTopComponents(int n){
		//sort and return top n
		Map<Component, Double> topComponents = new LinkedHashMap<>();
		Map<Component, Double> sortedComponents = componentScores.entrySet()
				.stream()
                .sorted(Map.Entry.<Component, Double> comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
		
		for(Map.Entry<Component, Double> entry : sortedComponents.entrySet()) {
			topComponents.put(entry.getKey(), entry.getValue());
			n-=1;
			if(n==0)
				break;
		}
		return topComponents;
	}	
}
