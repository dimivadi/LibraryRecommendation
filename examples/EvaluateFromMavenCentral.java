package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatypes.Component;
import datatypes.Connections;
import datatypes.Keyword;
import datatypes.Library;
import evaluation.EvaluationDataSource;
import miners.ComponentMiner;
import miners.RelatedLibraries;

public class EvaluateFromMavenCentral implements EvaluationDataSource{
	
	Connections connections = new Connections();
	Map<Set<Component>, Set<Component>> existingConnections = new HashMap<>();
	Set<String> stopwords = new HashSet<>();
	
	
	public EvaluateFromMavenCentral(String filePath) throws IOException {
		
		updateStopwords(filePath);
		
		Set<Component> libraryTermsAsKeywords = new HashSet<>();
		
		String line;
		String library;
		String[] libraryTerms;
		Random rand = new Random();
		boolean usedForTestingSet = false; //true if that line is used for the testing set. Is set by a random number generator
		String currentLibrary = "";
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		br.readLine();
		
		Pattern pattern = Pattern.compile(".+:"); 
		Matcher m = pattern.matcher(" ");
		while((line = br.readLine()) != null) {
			
			m.reset(line.split(",")[0]);
			m.find();
			library = m.group();
			if(!library.equals(currentLibrary)) {
				usedForTestingSet = (rand.nextInt(1000) < 1);
					
				currentLibrary = library;
				libraryTerms = library.split("\\W");
				Set<String> libraryTermsSet = new HashSet<>(Arrays.asList(libraryTerms));
				for(Iterator<String> i = libraryTermsSet.iterator(); i.hasNext();) {
					String element = i.next();
					if(stopwords.contains(element) || element.length() < 2) {
						i.remove();
					}
				}
				libraryTermsAsKeywords = new HashSet<>();
				for(String s: libraryTermsSet) {
					libraryTermsAsKeywords.add(new Keyword(s));
				}
				
//				keywords.clear();
//				for(String s: libraryTermsSet) {
//					if(stopwords.contains(s) || s.length() < 2)
//						continue;
//					keywords.add(new Keyword(s));
//						
//				}
			}
			String dependency = line.split(",")[1];
			if(usedForTestingSet) {
				Set<Component> value = new HashSet<Component>();
				if(existingConnections.get(libraryTermsAsKeywords) == null) {
					value.add(new Library(dependency));
					existingConnections.put(libraryTermsAsKeywords, value);
				}else {
					existingConnections.get(libraryTermsAsKeywords).add(new Library(dependency));
				}
						
					
				
			}else {

				for(Component k: libraryTermsAsKeywords) {
					connections.addConnection(k, new Library(dependency));
				}
			}
		
		}	
		
		
		System.out.println("Size of existing connections: "+existingConnections.size());
		System.out.println("existingConnections: "+ existingConnections);
	}
	
	
	void updateStopwords(String filePath) throws IOException {

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
			terms = library.split("\\W");
			Set<String> termsSet = new HashSet<>(Arrays.asList(terms));
			for(String term: termsSet) {
				termsFreq.put(term, termsFreq.getOrDefault(term, 0) + 1);
			}
			
		}
	
		for(Map.Entry<String, Integer> term: termsFreq.entrySet()) {
			if(((float)term.getValue() / numOfLibs) > 0.1) {
				if(!stopwords.contains(term.getKey())) {
					stopwords.add(term.getKey());
					}		
			}
		}
	}
	
	@Override
	public ComponentMiner getComponentMiner() {
		
		ComponentMiner componentMiner = new RelatedLibraries(connections);
		return componentMiner;
	}

	@Override
	public Map<Set<Component>, Set<Component>> getExistingConnections() {
		
		return existingConnections;
	}
	
	
}
