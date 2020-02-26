package package1;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Connections {

	private Map<Component, List<Component>> connections = new HashMap<Component, List<Component>>();
	
	Connections(CodeFile codefile) throws FileNotFoundException{
		//Create vertices without edges
		for(Component comp : codefile.getComponents()) {
			connections.put(comp, new ArrayList<Component>());
		}
	}
	
	// create edges between all vertices
	public void connect() {
		Set<Component> elems = new HashSet<Component>();
		elems = connections.keySet();
		for(Component each : elems) {
			Component temp = each;
			elems.remove(each);
			for(Component c : elems) {
				addConnection(temp, c);
			}
		}
	}
	
	// 
	public void connect(int i) {
		// i -> parameter for different connections in graphs
	}

	// add edge given two vertices
	public void addConnection(Component A, Component B) {
		connections.get(A).add(B);
		connections.get(B).add(A);
	}
	
}
