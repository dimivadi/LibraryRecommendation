package evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
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
 * componentMiner: Instance of ComponentMiner containing the graph between components created by the training set
 * 
 */

public class HitRate implements Evaluate{

	ComponentMiner componentMiner;
	Map<Set<Component>, Set<Component>> existingConnections;
	
	int hits = 0;
	int total = 0;
	
	
	public HitRate(EvaluationDataSource evaluationDataProvider) {
		this.componentMiner = evaluationDataProvider.getComponentMiner();
		this.existingConnections = evaluationDataProvider.getExistingConnections();
	}
	
	@Override
	public void run() {
		Map<Component, Double> topComponents;
		
		//For every Component in the testing set compare the existing Connections with the predictions from the recommendation system. 
		//First, get top 3 recommended Components
		for(Entry<Set<Component>, Set<Component>> entry : existingConnections.entrySet()) {
			
			RecommendedComponents rc = new RecommendedComponents(componentMiner.componentMining(entry.getKey()));
			topComponents = rc.getTopComponents(3);
			
			int t = 0;
			
			//Search for at least one hit
			for(Map.Entry<Component, Double> recommendedComp : topComponents.entrySet()) {
				//System.out.println("recommendedComp: "+recommendedComp.getKey()+" for keyword: "+ entry.getKey());
				for(Component comp : entry.getValue()) {
					if(recommendedComp.getKey().equals(comp)) {
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
