package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.Component;
import datatypes.Connections;
import datatypes.Keyword;
import datatypes.Library;
import datatypes.Project;
import evaluation.EvaluationDataSource;

public class EvaluateFromMaven implements EvaluationDataSource{
	
	private Connections connections = new Connections();
	private Map<Set<Component>, Set<Component>> existingConnections = new HashMap<>();
	private Set<String> stopwords = new HashSet<>();
//	private static final boolean LINKS_BETWEEN_DEPENDENCIES = true;
//	private static final boolean LINKS_BETWEEN_DEPENDENCIES_AND_LIBRARY = true;
	
	public EvaluateFromMaven(String filePath, boolean linkDependencies, boolean linkDependenciesToProject) throws IOException {
		
		
		String[] libraryTerms;
		Random rand = new Random();
		
		updateStopwords(filePath);
		
		//read csv file and store contents in libraryDependencies map
		File file = new File(filePath);
		Map<String, Set<String>> libraryDependencies = new HashMap<>();
		Pattern libraryPattern = Pattern.compile(".*(?=:)");
		Matcher libraryMatcher = libraryPattern.matcher("");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line;
		br.readLine();
		while((line = br.readLine()) != null) {
			String[] terms = Arrays.copyOfRange(line.split(",(?=\")"), 0, 2);
			
			for(int i = 0; i < 2; i++) {
				terms[i] = terms[i].replaceAll("^\"+|\"+$", "");
				libraryMatcher.reset(terms[i]);
				libraryMatcher.find();
				terms[i] = libraryMatcher.group();
				
			}	
			libraryDependencies.putIfAbsent(terms[0], new HashSet<String>());
			libraryDependencies.get(terms[0]).add(terms[1]);
		}

		br.close();
		
		
		//for every library
		for(Map.Entry<String, Set<String>> entry: libraryDependencies.entrySet()) {
			
			//split library name into terms
			String library = entry.getKey();
			libraryTerms = library.split("[^a-zA-Z0-9]");
			Set<String> libraryTermsSet = new HashSet<>(Arrays.asList(libraryTerms));
			for(Iterator<String> i = libraryTermsSet.iterator(); i.hasNext();) {
				String element = i.next();
				if(stopwords.contains(element) 
						|| element.length() < 2 
								||!element.matches("[a-zA-Z]+")){
					i.remove();
				}
			}
			//store terms as Keyword components
			Set<Component> libraryTermsAsKeywords = new HashSet<>();
			libraryTermsSet.forEach(term -> libraryTermsAsKeywords.add(new Keyword(term)));
			
			//store dependencies of library in a set as Library components
			Set<Component> dependencyAsComponentSet = new HashSet<>();
			entry.getValue().forEach(dependency -> dependencyAsComponentSet.add(new Library(dependency)));
			
			//only libraries with at least 10 dependencies are used for testing (a percentage of the libraries that qualify is used)
			boolean usedForTestingSet = (entry.getValue().size() >= 10 && ((rand.nextInt(100) < 1)));
			if(usedForTestingSet) {
				existingConnections.put(libraryTermsAsKeywords, dependencyAsComponentSet);
			}else {
				//create links between keywords and dependencies
				for(Component keyword: libraryTermsAsKeywords) 
					for(Component dependency: dependencyAsComponentSet) 
						connections.addConnection(keyword, dependency);
				
				//create links between dependencies
				if(linkDependencies) {
					// create links between the dependencies of a library
					List<Component> dependencyList = new ArrayList<>(dependencyAsComponentSet);
					int size = dependencyList.size();
					for(int i = 0; i < size-1; i++) 
						for(int j = i + 1; j < size; j++) 
							connections.addConnection(dependencyList.get(i), dependencyList.get(j));
				}
				
				//create links between library (as a Project component) and its dependencies
				if(linkDependenciesToProject) {
					//create links between the library (as a Project component) and the dependencies of the library
					Project libraryAsProject = new Project(entry.getKey());
					dependencyAsComponentSet.forEach(dependency -> connections.addConnection(dependency, libraryAsProject));				
				}
			}
		}
	}
	
	
	private void updateStopwords(String filePath) throws IOException {

		String line;
		String library;
		String[] terms;
		int numOfLibs;
		Map<String, Integer> termsFreq = new HashMap<>();
		String currentLibrary = "";
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		br.readLine();
		
		numOfLibs = 0;
		Pattern pattern = Pattern.compile(".+:"); 
		Matcher m = pattern.matcher(" ");
		while((line = br.readLine()) != null) {
			
			m.reset(line.split(",")[0]);
			m.find();
			library = m.group();
			if(library.equals(currentLibrary))
				continue;	
			numOfLibs++;
			currentLibrary = library;
			terms = library.split("[^a-zA-Z0-9]");
			Set<String> termsSet = new HashSet<>(Arrays.asList(terms));
			for(String term: termsSet) {
				termsFreq.put(term, termsFreq.getOrDefault(term, 0) + 1);
			}
			
		}
		br.close();
	
		for(Map.Entry<String, Integer> term: termsFreq.entrySet()) {
			if(((double)term.getValue() / numOfLibs) > 0.1) {
				stopwords.add(term.getKey());
							
			}
		}
	}

	
	@Override
	public Connections getConnections() {
		return connections;
	}

	@Override
	public Map<Set<Component>, Set<Component>> getExistingConnections() {
		
		return existingConnections;
	}
	
	
}
