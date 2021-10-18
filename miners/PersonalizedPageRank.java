package miners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import datatypes.Component;

public class PersonalizedPageRank implements PersonalizedScoringAlgorithm{
	
	public static final int MAX_ITERATIONS_DEFAULT = 100;
	
	public static final double TOLERANCE_DEFAULT = 0.0001;
	
	public static final double DAMPING_FACTOR_DEFAULT = 0.85d;
	
	private final Graph<Component, DefaultEdge> graph;
	
	private final double dampingFactor;
	private final int maxIterations;
	private final double tolerance;
	private Map<Component, Double> scores;
	//private Collection<Component> seedComponents; 
	private Set<Component> seedComponents; //personalization components
	

	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, int maxIterations, double tolerance, double dampingFactor, Set<Component> seedComponents){	
		//TODO Add exceptions
		
		this.graph = graph;
		this.tolerance = tolerance;
		this.dampingFactor = dampingFactor;
		this.maxIterations = maxIterations;
		this.seedComponents = seedComponents;
		
	}
	

	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, Set<Component> seedComponents){	
		
		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT, seedComponents);
	}
	
	public Map<Component, Double> getScores(){
		if(scores == null) {
			scores = new Algorithm().getScores();
		}
		return scores;
	}
	
	private class Algorithm{
		
		private int totalVertices;
		private double[] seedVector;  //personalization vector
		private int numOfPersonalizationComponents;
		private double[] curScore;
		private double[] nextScore;
		private int[] outDegree;
		private Map<Component, Integer> vertexIndexMap;
		private Component[] vertexMap; 
		private ArrayList<int[]> adjList;
	
		
		
		Algorithm(){
			
			this.totalVertices = graph.vertexSet().size();
			this.seedVector = new double[totalVertices];
			this.curScore = new double[totalVertices];
			this.nextScore = new double[totalVertices];
			this.outDegree = new int[totalVertices];
			this.vertexMap =  new Component[totalVertices];
			this.vertexIndexMap = new HashMap<>();
			this.adjList = new ArrayList<int[]>(totalVertices);
			this.numOfPersonalizationComponents = seedComponents.size();
			
			double initScore = 1.0d / totalVertices;
			int i = 0;
			
			for(Component c : graph.vertexSet()) {
				curScore[i] = initScore;
				outDegree[i] = graph.outDegreeOf(c);
				vertexIndexMap.put(c, i);
				vertexMap[i] = c;
				seedVector[i] = (seedComponents.contains(c) == true)? 1 / numOfPersonalizationComponents : 0d;
				i++;
			}
			for(int k=0; k<totalVertices; k++) { 
				Component c = vertexMap[k];
				int[] inNeighbors = new int[graph.inDegreeOf(c)];
				int j = 0;
				for(DefaultEdge e : graph.incomingEdgesOf(c)) {
					Component nc = Graphs.getOppositeVertex(graph, e, c);
					inNeighbors[j++] = vertexIndexMap.get(nc);
				}
				adjList.add(inNeighbors);
			}
			
			
			
		}
		
		private void run() { 	// Power iteration 
			
			int iterations = maxIterations;
			double maxChange = tolerance;
			
			while(iterations > 0 && maxChange >= tolerance) {
				
				for(int i=0; i<totalVertices; i++) { 
					double contribution = 0d;
					// if outDegree = 0???
					for(int j : adjList.get(i)) {
						contribution += dampingFactor * curScore[j] / outDegree[j]; 
					}
					double newValue = contribution + (1d - dampingFactor) * seedVector[i]; 
					maxChange = Math.max(maxChange, Math.abs(newValue) - curScore[i]);
					
					nextScore[i] = newValue;
				}
				curScore = nextScore;
				iterations--;
			}
			if(iterations<0)
				System.out.println("failed to converge in "+maxIterations+ " iterations");
			
		}
		
		public Map<Component, Double> getScores() {
			
			run();
			Map<Component, Double> scores = new HashMap<>();
			for(int i=0; i<totalVertices; i++) {
				scores.put(vertexMap[i], curScore[i]);
			}
			
			return scores;
		}
		
	}
	
	
}
