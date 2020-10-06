package miners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import datatypes.Component;
import datatypes.Library;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.BreadthFirstIterator;
import datatypes.Connections;
import org.jgrapht.alg.scoring.PageRank;
import org.jgrapht.alg.interfaces.VertexScoringAlgorithm;

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
	
	/*
	 * 
	 */
	public Collection<Component> componentMining(Collection<Component> components){
		
		
		//Create Graph
		//TODO Abstract CreateGraph class
		ComponentGraph cg = new ComponentGraph();
		cg.addConnectionsToGraph(connections);
		graph = cg.getGraph();
		
		//Scoring Algorithm
		//VertexScoringAlgorithm<Component, Double> pr = new PageRank(graph);
		//Map<Component, Double> scores = pr.getScores();
		PersonalizedScoringAlgorithm ppr = new PersonalizedPageRank(graph, components);
		Map<Component, Double> scores = ppr.getScores();
		
		System.out.println(scores);
		
		//Traverse graph starting from input
		Component tempComponent;
	/*	
		BreadthFirstIterator<Component, DefaultEdge> iterator = new BreadthFirstIterator(graph, component);
		while(iterator.hasNext()) {
			tempComponent = iterator.next();
			if(iterator.getDepth(tempComponent) == 4)
				break;
			if(tempComponent.getClass() == Library.class) {
				relatedLibraries.add(tempComponent);
			}
		}
		
		
		//Sort list based on scoring
		Collections.sort(relatedLibraries, new Comparator<Component>() {
																@Override
																public int compare(Component a, Component b) {
																return (int) (Math.signum(scores.get(b) - scores.get(a)));
																}
															});
	*/	
		return relatedLibraries;
		
	}
	
	
}
