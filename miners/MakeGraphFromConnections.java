package miners;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.*;
import datatypes.Component;
import datatypes.Connections;


public class MakeGraphFromConnections {
	
	private Connections connections;
	private Graph<Component, DefaultEdge> graph = GraphTypeBuilder
		     										.<Component, DefaultEdge> undirected()
		     											.allowingMultipleEdges(false)
		     												.allowingSelfLoops(false)
		     													.edgeClass(DefaultEdge.class)
		     														.weighted(false)
		     															.buildGraph();
	
	MakeGraphFromConnections(Connections connections){
		this.connections = connections;
		
		for (Component component : connections.getComponents()) {
			graph.addVertex(component);
			for(Component comp : connections.getComponentConnections(component)) {
				graph.addVertex(comp);
				graph.addEdge(component, comp);
			}
		}
	}
	
	Graph<Component, DefaultEdge> getGraph(){
		return graph;
	}

}
