package miners;

import java.util.Collection;
import datatypes.Component;

public interface ComponentMiner {
	
	Collection<Component> componentMining(Collection<Component> seedComponents);
}