package miners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import datatypes.Component;
import datatypes.Keyword;
import datatypes.Library;
import datatypes.Project;

public class PersonalizedPageRank implements PersonalizedScoringAlgorithm{
	
	public static final int MAX_ITERATIONS_DEFAULT = 100;
	
	public static final double TOLERANCE_DEFAULT = 0.000001;
	
	public static final double DAMPING_FACTOR_DEFAULT = 0.95d;
	
	public static final String NORMALIZATION_DEFAULT = "original";
	
	public static final double[] WEIGHT_VALUES_DEFAULT = null;
	
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
	private double[] sqrtOutDegree;
	private Map<Component, Integer> vertexIndexMap; 
	private Component[] vertexMap; 
	private ArrayList<int[]> adjList;
	private double totalScore;
	private String normalization;
	private double[] weights;
	private double[] weightValues;

	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, int maxIterations, double tolerance, double dampingFactor, Set<Component> seedComponents, String normalization, double[] weightValues)	{
				
		this.graph = graph;
		this.tolerance = tolerance;
		this.dampingFactor = dampingFactor;
		this.maxIterations = maxIterations;
		this.seedComponents = seedComponents;
		this.normalization = normalization;
		this.weightValues = weightValues;
		

	}
	
	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, int maxIterations, double tolerance, double dampingFactor, Set<Component> seedComponents)	{
		
		this.graph = graph;
		this.tolerance = tolerance;
		this.dampingFactor = dampingFactor;
		this.maxIterations = maxIterations;
		this.seedComponents = seedComponents;
		

	}
	

	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, Set<Component> seedComponents){	
		
		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT, seedComponents, NORMALIZATION_DEFAULT, WEIGHT_VALUES_DEFAULT);
	}
	
	PersonalizedPageRank(Graph<Component, DefaultEdge> graph){
		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT, new HashSet<Component>(), NORMALIZATION_DEFAULT, WEIGHT_VALUES_DEFAULT);
	}
	
	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, double dampingFactor, Set<Component> seedComponents, String normalization, double[] weightValues){	
		
		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, dampingFactor, seedComponents, normalization, weightValues);
	}
	
	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, double dampingFactor, String normalization, double[] weightValues){
		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, dampingFactor, new HashSet<Component>(), normalization, weightValues);
	}
	
