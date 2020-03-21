package datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Connections {
	
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
		Collection<Component> class1 = new ArrayList<Component>();
		Collection<Component> class2 = new ArrayList<Component>();
		
		for(Component component : components) {
			if(component.getClass() == c1)
				class1.add(component);
			else
				class2.add(component);
		}
		for(Component component1 : class1) {
			for(Component component2 : class2)
				addConnection(component1, component2);
		}
	}
	
	public void addComponent(Component component) {
		if(!connections.containsKey(component))
			connections.put(component, new ArrayList<Component>());
	}
	
	public void addComponent(Collection<Component> components) {
		for(Component component : components)
			addComponent(component);
	}
	
	public Set<Component> getComponents(){
		return connections.keySet();
	}
	
	public Set<Map.Entry<Component, Collection<Component>>> getEntries(){
		return connections.entrySet();
	}
	
	
	public Collection<Component> getComponentConnections(Component component) {
		return connections.get(component);
	}
	
	
	void removeConnection(Component componentA, Component componentB) {
		connections.get(componentA).remove(componentB);
		connections.get(componentB).remove(componentA);
	}
	
	
	
	void merge(Connections connections) {

		for(Map.Entry<Component, Collection<Component>> e : connections.getEntries()) {
			if(!this.connections.containsKey(e.getKey()))
				this.connections.put(e.getKey(), e.getValue());
			else {
				this.connections.get(e.getKey()).addAll(e.getValue());
			}
		}
	}
	
	Map<Component, Collection<Component>> getConnections(){
		 return connections;
	 }
	

	
	
}

