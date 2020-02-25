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
	public boolean existsInArrayList(ArrayList<Component> components) {
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
	
}
