package evaluation;

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
import miners.ComponentMiner;
import miners.RelatedLibraries;


/*
 * 
 * Abstract class to provide essential fields for every evaluation method in the package,
 * i.e. the graph of components (ComponentMiner) 
 * and the existing connections between components in the testing set (existingConnections)
 * Every evaluation class must extend this abstract class 
 * 
 */


public abstract class Evaluate {
	
	List<File> trainingFiles; 
	List<File> testingFiles;
	Collection<Component> components;
	FindComponents find = new FindLibrariesAndKeywords();
	Connections connections = new Connections();
	ComponentMiner componentMiner;
	Map<Component, Set<Component>> existingConnections = new HashMap<>();
	
	/*
	 * trainingSet: path of the training set as string
	 * testingSet: path of the testing set as string
	 * filesExtensions: extensions of files to use, as string separated by non word characters
	 */
	Evaluate(String trainingSet, String testingSet, String filesExtensions) throws FileNotFoundException{
		
		trainingFiles = FilesList.listAllFiles(trainingSet, filesExtensions);
		testingFiles = FilesList.listAllFiles(testingSet, filesExtensions);
		
		List<CodeFile> trainingCodefiles = CodeFile.getCodeFiles(this.trainingFiles);
		
		
		for(CodeFile codefile : trainingCodefiles){
			components = find.findComponents(codefile);
			connections.addConnectionsByType(components, Library.class, Keyword.class);
		}
		
		componentMiner = new RelatedLibraries(connections);
		
		FileNameToKeywords f = new FileNameToKeywords();
		
		for(File file: testingFiles) {
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
	}
	
	public abstract void run();
	
	
	
}
