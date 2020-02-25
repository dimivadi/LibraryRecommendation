package package1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class CodeFile{
	
	private FindComponents find;
	private File codefile; 
	//private ArrayList<Library> librariesInFile;
	//private ArrayList<Keyword> keywordsInFile;
	
	
	CodeFile(String filename) throws FileNotFoundException{
		find = new FindComponents();
		this.codefile = new File(filename);	
	}

	CodeFile(File file) throws FileNotFoundException{
		find = new FindComponents();
		this.codefile = file;	
	}
	
	
	public File getFile() {
		return codefile;
	}
	
	
	public ArrayList<Component> getComponents() throws FileNotFoundException{
		return find.findComponents(this);
	}
	
	//return arraylist<Library>?
	public ArrayList<Component> getLibraries() throws FileNotFoundException{
		return find.findLibraries(this);
	}
	
	public ArrayList<Component> getKeywords() throws FileNotFoundException{
		return find.findKeywords(this);
	}
	
}



