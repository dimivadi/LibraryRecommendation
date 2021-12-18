package evaluation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import miners.ComponentGraph;
import datatypes.Component;
import datatypes.Library;

public class Coverage {
	
	private Set<Component> recommendedComponents = new HashSet<>();
	private ComponentGraph componentGraph;
	
	//takes as input the return type of ComponentMiner.componentMining() method
	public void addToRecommendedComponents(Map<Component, Double> recommendedComponents) {
		for(Map.Entry<Component, Double> entry: recommendedComponents.entrySet()) {
			this.recommendedComponents.add(entry.getKey());
		}
	}
	
	public void setGraph(ComponentGraph componentGraph) {
		this.componentGraph = componentGraph;
	}
	
	public double getCoverage() {
		Set<Component> graphVertexSet = this.componentGraph.getGraph().vertexSet();
		long numOfLibrariesInGraph = graphVertexSet.stream().filter(e -> e.getClass() == Library.class).count();
		
		return recommendedComponents.size() / numOfLibrariesInGraph;
		
	}
}
