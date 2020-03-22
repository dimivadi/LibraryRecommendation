package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CodeFile{
	
	private FindComponents find;
	private File codefile; 
	
	
	CodeFile(String filename) throws FileNotFoundException{
		find = new FindLibrariesAndKeywords();
		this.codefile = new File(filename);	
	}

	CodeFile(File file) throws FileNotFoundException{
		find = new FindLibrariesAndKeywords();
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



