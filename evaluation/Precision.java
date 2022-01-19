package evaluation;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import datatypes.Component;
import miners.ComponentMiner;
import miners.RankedComponents;

public class Precision implements Evaluate{

	ComponentMiner componentMiner;
	Map<Set<Component>, Set<Component>> existingConnections;
	int numOfRecommendedComponents;
	RankedComponents rankedComponents;
	
	public Precision(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections, int numOfRecommendedComponents){
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		this.numOfRecommendedComponents = numOfRecommendedComponents;
	}
	
	public Precision(RankedComponents rankedComponents) {
		this.rankedComponents = rankedComponents;
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

			RankedComponents rc = new RankedComponents(componentMiner.componentMining(existingConnection.getKey()));
			Map<Component, Double> topComponents = rc.getTopComponents(numOfRecommendedComponents);
			
			coverage.addToRecommendedComponents(topComponents);
			
			int count = 0;
			for(Map.Entry<Component, Double> recommendedComp : topComponents.entrySet()) {
				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					count++;
				}
			}
			precision += (double) count / (topComponents.size());

			System.out.println("precision for "+existingConnection.getKey()+" for top "+topComponents.size()+" components: "+(double) count / topComponents.size());
			
		}
		
		System.out.println("mean pricision: " + (double) precision/sizeOfTestingSet);
		System.out.println("Coverage: " + coverage.getCoverage());
	}	
}
