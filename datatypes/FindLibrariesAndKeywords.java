package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class FindLibrariesAndKeywords extends FindComponents{
	
	private Collection<Component> libraries;
	private Collection<Component> keywords;
	private Set<Component> components;
	
	
	
	public Set<Component> findComponents(CodeFile codefile) {
			
		components = new HashSet<Component>();
		
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
