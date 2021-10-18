package evaluation;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import datatypes.Stopwords;
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
	//Map that contains as keys a Set of seed components, and as values the libraries they are connected with
	Map<Set<Component>, Set<Component>> existingConnections = new HashMap<>();
	
	/*
	 * trainingSet: path of the training set as string
	 * testingSet: path of the testing set as string
	 * filesExtensions: extensions of files to use, as string separated by non word characters
	 */
	Evaluate(String trainingSet, String testingSet, String filesExtensions) throws IOException{
		
		//update stopwords
		Stopwords stopwords = new Stopwords();
		stopwords.addStopwords(trainingSet);
		stopwords.addStopwords(testingSet);
		
		
		trainingFiles = FilesList.listAllFiles(trainingSet, filesExtensions);
		testingFiles = FilesList.listAllFiles(testingSet, filesExtensions);
		
		List<CodeFile> trainingCodefiles = CodeFile.getCodeFiles(this.trainingFiles);
		
		
		for(CodeFile codefile : trainingCodefiles){
			components = find.findComponents(codefile);
			connections.addConnectionsByType(components, Library.class, Keyword.class);
		}
		
		componentMiner = new RelatedLibraries(connections);
		
		FileNameToKeywords fileNameToKeywords = new FileNameToKeywords();
		
		for(File file: testingFiles) {
			FindComponents findLibraries = new FindLibraries();
			Set<Component> fileLibraries = findLibraries.findComponents(new CodeFile(file));
			
			Set<Component> fileNameAsKeywords = fileNameToKeywords.getKeywords(file);
			Set<String> stopwordsSet = stopwords.getStopwords();
			
			for (Iterator<Component> i = fileNameAsKeywords.iterator(); i.hasNext();) {
			    Component element = i.next();
			    if (stopwordsSet.contains(element.getName())) {
			        i.remove();
			    }
			}
			
			if(existingConnections.putIfAbsent(fileNameAsKeywords, fileLibraries) == null)
				continue;
			else
				existingConnections.get(fileNameAsKeywords).addAll(fileLibraries);
			
		}
		
		
//		for(File file: testingFiles) {
//			FindComponents findLibraries = new FindLibraries();
//			Set<Component> fileLibraries = findLibraries.findComponents(new CodeFile(file));
//			Set<Keyword> keywords = f.getKeywords(file);
//			for(Keyword k: keywords) {
//				Set<Component> keywordLibraries = new HashSet<>(fileLibraries);
//					
//				if(existingConnections.putIfAbsent(k, keywordLibraries) == null) 
//					continue;
//				else {
//					existingConnections.get(k).addAll(keywordLibraries);
//				}
//			}
//		}
	}
	
	//for a given set of components, return a subset excluding any components that their name attribute matches any stopword
//	public Set<Component> componentsExcludingStopwords(Set<Component> c) throws FileNotFoundException{
//		Set<Component> components = c;
//		Stopwords stopwords = new Stopwords();
//		
//		for (Iterator<Component> i = components.iterator(); i.hasNext();) {
//		    Component element = i.next();
//		    if (stopwords.contains(element.getName())) {
//		        i.remove();
//		    }
//		}
//		
//		return components;
//	}
//	
	public abstract void run();
	
	
	
}
