import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "load", description = "Load data based on certain types of datasets")
public class CmdLoad implements Callable<Integer> {
	
	@Option(names = "-g", required = true, description = "(REQUIRED)Type of imported dataset (valid values (case insensitive): ${COMPLETION-CANDIDATES})")
	private GraphType graphType;
	
	@Parameters(description = "Filepaths of imported data.\nFor Malib-type datasets, type the filepaths seperated by spaces, without quotes,  in the following order: apk_info.csv lib_info.csv relation.csv"
			+ "\nIf filepath has not been provided, then a default dataset will be loaded")
	private String[] filePaths = null;
	
	@Option(names= {"-h","--help"}, usageHelp = true, description="Display instructions")
	boolean help = false;
	
	private static GraphData graphData;
	
	public void setGraphData(GraphData graphData) {
		this.graphData = graphData;
	}
	
	@Override
    public Integer call() throws Exception {
		
		switch(graphType) {
		case MAVEN:
			if(filePaths == null) 
				graphData.loadSerializedMaven();
			else 
				graphData.loadMavenType(filePaths[0]);
		case MALIB:
			if(filePaths == null)
				graphData.loadSerializedMalib();
			else
				try {
					graphData.loadMalibType(filePaths[0], filePaths[1], filePaths[2]);
				} catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("Not enough filepaths");
				}
		}
		return 0;
	}
	
	public void runLoad(String[] args) {
		int exitCode = new CommandLine(new CmdLoad()).setCaseInsensitiveEnumValuesAllowed(true).execute(args);
	}
}
