package examples;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
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
import evaluation.EvaluationDataSource;


/*
 * 
 * Class to provide essential fields for an evaluation method,
 * i.e. the graph of components (ComponentMiner) 
 * and the existing connections between components in the testing set (existingConnections)
 * This class takes as input a string pointing to the training set, another one for the testing set, and a string indicating the extensions of the files to be used
 * 
 * trainingSet: path of the training set as string
 * testingSet: path of the testing set as string
 * filesExtensions: extensions of files to use, as string separated by non word characters
 * 
 */


public class EvaluateFromFiles implements EvaluationDataSource{
	
	List<File> trainingFiles; 
	List<File> testingFiles;
	Collection<Component> components;
	FindComponents find = new FindLibrariesAndKeywords();
	Connections connections = new Connections();
	ComponentMiner componentMiner;
	//Map that contains as keys a Set of seed components, and as values the libraries they are connected with
	Map<Set<Component>, Set<Component>> existingConnections = new HashMap<>();
	
	/*

	 */
	public EvaluateFromFiles(String trainingSet, String testingSet, String filesExtensions) throws IOException{
		
		//update stopwords
		Stopwords stopwords = new Stopwords();
		stopwords.resetStopwordsList();
		stopwords.addStopwords(trainingSet);
		stopwords.addStopwords(testingSet);
		
		
		trainingFiles = FilesList.listAllFiles(trainingSet, filesExtensions);
		testingFiles = FilesList.listAllFiles(testingSet, filesExtensions);
		
		List<CodeFile> trainingCodefiles = CodeFile.getCodeFiles(this.trainingFiles);
		
		
		for(CodeFile codefile : trainingCodefiles){
			components = find.findComponents(codefile);
			connections.addConnectionsByType(components, Library.class, Keyword.class);
		}
		
		componentMiner = new RelatedLibraries();
		componentMiner.createGraph(connections);
		
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
		

	}
	
//	@Override
//	public ComponentMiner getComponentMiner() {
//		
//		return componentMiner;
//	}
	
	@Override
	public Connections getConnections() {
		return connections;
	}
	
	@Override
	public Map<Set<Component>, Set<Component>> getExistingConnections(){
		
		return existingConnections;
	}
	
}
