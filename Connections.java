package package1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class Connections {
	
	private Map<Component, Collection<Component>> connections;
	
	void addConnection(Component componentA, Component componentB) {
		if(!connections.containsKey(componentA)) {
			connections.put(componentA, new ArrayList<Component>());
		}
		if(!connections.containsKey(componentB)) {
			connections.put(componentB, new ArrayList<Component>());
		}
		
		connections.get(componentA).add(componentB);
		connections.get(componentB).add(componentA);
	}
	
	void addConnectionsByType(Collection<Component> components, Class c1, Class c2) {
		//
	}
	
	void addComponent(Component component) {
		connections.put(component, new ArrayList<Component>());
	}
	
	void addComponent(Collection<Component> components) {
		for(Component component : components)
			connections.put(component, new ArrayList<Component>());
	}
	
	Set<Component> getComponents(){
		return connections.keySet();
	}
	
	
	Collection<Component> getComponentConnections(Component component) {
		return connections.get(component);
	}
	
	
	void removeConnection(Component componentA, Component componentB) {
		connections.get(componentA).remove(componentB);
		connections.get(componentB).remove(componentA);
	}
	
	
	
	void merge(Connections connections) {
		this.connections.putAll(connections.getConnections());
	}
	
	 Map<Component, Collection<Component>> getConnections(){
		 return connections;
	 }
	

	
	
}

