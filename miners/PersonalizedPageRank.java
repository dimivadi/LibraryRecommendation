package miners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
	private Collection<Component> seedComponents; //personalization components
	
	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, Collection<Component> components, int maxIterations, double tolerance, double dampingFactor){
		
		//TODO Add exceptions
		
		this.graph = graph;
		this.tolerance = tolerance;
		this.dampingFactor = dampingFactor;
		this.maxIterations = maxIterations;
		this.seedComponents = components;
		
	}
	
	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, Collection<Component> components){
		
		this(graph, components, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT);
		
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
		private ArrayList<Component> vertexMap;
		private ArrayList<int[]> adjList;
	
		
		
		Algorithm(){
			
			this.totalVertices = graph.vertexSet().size();
			this.seedVector = new double[totalVertices];
			this.curScore = new double[totalVertices];
			this.nextScore = new double[totalVertices];
			this.outDegree = new int[totalVertices];
			this.vertexMap = new ArrayList<Component>(totalVertices); //or using array
			this.vertexIndexMap = new HashMap<>();
			this.adjList = new ArrayList<int[]>(totalVertices);
			this.numOfPersonalizationComponents = seedComponents.size();
			
			double initScore = 1.0d / totalVertices;
			int i = 0;
			
			for(Component c : graph.vertexSet()) {
				curScore[i] = initScore;
				outDegree[i] = graph.outDegreeOf(c);
				vertexIndexMap.put(c, i);
				vertexMap.add(c);
				
			}
			for(Component c : graph.vertexSet()) {
				int[] inNeighbors = new int[graph.inDegreeOf(c)];
				int j = 0;
				for(DefaultEdge e : graph.incomingEdgesOf(c)) {
					Component nc = Graphs.getOppositeVertex(graph, e, c);					
					inNeighbors[j++] = vertexIndexMap.get(nc);
				}
				adjList.add(inNeighbors);
				seedVector[i] = (seedComponents.contains(c) == true)? 1 / numOfPersonalizationComponents : 0d;
				i++;
			}
			
			
			
		}
		
		private void run() { 	// Power iteration 
			
			int iterations = maxIterations;
			double maxChange = tolerance;
			
			while(iterations > 0 && maxChange >= tolerance) {
				
				for(int i=0; i<totalVertices; i++) {
					double contribution = 0d;
					for(int j : adjList.get(i)) {
						contribution += dampingFactor * curScore[j] / outDegree[j]; // if outDegree = 0
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
				scores.put(vertexMap.get(i), curScore[i]);
			}
			
			return scores;
		}
		
	}
	
	
}
