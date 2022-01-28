package evaluation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import miners.ComponentGraph;
import miners.ComponentMiner;
import datatypes.Component;
import datatypes.Library;

public class Coverage {
	
	private Set<Component> recommendedComponents = new HashSet<>();
//	private ComponentGraph componentGraph;
	private int numberOfCommonLibraries;
	
	
	public void addToRecommendedComponents(Map<Component, Double> recommendedComponents) {
		for(Map.Entry<Component, Double> entry: recommendedComponents.entrySet()) {
			this.recommendedComponents.add(entry.getKey());
		}
	}
	
//	public void setGraph(ComponentGraph componentGraph) {
//		this.componentGraph = componentGraph;
//	}
//	
	public void setNumberOfCommonLibraries(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections) {
		
		HashSet<Component> testingSetLibraries = new HashSet<>();
		HashSet<Component> trainingSetLibraries = new HashSet<>();
		
		for(Map.Entry<Set<Component>, Set<Component>> connection: existingConnections.entrySet()) {
			testingSetLibraries.addAll(connection.getValue());
		}
		
		trainingSetLibraries = componentMiner.getComponentGraph().getGraph().vertexSet().stream()
																							.filter(x -> x.getClass().equals(Library.class))
																								.collect(Collectors.toCollection(HashSet::new));
		
		trainingSetLibraries.retainAll(testingSetLibraries);
		numberOfCommonLibraries = trainingSetLibraries.size();
	}
	
	public double getCoverage() {
		
		System.out.println("Size of recommendedComponents: " + recommendedComponents.size());
		System.out.println("Number of common libraries in testing and training sets: " + numberOfCommonLibraries);
		
		return (double) recommendedComponents.size() / numberOfCommonLibraries;
	}
	
	
//	public double getCoverage() {
//		Set<Component> graphVertexSet = this.componentGraph.getGraph().vertexSet();
//		long numOfLibrariesInGraph = graphVertexSet.stream().filter(e -> e.getClass() == Library.class).count();
//		
//		System.out.println("numOfLibrariesInGraph: "+ numOfLibrariesInGraph);
//		System.out.println("Size of recommendedComponents: "+recommendedComponents.size());
//		
//		return (double) recommendedComponents.size() / numOfLibrariesInGraph;
//		
//	}
	
}
