
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import datatypes.Component;
import datatypes.Connections;
import evaluation.*;
import examples.*;
import miners.ComponentMiner;
import miners.RelatedLibraries;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
		//get data
//		EvaluationDataSource evaluationDataSource = new EvaluateFromFiles("jEdit", "test", "java");
		EvaluationDataSource evaluationDataSource = new EvaluateFromMavenCentral("maven-data.csv/links_all.csv");
		
		//get connections (part of the data) that will be used as a testing set
		Map<Set<Component>, Set<Component>> existingConnections = evaluationDataSource.getExistingConnections();
		
		//get data structure to use as input for component miner
		Connections connections = evaluationDataSource.getConnections();
		
		//instantiate a miner and create the graph
		ComponentMiner componentMiner = new RelatedLibraries();
		componentMiner.createGraph(connections);
		
		//call miner for a user defined seed vector
		
//		//get the components of the graph ranked, with respect to the seed vector 
//		//and the class that implemented the ComponentMiner interface (e.g. keep only the library components of the graph)
//		Map<Component, Double> rankedComponents = componentMiner.componentMining(seedComponents);
//		//keep top-n of the ranked components
//		RecommendedComponents rc = new RecommendedComponents(rankedComponents);
//		rankedComponents = rc.getTopComponents(10);
		
		
		//call an evaluation method 
		
		Evaluate evaluate = new AreaUnderCurve(componentMiner, existingConnections);
//		Evaluate evaluate = new HitRate(componentMiner, existingConnections);
		
		evaluate.run();
	}
}









