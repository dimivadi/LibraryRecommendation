package evaluation;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import datatypes.Component;
import miners.ComponentMiner;
import miners.RecommendedComponents;

public class Precision implements Evaluate{

	ComponentMiner componentMiner;
	Map<Set<Component>, Set<Component>> existingConnections;
	
	
	public Precision(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections){
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
	}
	
	public void run() {
		
		double precision = 0;
		int sizeOfTestingSet = existingConnections.size();
		
		Coverage coverage = new Coverage();
		coverage.setGraph(componentMiner.getComponentGraph());
		
		for(Entry<Set<Component>, Set<Component>> existingConnection : existingConnections.entrySet()) {
			
			if(existingConnection.getKey().isEmpty() || existingConnection.getValue().isEmpty()) {
				sizeOfTestingSet--;
				continue;
			}

			RecommendedComponents rc = new RecommendedComponents(componentMiner.componentMining(existingConnection.getKey()));
			Map<Component, Double> topComponents = rc.getTopComponents(10);
			
			coverage.addToRecommendedComponents(topComponents);
			
			int count = 0;
			for(Map.Entry<Component, Double> recommendedComp : topComponents.entrySet()) {
				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					count++;
				}
			}
			precision += (double) count / topComponents.size();
			
		}
		
		System.out.println("mean pricision: " + (double) precision/sizeOfTestingSet);
		System.out.println("Coverage: " + coverage.getCoverage());
	}	
}
