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
	
	private Map<Component, Double> componentScores;
	private Map<Component, Double> sortedComponents;
	

	public RankedComponents(Map<Component, Double> scores) {
		this.componentScores = scores;
		
	}
	
	// return top-n libraries
	public Map<Component, Double> getTopComponents(int n){
		//sort and return top n
		Map<Component, Double> topComponents = new LinkedHashMap<>();
		
		if(sortedComponents == null) 
			sortComponentsByScore();

		for(Map.Entry<Component, Double> entry : sortedComponents.entrySet()) {
			topComponents.put(entry.getKey(), entry.getValue());
			n-=1;
			if(n==0)
				break;
		}
		return topComponents;
	}	
	
	
	
	public void getLibraryPosition(Component library) {
		if(sortedComponents == null) 
			sortComponentsByScore();
		int i = 1;
		for(Map.Entry<Component, Double> entry: sortedComponents.entrySet()) {
			if(entry.getKey().equals(library)) {
				System.out.println("position: "+i);
				break;
			}
			i++;
		}
	}
	
	private void sortComponentsByScore() {
		sortedComponents = componentScores.entrySet()
				.stream()
                .sorted(Map.Entry.<Component, Double> comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
}
