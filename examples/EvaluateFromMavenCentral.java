package examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

public class EvaluateFromMavenCentral implements EvaluationDataSource{
	
	Connections connections = new Connections();
	Set<String> stopwords = new HashSet<>();
	Set<Keyword> keywords = new HashSet<>();
	
	public EvaluateFromMavenCentral(String filePath) throws IOException {
		
		updateStopwords(filePath);
		
		
		String line;
		String library;
		String[] terms;

		String currentLibrary = "";
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		br.readLine();
		
		Pattern pattern = Pattern.compile(".+:"); 
		Matcher m = pattern.matcher(" ");
		while((line = br.readLine()) != null) {
			
			m.reset(line.split(",")[0]);
			m.find();
			library = m.group();
			if(library.equals(currentLibrary))
				continue;
			
			currentLibrary = library;
			terms = library.split("\\W");
			Set<String> termsSet = new HashSet<>(Arrays.asList(terms));
			keywords.clear();
			for(String s: termsSet) {
				if(stopwords.contains(s) || s.length() < 2)
					continue;
				keywords.add(new Keyword(s));
					
			}
		
			String dependency = line.split(",")[1];
			for(Keyword k: keywords) {
				connections.addConnection(k, new Library(dependency));
		}
	}
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Set<Component>, Set<Component>> getExistingConnections() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
