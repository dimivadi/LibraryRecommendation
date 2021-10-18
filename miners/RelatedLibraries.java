package miners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.HashMap;

import datatypes.Component;
import datatypes.Library;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import datatypes.Connections;
import datatypes.Keyword;

import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;


/*
 *  Class that implements ComponentMiner Interface. 
 *  Use as input the Connections data structure and the seed components
 *  Return a Map containing all the the Libraries as keys 
 *  and their corresponding scores as values
 *  
 */
public class RelatedLibraries implements ComponentMiner{
	
	private Connections connections;
	private List<Component> relatedLibraries = new ArrayList<Component>();
	private Graph<Component, DefaultEdge> graph;
	
	/*
	 * Constructor's argument is a Connections data structure.
	 * 
	 */
	public RelatedLibraries(Connections connections){
		this.connections = connections;
		
		//TIME
		long start = System.nanoTime();
		//Create Graph
		ComponentGraph cg = new ComponentGraph();
		cg.addConnectionsToGraph(connections);
		graph = cg.getGraph();
		//TIME
		long elapsedTime = System.nanoTime() - start;
		double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000_000;
		System.out.println("time to create graph: "+ elapsedTimeInSeconds);
	}
	
	//return a Map that will have as keys the libraries of the graph, and as values their corresponding scores
	public Map<Component, Double> componentMining(Set<Component> seedComponents){	

		
		/*
		//Check graph 
		
		Set<DefaultEdge> testEdges = graph.edgesOf(new Library("lib2"));
		for(DefaultEdge e: testEdges) {
			System.out.println("Edge Source: "+graph.getEdgeSource(e));
			System.out.println("Edge Target: "+graph.getEdgeTarget(e));
		}
		*/
		
		for(Component comp: seedComponents) {
			if (comp.getClass() == Library.class) {
				System.out.println("Error: Do not use an instance of Library with the class 'RelatedLibraries' ");
			}
		}
		
		//Scoring Algorithm
		//TIME
		long start1 = System.nanoTime();
		PersonalizedScoringAlgorithm ppr = new PersonalizedPageRank(graph, seedComponents);
		Map<Component, Double> scores = ppr.getScores();
		//TIME
		long elapsedTime1 = System.nanoTime() - start1;
		double elapsedTimeInSeconds1 = (double) elapsedTime1 / 1_000_000_000;
		System.out.println("time to run algorithm: "+ elapsedTimeInSeconds1);
		
		
		//keep scores only for Library Components
		Map<Component, Double> libScores = new HashMap<>();
		for(Map.Entry<Component, Double> entry : scores.entrySet()) {
			if(entry.getKey().getClass() == Library.class) {
				libScores.put(entry.getKey(), entry.getValue());
			}
		}
	
		return libScores;
		
		
	}
	
	
}
