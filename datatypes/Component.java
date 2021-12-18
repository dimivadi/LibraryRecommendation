package datatypes;

public abstract class Component {
	
	private String name;
	
	
	public Component(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

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
		return (this.name+this.getClass()).equalsIgnoreCase(c.getName()+c.getClass());
	
	}
	
	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}
	
}
