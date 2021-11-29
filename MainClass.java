
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import datatypes.Component;
import datatypes.Connections;
import evaluation.*;
import examples.*;
import miners.ComponentMiner;
import miners.RelatedLibraries;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		

//		EvaluationDataSource evaluationDataSource = new EvaluateFromFiles("jEdit", "test", "java");
		EvaluationDataSource evaluationDataSource = new EvaluateFromMavenCentral("maven-data.csv/links_all.csv");
		
		
		Connections connections = evaluationDataSource.getConnections();
		ComponentMiner componentMiner = new RelatedLibraries(connections);
		Map<Set<Component>, Set<Component>> existingConnections = evaluationDataSource.getExistingConnections();
		
		
		Evaluate evaluate = new AreaUnderCurve(componentMiner, existingConnections);
//		Evaluate evaluate = new HitRate(componentMiner, existingConnections);
		
		evaluate.run();
	}
}









