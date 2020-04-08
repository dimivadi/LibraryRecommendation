package miners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import datatypes.Component;
import datatypes.Library;
import datatypes.Keyword;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.*;
import datatypes.Component;
import datatypes.Connections;

public class RelatedLibraries extends ComponentMiner{
	
	private Connections connections;
	private Collection<Component> relatedLibraries = new HashSet<Component>();
	private Graph<Component, DefaultEdge> graph;
	
	public RelatedLibraries(Connections connections){
		this.connections = connections;
	}
	
	
	public Collection<Component> componentMining(Collection<Component> components){
		
		MakeDirectedGraphByClass makeDG = new MakeDirectedGraphByClass(connections, Keyword.class, Library.class);
		graph = makeDG.getGraph();

		for (Component component : components) {
			if(graph.containsVertex(component)) {
				for(DefaultEdge de : graph.edgesOf(component)) {
					relatedLibraries.add(graph.getEdgeTarget(de));
				}
			}else {
				System.out.println("Component "+component.getName()+" does not exist in graph");
			}
		}
		
		return relatedLibraries;
		
	}
	
	
}
