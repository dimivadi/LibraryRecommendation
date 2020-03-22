package miners;

import java.util.Collection;
import datatypes.Component;

public abstract class ComponentMiner {
	
	abstract Collection<Component> componentMining(Collection<Component> components);
}
