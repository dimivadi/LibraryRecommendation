package miners;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import datatypes.CodeFile;
import datatypes.Component;
import datatypes.Connections;
import datatypes.FindComponents;
import datatypes.Keyword;
import datatypes.Library;

public class RecommendationSystem {
	
	List<File> files;
	FindComponents find;
	
	public RecommendationSystem(List<File> files, FindComponents find){
		this.files = files;
		this.find = find;
	}
	
	Collection<Component> getRecommendations(Component... seed) throws FileNotFoundException{
		List<CodeFile> codefiles = new ArrayList<CodeFile>();
		Connections connections = new Connections();
		Collection<Component> components;
		
		
		for(File file : files){
			CodeFile codefile = new CodeFile(file);
			codefiles.add(codefile);
			
		}
		System.out.println("Files to codefiles ended");
		
		for(CodeFile codefile : codefiles){
			components = find.findComponents(codefile);
			connections.addConnectionsByType(components, Library.class, Keyword.class);
		}
		
		ComponentMiner cm = new RelatedLibraries(connections);
	}
}
