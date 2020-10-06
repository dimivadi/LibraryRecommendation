package datatypes;

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
	/*
	Component getComponentByName(ArrayList<Component> compList, String name) {
		
		for(Component comp : compList) {
			if(comp.getName() == name) {
				return comp;
			}
		}
		return this;
	}
	*/
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null)
			return false;
		if(o == this)
			return true;
		if(!(o instanceof Component))
			return false;
		Component c = (Component) o;
		return this.name.equalsIgnoreCase(c.getName());
	
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
}
