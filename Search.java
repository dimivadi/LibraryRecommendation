import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import datatypes.Component;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.NoSuchKeywordsExistException;
import miners.RankedComponents;
import miners.RelatedLibraries;

public class Search extends Mode {


	private Settings settings;
	private Scanner scanner;
	private ComponentGraph componentGraph = null;
	private UserInput userInput = new UserInput();
	private ComponentMiner componentMiner = new RelatedLibraries();
	
	public Search(){
		settings = new Settings();
		scanner = new Scanner(System.in);
	}

	@Override
	public Settings chooseSettings() {
		
		chooseGraph();
		
		int methodInt;
		while(true) {
			System.out.println("Choose one of the recommendation methods below.\n"
					+"Type 1 for PPR85, 2 for PPR50, 3 for SymRenorm, \n"
					+"4 for PPR50 with sweep or 5 for PPR50 on weighted graph");
			methodInt = Integer.parseInt(scanner.nextLine());
			
			if(methodInt == 1) {
				setPPR85();
				break;
			}else if(methodInt == 2) {
				setPPR50();
				break;
			}else if(methodInt == 3) {
				setSymRenorm50();
				break;
			}else if(methodInt == 4) {
				setSweepPPR50();
				break;
			}else if(methodInt == 5) {
				setW1501PPR50();
				break;
			}
		}
		
		int n;
		while(true) {
			System.out.println("Choose number of recommendations. Max number is 30: ");
			n = Integer.parseInt(scanner.nextLine());
			if(n >= 1 && n <=30) {
				settings.setNumOfRecommendations(n);
				break;
			}
		}
		
		return settings;
		
	}
	
	private void chooseGraph() {
		String graph;
		int datasetInt;
		while(true) {
			System.out.println("Type 1 to use Maven dataset or 2 to use MALib dataset: ");
			datasetInt = Integer.parseInt(scanner.nextLine());
			if(datasetInt == 1) {
				graph = "graphMavenTF.ser";
				break;
			}else if(datasetInt == 2) {
				graph = "graphApkTF.ser";
				break;
			}
		}
		System.out.println("Loading dataset...");
		try {				
			
			FileInputStream file = new FileInputStream(graph);
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
		componentMiner.setComponentGraph(componentGraph);
	}

	@Override
	public void run() {
		
		while(true) {
			System.out.println("Type '/' to change recommendation parameters \nor type some keywords to get relevant libraries: ");
			String inputText = scanner.nextLine();
			if(inputText.matches("/"))
				chooseSettings();
			else {
				Set<Component> seedComponents = userInput.stringToKeywords(inputText);
				try {
					Map<Component, Double> result = componentMiner.componentMining(seedComponents, settings.getSweepRatio(), settings.getDampingFactor(), settings.getNormalization(), settings.getWeightValues());
					RankedComponents rankedComponents = new RankedComponents(result);
					result = rankedComponents.getTopComponents(settings.getNumOfRecommendations());
					result.forEach((x,y) -> System.out.println(x));
				} catch (NoSuchKeywordsExistException e) {
					System.out.println("No such keywords exist in graph. Try different keywords. ");
				}
			}
			
		}
	}
	
	private void setPPR50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("original");
		settings.setSweepRatio(false);
		settings.setWeightValues(null);
	}
	
	private void setPPR85() {
		settings.setDampingFactor(0.85);
		settings.setNormalization("original");
		settings.setSweepRatio(false);
		settings.setWeightValues(null);
	}
	
	private void setSymRenorm50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("symmetricNormRenorm");
		settings.setSweepRatio(false);
		settings.setWeightValues(null);
	}
	
	private void setSweepPPR50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("original");
		settings.setSweepRatio(true);
		settings.setWeightValues(null);
	}
	
	private void setW1501PPR50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("original");
		settings.setSweepRatio(false);
		settings.setWeightValues(new double[] {1,50,1});
	}
	
}
