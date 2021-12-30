package miners;

import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import datatypes.Component;
import datatypes.Library;
import datatypes.Connections;


/*
 *  Class that implements ComponentMiner Interface. 
 *  Use as input the Connections data structure and the seed components
 *  Return a Map containing all the the Libraries as keys 
 *  and their corresponding scores as values
 *  
 */
public class RelatedLibraries implements ComponentMiner{
	
	private ComponentGraph componentGraph;
	
	/*
	 * Constructor's argument is a Connections data structure.
	 * 
	 */
//	public RelatedLibraries(Connections connections){
//		this.connections = connections;
//		
//		//TIME
//		long start = System.nanoTime();
//		System.out.println("Creating graph...");
//		//Create Graph
//		ComponentGraph cg = new ComponentGraph();
//		cg.addConnectionsToGraph(connections);
//		graph = cg.getGraph();
//		//TIME
//		long elapsedTime = System.nanoTime() - start;
//		double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000_000;
//		System.out.println("time to create graph: "+ elapsedTimeInSeconds);
//	}

	
	//return a Map that will have as keys the libraries of the graph, and as values their corresponding scores
	public Map<Component, Double> componentMining(Set<Component> seedComponents){	

			
		for(Component comp: seedComponents) {
			if (comp.getClass() == Library.class) {
				System.out.println("Error: Do not use a Library object as seed for 'RelatedLibraries' ");
			}
		}
			
		//Scoring Algorithm
		//TIME
		long start1 = System.nanoTime();
		PersonalizedScoringAlgorithm ppr = new PersonalizedPageRank(componentGraph.getGraph(), seedComponents);
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
		
	
	public void createGraph(Connections connections){
		
		//TIME
		long start = System.nanoTime();
		System.out.println("Creating graph...");
		//Create Graph
		ComponentGraph cg = new ComponentGraph();
		cg.addConnectionsToGraph(connections);
		this.componentGraph = cg;
		//graph = cg.getGraph();
		//TIME
		long elapsedTime = System.nanoTime() - start;
		double elapsedTimeInSeconds = (double) elapsedTime / 1_000_000_000;
		System.out.println("time to create graph: "+ elapsedTimeInSeconds);
	}
	
	public void setComponentGraph(ComponentGraph componentGraph) {
		this.componentGraph = componentGraph;
	}
	
	public ComponentGraph getComponentGraph() {
		return componentGraph;
	}
	
	
	
}
