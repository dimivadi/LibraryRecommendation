package util;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

import datasets.EvaluateFromApk;
import datasets.EvaluateFromMaven;
import datatypes.Component;
import evaluation.EvaluationDataSource;
import miners.ComponentGraph;

public class GraphData {

	ComponentGraph componentGraphMalib;
	ComponentGraph componentGraphMaven;
	Map<Set<Component>, Set<Component>> existingConnectionsMalib;
	Map<Set<Component>, Set<Component>> existingConnectionsMaven;
	
	public GraphData() {
//		this.loadSerializedMalib();
		this.loadSerializedMaven();
	}
	
	public void printsmt() {
		System.out.println("smtsmt");
	}
	
	public void loadMavenType(String filePath) {
		EvaluationDataSource evaluationDataSource = null;
		try {
			evaluationDataSource = new EvaluateFromMaven(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Files loaded.\nCreating graph...");
		ComponentGraph componentGraph = new ComponentGraph();
		componentGraph.addConnectionsToGraph(evaluationDataSource.getConnections());
		System.out.println("Graph created");
		componentGraphMaven = componentGraph;
		existingConnectionsMaven = evaluationDataSource.getExistingConnections();
		
	}
	
	public void loadMalibType(String apkPath, String libPath, String relationPath) {
		EvaluationDataSource evaluationDataSource = null;
		try {
			evaluationDataSource = new EvaluateFromApk(apkPath, libPath, relationPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Files loaded.\nCreating graph...");
		ComponentGraph componentGraph = new ComponentGraph();
		componentGraph.addConnectionsToGraph(evaluationDataSource.getConnections());
		System.out.println("Graph created");
		componentGraphMalib = componentGraph;
		existingConnectionsMalib = evaluationDataSource.getExistingConnections();
	}
	
	public void loadSerializedMaven() {
		componentGraphMaven = deserializeComponentGraph("graphMavenTF.ser");
		existingConnectionsMaven = deserializeTestingSet("testingMavenTF.ser");
	}
	
	
	public void loadSerializedMalib() {
		componentGraphMalib = deserializeComponentGraph("graphApkNT.ser");
		existingConnectionsMalib = deserializeTestingSet("testingApkNT.ser");
	}
	
	public ComponentGraph getComponentGraphMalib() {
		return componentGraphMalib;
	}
	
	public ComponentGraph getComponentGraphMaven() {
		return componentGraphMaven;
	}
	
	public Map<Set<Component>, Set<Component>> getMalibTestingSet(){
		return existingConnectionsMalib;
	}
	
	public Map<Set<Component>, Set<Component>> getMavenTestingSet(){
		return existingConnectionsMaven;
	}
	
	private ComponentGraph deserializeComponentGraph(String serilizedComponentGraph) {
		ComponentGraph componentGraph = null;
		try {				
			
			FileInputStream file = new FileInputStream(serilizedComponentGraph);
			InputStream is = new BufferedInputStream(file);
			ObjectInputStream in = new ObjectInputStream(is);
			
			componentGraph = (ComponentGraph) in.readObject();
			in.close();
			file.close();
			System.out.println("Loaded graph.");
		}catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
          
        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
		
		return componentGraph;
	}
	
	private Map<Set<Component>, Set<Component>> deserializeTestingSet(String serializedTestingSet){
		Map<Set<Component>, Set<Component>> existingConnections = null;
		try {				
			
			FileInputStream file = new FileInputStream(serializedTestingSet);
			InputStream is = new BufferedInputStream(file);
			ObjectInputStream in = new ObjectInputStream(file);
			
			existingConnections = (Map<Set<Component>, Set<Component>>) in.readObject();
			
			in.close();
			file.close();
			
			
		}catch(IOException ex)
        {
            System.out.println("IOException is caught");
        }
          
        catch(ClassNotFoundException ex)
        {
            System.out.println("ClassNotFoundException is caught");
        }
		return existingConnections;
	}
	
}
