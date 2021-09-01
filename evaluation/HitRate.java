package evaluation;

import java.util.ArrayList;
import java.util.Map;

import datatypes.*;
import miners.*;

/*
 * Calculate the hit rate metric calling the method run()
 * Inputs:
 * existingConnections: Map containing as keys the components of the testing set and as values the components they are connected to. 
 * cm: Instance of ComponentMiner containing the graph between components created by the training set
 * 
 */

public class HitRate {
	
	int hits = 0;
	int total = 0;
	
	public float run(Map<Component, ArrayList<Component>> existingConnections, ComponentMiner cm) {
	
		Map<Component, Double> recommendedComponents;
		
		//For every Component in the testing set compare the existing Connections with the predictions from the recommendation system. 
		for(Map.Entry<Component, ArrayList<Component>> entry : existingConnections.entrySet()) {
			
			RecommendedComponents rc = new RecommendedComponents(cm.componentMining(entry.getKey()));
			recommendedComponents = rc.getTopComponents(3);
			
			int t = 0;
			
			//Search for at least one hit
			for(Map.Entry<Component, Double> recommendedComp : recommendedComponents.entrySet()) {
				for(Component comp : entry.getValue()) {
					if(recommendedComp == comp) {
						hits++;
						t = 1;
						break;
					}
				}
				if(t == 1)
					break;
			}
			
			total++;
			
		}
		
		return hits/total;
		
	}
	
}
