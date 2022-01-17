package miners;


import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import datatypes.Component;
import datatypes.Connections;

public class ComponentGraph implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
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
	
	public void getNumOfEdges() {
		
		int numOfEdges = 0;
		for(Component component: graph.vertexSet()) {
			numOfEdges += graph.inDegreeOf(component);
		}
		System.out.println("Number of Edges: " + numOfEdges/2);
		
	}
	
	public void getNumOfNodes() {
		
		System.out.println("Number of Nodes: " + graph.vertexSet().size()); 
		
	}
	
	public Graph<Component, DefaultEdge> getGraph(){
		return graph;
	}
}
