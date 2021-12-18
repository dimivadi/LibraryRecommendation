package datatypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Connections {
	
	private Map<Component, Set<Component>> connections = new HashMap<Component, Set<Component>>();
	
	/*
	 * given 2 components, add each one as key in the connections map, and the other as an element in its corresponding set
	 * e.g. 
	 * (A -> (...,B))
	 * (B -> (...,A))
	 */
	
	public void addConnection(Component componentA, Component componentB) {
		if(!connections.containsKey(componentA)) {
			connections.put(componentA, new HashSet<Component>());
		}
		if(!connections.containsKey(componentB)) {
			connections.put(componentB, new HashSet<Component>());
		}
		if(!componentA.equals(componentB)) {
			connections.get(componentA).add(componentB);
			connections.get(componentB).add(componentA);
		}
		
	}
	
	/*
	 * given a collection of components, 
	 * call addConnection(A,B) for every 2 components that are of different class, for any class defined in the classes array
	 */
	public void addConnectionsByType(Collection<Component> components, Class... classes) {
		
		//if classes array contains only one element, then add a connection between all the components of this unique class
		if(classes.length == 1) {
			
			Iterator<Component> i = components.iterator();
			//save an element of collection components in a local variable
			Component currentComponent = i.next();
			//remove this element from the collection
			i.remove();
			//add a connection with every element of the collection
			if(currentComponent.getClass() == classes[0]) {
				for(Component comp: components) {
					if(comp.getClass() == classes[0])
						addConnection(currentComponent, comp);
				}	
			}
			
	
		}else {
			
			List<Class> classesAsList = Arrays.asList(classes);
			
			List<List<Component>> classesOfComponents = new ArrayList<List<Component>>(classes.length);
			int index;
			
			for (int i=0; i < classes.length; i++) {
				classesOfComponents.add(new ArrayList<Component>());
			}
			//Create a List for every Class
			for(Component component : components) {
				for(Class c : classes) {
					if(component.getClass() == c) {
						index = classesAsList.indexOf(c);
						classesOfComponents.get(index).add(component);
						break;
					}
				}
			}
			
			int i,j;
			for(i=0; i < classes.length-1; i++) {
				for(j=i+1; j < classes.length; j++) {
					for(Component component1 : classesOfComponents.get(i)) {
						for(Component component2 : classesOfComponents.get(j)) 
							addConnection(component1, component2);
					}	
				}
			}
			
		}

	}
	
	public Map<Component, Set<Component>> getConnections(){
		return connections;
	}
	
	public void addComponent(Component component) {
		if(!connections.containsKey(component))
			connections.put(component, new HashSet<Component>());
	}
	
	public void addComponent(Collection<Component> components) {
		for(Component component : components)
			addComponent(component);
	}
	
	public Set<Component> getComponents(){
		return connections.keySet();
	}
	
	public Set<Map.Entry<Component, Set<Component>>> getEntries(){
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

		for(Map.Entry<Component, Set<Component>> e : connections.getEntries()) {
			if(!this.connections.containsKey(e.getKey()))
				this.connections.put(e.getKey(), e.getValue());
			else {
				this.connections.get(e.getKey()).addAll(e.getValue());
			}
		}
	}

	

	
	
}

