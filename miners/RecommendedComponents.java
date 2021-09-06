package miners;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import datatypes.*;

/*
 * Return top-N recommended components. 
 * Use as input a ComponentMiner object (the appropriate graph of components)
 * and a seedVector that will be used to the ScoringAlgorithm,
 * or use as input directly a Map containing the Components as keys 
 * and their score as values (e.g. the result of RelatedLibraries.componentMining())
 * Call getComponents() using as input the number N
 * 
 */
public class RecommendedComponents {
	
	Map<Component, Double> componentScores;
	
	/*
	public RecommendedComponents(ComponentMiner cm, Component... seedComponents ) {
		this.componentScores = cm.componentMining(seedComponents);
	}

	*/
	public RecommendedComponents(Map<Component, Double> scores) {
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
