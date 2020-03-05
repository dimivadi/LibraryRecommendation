package package1;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {
	
	private String name;
	
	
	public Component(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	// List or ArrayList
	public boolean existsInArrayList(List<Component> components) {
		for(Component comp : components) {
			if(comp.getName() == this.name) {
				return true;
			}
		}
		return false;
	}
	
	Component getComponentByName(ArrayList<Component> compList, String name) {
		
		for(Component comp : compList) {
			if(comp.getName() == name) {
				return comp;
			}
		}
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this)
			return true;
		if(!(o instanceof Component))
			return false;
		Component c = (Component) o;
		return c.getName() == this.getName();
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
}
