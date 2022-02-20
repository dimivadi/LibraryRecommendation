package miners;

import java.util.Map;
import java.util.Set;

import datatypes.Component;
import datatypes.Connections;

public interface ComponentMiner {
	
	Map<Component, Double> componentMining(Set<Component> seedComponents, boolean sweepRatio, double dampingFactor, String normalization, boolean isWeighted);
//	Map<Component, Double> componentMining(Set<Component> seedComponents);
	void createGraph(Connections connections, boolean isWeighted);
	ComponentGraph getComponentGraph();
	void setComponentGraph(ComponentGraph componentGraph);
}