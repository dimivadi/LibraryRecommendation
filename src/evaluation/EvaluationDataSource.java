package evaluation;

import java.util.Map;
import java.util.Set;

import datatypes.Component;
import datatypes.Connections;

/*
 * 
 * Interface to provide the following essential data for evaluation classes
 * i) connections: Connections between components in the training test 
 * ii) existingConnections: connections between components in the testing set
 * 
 */

public interface EvaluationDataSource {
	
	Connections getConnections();
	
	Map<Set<Component>, Set<Component>> getExistingConnections();
	
}
