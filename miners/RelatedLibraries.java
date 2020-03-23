package miners;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import datatypes.Component;
import datatypes.Connections;

public class RelatedLibraries extends ComponentMiner{
	
	private Connections connections;
	private Collection<Component> relatedLibraries = new HashSet<Component>();
	
	public RelatedLibraries(Connections connections){
		this.connections = connections;
	}
	
	public Collection<Component> componentMining(Collection<Component> components){
		
		Collection<Component> connectedComponents;
		
		for(Component component : components) {
			connectedComponents = connections.getConnections().get(component);
			if(connectedComponents == null) {
				System.out.println("Component "+component.getName()+" does not exist in graph");
				continue;
			}
			
			relatedLibraries = connectedComponents.stream().filter(c->c.getClass() == Library.class).collect(Collectors.toSet());
			relatedLibraries.addAll(connectedComponents);
		}
		return relatedLibraries;
	}

}
