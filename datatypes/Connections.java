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


public class Connections implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
//	private Map<Component, Set<Component>> connections = new HashMap<Component, Set<Component>>();
	
	private Map<Component, Integer> componentIndex = new HashMap<Component, Integer>();
	private int index = 0;
	private Map<Integer, Component> indexToComponent = new HashMap<>();
	private Map<Component, Set<Integer>> adjacentComponents = new HashMap<>();
	/*
	 * given 2 components, add each one as key in the connections map, and the other as an element in its corresponding set
	 * e.g. 
	 * (A -> (...,B))
	 * (B -> (...,A))
	 */
	
	public void addConnection(Component componentA, Component componentB) {
//		if(!connections.containsKey(componentA)) {
//			connections.put(componentA, new HashSet<Component>());
//		}
//		if(!connections.containsKey(componentB)) {
//			connections.put(componentB, new HashSet<Component>());
//		}
//		if(!componentA.equals(componentB)) {
//			connections.get(componentA).add(componentB);
//			connections.get(componentB).add(componentA);
//		}
		
		if(!componentIndex.containsKey(componentA)) {
			componentIndex.put(componentA, index);
			adjacentComponents.put(componentA, new HashSet<Integer>());
			indexToComponent.put(index++, componentA);
			
		}
		if(!componentIndex.containsKey(componentB)) {
			componentIndex.put(componentB, index);
			adjacentComponents.put(componentB, new HashSet<Integer>());
			indexToComponent.put(index++, componentB);
		}
//		addComponent(componentA);
//		addComponent(componentB);
		int indexA, indexB;
		if(!componentA.equals(componentB)) {
			indexA = componentIndex.get(componentA);
			indexB = componentIndex.get(componentB);
			adjacentComponents.get(componentA).add(indexB);
			adjacentComponents.get(componentB).add(indexA);
			
			
		}
	}
	
//	private void addComponent(Component component) {
//		if(!componentsList.contains(component)) {
//			componentsList.add(component);
//			index++;
//			adjacencyMatrix.add(new ArrayList<Integer>(Arrays.asList(new Integer[index])));
//			for(int i = 0; i < index-1; i++) {
//				adjacencyMatrix.get(index-1).set(i, 0);
//			}
//			for(ArrayList<Integer> list: adjacencyMatrix) {
//				list.add(0);
//			}
//			adjacencyMatrix.get(index-1).set(index-1, 1);
//		}
//	}
	
	/*
	 * given a collection of components, 
	 * call addConnection(A,B) for every 2 components that are of different class, for any class defined in the classes array
	 */
	public void addConnectionsByType(Collection<Component> componentsCollection, Class... classes) {
		Collection<Component> components = new HashSet<Component>(componentsCollection);
		//if classes array contains only one element, then add a connection between all the components of this unique class
		if(classes.length == 1) {
			
			Iterator<Component> i = components.iterator();
				while(i.hasNext()) {
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
	

	public Set<Component> getComponents(){
		
		//return connections.keySet();
		return componentIndex.keySet();
	}
	
	
	public Collection<Component> getComponentConnections(Component component){
		Set<Component> componentConnections = new HashSet<Component>();

//		return connections.get(component);
		for(Integer i: adjacentComponents.get(component)) {
			componentConnections.add(indexToComponent.get(i));
		}
		return componentConnections;
	}
	
	public void getAllConnectionsByName() {
		for(Map.Entry<Component, Set<Integer>> entry: adjacentComponents.entrySet()) {
			System.out.println("Connections of component: "+entry.getKey());
			for(Integer i: entry.getValue()) {
				System.out.print(indexToComponent.get(i)+", ");
			}
			System.out.println();
		}
	}
	
	public Collection<Component> getComponentConnectionsByType(Component component, Class connectionClass){
		
		Set<Component> componentConnections = new HashSet<>();
		
		for(Integer i: adjacentComponents.get(component)) 
			if(indexToComponent.get(i).getClass().equals(connectionClass))
				componentConnections.add(indexToComponent.get(i));
		
		
		return componentConnections;
	}
	

	
	
}

