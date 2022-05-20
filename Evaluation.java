import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import datatypes.Component;
import evaluation.Metrics;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.RelatedLibraries;

public class Evaluation extends Mode {

	private Scanner scanner;
	private Settings settings;
	Map<Set<Component>, Set<Component>> existingConnections = null;
	ComponentMiner componentMiner = new RelatedLibraries();
	
	Evaluation(){
		scanner = new Scanner(System.in);
		settings = new Settings();
	}
	
	@Override
	public Settings selectSettings() {
		
		
		while(true) {
			System.out.println("Choose value for damping facor, in range [0.1, 0.95]: ");
			double dampingFactor  = Double.parseDouble(scanner.nextLine().replace(',', '.'));
			if(dampingFactor >= 0.1 && dampingFactor <= 0.95) {
				settings.setDampingFactor(dampingFactor);
				break;
			}
		}
		
		while(true) {
			System.out.println("Choose one of the methods below.\n"
					+ "Type 1 for simple PPR, 2 for Symetric Normalization, \n"
					+ "3 for Symmetric Normalization with Renormalization or 4 for PPR on weighted graph");
			int methodInt = Integer.parseInt(scanner.nextLine());
			if(methodInt == 1) {
				settings.setNormalization("original");
				settings.setWeightValues(null);
				break;
			}else if(methodInt == 2) {
				settings.setNormalization("symmetricNorm");
				settings.setWeightValues(null);
				break;
			}else if(methodInt == 3) {
				settings.setNormalization("symmetricNormRenorm");
				settings.setWeightValues(null);
				break;
			}else if(methodInt == 4) {
				settings.setNormalization("original");
				settings.setWeightValues(new double[] {1,50,1});
				break;
			}
		}
		
		while(true) {
			System.out.println("Apply sweep procedure?(y/n)");
			String sweep = scanner.nextLine();
			if(sweep.equalsIgnoreCase("y")) {
				settings.setSweepRatio(true);
				break;
			}else if(sweep.equalsIgnoreCase("n")) {
			settings.setSweepRatio(false);
			break;
			}	
		}
		
		return settings;

	}

	public void selectGraph() {
		String graph;
		String testing;
		ComponentGraph componentGraph = null;
		int datasetInt;
		int nodesInt;
		while(true) {
			System.out.println("Type 1 to use Maven dataset or 2 to use MALib dataset: ");
			datasetInt = Integer.parseInt(scanner.nextLine());
			if(datasetInt == 1 || datasetInt == 2)
				break;
		}
		while(true) {
			System.out.println("Type 1 to use nodes K-L-P or 2 to use nodes K-L: ");
			nodesInt = Integer.parseInt(scanner.nextLine());
			if(datasetInt == 1 && nodesInt == 1) {
				graph = "graphMavenTT.ser";
				testing = "testingMavenTT.ser";
				break;
			}else if(datasetInt == 1 && nodesInt == 2) {
				graph = "graphMavenTF.ser";
				testing = "testingMavenTF.ser";
				break;
			}else if(datasetInt == 2 && nodesInt == 1) {
				graph = "graphApkTT.ser";
				testing = "testingApkTT.ser";
				break;
			}else if(datasetInt == 2 && nodesInt == 2) {
				graph = "graphApkTF.ser";
				testing = "testingApkTF.ser";
				break;
			}
		}
		System.out.println("Loading dataset...");
		try {				
			
			FileInputStream file = new FileInputStream(testing);
			InputStream is = new BufferedInputStream(file);
			ObjectInputStream in = new ObjectInputStream(file);
			
			existingConnections = (Map<Set<Component>, Set<Component>>) in.readObject();
			
			in.close();
			file.close();
			
			file = new FileInputStream(graph);
			is = new BufferedInputStream(file);
			in = new ObjectInputStream(is);
			
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
		
		componentMiner.setComponentGraph(componentGraph);
	}
	
	
	@Override
	public void run() {
		
		selectSettings();
		
		while(true) {
			Metrics metrics = new Metrics(componentMiner, existingConnections, settings.getSweepRatio(), settings.getDampingFactor(), settings.getNormalization(), settings.getWeightValues());
			System.out.println("Evaluation in progress... ");
			metrics.run();
			System.out.println("Type 1 to change dataset along with recommendation settings \n"
					+ "or any other number to only change recommendation settings: ");
			int change = Integer.parseInt(scanner.nextLine());
			if(change == 1)
				selectGraph();
			selectSettings();
			
		}
		
	}
	
	
	

}
