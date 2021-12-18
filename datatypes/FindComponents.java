package datatypes;

import java.util.Set;

//Scans a file and finds the appropriate components depending on the subclass
public abstract class FindComponents {
	
	public abstract Set<Component> findComponents(CodeFile codefile);
}
