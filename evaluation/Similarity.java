package evaluation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import datatypes.Component;
import datatypes.Library;
import miners.ComponentMiner;

public class Similarity {
	
	private Map<Component, Map<String, Integer>> componentsMap = new HashMap<>();
	private Set<Component> trainingSetLibraries;
	
	public Similarity(ComponentMiner componentMiner){

		trainingSetLibraries = componentMiner.getComponentGraph().getGraph().vertexSet().stream()
							.filter(x -> x.getClass().equals(Library.class))
								.collect(Collectors.toSet());
		for(Component component: trainingSetLibraries) {
			addComponentToMap(component);
		}
	}
	
	public Map<Component, Double> getLibrarySimilarity(Set<Component> input) {
		Map<String, Integer> inputMap = new HashMap<>();
		for(Component component: input) {
			inputMap.put(component.toString().toLowerCase(), 1);
		}
		Map<Component, Double> librarySimilarity = new HashMap<>();
		for(Map.Entry<Component, Map<String, Integer>> entry: componentsMap.entrySet()) {
			double value = cosineSimilarity(inputMap, entry.getValue());
			librarySimilarity.put(entry.getKey(), value);
		}
		return librarySimilarity;
	}
	
//	public Component getMostSimilarLibrary(Set<Component> input) {
//		Map<String, Integer> inputMap = new HashMap<>();
//		for(Component component: input) {
//			inputMap.put(component.toString(), 1);
//		}
//		double maxValue = 0;
//		Component bestLibrary = new Library("");
//		for(Map.Entry<Component, Map<String, Integer>> entry: componentsMap.entrySet()) {
//			double value = cosineSimilarity(inputMap, entry.getValue());
//			if(value > maxValue) {
//				maxValue = value;
//				bestLibrary = entry.getKey();
//			}
//				
//		}
//		return bestLibrary;
//	}
	
	private double cosineSimilarity(Map<String, Integer> leftVector, Map<String, Integer> rightVector) {
		if (leftVector == null || rightVector == null) {
            throw new IllegalArgumentException("Vectors must not be null");
        }
		Set<String> intersection = getIntersection(leftVector, rightVector);
		
		double dotProduct = dot(leftVector, rightVector, intersection);
		double d1 = 0.0d;
		for (Integer value : leftVector.values()) {
            d1 += Math.pow(value, 2);
        }
        double d2 = 0.0d;
        for (Integer value : rightVector.values()) {
            d2 += Math.pow(value, 2);
        }
        double cosineSimilarity;
        if (d1 <= 0.0 || d2 <= 0.0) {
            cosineSimilarity = 0.0;
        } else {
            cosineSimilarity = (double) (dotProduct / (double) (Math.sqrt(d1) * Math.sqrt(d2)));
        }
        return cosineSimilarity;
	}
	
	private Set<String> getIntersection(Map<String, Integer> leftVector, Map<String, Integer> rightVector){
		Set<String> intersection = new HashSet<String>(leftVector.keySet());
		intersection.retainAll(rightVector.keySet());
		
		return intersection;
	}

	private double dot(Map<String, Integer> leftVector, Map<String, Integer> rightVector, Set<String> intersection) {
		long dotProduct = 0;
		for(String key: intersection) {
			dotProduct += leftVector.get(key) * rightVector.get(key);
		}
		
		return dotProduct;
	}

	private void addComponentToMap(Component component) {
		if(!componentsMap.containsKey(component)) {
			Map<String, Integer> terms = new HashMap<>();
			for(String term: componentTerms(component)) {
				term = term.toLowerCase();
				terms.put(term, terms.getOrDefault(term, 0) + 1);
			}
			componentsMap.put(component, terms);
		}

		
	}
	private String[] componentTerms(Component component) { 
		String[] terms = component.toString().split("[^a-zA-Z0-9]|(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])"); 
		return terms;
	}
	
	
}
