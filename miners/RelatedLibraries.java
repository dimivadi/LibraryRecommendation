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
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;


/*
 *  Class to take as input the Connections data structure and return
 *  a Map containing all the the Libraries as keys 
 *  and their corresponding scores as values
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
	}
	
	
	public Map<Component, Double> componentMining(Component... components){	

		//Create Graph
		ComponentGraph cg = new ComponentGraph();
		cg.addConnectionsToGraph(connections);
		graph = cg.getGraph();
		
		//Check graph 
		
		Set<DefaultEdge> testEdges = graph.edgesOf(new Library("lib2"));
		for(DefaultEdge e: testEdges) {
			System.out.println("Edge Source: "+graph.getEdgeSource(e));
			System.out.println("Edge Target: "+graph.getEdgeTarget(e));
		}
		
		
		//Scoring Algorithm

		PersonalizedScoringAlgorithm ppr = new PersonalizedPageRank(graph, components);
		Map<Component, Double> scores = ppr.getScores();
		
		
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
