package miners;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;

import datatypes.Component;
import datatypes.Library;
import datatypes.Project;
import datatypes.Connections;
import datatypes.Keyword;


/*
 *  Class that implements ComponentMiner Interface. 
 *  Use as input the Connections data structure and the seed components
 *  Return a Map containing all the the Libraries as keys 
 *  and their corresponding scores as values
 *  
 */
public class RelatedLibraries implements ComponentMiner{
	
	private ComponentGraph componentGraph;
	private Map<Component, Double> globalLibraryScores; //scores calculated using global pagerank
	private double[] weightsVector;
	
	class InvalidSeedException extends RuntimeException{
		InvalidSeedException(String message){
			super(message);
		}
	}
	
	
//	class NoSuchKeywordsExistException extends Exception{
//		NoSuchKeywordsExistException(String message){
//			super(message);
//		}
//	}
	
	
	//return a Map that will have as keys the libraries of the graph, and as values their corresponding scores
	public Map<Component, Double> componentMining(Set<Component> seedComponents, boolean sweepRatio, double dampingFactor, String normalization, double[] weightValues) throws NoSuchKeywordsExistException,  EmptyGraphException{	
//	public Map<Component, Double> componentMining(Set<Component> seedComponents) {

		if(componentGraph == null) {
			throw new EmptyGraphException("Component Graph has not been created");
		}
		boolean atLeastOneKeywordExistsInGraph = false;
		for(Component seedComp: seedComponents) {
			if (seedComp.getClass().equals(Library.class)) {
				throw new InvalidSeedException("Seed component for 'RelatedLibraries' should not be a Library");
			}
			if(componentGraph.contains(seedComp))
				atLeastOneKeywordExistsInGraph = true;
		}
		
		if(!atLeastOneKeywordExistsInGraph)
			throw new NoSuchKeywordsExistException("No such keywords exist in graph. Try different keywords");
		
		if(sweepRatio && globalLibraryScores == null) {
			globalLibraryScores = getGlobalScores(dampingFactor, normalization, weightValues);
//			System.out.println("Calculated PR");
		}
		
//		System.out.println("seed: " + seedComponents);
		//Scoring Algorithm
		//TIME
//		long start1 = System.nanoTime();
		PersonalizedScoringAlgorithm ppr = new PersonalizedPageRank(componentGraph.getGraph(), dampingFactor, seedComponents, normalization, weightValues);
//		PersonalizedScoringAlgorithm ppr = new PersonalizedPageRank(componentGraph.getGraph(), seedComponents);
		Map<Component, Double> scores = ppr.getScores();
		//TIME
//		long elapsedTime1 = System.nanoTime() - start1;
//		double elapsedTimeInSeconds1 = (double) elapsedTime1 / 1_000_000_000;
//		System.out.println("time to run algorithm: "+ elapsedTimeInSeconds1);	
			

		
		Map<Component, Double> libScores = keepLibraryScores(scores);
		
		
		if(sweepRatio) {
			Map<Component, Double> libScoresUsingConductance = new HashMap<>();										//sweep ratio							
			
			for(Map.Entry<Component, Double> entry: libScores.entrySet()) { 
				double newScore = entry.getValue() / globalLibraryScores.get(entry.getKey());
				libScoresUsingConductance.put(entry.getKey(), newScore);
			}
				
			return libScoresUsingConductance;
		}
		
		return libScores;																						//original
		

	}
	
	private Map<Component, Double> getGlobalScores(double dampingFactor, String normalization, double[] weightValues){
		
		PersonalizedScoringAlgorithm ppr = new PersonalizedPageRank(componentGraph.getGraph(), dampingFactor, normalization, weightValues);
		Map<Component, Double> scores = ppr.getScores();
		scores = keepLibraryScores(scores);
		
		return scores;
	}
	
	private Map<Component, Double> keepLibraryScores(Map<Component, Double> scores) {
		
		Map<Component, Double> libScores = scores.entrySet().stream()
															.filter(x -> x.getKey().getClass().equals(Library.class))
																.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
		
		return libScores;
	}
	
	public void createGraph(Connections connections){
		
		//TIME
		long start = System.nanoTime();
		System.out.println("Creating graph...");
		//Create Graph
//		ComponentGraph cg = new ComponentGraph();
//		cg.addConnectionsToGraph(connections);
//		this.componentGraph = cg;
		componentGraph = new ComponentGraph();
		componentGraph.addConnectionsToGraph(connections);
//		if(isWeighted) {
//			componentGraph.setEdgeWeightOfClasses(Keyword.class, Library.class, 1);
//			componentGraph.setEdgeWeightOfClass(Library.class, 5);
//			componentGraph.setEdgeWeightOfClasses(Library.class, Project.class, 1);
//		}
		
		//graph = cg.getGraph();
		//TIME
		long elapsedTime = System.nanoTime() - start;
		double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000_000;
		System.out.println("time to create graph: "+ elapsedTimeInSeconds);
	}
	
	public void setComponentGraph(ComponentGraph componentGraph) {
		this.componentGraph = componentGraph;
	}
	
	public ComponentGraph getComponentGraph() {
		return componentGraph;
	}
	
	public double[] setWeights(double[] weightsVector) {
		this.weightsVector = weightsVector;
		return weightsVector;
	}
	//keep scores only for Library Components
//	Map<Component, Double> libScores = new HashMap<>();
//	for(Map.Entry<Component, Double> entry : scores.entrySet()) {
//		if(entry.getKey().getClass() == Library.class) {
//			libScores.put(entry.getKey(), entry.getValue());
//		}
//	}
	
}
