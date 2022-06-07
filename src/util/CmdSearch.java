import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import datatypes.Component;
import evaluation.Similarity;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.NoSuchKeywordsExistException;
import miners.RankedComponents;
import miners.RelatedLibraries;
import miners.EmptyGraphException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.ColorScheme;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;


@Command(name="search", description="get recommended libraries for certain keywords")
public class CmdSearch implements Callable<Integer> {
	
	@Parameters(description="Keywords, seperated by space, without quotes", arity="1..*")
	private String[] keywords;
	
	@Option(names="-g", description = "Graph on which search will be applied to (valid values (case insensitive): @|bold ${COMPLETION-CANDIDATES})|@ (default: ${DEFAULT-VALUE})")
	private GraphType graphType = GraphType.MALIB;
	
	@Option(names = "-m",description = "Search method (valid values (case insensitive): ${COMPLETION-CANDIDATES}) (default: ${DEFAULT-VALUE})")
	private ScoringMethod method = ScoringMethod.SymRenorm;
	
	@Option(names = {"-n"}, description = "Number of recommended libraries. Max value: 30 (default: ${DEFAULT-VALUE})")
	private int n = 10;
	
	@Option(names = {"-o"}, description = "Save results to file named 'recommendations.txt'")
	private boolean saveResults = false;
	
	@Option(names= {"-h","--help"}, usageHelp = true, description="Display instructions")
	boolean help=false;
	
	private static GraphData graphData;
	private ComponentGraph componentGraph;
	private Settings settings = new Settings();
	private UserInput userInput = new UserInput();
	private ComponentMiner componentMiner = new RelatedLibraries();
	
	public void setGraphData(GraphData graphData) {
		this.graphData = graphData;
	}
	
	
	@Override
    public Integer call() throws Exception {
		
		
		if(n >= 1 && n <=30) 
			settings.setNumOfRecommendations(n);
		else {
			System.err.println("Invalid number of recommended libraries\n");
			return 1;
		}
		
		switch(graphType) {
		case MALIB: 
			componentGraph = graphData.getComponentGraphMalib();
			break;
		case MAVEN:
			componentGraph = graphData.getComponentGraphMaven();
		}
		componentMiner.setComponentGraph(componentGraph);
		
		switch(method) {
		case PPR50:
			setPPR50();
			break;
		case PPR85:
			setPPR85();
			break;
		case SymRenorm:
			setSymRenorm50();
			break;
		case PPR50Sweep:
			setSweepPPR50();
			break;
		case PPR50weighted:
			setW1501PPR50();
			break;
		case CosSim:
			settings.setCosSim(true);
			settings.setMethodShortname("Cosine similarity");
			break;
		}
		
		Map<Component, Double> result = null;
		Set<Component> seedComponents = userInput.stringArrayToKeywords(keywords);
		if(!settings.getCosSim()) {
			try {
				result = componentMiner.componentMining(seedComponents, settings.getSweepRatio(), settings.getDampingFactor(), settings.getNormalization(), settings.getWeightValues());
				
			} catch (NoSuchKeywordsExistException e) {
				System.out.println("No such keywords exist in graph. Try different keywords. ");
				return 0;
			} catch (EmptyGraphException e) {
				System.out.println("Dataset is not loaded yet. Use the command 'load' to import it.");
				return 0;
			}

		}else {
			Similarity  similarity = new Similarity(componentMiner, false);
			result = similarity.getLibrarySimilarity(seedComponents);
		}
		RankedComponents rankedComponents = new RankedComponents(result);
		result = rankedComponents.getTopComponents(settings.getNumOfRecommendations());
		if(saveResults) {
			FileWriter fileWriter = new FileWriter(new File("recommendations.txt"));
			result.forEach((x,y) -> {
				try {
					fileWriter.append(x.getName() + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			fileWriter.close();
			System.out.println("Results exported to file 'recommendations.txt'");
		}else 
			result.forEach((x,y) -> System.out.println(x));
		
		
		return 0; 
		
	}
	
	public void runSearch(String[] args) {
		ColorScheme colorScheme = CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.AUTO);
		int exitCode = new CommandLine(new CmdSearch())
				.setCaseInsensitiveEnumValuesAllowed(true)
				.setColorScheme(colorScheme)
				.execute(args);
	}
	



	private void setPPR50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("original");
		settings.setSweepRatio(false);
		settings.setWeightValues(null);
		settings.setMethodShortname("PPR50");
		settings.setCosSim(false);
	}
	
	private void setPPR85() {
		settings.setDampingFactor(0.85);
		settings.setNormalization("original");
		settings.setSweepRatio(false);
		settings.setWeightValues(null);
		settings.setMethodShortname("PPR85");
		settings.setCosSim(false);
	}
	
	private void setSymRenorm50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("symmetricNormRenorm");
		settings.setSweepRatio(false);
		settings.setWeightValues(null);
		settings.setMethodShortname("SymRenorm");
		settings.setCosSim(false);
	}
	
	private void setSweepPPR50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("original");
		settings.setSweepRatio(true);
		settings.setWeightValues(null);
		settings.setMethodShortname("Sweep/PPR50");
		settings.setCosSim(false);
	}
	
	private void setW1501PPR50() {
		settings.setDampingFactor(0.5);
		settings.setNormalization("original");
		settings.setSweepRatio(false);
		settings.setWeightValues(new double[] {1,50,1});
		settings.setMethodShortname("W1-50-1");
		settings.setCosSim(false);
	}
	
}
