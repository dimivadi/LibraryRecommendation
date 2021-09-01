
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import miners.*;
import datatypes.*;
import evaluation.*;


public class MainClass{
	

	
	public static void main(String[] args) throws IOException{
		
		
		List<CodeFile> codefiles = new ArrayList<CodeFile>();
		Connections connections = new Connections();
		FindComponents find = new FindLibrariesAndKeywords();
		Collection<Component> components;
		
		
		List<File> files = FilesList.listAllFiles("testFolder", "java");
		System.out.println("-------ListedAllFiles-----------");
		for(File file : files){
			CodeFile codefile = new CodeFile(file);
			codefiles.add(codefile);
			
		}
		System.out.println("Files to codefiles ended");
		int i = 0;
		for(CodeFile codefile : codefiles){
			i++;
			System.out.println("CodeFile # " + i+"\n");
			components = find.findComponents(codefile);
			
			connections.addConnectionsByType(components, Library.class, Keyword.class);
		}
		
		
		ComponentMiner cm = new RelatedLibraries(connections);

		RecommendedComponents rc = new RecommendedComponents(cm.componentMining(new Keyword("keyword2")));
		System.out.println(rc.getTopComponents(3));
		
		
	}
}









