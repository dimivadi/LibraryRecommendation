package miners;

import java.util.Collection;
import java.util.Map;

import datatypes.Component;

public interface ComponentMiner {
	
	Map<Component, Double> componentMining(Collection<Component> seedComponents);
}