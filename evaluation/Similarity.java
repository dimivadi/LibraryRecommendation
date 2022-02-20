package evaluation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Iterator;

import datatypes.Component;
import datatypes.Library;
import miners.ComponentMiner;

/*
 * 
 * Class for calculating cosine similarity between a set of components given as input,
 * and every library component in the components graph.
 * There are methods included to get the most similar library,
 * or a map containing as keys the libraries of the graph and as values their similarity to the input
 *  
 */

public class Similarity {
	
	//Map to store as keys the libraries of the graph, 
	//and as value another map with keys the words comprising the library name, and value the number of occurrences of this word in the library name
	private Map<Component, Map<String, Integer>> componentsMap = new HashMap<>();
	private Map<Component, Map<String, Integer>> componentsMapIncludingNeighbours = new HashMap<>();
//	private ComponentMiner componentMiner;
	private Set<Component> trainingSetLibraries;
	private boolean useNeighbours;

	
	public Similarity(ComponentMiner componentMiner, boolean useNeighbours){
		
//		this.componentMiner = componentMiner;
		this.useNeighbours = useNeighbours;
		trainingSetLibraries = componentMiner.getComponentGraph().getGraph().vertexSet().stream()
										.filter(x -> x.getClass().equals(Library.class))
											.collect(Collectors.toSet());
		
		for(Component component: trainingSetLibraries) 
			addComponentToMap(component);
		//if library neighbours are used, the vector of the library is the result of the addition of its initial vector and the vectors of its neighbours
		if(useNeighbours) {
			//for each library in the graph
			for(Map.Entry<Component, Map<String, Integer>> library: componentsMap.entrySet()) {
				//store the map representing the library's terms as strings and the number of their appearences
				Map<String, Integer> libraryMap = library.getValue();
				//for each of the library's neighbours
				for(Component neighbour: componentMiner.getComponentGraph().getNeighbouringComponents(library.getKey())) {
					if(!neighbour.getClass().equals(Library.class))
						continue;
					//add only missing words
//					libraryMap.putAll(componentsMap.get(neighbour));
					//merge library's terms map with the neighbour's terms map
					for(Map.Entry<String, Integer> neighbourMapEntry: componentsMap.get(neighbour).entrySet()) 
						libraryMap.merge(neighbourMapEntry.getKey(), neighbourMapEntry.getValue(), (x, y) -> (x + y));
					
				}
				componentsMapIncludingNeighbours.put(library.getKey(), libraryMap);
			}
		}
	}
	
	// returns a Map with keys the libraries of the graph and values the similarity of each library to the input
	public Map<Component, Double> getLibrarySimilarity(Set<Component> input) {
		Map<String, Integer> inputMap = new HashMap<>();
		for(Component component: input) {
			inputMap.put(component.toString().toLowerCase(), 1);
		}
		Map<Component, Double> librarySimilarity = new HashMap<>();
		
		if(useNeighbours) {
	
			for(Map.Entry<Component, Map<String, Integer>> entry: componentsMapIncludingNeighbours.entrySet()) {
				double value = cosineSimilarity(inputMap, entry.getValue());
				librarySimilarity.put(entry.getKey(), value);
			}
		}else {
			for(Map.Entry<Component, Map<String, Integer>> entry: componentsMap.entrySet()) {
				double value = cosineSimilarity(inputMap, entry.getValue());
				librarySimilarity.put(entry.getKey(), value);
			}
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
	private Set<String> componentTerms(Component component) { 
		String[] terms = component.toString().split("[^a-zA-Z0-9]|(?<=[a-z])(?=[A-Z][a-z])|(?<=[A-Z][A-Z])(?=[a-z][^a-zA-Z0-9])|(?<=[A-Z][A-Z])(?=[A-Z][a-z][a-z])"); 
		Set<String> termsSet = new HashSet<String>(Arrays.asList(terms));
		for(Iterator<String> i = termsSet.iterator(); i.hasNext();) {
			String nextTerm = i.next();
			if(nextTerm.length() < 2
					|| !nextTerm.matches(".*[a-zA-Z]+.*"))
				i.remove();
		}
		
		return termsSet;
	}
	
	
}
