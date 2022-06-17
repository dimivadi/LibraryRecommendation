package datasets;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

public class EvaluateFromApk implements EvaluationDataSource{
	
//	private static final boolean LINKS_BETWEEN_LIBRARIES = true;
//	private static final boolean LINKS_BETWEEN_LIBRARIES_AND_PROJECT = false;
	
	private Connections connections = new Connections();
	private Map<Set<Component>, Set<Component>> existingConnections = new HashMap<>();
	private Set<String> stopwords = new HashSet<>();
	private boolean linkLibraries = true;
	boolean linkLibsToProject = true;
	
	
	public EvaluateFromApk(String apkPath, String libPath, String relationPath) throws IOException{
		
		//read libInfo.csv
		Map<Integer, Component> libInfo = new HashMap<>();
		BufferedReader libReader = new BufferedReader(new FileReader(libPath));
		String line;
		while((line = libReader.readLine()) != null) {
			String[] values = line.split(",");
			
			//store lib info to corresponding set
			libInfo.put(Integer.parseInt(values[0]), new Library(values[1]));
		}
		libReader.close();
		
		
		
		//read apkInfo.csv
		Map<Integer, Component> apkInfo	= new HashMap<>();
		BufferedReader apkReader = new BufferedReader(new FileReader(apkPath));
		
		String apkLine;	
		
		while((apkLine = apkReader.readLine()) != null) {
			String[] apkValues = apkLine.split(",");
			
			//remove version and extension
			apkValues[1] = apkValues[1].split("(?<=.)(?=_v\\d[\\.\\d]+apk)|(?<=.)(?=\\.apk)")[0];	
			
			//store apk info to corresponding set
			apkInfo.put(Integer.parseInt(apkValues[0]), new Project(apkValues[1]));
		}
		apkReader.close();
		System.out.println("num of apks: "+apkInfo.size());
//		updateStopwords(apkInfo);
		stopwords.add("com");
		stopwords.add("apk");

		
		
		//read relation.csv
		Map<Integer, Set<Integer>> relation = new HashMap<>();
		BufferedReader relationReader = new BufferedReader(new FileReader(relationPath));
		String relationLine;
		
		while((relationLine = relationReader.readLine()) != null) {
			String[] relationValues = relationLine.split(",");
			relation.putIfAbsent(Integer.parseInt(relationValues[0]), new HashSet<Integer>());
			relation.get(Integer.parseInt(relationValues[0])).add(Integer.parseInt(relationValues[1]));
		}
		relationReader.close();
		
		
		//create appropriate connections between components
		//split apk name at non word characters to get terms
		Pattern apkTermsPattern = Pattern.compile("[a-zA-Z0-9]{2,}");
		Matcher apkTermsMatcher = apkTermsPattern.matcher("");

		Map<Integer, Integer> hist = new HashMap<>();
		for(Map.Entry<Integer, Set<Integer>> entry: relation.entrySet()) {
			
			//store keywords created by apk name, as Keyword components
			Set<Component> apkNameAsKeywords = new HashSet<>();
			apkTermsMatcher.reset(apkInfo.get(entry.getKey()).getName());
			
			while(apkTermsMatcher.find()) {
				String[] apkWords = apkTermsMatcher.group().split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
				for(String word: apkWords) {
					if(!stopwords.contains(word))
						apkNameAsKeywords.add(new Keyword(word));
				}
			}
				
			
			//store libraries of apk as Library components
			Set<Component> apkLibraries = new HashSet<>();
			for(Integer library: entry.getValue()) {
				apkLibraries.add(libInfo.get(library));
			}
			
			//add apk to testing set only if it uses 10 libraries or more (use a percentage of the apks that qualify)
			Random rand = new Random();
			boolean usedForTestingSet = (entry.getValue().size() >= 10 && (rand.nextInt(150) < 1));
//			boolean usedForTestingSet = (entry.getValue().size() >= 10 && (rand.nextInt(5) < 1));
//			hist.put(entry.getValue().size(), hist.getOrDefault(entry.getValue().size(), 0)+1);
			
			if(usedForTestingSet) 
				existingConnections.put(apkNameAsKeywords, apkLibraries);
			else {
				//add connection between keywords and libraries
				for(Component keyword: apkNameAsKeywords) 
					for(Component library: apkLibraries)
						connections.addConnection(keyword, library);
				
				if(linkLibraries) {
					//add connection between libraries
					List<Component> libraryList = new ArrayList<>(apkLibraries);
					int size = libraryList.size();
					for(int i = 0; i < size-1; i++) 
						for(int j = i + 1; j < size; j++) 
							connections.addConnection(libraryList.get(i), libraryList.get(j));
				}
				
				if(linkLibsToProject) 
					//add connection between libraries and project
					for(Component library: apkLibraries)
						connections.addConnection(apkInfo.get(entry.getKey()), library);
			}
		}
//		hist.forEach((key, value) -> System.out.println(key + ":" + value));
	}

	private void updateStopwords(Map<Integer, Component> apkInfo) throws IOException {
		
		Pattern pattern = Pattern.compile("[a-zA-Z0-9]{2,}"); 
		Matcher matcher = pattern.matcher(" ");
		
		Map<String, Integer> termsFreq = new HashMap<>();
		String apkName;
		
		for(Map.Entry<Integer, Component> entry: apkInfo.entrySet()) {
			
			apkName = entry.getValue().getName();
			matcher.reset(apkName);
			
			while(matcher.find()) {
				//check if matching pattern contains only numbers
				String matcherWords[] = matcher.group().split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
				for(String word: matcherWords) {
					if(!word.matches("[0-9]+"))
						termsFreq.put(matcher.group(), termsFreq.getOrDefault(word, 0) + 1);
				}
			}
				
				
			
			for(Map.Entry<String, Integer> term: termsFreq.entrySet()) 
				if(((double)term.getValue() / apkInfo.size()) > 0.1) 
					stopwords.add(term.getKey());
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
