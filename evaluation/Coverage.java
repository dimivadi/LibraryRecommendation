package evaluation;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import miners.ComponentGraph;
import miners.ComponentMiner;
import datatypes.Component;
import datatypes.Library;

/*
 * Class to calculate the fraction of the libraries in the testing set that are recommended
 */

public class Coverage {
	
	private Set<Component> recommendedComponents = new HashSet<>();
	private int numberOfCommonLibraries;
	private HashSet<Component> testingSetLibraries = new HashSet<>();
	private HashSet<Component> trainingSetLibraries = new HashSet<>();
	
	
	public void addToRecommendedComponents(Map<Component, Double> recommendedComponents) {
		for(Map.Entry<Component, Double> entry: recommendedComponents.entrySet()) {
			if(testingSetLibraries.contains(entry.getKey()))
				this.recommendedComponents.add(entry.getKey());
		}
	}

	public void setNumberOfCommonLibraries(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections) {
		
		for(Map.Entry<Set<Component>, Set<Component>> connection: existingConnections.entrySet()) {
			testingSetLibraries.addAll(connection.getValue());
		}
		
		trainingSetLibraries = componentMiner.getComponentGraph().getGraph().vertexSet().stream()
																							.filter(x -> x.getClass().equals(Library.class))
																								.collect(Collectors.toCollection(HashSet::new));
		Set<Component> commonLibraries = new HashSet<Component>(trainingSetLibraries);
		commonLibraries.retainAll(testingSetLibraries);
		numberOfCommonLibraries = commonLibraries.size();
	}
	
	public double getCoverage() {
		
		System.out.println("Size of recommendedComponents: " + recommendedComponents.size());
		System.out.println("Number of common libraries in testing and training sets: " + numberOfCommonLibraries);
		
		return (double) recommendedComponents.size() / numberOfCommonLibraries;
	}
	
	
	
}
