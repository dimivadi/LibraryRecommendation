package miners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import datatypes.Component;

public class PersonalizedPageRank implements PersonalizedScoringAlgorithm{
	
	public static final int MAX_ITERATIONS_DEFAULT = 100;
	
	public static final double TOLERANCE_DEFAULT = 0.000001;
	
	public static final double DAMPING_FACTOR_DEFAULT = 0.85d;
	
	private final Graph<Component, DefaultEdge> graph;
	
	private final double dampingFactor;
	private final int maxIterations;
	private final double tolerance;
	private Map<Component, Double> scores; 
	private Set<Component> seedComponents;
	private int totalVertices;
	private double[] seedVector;  //personalization vector
	private int numOfPersonalizationComponents;
	private double[] curScore;
	private double[] nextScore;
	private int[] outDegree;
	private Map<Component, Integer> vertexIndexMap; 
	Component[] vertexMap; 
	private ArrayList<int[]> adjList;
	

	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, int maxIterations, double tolerance, double dampingFactor, Set<Component> seedComponents)	{
				
		this.graph = graph;
		this.tolerance = tolerance;
		this.dampingFactor = dampingFactor;
		this.maxIterations = maxIterations;
		this.seedComponents = seedComponents;

	}
	

	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, Set<Component> seedComponents){	
		
		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT, seedComponents);
	}
	
	PersonalizedPageRank(Graph<Component, DefaultEdge> graph){
		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT, new HashSet<Component>());
	}
	
	
	public Map<Component, Double> getScores(){
		if(scores == null) {
			run();
			
			scores = new HashMap<>();
			for(int i=0; i<totalVertices; i++) {
				scores.put(vertexMap[i], curScore[i]);
			}
		}
		return scores;
	}
	
	
	private void run() {
		totalVertices = graph.vertexSet().size();
		seedVector = new double[totalVertices];  //personalization vector
		numOfPersonalizationComponents = (seedComponents.size() > 0? seedComponents.size() : totalVertices);
		curScore = new double[totalVertices];
		nextScore = new double[totalVertices];
		outDegree = new int[totalVertices];
		vertexIndexMap = new HashMap<>(); 
		vertexMap =  new Component[totalVertices]; 
		adjList = new ArrayList<int[]>(totalVertices);
		
		double initScore = 1.0d / totalVertices;
		
		int l = 0;
		
		for(Component c : graph.vertexSet()) {
			curScore[l] = initScore;
			outDegree[l] = graph.outDegreeOf(c);
			vertexIndexMap.put(c, l);
			vertexMap[l] = c;
			if(seedComponents.size() > 0) {
				seedVector[l] = (seedComponents.contains(c) == true)? ((double) 1 / numOfPersonalizationComponents) : 0d;
			}else {
				seedVector[l] = (double) 1 / numOfPersonalizationComponents;
			}
						
			l++;
		}
		
		for(int k=0; k<totalVertices; k++) { 
			Component c = vertexMap[k];
			int[] inNeighbors = new int[graph.inDegreeOf(c)];
			int j = 0;
			for(DefaultEdge e : graph.incomingEdgesOf(c)) {
				Component nc = Graphs.getOppositeVertex(graph, e, c);
				inNeighbors[j] = vertexIndexMap.get(nc);
				j++;
			}
			adjList.add(inNeighbors);
		}
		
		int iterations = maxIterations;
		double maxChange = tolerance;
		double change = tolerance;
		
		while(iterations > 0 && change >= tolerance) {
//			long start = System.nanoTime();
			
			for(int i=0; i<totalVertices; i++) { 
				double contribution = 0d;

				for(int j : adjList.get(i)) {
					contribution += curScore[j] / outDegree[j];
				}
				
				double newValue = dampingFactor * contribution + (1d - dampingFactor) * seedVector[i]; 
//				maxChange = Math.max(maxChange, Math.abs(newValue - curScore[i]));		
				nextScore[i] = newValue;
								
			}
			change = euclideanDistance(nextScore, curScore);
			
			double[] temp = curScore;
			curScore = nextScore;
			nextScore = temp;
			
			iterations--;
			
//			long elapsedTime = System.nanoTime() - start;
//			double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000;
//			System.out.println("time to complete iteration: "+ elapsedTimeInSeconds +"ms");
		}
		if(iterations<=0)
			System.out.println("failed to converge in "+maxIterations+ " iterations");
		System.out.println("Iterations: "+(maxIterations-iterations));
	
	}
	
	
	
	private double euclideanDistance(double[] a, double[] b) {
		if(a.length != b.length) {
			throw new RuntimeException();
		}
		double sum = 0;
		for(int i = 0; i < a.length; i++) {
			double d = a[i] - b[i];
			sum += d * d;
		}
		return Math.sqrt(sum);
	}
	

}
