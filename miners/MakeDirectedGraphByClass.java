package miners;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import datatypes.Component;
import datatypes.Connections;


public class MakeDirectedGraphByClass {
	
	private Graph<Component, DefaultEdge> graph = GraphTypeBuilder.<Component, DefaultEdge> directed().allowingMultipleEdges(false)
			.allowingSelfLoops(false).edgeClass(DefaultEdge.class).weighted(false).buildGraph();
	
	public MakeDirectedGraphByClass(Connections connections, Class A, Class B){
		for(Component component : connections.getComponents()) {
			if(component.getClass() != A && component.getClass() != B)
				continue;
			graph.addVertex(component);
		}
		for(Component component : connections.getComponents()) {
			if(component.getClass() != A)
				continue;
			for(Component comp : connections.getComponentConnections(component)) {
				if(comp.getClass() != B)
					continue;
				graph.addEdge(component, comp);
			}
		}
	}
	
	public Graph<Component, DefaultEdge> getGraph(){
		return graph;
	}
	
}
