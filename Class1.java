package datatypes;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Class1{
	

	
	public static void main(String[] args) throws IOException{
		
		/*
		List<CodeFile> codefiles = new ArrayList<CodeFile>();
		Connections connections;
		FindComponents find = new FindLibrariesAndKeywords();
		Collection<Component> components;
		
		List<File> files = FilesList.listAllFiles("Java-master", "java");
		
		for(File file : files){
			CodeFile codefile = new CodeFile(file);
			codefiles.add(codefile);
			
		}
		for(CodeFile codefile : codefiles){
			components = find.findComponents(codefile);
			connections.addConnectionsbyType(components, Library.class, Keyword.class);
			
		}
		*/
		
		CodeFile codefile = new CodeFile("ConnectedComponent.java");
		FindComponents find = new FindLibrariesAndKeywords();
		Collection<Component> components = find.findComponents(codefile);
		Connections connections = new Connections();
		//connections.addConnectionsByType(components, Library.class, Keyword.class);
		

		
	}
}









