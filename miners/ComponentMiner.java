package miners;

import java.util.Map;
import java.util.Set;

import datatypes.Component;

public interface ComponentMiner {
	
	Map<Component, Double> componentMining(Set<Component> seedComponents);
}