package util;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Help.ColorScheme;

@Command(name = "load", description = "Load data based on certain types of datasets")
public class CmdLoad implements Callable<Integer> {
	
	@Option(names = "-g", required = true, description = "(REQUIRED)Type of imported graph (valid values (case insensitive): ${COMPLETION-CANDIDATES})")
	private GraphType graphType;
	
	@Parameters(description = "If filepath is provided, a new graph will be constructed based on the loaded data. "
			+ "This will take a bit longer that loading the pre-constructed graph.\n"
			+ "For Malib-type datasets, insert filepaths seperated by spaces, without quotes,  in the following order: apk_info.csv lib_info.csv relation.csv\n"
			+ "For Maven-type datasets, insert filepath for its unique file, without quotes")
			
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
			break;
		case MALIB:
			if(filePaths == null)
				graphData.loadSerializedMalib();
			else {
				try {
					graphData.loadMalibType(filePaths[0], filePaths[1], filePaths[2]);
				} catch(ArrayIndexOutOfBoundsException e) {
					System.out.println("Not enough filepaths");
				}
				break;
			}
				
		}
		return 0;
	}
	
	public void runLoad(String[] args) {
		ColorScheme colorScheme = CommandLine.Help.defaultColorScheme(CommandLine.Help.Ansi.AUTO);
		int exitCode = new CommandLine(new CmdLoad())
				.setCaseInsensitiveEnumValuesAllowed(true)
				.setColorScheme(colorScheme)
				.execute(args);
	}
}
