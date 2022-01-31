package examples;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import evaluation.EvaluationDataSource;

public class EvaluateFromMaven implements EvaluationDataSource{
	
	private Connections connections = new Connections();
	private Map<Set<Component>, Set<Component>> existingConnections = new HashMap<>();
	private Set<String> stopwords = new HashSet<>();
	private static final boolean UPDATE_STOPWORDS = true;
	private String stopwordsSerialized = "stopwords.ser";
	
	
	public EvaluateFromMaven(String filePath) throws IOException {
		

		String[] libraryTerms;
		Random rand = new Random();
		
		if(UPDATE_STOPWORDS) {
			updateStopwords(filePath);
			FileOutputStream file = new FileOutputStream(stopwordsSerialized);
			ObjectOutputStream out = new ObjectOutputStream(file);
			
			out.writeObject(stopwords);
			
			out.close();
			file.close();
			System.out.println("stopwords");
		}else {
			FileInputStream file = new FileInputStream(stopwordsSerialized);
			InputStream is = new BufferedInputStream(file);
			ObjectInputStream in = new ObjectInputStream(file);
			
			try {
				stopwords = (Set<String>) in.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			in.close();
			file.close();
		}
		

		
		File file = new File(filePath);
		Map<String, Set<String>> libraryDependencies = new HashMap<>();
		Pattern libraryPattern = Pattern.compile(".*(?=:)");
		Matcher libraryMatcher = libraryPattern.matcher("");
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		String line;
		br.readLine();
		while((line = br.readLine()) != null) {
			String[] terms = Arrays.copyOfRange(line.split(",(?=\\\")"), 0, 2);
			
			for(int i = 0; i < terms.length; i++) {
				terms[i] = terms[i].replaceAll("^\"+|\"+$", "");
//				int lastIndex = terms[i].lastIndexOf(":");
//				if(lastIndex == -1)
//					lastIndex = terms[i].length();
//				terms[i] = terms[i].substring(0, lastIndex);
				libraryMatcher.reset(terms[i]);
				libraryMatcher.find();
				terms[i] = libraryMatcher.group();
				
				libraryDependencies.putIfAbsent(terms[0], new HashSet<String>());
				libraryDependencies.get(terms[0]).add(terms[1]);
			}		
		}
		System.out.println("libraryDependencies");
		br.close();
		
//		Set<String> librariesEligibleForTesting = new HashSet<>();
//		for(Map.Entry<String, Set<String>> entry: libraryDependencies.entrySet()) {
//			if(entry.getValue().size() >= 10) {
//				librariesEligibleForTesting.add(entry.getKey());
//			}
//		}
		
		
		for(Map.Entry<String, Set<String>> entry: libraryDependencies.entrySet()) {
			
//			if(entry.getValue().size() >= 10 && ((rand.nextInt(1000) < 1))
			boolean usedForTestingSet = (entry.getValue().size() >= 10 && ((rand.nextInt(1000) < 1)));
//			if(librariesEligibleForTesting.contains(entry.getKey())){
//				usedForTestingSet = (rand.nextInt(1000) < 1);
//			}else {
//				usedForTestingSet = false;
//			}
			
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
			Set<Component> libraryTermsAsKeywords = new HashSet<>();
			for(String s: libraryTermsSet) {
				libraryTermsAsKeywords.add(new Keyword(s));
			}
			Set<Component> dependencyAsComponentSet = new HashSet<>();
			for(String dependency: entry.getValue()) {
				dependencyAsComponentSet.add(new Library(dependency));
			}
			if(usedForTestingSet) {
				existingConnections.put(libraryTermsAsKeywords, new HashSet<Component>());

				for(Component dependencyComponent: dependencyAsComponentSet) {
					existingConnections.get(libraryTermsAsKeywords).add(dependencyComponent);
				}
			}else {
				for(Component keyword: libraryTermsAsKeywords) {
					for(Component dependency: dependencyAsComponentSet) {
						connections.addConnection(keyword, dependency);
					}
				}
				List<Component> dependencyList = new ArrayList<>(dependencyAsComponentSet);
				int size = dependencyList.size();
				
				for(int i = 0; i < size-1; i++) {
					for(int j = i + 1; j < size; j++) {
						connections.addConnection(dependencyList.get(i), dependencyList.get(j));
					}
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
			if(((float)term.getValue() / numOfLibs) > 0.1) {
				if(!stopwords.contains(term.getKey())) {
					stopwords.add(term.getKey());
					}		
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
