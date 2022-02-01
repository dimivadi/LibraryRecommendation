
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import datatypes.Component;
import datatypes.Connections;
import datatypes.Library;
import evaluation.*;
import examples.*;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.RelatedLibraries;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
		final boolean buildNewGraph = false;
		
		String testing = "testing.ser";
		String graph = "graph.ser";
		
		Map<Set<Component>, Set<Component>> existingConnections = null;
		Connections connections = null;
		
		ComponentMiner componentMiner = new RelatedLibraries();
		
		
		if(buildNewGraph) {
		
		
		//get data
//			EvaluationDataSource evaluationDataSource = new EvaluateFromFiles("jEdit", "test", "java");
//			EvaluationDataSource evaluationDataSource = new EvaluateFromMavenCentral("maven-data2.csv/links_all.csv");
			EvaluationDataSource evaluationDataSource = new EvaluateFromMaven("maven-data2.csv/links_all.csv");
			
			//get connections (part of the data) that will be used as a testing set
			existingConnections = evaluationDataSource.getExistingConnections();
			
			//get data structure to use as input for component miner
			connections = evaluationDataSource.getConnections();
			
			componentMiner.createGraph(connections);
			
			//num of edges for debugging
			componentMiner.getComponentGraph().getNumOfEdges();
			
			//Serialization
			try {
			
				FileOutputStream file = new FileOutputStream(testing);
				ObjectOutputStream out = new ObjectOutputStream(file);
				
				out.writeObject(existingConnections);
				
				out.close();
				file.close();
				
				System.out.println("existingConnections have been serialized");
				
				file = new FileOutputStream(graph);
				out = new ObjectOutputStream(file);
				
				out.writeObject(componentMiner.getComponentGraph());
				
				out.close();
				file.close();
				
				System.out.println("Graph has been serialized");
				
			}catch(IOException ex)
	        {
	            System.out.println("IOException is caught");
	        }
			
		}else {
		
			//Deserialization
			
			ComponentGraph componentGraph = null;
			
			try {				
				
				FileInputStream file = new FileInputStream(testing);
				InputStream is = new BufferedInputStream(file);
				ObjectInputStream in = new ObjectInputStream(file);
				
				existingConnections = (Map<Set<Component>, Set<Component>>) in.readObject();
				
				in.close();
				file.close();
				
				System.out.println("existingConnections have been deserialized");
				
				file = new FileInputStream(graph);
				is = new BufferedInputStream(file);
				in = new ObjectInputStream(is);
				
				componentGraph = (ComponentGraph) in.readObject();
				componentGraph.getNumOfEdges();
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
			
			componentMiner.setComponentGraph(componentGraph);
		}
		
		
		//call miner for a user defined seed vector
		
//		//get the components of the graph ranked, with respect to the seed vector 
//		//and the class that implemented the ComponentMiner interface (e.g. keep only the library components of the graph)
//		Map<Component, Double> rankedComponents = componentMiner.componentMining(seedComponents);
//		//keep top-n of the ranked components
//		RecommendedComponents rc = new RecommendedComponents(rankedComponents);
//		rankedComponents = rc.getTopComponents(10);
		

//		Metrics metrics = new Metrics(componentMiner, existingConnections);
//		metrics.run();
		
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
		File file = new File(dateFormat.format(date) + ".txt") ;
		FileWriter fileWriter = new FileWriter(file);
		
		double[] dampingFactor = {0.85, 0.9, 0.95};
		boolean[] sweepRatio = {false, true};
		String[] normalization = {"original", "symmetricNorm", "renorm", "symmetricNormRenorm"};
		
		for(double a: dampingFactor) {
			for(boolean s: sweepRatio) {
				for(String n: normalization) {
					Metrics metrics = new Metrics(componentMiner, existingConnections, s, a, n, fileWriter);
					metrics.run();
				}
			}
		}
		fileWriter.close();
	}
}









