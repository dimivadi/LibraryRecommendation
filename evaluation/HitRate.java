package evaluation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import datatypes.*;
import miners.*;

/*
 * Calculate the hit rate metric calling the method run()
 * Inputs:
 * existingConnections: Map containing as keys the components of the testing set and as values the components they are connected to. 
 * cm: Instance of ComponentMiner containing the graph between components created by the training set
 * 
 */

public class HitRate extends Evaluate{

	int hits = 0;
	int total = 0;
	
	public HitRate(String trainingSet, String testingSet, String filesExtensions) throws FileNotFoundException {
		super(trainingSet, testingSet, filesExtensions);

	}
	
//	public float run(Map<Component, Set<Component>> existingConnections, ComponentMiner cm) {
	@Override
	public void run() {
		Map<Component, Double> recommendedComponents;
		
		//For every Component in the testing set compare the existing Connections with the predictions from the recommendation system. 
		//First, get top 3 recommended Components
		for(Entry<Component, Set<Component>> entry : existingConnections.entrySet()) {
			
			RecommendedComponents rc = new RecommendedComponents(componentMiner.componentMining(entry.getKey()));
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
		
		System.out.println("Hit Rate: " + (float) hits/total);
		
	}

}
