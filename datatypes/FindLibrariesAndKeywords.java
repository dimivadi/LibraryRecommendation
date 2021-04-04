package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class FindLibrariesAndKeywords extends FindComponents{
	
	private Collection<Component> libraries;
	private Collection<Component> keywords;
	private Collection<Component> components;
	
	
	
	public Collection<Component> findComponents(CodeFile codefile) {
			
		components = new ArrayList<Component>();
		
		try {
			FindLibraries fl = new FindLibraries();
			libraries = fl.findComponents(codefile);
			FindKeywords fk = new FindKeywords();
			keywords = fk.findComponents(codefile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		components.addAll(libraries);
		components.addAll(keywords);
		
		
		return components;
			
	}
	
	

}
