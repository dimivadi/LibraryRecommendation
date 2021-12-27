
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Set;

import datatypes.Component;
import datatypes.Connections;
import evaluation.*;
import examples.*;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.RelatedLibraries;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
		final boolean buildNewGraph = false;
		final boolean b2 = true;
		
		String training = "training.ser";
		String testing = "testing.set";
		String graph = "graph.set";
		
		Map<Set<Component>, Set<Component>> existingConnections = null;
		Connections connections = null;
		ComponentMiner componentMiner;
		ComponentGraph componentGraph = null;
		
		if(buildNewGraph) {
		
		
		//get data
//			EvaluationDataSource evaluationDataSource = new EvaluateFromFiles("jEdit", "test", "java");
			EvaluationDataSource evaluationDataSource = new EvaluateFromMavenCentral("maven-data.csv/links_all.csv");
			
			//get connections (part of the data) that will be used as a testing set
			existingConnections = evaluationDataSource.getExistingConnections();
			
			//get data structure to use as input for component miner
			connections = evaluationDataSource.getConnections();
			
			
			//Serialization
			try {
				
				FileOutputStream file = new FileOutputStream(training);
				ObjectOutputStream out = new ObjectOutputStream(file);
				
				out.writeObject(connections);
				
				out.close();
				file.close();
			
				file = new FileOutputStream(testing);
				out = new ObjectOutputStream(file);
				
				out.writeObject(existingConnections);
				
				out.close();
				file.close();
				
				System.out.println("Objects have been serialized");
			}catch(IOException ex)
	        {
	            System.out.println("IOException is caught");
	        }
			
		}else {
		
			//Deserialization
			try {
				FileInputStream file = new FileInputStream(training);
				ObjectInputStream in = new ObjectInputStream(file);
				
				connections = (Connections) in.readObject();
				
				in.close();
				file.close();
				
				file = new FileInputStream(testing);
				in = new ObjectInputStream(file);
				
				existingConnections = (Map<Set<Component>, Set<Component>>) in.readObject();
				
				in.close();
				file.close();
				
				System.out.println("Objects have been deserialized");
				
			}catch(IOException ex)
	        {
	            System.out.println("IOException is caught");
	        }
	          
	        catch(ClassNotFoundException ex)
	        {
	            System.out.println("ClassNotFoundException is caught");
	        }
		
		}
		
		if(b2) {
			//instantiate a miner and create the graph
			componentMiner = new RelatedLibraries();
			componentMiner.createGraph(connections);
			
			try {
				FileOutputStream file = new FileOutputStream(graph);
				ObjectOutputStream out = new ObjectOutputStream(file);
				
				out.writeObject(componentMiner.getComponentGraph());
				
				out.close();
				file.close();
				
				System.out.println("Graph has been serialized");
				
			}catch(IOException ex)
	        {
	            System.out.println("IOException is caught");
	        }
			
		}else {
			
			try {
				FileInputStream file = new FileInputStream(graph);
				ObjectInputStream in = new ObjectInputStream(file);
				
				componentGraph = (ComponentGraph) in.readObject();
				
				in.close();
				file.close();
				
				System.out.println("Graph has been deserialized");
				
			}catch(IOException ex)
	        {
	            System.out.println("IOException is caught");
	        }
	          
	        catch(ClassNotFoundException ex)
	        {
	            System.out.println("ClassNotFoundException is caught");
	        }
		}
		
		
		//call miner for a user defined seed vector
		
//		//get the components of the graph ranked, with respect to the seed vector 
//		//and the class that implemented the ComponentMiner interface (e.g. keep only the library components of the graph)
//		Map<Component, Double> rankedComponents = componentMiner.componentMining(seedComponents);
//		//keep top-n of the ranked components
//		RecommendedComponents rc = new RecommendedComponents(rankedComponents);
//		rankedComponents = rc.getTopComponents(10);
		
		
		//call an evaluation method 
		
//		Evaluate evaluate = new AreaUnderCurve(componentMiner, existingConnections);
		Evaluate evaluate = new HitRate(componentMiner, existingConnections);
		
		evaluate.run();
	}
}









