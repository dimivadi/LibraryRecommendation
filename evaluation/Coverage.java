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
	
	private Set<Component> recommendedComponents3 = new HashSet<>();
	private Set<Component> recommendedComponents5 = new HashSet<>();
	private Set<Component> recommendedComponents10 = new HashSet<>();
	private int numberOfCommonLibraries;
	private HashSet<Component> testingSetLibraries = new HashSet<>();
	private HashSet<Component> trainingSetLibraries = new HashSet<>();
	
	
	public Coverage addToRecommendedComponents(Map<Component, Double> recommendedComponents, int i) {
		if(i == 3) {
			for(Map.Entry<Component, Double> entry: recommendedComponents.entrySet()) 
				if(testingSetLibraries.contains(entry.getKey()))
					this.recommendedComponents3.add(entry.getKey());
		}else if(i == 5) {
			for(Map.Entry<Component, Double> entry: recommendedComponents.entrySet()) 
				if(testingSetLibraries.contains(entry.getKey()))
					this.recommendedComponents5.add(entry.getKey());
		}else if(i == 10) {
			for(Map.Entry<Component, Double> entry: recommendedComponents.entrySet()) 
				if(testingSetLibraries.contains(entry.getKey()))
					this.recommendedComponents10.add(entry.getKey());
		}else 
			throw new RuntimeException("recommendedComponents number");
		
		
		
		return this;
	}

	public Coverage setNumberOfCommonLibraries(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections) {
		
		for(Map.Entry<Set<Component>, Set<Component>> connection: existingConnections.entrySet()) {
			testingSetLibraries.addAll(connection.getValue());
		}
		
		trainingSetLibraries = componentMiner.getComponentGraph().getGraph().vertexSet().stream()
											.filter(vertex -> vertex.getClass().equals(Library.class))
												.collect(Collectors.toCollection(HashSet::new));
		Set<Component> commonLibraries = new HashSet<Component>(trainingSetLibraries);
		commonLibraries.retainAll(testingSetLibraries);
		numberOfCommonLibraries = commonLibraries.size();
		
		return this;
	}
	

	public double[] getCoverage() {
		
		System.out.println("Size of recommendedComponents@3: " + recommendedComponents3.size());
		System.out.println("Size of recommendedComponents@5: " + recommendedComponents5.size());
		System.out.println("Size of recommendedComponents@10: " + recommendedComponents10.size());
		System.out.println("Number of common libraries in testing and training sets: " + numberOfCommonLibraries);
		
		double[] coverage = new double[3];
		coverage[0] = (double) recommendedComponents3.size() / numberOfCommonLibraries;
		coverage[1] = (double) recommendedComponents5.size() / numberOfCommonLibraries;
		coverage[2] = (double) recommendedComponents10.size() / numberOfCommonLibraries;
		return coverage;
	}
	
	
	
}
