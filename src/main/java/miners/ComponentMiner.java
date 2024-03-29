package miners;

import java.util.Map;
import java.util.Set;

import datatypes.Component;
import datatypes.Connections;
import miners.NoSuchKeywordsExistException;

public interface ComponentMiner {
	
	Map<Component, Double> componentMining(Set<Component> seedComponents, boolean sweepRatio, double dampingFactor, String normalization, double[] weightValues) throws NoSuchKeywordsExistException;
//	Map<Component, Double> componentMining(Set<Component> seedComponents);
	void createGraph(Connections connections);
	ComponentGraph getComponentGraph();
	void setComponentGraph(ComponentGraph componentGraph);
}