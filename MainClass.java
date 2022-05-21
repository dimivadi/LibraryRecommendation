
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

import datatypes.Component;
import datatypes.Connections;
import datatypes.Keyword;
import datatypes.Library;
import datatypes.Project;
import evaluation.*;
import examples.*;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.NoSuchKeywordsExistException;
import miners.RankedComponents;
import miners.RelatedLibraries;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
		Scanner scanner = new Scanner(System.in);
		int selectMode;
		Mode mode;
		while(true) {
			System.out.println("Type 1 for library recommendation based on keywords\n"
					+ "or 2 for system evaluation");
			selectMode = Integer.parseInt(scanner.nextLine());
			if(selectMode == 1) {
				mode = new Search();
				break;
			}else if(selectMode == 2) {
				mode = new Evaluation();
				break;
			}
		}
		
		mode.selectGraph();
		mode.run();
		
		
		final boolean buildNewGraph = false;
		
		String testing = "testingApkN.ser";
		String graph = "graphApkN.ser";
		System.out.println(graph);
		boolean linkLibs = true;
		boolean[] linkLibsProject = {true};
				
		Map<Set<Component>, Set<Component>> existingConnections = null;
		Connections connections = null;
		
		
		for(boolean link: linkLibsProject) {
		ComponentMiner componentMiner = new RelatedLibraries();
		if(buildNewGraph) {
		
		
		//get data
//			EvaluationDataSource evaluationDataSource = new EvaluateFromFiles("elasticsearch-master", "testElastic", "java", linkLibs, link);
//			EvaluationDataSource evaluationDataSource = new EvaluateFromMaven("maven-data.csv/links_all.csv", linkLibs, link);
			EvaluationDataSource evaluationDataSource = new EvaluateFromApk("apk_info.csv", "lib_info.csv", "relation.csv", linkLibs, link);
			
			
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
		
		
		
		
	
		
//		while(true) {
//			
//			if(!invalidInput) 
//				System.out.println(cmd.getPromptText());
//				
//			invalidInput = false;
//			input = scanner.nextLine();
//
//			else {
//				Set<Component> seedComponents = input2.stringToKeywords(input);
//				try {
//					Map<Component, Double> result = componentMiner.componentMining(seedComponents, cmdSweepRatio, cmdDampingFactor, cmdNormalization, cmdWeightValues);
//					RankedComponents rankedComponents = new RankedComponents(result);
//					result = rankedComponents.getTopComponents(20);
//					result.forEach((x,y) -> System.out.println(x));
//				} catch (NoSuchKeywordsExistException e) {
//					System.out.println("No such keywords exist in graph. Try different keywords: ");
//					invalidInput = true;
//				}
//			}
//		}
		

		
//		double[][] weightValuesArray = {
//				{1d, 1d, 1d}, {1d, 1d, 50d}, {1d, 50d, 1d}, {1d, 50d, 50d}, {50d, 1d, 1d}, {50d, 1d, 50d},
//				{50d, 50d, 1d}
//		};
		
		double[][] weightValuesArray = {
				{50d, 1d, 1d},{1d, 50d, 1d}
		};
		
		double[] dampingFactor = {0.85};
		boolean[] sweepRatio = {false};
		String[] normalization = {"original"};

		
		double[] dampingFactor2 = {0.5};
		boolean[] sweepRatio2 = {false};
		String[] normalization2 = {"original","symmetricNormRenorm"};
		
		double[] dampingFactor3 = {0.5};
		boolean[] sweepRatio3 = {true};
		String[] normalization3 = {"original"};
		
		double[] weightValues = {50d, 1d, 1d};
		
		
//		double[][] weightValuesArray = {null};
		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
		File file = new File(dateFormat.format(date) + ".txt") ;
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.append(graph +"\nLink libs: " + true + "\nLink libs to project: " + link+"\n");

//		String[] normalization = {"original", "symmetricNorm", "renorm", "symmetricNormRenorm"};
		

	
//		for(double a: dampingFactor) {
//			for(boolean s: sweepRatio) {
//				for(String n: normalization) {
//					Metrics metrics = new Metrics(componentMiner, existingConnections, s, a, n, null, fileWriter);
//					metrics.run();
//				}
//			}
//		}
//		
//
//		for(double a: dampingFactor2) {
//			for(boolean s: sweepRatio2) {
//				for(String n: normalization2) {
//					Metrics metrics = new Metrics(componentMiner, existingConnections, s, a, n, null, fileWriter);
//					metrics.run();
//				}
//			}
//		}
		
		for(double a: dampingFactor3) {
			for(boolean s: sweepRatio3) {
				for(String n: normalization3) {
					Metrics metrics = new Metrics(componentMiner, existingConnections, s, a, n, null, fileWriter);
					metrics.run();
				}
			}
		}
		
		
		for(double[] weightValues2: weightValuesArray) {
			Metrics metrics = new Metrics(componentMiner, existingConnections, false, 0.5d, "original", weightValues2, fileWriter);
			metrics.run();
		}
		
		
		fileWriter.close();
		
		
		}
	}
}









