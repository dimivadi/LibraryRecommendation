package evaluation;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import datatypes.Component;
import miners.ComponentMiner;
import miners.RankedComponents;

public class Recall implements Evaluate{
	
	ComponentMiner componentMiner;
	Map<Set<Component>, Set<Component>> existingConnections;
	int numOfRecommendedComponents;
	
	
	public Recall(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections, int numOfRecommendedComponents){
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		this.numOfRecommendedComponents = numOfRecommendedComponents;
	}
	
	public void run() {
		
		double recall = 0;
		int sizeOfTestingSet = existingConnections.size();
		
		Coverage coverage = new Coverage();
//		coverage.setGraph(componentMiner.getComponentGraph());
		
		for(Entry<Set<Component>, Set<Component>> existingConnection : existingConnections.entrySet()) {
			
			if(existingConnection.getKey().isEmpty() || existingConnection.getValue().isEmpty()) {
				sizeOfTestingSet--;
				continue;
			}

			RankedComponents rc = new RankedComponents(componentMiner.componentMining(existingConnection.getKey()));
			Map<Component, Double> topComponents = rc.getTopComponents(numOfRecommendedComponents);
			
			coverage.addToRecommendedComponents(topComponents);
			
			int count = 0;
			
			for(Component existingLibrary: existingConnection.getValue()) {
				if(topComponents.containsKey(existingLibrary)) {
					count++;
				}
			}
			recall += (double) count / existingConnection.getValue().size();
			System.out.println("recall for "+existingConnection.getKey()+" for top "+topComponents.size()+" components: "+(double) count / existingConnection.getValue().size());
			
		}
		
		System.out.println("mean recall: " + (double) recall/sizeOfTestingSet);
		System.out.println("Coverage: " + coverage.getCoverage());
	}

}
