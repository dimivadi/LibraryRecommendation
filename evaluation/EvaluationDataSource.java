package evaluation;

import java.util.Map;
import java.util.Set;

import datatypes.Component;
import datatypes.Connections;

/*
 * 
 * Interface to provide the following essential data for evaluation classes
 * i) ComponentMiner: an instance of ComponentMiner containing the graph between components created by the training set
 * ii) existingConnections: Map containing as keys the components of the testing set and as values the components that the keys are connected to
 * 
 * 
 */

public interface EvaluationDataSource {

	//ComponentMiner getComponentMiner();
	
	Map<Set<Component>, Set<Component>> getExistingConnections();
	
	Connections getConnections();
	
}
