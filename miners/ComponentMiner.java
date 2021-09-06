package miners;

import java.util.Map;
import datatypes.Component;

public interface ComponentMiner {
	
	Map<Component, Double> componentMining(Component... seedComponents);
}