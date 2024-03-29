package datasets;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import datatypes.CodeFile;
import datatypes.Component;
import datatypes.Connections;
import datatypes.FileNameToKeywords;
import datatypes.FilesList;
import datatypes.FindComponents;
import datatypes.FindKeywords;
import datatypes.FindLibraries;
import datatypes.FindLibrariesAndKeywords;
import datatypes.Keyword;
import datatypes.Library;
import datatypes.Project;
import datatypes.Stopwords;
import evaluation.EvaluationDataSource;


/*
 * 
 * Class to provide essential fields for an evaluation method,
 * i.e. the graph of components (ComponentMiner) 
 * and the existing connections between components in the testing set (existingConnections)
 * This class takes as input the path of the training set as a string, another one for the testing set, and a string indicating the extensions of the files to be used
 * 
 * trainingSet: path of the training set as string
 * testingSet: path of the testing set as string
 * filesExtensions: string separated by non word characters denoting extensions of files to be used
 * 
 */


public class EvaluateFromFiles implements EvaluationDataSource{
	
	private List<File> trainingFiles; 
	private List<File> testingFiles;
	private Collection<Component> components;
	private FindComponents find = new FindLibrariesAndKeywords();
	private Connections connections = new Connections();
	private Map<Set<Component>, Set<Component>> existingConnections = new HashMap<>();
	private boolean linkLibraries;
	private boolean linkLibsProject;
//	private final static boolean LINKS_BETWEEN_LIBRARIES_AND_FILE = true;
//	private final static boolean LINKS_BETWEEN_LIBRARIES = true;

	public EvaluateFromFiles(String trainingSet, String testingSet, String filesExtensions, boolean linkLibraries, boolean linkLibsProject) throws IOException{
		
		this.linkLibraries = linkLibraries;
		this.linkLibsProject = linkLibsProject;
		
		
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
			if(linkLibraries)
				connections.addConnectionsByType(components, Library.class);
			
			//add connections between a Project component(name of file stored as Project component) and its libraries
			if(linkLibsProject) {
				Set<Component> fileLibraries = components.stream()
						.filter(component -> (component.getClass().equals(Library.class)))
							.collect(Collectors.toCollection(HashSet::new));
				Project fileNameAsProject = new Project(codefile.getFileName());
				fileLibraries.forEach(library -> connections.addConnection(library, fileNameAsProject));
			}
		}
	
		FileNameToKeywords fileNameToKeywords = new FileNameToKeywords();
		
		//The keywords used as seed for testing, comprise the name of the file
		//and the libraries are the libraries that are imported from the same file
		//Alternatively, the seed components may be the keywords contained in the file
		for(File file: testingFiles) {
			FindComponents findLibraries = new FindLibraries();
			Set<Component> fileLibraries = findLibraries.findComponents(new CodeFile(file));
			if(fileLibraries.size() < 10)
				continue;
			FindComponents findKeywords = new FindKeywords();
			
			Set<Component> fileKeywords = findKeywords.findComponents(new CodeFile(file)); //use as seed the keywords of the file
//			Set<Component> fileKeywords = fileNameToKeywords.getKeywords(file); //use as seed the terms in the file name
			
			Set<String> stopwordsSet = stopwords.getStopwords();
			
			for (Iterator<Component> i = fileKeywords.iterator(); i.hasNext();) {
			    Component element = i.next();
			    if (stopwordsSet.contains(element.getName())) {
			        i.remove();
			    }
			}
			
			if(existingConnections.putIfAbsent(fileKeywords, fileLibraries) == null)
				continue;
			else
				existingConnections.get(fileKeywords).addAll(fileLibraries);
			
		}
		

	}
	
	@Override
	public Connections getConnections() {
		return connections;
	}
	
	@Override
	public Map<Set<Component>, Set<Component>> getExistingConnections(){
		
		return existingConnections;
	}
	
}
