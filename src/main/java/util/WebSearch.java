package util;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import datatypes.Component;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.NoSuchKeywordsExistException;
import miners.RankedComponents;
import miners.RelatedLibraries;
import web.Language;
import web.Query;

public class WebSearch {
	
	private Settings settings = new Settings();
	private ComponentMiner componentMiner = new RelatedLibraries();
	private UserInput userInput = new UserInput();
	private Set<Component> seedComponents = new HashSet<Component>();
	
	public WebSearch(Query query) {
		settings.setForType(Language.valueOf(query.getLang().toUpperCase()));
//		configureSettings(query);
		loadGraph(query);
		this.seedComponents = userInput.stringToKeywords(query.getKeywords());
	}
	
	
	public Set<Component> search() {
		Map<Component, Double> result;
		Set<Component> recommendedLibraries = new LinkedHashSet<Component>();
		try {
			result = componentMiner.componentMining(seedComponents, settings.getSweepRatio(), settings.getDampingFactor(), settings.getNormalization(), settings.getWeightValues());
			
		} catch (NoSuchKeywordsExistException e) {
			System.out.println("No such keywords exist in graph. Try different keywords. ");
			return null; //TODO
		}
		
		RankedComponents rankedComponents = new RankedComponents(result);
		result = rankedComponents.getTopComponents(settings.getNumOfRecommendations());
		
		result.forEach((x,y) -> recommendedLibraries.add(x));
		
		return recommendedLibraries;
	}
	
//	private Settings configureSettings(Query query) {
//		settings.set
//		if(query.getLang().equals("Java"))
//			settings.setForType(GraphType.MAVEN);
//		else if(query.getLang().equals("Android")) 
//			settings.setForType(GraphType.MALIB);
//		
//		settings.setNumOfRecommendations(Integer.parseInt(query.getNumOfRecommendations()));
//			
//		return this.settings;
//	}
	
	private void loadGraph(Query query) {
		GraphData graphData = new GraphData();
		graphData.loadSerializedMalib();
		ComponentGraph componentGraph = graphData.getComponentGraphMalib();
		componentMiner.setComponentGraph(componentGraph);
	}
	
}
