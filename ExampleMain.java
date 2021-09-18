import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import datatypes.CodeFile;
import datatypes.Component;
import datatypes.Connections;
import datatypes.FileNameToKeywords;
import datatypes.FilesList;
import datatypes.FindComponents;
import datatypes.FindLibraries;
import datatypes.FindLibrariesAndKeywords;
import datatypes.Keyword;
import datatypes.Library;
import evaluation.HitRate;
import miners.ComponentMiner;
import miners.RelatedLibraries;

public class ExampleMain {

	public static void main(String[] args) throws FileNotFoundException {
		
		Connections connections = new Connections();
		FindComponents find = new FindLibrariesAndKeywords();
		Collection<Component> components;
		

		List<File> files = FilesList.listAllFiles("jEdit", "java");
		System.out.println("-------ListedAllFiles-----------");
		
		List<CodeFile> codefiles = CodeFile.getCodeFiles(files);
		System.out.println("-------Files to codefiles ended-------");
		
		//TIME
		long start2 = System.nanoTime();
		//
		for(CodeFile codefile : codefiles){
			components = find.findComponents(codefile);
			connections.addConnectionsByType(components, Library.class, Keyword.class);
		}
		//TIME
		long elapsedTime2 = System.nanoTime() - start2;
		double elapsedTimeInSeconds2 = (double) elapsedTime2 / 1_000_000_000;
		System.out.println("time to create connections: "+ elapsedTimeInSeconds2);
		//
		ComponentMiner cm = new RelatedLibraries(connections);
		

		//Evaluation
		Map<Component, Set<Component>> existingConnections = new HashMap<>();
		
		
		List<File> testFiles = FilesList.listAllFiles("test", "java");
	
		FileNameToKeywords f = new FileNameToKeywords();
		
		for(File file: testFiles) {
			FindComponents findLibraries = new FindLibraries();
			Set<Component> fileLibraries = findLibraries.findComponents(new CodeFile(file));
			Set<Keyword> keywords = f.getKeywords(file);
			for(Keyword k: keywords) {
				Set<Component> keywordLibraries = new HashSet<>(fileLibraries);
					
				
					if(existingConnections.putIfAbsent(k, keywordLibraries) == null) 
						continue;
					else {
						existingConnections.get(k).addAll(keywordLibraries);
					}
				}
			}
		
		
		HitRate hr = new HitRate();
		System.out.println("hitRate: "+ hr.run(existingConnections, cm));
	}

}