//	PersonalizedPageRank(Graph<Component, DefaultEdge> graph, Set<Component> seedComponents){	
//		
//		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT, seedComponents);
//	}
//	
//	PersonalizedPageRank(Graph<Component, DefaultEdge> graph){
//		this(graph, MAX_ITERATIONS_DEFAULT, TOLERANCE_DEFAULT, DAMPING_FACTOR_DEFAULT, new HashSet<Component>());
//	}
	
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
		sqrtOutDegree = new double[totalVertices];
		vertexIndexMap = new HashMap<>(); 
		vertexMap =  new Component[totalVertices]; 
		adjList = new ArrayList<int[]>(totalVertices);
		
		double initScore = 1.0d / totalVertices;
		
		int index = 0;
		
		
		for(Component c : graph.vertexSet()) {
			curScore[index] = initScore;
			if(normalization == "renorm" || normalization == "symmetricNormRenorm") {				//renormalization
				outDegree[index] = graph.outDegreeOf(c) + 1;
				sqrtOutDegree[index] = Math.sqrt(outDegree[index]);
			}else {
				outDegree[index] = graph.outDegreeOf(c);											//original		
				sqrtOutDegree[index] = Math.sqrt(outDegree[index]);
			}																			
			vertexIndexMap.put(c, index);
			vertexMap[index] = c;
			if(seedComponents.size() > 0) 
				seedVector[index] = (seedComponents.contains(c) == true)? ((double) 1 / numOfPersonalizationComponents) : 0d;
			else 
				seedVector[index] = (double) 1 / numOfPersonalizationComponents;
						
			index++;
		}
		
		for(int k=0; k < totalVertices; k++) { 
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
		
//		if(isWeighted) {	//calculate sum of weight of the edges for every component
//			this.weights = new double[totalVertices];
//			for (Component component : graph.vertexSet()) {
//				double sum = 0;
//				for (DefaultEdge edge: graph.outgoingEdgesOf(component)) 
//					sum += graph.getEdgeWeight(edge);
//				
//				weights[vertexIndexMap.get(component)] = sum;
//			}
//		}
		
		if(weightValues != null) {	//calculate sum of weight of the edges for every component
			this.weights = new double[totalVertices];
			for(int i = 0; i < totalVertices; i++) {
				Component component = vertexMap[i];
				double sum = 0;
				for(int j: adjList.get(i)) {
					Component neighbour = vertexMap[j];
					sum += calculateWeight(component, neighbour);
				}
				
				weights[vertexIndexMap.get(component)] = sum;
			}
		}
		
//		long start = System.nanoTime();
		
		int iterations = maxIterations;
		double change = tolerance;
		
		if(weightValues != null) {
			
			while(iterations > 0 && change >= tolerance) {
//				long start = System.nanoTime();
				totalScore = 0;
				for(int i = 0; i < totalVertices; i++) {
					double contribution = 0d;
					Component component = vertexMap[i];
					//
					for(int j: adjList.get(i)) {
						Component neighbour = vertexMap[j];
						Double weight = calculateWeight(component, neighbour);
						
						contribution +=  curScore[j] * weight /  weights[j];	
					}
					//

//					for(int j: adjList.get(i)) {
//						Component neighbour = vertexMap[j];
//						DefaultEdge edge = graph.getEdge(component, neighbour);
//						contribution += curScore[j] * graph.getEdgeWeight(edge) /  weights[j];
//					}

					
					double newValue = dampingFactor * contribution + (1d - dampingFactor) * seedVector[i];
					nextScore[i] = newValue;
					totalScore += newValue;
				}

				//normalize scores
				nextScore = Arrays.stream(nextScore).map(i -> i / totalScore).toArray(); 
				
				change = euclideanDistance(nextScore, curScore)/Math.sqrt(totalVertices);
				
				double[] temp = curScore;
				curScore = nextScore;
				nextScore = temp;
				
				iterations--;
				
//				long elapsedTime = System.nanoTime() - start;
//				double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000;
//				System.out.println("time to complete iteration: "+ elapsedTimeInSeconds +"ms");
				
			}
		}else {
			while(iterations > 0 && change >= tolerance) {
//				long start = System.nanoTime();
	//			maxChange = 0;
				totalScore = 0;
				if(normalization == "symmetricNorm" || normalization == "symmetricNormRenorm") {					//symmetric normalization
					for(int i=0; i<totalVertices; i++) { 
						double contribution = 0d;
	
						for(int j : adjList.get(i))
//							contribution += curScore[j] /(outDegree[j] * outDegree[i]);
							contribution += curScore[j] / (sqrtOutDegree[j] * sqrtOutDegree[i]);				
									
						if(normalization == "renorm" || normalization == "symmetricNormRenorm")
							contribution += curScore[i] / outDegree[i];												//renormalization
						
																		
						double newValue = dampingFactor * contribution + (1d - dampingFactor) * seedVector[i]; 
						
	//					maxChange = Math.max(maxChange, Math.abs(newValue - curScore[i]));												//L inf
						nextScore[i] = newValue;
						totalScore += newValue;
					}
				}else {																								//original
					for(int i=0; i<totalVertices; i++) { 
						double contribution = 0d;
						
						for(int j : adjList.get(i)) 
							contribution += curScore[j] / outDegree[j];											
						
						if(normalization == "renorm" || normalization == "symmetricNormRenorm") 
							contribution += curScore[i] / outDegree[i];												//renormalization
						
																		
						double newValue = dampingFactor * contribution + (1d - dampingFactor) * seedVector[i]; 
						
	//					maxChange = Math.max(maxChange, Math.abs(newValue - curScore[i]));												//L inf
						nextScore[i] = newValue;
						totalScore += newValue;
					}
				}
			
				if(normalization == "symmetricNorm" || normalization == "symmetricNormRenorm") 					//symmetric normalization
					nextScore = Arrays.stream(nextScore).map(i -> i / totalScore).toArray(); 
			
											
	//			System.out.println("totalScore: "+totalScore);
				change = euclideanDistance(nextScore, curScore)/Math.sqrt(totalVertices);												//L2 normalized
	//			change = euclideanDistance(nextScore, curScore);																		//L2
	//			change = l1Norm(nextScore, curScore);																					//L1
			
			
			double[] temp = curScore;
			curScore = nextScore;
			nextScore = temp;
			
			iterations--;
			
//			long elapsedTime = System.nanoTime() - start;
//			double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000;
//			System.out.println("time to complete iteration: "+ elapsedTimeInSeconds +"ms");
			}
		}
		
//		long elapsedTime = System.nanoTime() - start;
//		double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000;
//		System.out.println("time to converge: "+ elapsedTimeInSeconds +"ms");
		
		if(iterations<=0)
			System.err.println("failed to converge in "+maxIterations+ " iterations");
//		System.out.println("Iterations: "+(maxIterations-iterations));
		
		
		//divide scores with idf
		
//		for(int i = 0; i < totalVertices; i++) 
//			curScore[i] = curScore[i] / Math.pow(outDegree[i], 2);
//			curScore[i] = curScore[i] / Math.log(outDegree[i]);

	
		
	}
	
	private double calculateWeight(Component component, Component neighbour) {
		
		double weight;
		//Library to Keyword weight
		if((component.getClass().hashCode() + neighbour.getClass().hashCode()) == (Library.class.hashCode() + Keyword.class.hashCode())) 
			weight = weightValues[0];
		//Library to Library weight
		else if((component.getClass().hashCode() + neighbour.getClass().hashCode()) == 2 * Library.class.hashCode()) 
			weight = weightValues[1];
		//Library to Project weight
		else if((component.getClass().hashCode() + neighbour.getClass().hashCode()) == (Library.class.hashCode() + Project.class.hashCode())) 
			weight = weightValues[2];
		else
			weight = 1d;
		
		return weight;
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
	
	private double l1Norm(double[] a, double[] b) {
		if(a.length != b.length) {
			throw new RuntimeException();
		}
		double sum = 0;
		for(int i = 0; i < a.length; i++) {
			sum += Math.abs(a[i] - b[i]);
		}
		return sum;
	}

}
