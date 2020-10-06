package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CodeFile{
	
	private FindComponents find;
	private File codefile; 
	
	
	public CodeFile(String filename) throws FileNotFoundException{
		this.codefile = new File(filename);	
	}

	public CodeFile(File file) throws FileNotFoundException{
		this.codefile = file;	
	}
	
	
	public File getFile() {
		return codefile;
	}
	
	/*
	public Collection<Component> getComponents() throws FileNotFoundException{
		if(components.isEmpty()) {
			components = find.findComponents(this);
		}
		return components;
	}
	*/
	
}



