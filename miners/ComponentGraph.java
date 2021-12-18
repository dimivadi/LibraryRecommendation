package miners;


import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import datatypes.Component;
import datatypes.Connections;

public class ComponentGraph {

	private Graph<Component, DefaultEdge> graph = GraphTypeBuilder
				.<Component, DefaultEdge> undirected()
					.allowingMultipleEdges(false)
						.allowingSelfLoops(false)
							.edgeClass(DefaultEdge.class)
								.weighted(false)
									.buildGraph();
	

	public void addConnectionsToGraph(Connections connections) {
		
		for (Component component : connections.getComponents()) {
			graph.addVertex(component);
			for(Component comp : connections.getComponentConnections(component)) {
				graph.addVertex(comp);
				graph.addEdge(component, comp);
			}
		}
	}
	
	
	public Graph<Component, DefaultEdge> getGraph(){
		return graph;
	}
}
