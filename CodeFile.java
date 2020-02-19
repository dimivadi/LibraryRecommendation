package package1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

class CodeFile{
	
	private FindComponents find;
	private File codefile; 
	private ArrayList<Library> librariesInFile;
	private ArrayList<Keyword> keywordsInFile;
	
	
	CodeFile(String filename, FindComponents find){
		this.find = find;
		this.codefile = new File(filename);	
	}
	
	CodeFile(File file, FindComponents find){
		this.find = find;
		this.codefile = file;	
	}
	
	
	File getFile() {
		return codefile;
	}
	
	void getComponents() throws FileNotFoundException {
		//TODO check if arrayList == NULL
		librariesInFile = find.findLibraries(this);
		keywordsInFile = find.findKeywords(this, librariesInFile);
	}
	

	
	
}


