
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import miners.*;
import datatypes.*;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
		
		List<CodeFile> codefiles = new ArrayList<CodeFile>();
		Connections connections = new Connections();
		FindComponents find = new FindLibrariesAndKeywords();
		Collection<Component> components;
		
		
		List<File> files = FilesList.listAllFiles("testFolder", "java");
		
		for(File file : files){
			CodeFile codefile = new CodeFile(file);
			codefiles.add(codefile);
			
		}
		for(CodeFile codefile : codefiles){
			components = find.findComponents(codefile);
			connections.addConnectionsByType(components, Library.class, Keyword.class);
		}
		
		//List<Component> temp = new ArrayList<Component>();
		//temp.add(new Keyword("keyword4"));
		ComponentMiner cm = new RelatedLibraries(connections);
		Collection<Component> seedComponents = new ArrayList<>();
		//seedComponents.add(new Keyword("Keyword4"));
		seedComponents.add(new Keyword("keyword3"));
		System.out.println(cm.componentMining(seedComponents));
	}
}









