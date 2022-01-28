package evaluation;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import datatypes.*;
import miners.*;

/*
 * Calculate the hit rate metric calling the method run()
 * Inputs:
 * existingConnections: Map containing as keys the components of the testing set and as values the components they are connected to. 
 * componentMiner: Instance of ComponentMiner containing the graph between components created by the training set
 * 
 */

public class HitRate implements Evaluate{

	ComponentMiner componentMiner;
	Map<Set<Component>, Set<Component>> existingConnections;
	int numOfRecommendedComponents;
	
	
	public HitRate(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections, int numOfRecommendedComponents) {
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		this.numOfRecommendedComponents = numOfRecommendedComponents;
	}
	
	@Override
	public void run() {

		int sizeOfTestingSet = existingConnections.size();
		int hits = 0;
		
		Coverage coverage = new Coverage();
//		coverage.setGraph(componentMiner.getComponentGraph());

		
		//For every Component in the testing set compare the existing Connections with the predictions from the recommendation system. 
		//First, get top 10 recommended Components
		for(Entry<Set<Component>, Set<Component>> existingConnection : existingConnections.entrySet()) {
			
			if(existingConnection.getKey().isEmpty() || existingConnection.getValue().isEmpty()) {
				sizeOfTestingSet--;
				continue;
			}
			
			RankedComponents rc = new RankedComponents(componentMiner.componentMining(existingConnection.getKey()));
			Map<Component, Double> topComponents = rc.getTopComponents(numOfRecommendedComponents);
			
//			coverage.addToRecommendedComponents(topComponents);
			
			//Search for at least one hit
			for(Map.Entry<Component, Double> recommendedComp : topComponents.entrySet()) {
				//System.out.println("recommendedComp: "+recommendedComp.getKey()+" for keyword: "+ entry.getKey());
				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					hits++;
					break;
				}
			}
		}
		
		System.out.println("Hit Rate: " + (float) hits/sizeOfTestingSet);
		System.out.println("Coverage: " + coverage.getCoverage());
		
	}

}
