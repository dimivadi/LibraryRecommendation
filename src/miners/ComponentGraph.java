package miners;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
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
								.weighted(true)
									.buildGraph();

	
	public ComponentGraph addConnectionsToGraph(Connections connections) {
		for (Component component : connections.getComponents()) {
			graph.addVertex(component);
			for(Component comp : connections.getComponentConnections(component)) {
				graph.addVertex(comp);
				graph.addEdge(component, comp);	
			}
		}
		return this;
	}
	
	//set the same weight for every edge of a specific component
	public ComponentGraph setComponentEdgesWeight(Component component, double weight) {
		graph.edgesOf(component).forEach(edge -> graph.setEdgeWeight(edge, weight));
		
		return this;
	}
	
	
	//set edge weight between neighboring vertices that are of the same type classA
	public ComponentGraph setEdgeWeightOfClass(Class classA, double weight) {
		Set<Component> verticesOfTypeA = graph.vertexSet()
				.stream()
					.filter(component -> component.getClass().equals(classA))
						.collect(Collectors.toCollection(HashSet::new));
		
		Iterator<Component> i = verticesOfTypeA.iterator();
		while(i.hasNext()) {
			Component currentComponent = i.next();
			i.remove();
			Set<DefaultEdge> currentEdges = graph.edgesOf(currentComponent);
			for(DefaultEdge edge: currentEdges) {
				Component oppositeVertex = Graphs.getOppositeVertex(graph, edge, currentComponent);
				if(oppositeVertex.getClass().equals(classA))
					graph.setEdgeWeight(edge, weight);
			}
		}
		
		return this;
	}
	
	
	//set edge weight if a node is an object of classA and the opposite node is an object of classB
	public ComponentGraph setEdgeWeightOfClasses(Class classA, Class classB, double weight) {
		
		Set<Component> verticesOfTypeA = graph.vertexSet()
				.stream()
					.filter(component -> component.getClass().equals(classA))
						.collect(Collectors.toCollection(HashSet::new));
	
		for(Component componentA : verticesOfTypeA) {
			Set<DefaultEdge> currentEdges = graph.edgesOf(componentA);
			for(DefaultEdge edge: currentEdges) {
				Component oppositeVertex = Graphs.getOppositeVertex(graph, edge, componentA);
				if(oppositeVertex.getClass().equals(classB))
					graph.setEdgeWeight(edge, weight);
			}
		}
		
		return this;
	}
	
	public Set<Component> getNeighbouringComponents(Component component){
		Set<Component> neighbours = new HashSet<Component>();
		for(DefaultEdge edge: graph.incomingEdgesOf(component)) 
			neighbours.add(Graphs.getOppositeVertex(graph, edge, component));
		
		return neighbours;
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
	
	public ComponentGraph setGraph(Graph graph) {
		this.graph = graph;
		
		return this;
	}
	
	public boolean contains(Component component) {
		
		return graph.containsVertex(component);
	}
	
	public Map<Component, Integer> getDistribution(Class class1, Class class2){
		Map<Component, Integer> distro = new HashMap<>();
		
		for(Component vertice: graph.vertexSet()) {
			if(vertice.getClass().equals(class1)) {
				distro.put(vertice, 0);
				Set<DefaultEdge> currentEdges = graph.edgesOf(vertice);
				for(DefaultEdge edge: currentEdges) {
					Component oppositeVertex = Graphs.getOppositeVertex(graph, edge, vertice);
					if(oppositeVertex.getClass().equals(class2)) 
						distro.put(vertice, distro.get(vertice) + 1);
					
				}
			}
		}
		
		
		return distro;
	}
	
}
