import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

import datatypes.Component;
import miners.ComponentGraph;

public class GraphData {

	ComponentGraph componentGraphMalib;
	ComponentGraph componentGraphMaven;
	Map<Set<Component>, Set<Component>> existingConnectionsMalib;
	Map<Set<Component>, Set<Component>> existingConnectionsMaven;
	
	public GraphData() {
		componentGraphMalib = deserializeComponentGraph("graphApkNT.ser");
		//componentGraphMaven = deserializeComponentGraph("graphMavenTT.ser");
		existingConnectionsMalib = deserializeTestingSet("testingApkNT.ser");
		existingConnectionsMaven = deserializeTestingSet("testingMavenTT.ser");
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
