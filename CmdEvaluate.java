
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import datatypes.Component;
import evaluation.Metrics;
import miners.ComponentGraph;
import miners.ComponentMiner;
import miners.RelatedLibraries;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Help.ColorScheme;


@Command(name="evaluate", description="Evaluate the recommendation system")
public class CmdEvaluate implements Callable<Integer> {
	
	@Option(names="-g", required = true, description = "REQUIRED. Graph on which search will be applied to (valid values (case insensitive): ${COMPLETION-CANDIDATES})")
	private GraphType graphType;
	
//	@Option(names = "x", description = "exclude 'Project' nodes from graph analysis, (default: ${DEFAULT-VALUE})")
//	private boolean excludeProjects = false;
	
	@Option(names="-m", description = "Method (valid values (case insensitive): ${COMPLETION-CANDIDATES}) (default: ${DEFAULT-VALUE})")
	private NormalizationMethods normalizationMethod = NormalizationMethods.ORIGINAL;
	
	@Option(names= {"-a"}, description ="Damping factor, must be in range (0.1, 0.95), (default: ${DEFAULT-VALUE})")
	private double a = 0.8;

	@Option(names = {"-s"}, description = "Apply sweep procedure, (default: ${DEFAULT-VALUE})")
	private boolean s= false;
	
	
	@Option(names= {"-h","--help"}, usageHelp = true, description="Display instructions")
	boolean help=false;
	
	private static GraphData graphData;
	private Settings settings = new Settings();
	private ComponentGraph componentGraph;
	private Map<Set<Component>, Set<Component>> existingConnections;
	private ComponentMiner componentMiner = new RelatedLibraries();
	
	public void setGraphData(GraphData graphData) {
		this.graphData = graphData;
	}
	
	@Override
    public Integer call() throws Exception {
		
		switch(graphType) {
		case MALIB: 
			componentGraph = graphData.getComponentGraphMalib();
			existingConnections = graphData.getMalibTestingSet();
			break;
		case MAVEN:
			componentGraph = graphData.getComponentGraphMaven();
			existingConnections = graphData.getMavenTestingSet();
		}
		componentMiner.setComponentGraph(componentGraph);
		
		switch(normalizationMethod) {
		case ORIGINAL:
			settings.setNormalization("original");
			settings.setWeightValues(null);
			break;
		case SYMMETRICNORM:
			settings.setNormalization("symmetricNorm");
			settings.setWeightValues(null);
			break;
		case SYMMETRICNORMRENORM:
			settings.setNormalization("symmetricNormRenorm");
			settings.setWeightValues(null);
			break;
		case WEIGHTED:
			settings.setNormalization("original");
			settings.setWeightValues(new double[] {1,50,1});
			break;
		}
		
		
		if(a >= 0.1 && a <= 0.95) {
			settings.setDampingFactor(a);
		}else {
			System.err.println("Invalid value of damping factor");
			return 0;
		}
		
		settings.setSweepRatio(s);
		
		Metrics metrics = new Metrics(componentMiner, existingConnections, settings.getSweepRatio(), settings.getDampingFactor(), settings.getNormalization(), settings.getWeightValues());
		System.out.println("Evaluation in progress... This might take a while");
		metrics.run();
		
		return 0; 
		
	}
	
	public void runEval(String[] args) {
		ColorScheme colorScheme = CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.AUTO);
		int exitCode = new CommandLine(new CmdEvaluate())
				.setCaseInsensitiveEnumValuesAllowed(true)
				.setColorScheme(colorScheme)
				.execute(args);
		
	}
	
}
