package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Class to handle a stopwords list 
 * 
 */
public class Stopwords {
	
	int numOfDocs;
	List<File> files;
	Map<String, Integer> termsFreq;

	
	//calculate IDF for every term in dir, then add to stopwords if IDF is smaller than 0.5
	public void addStopwords(String dir) throws IOException {

		
		List<String> stopwords = Files.readAllLines(Paths.get("stopwords.txt"));
		
		files = FilesList.listAllFiles(dir, "java");
		numOfDocs = files.size();
		termsFreq = termsFreq();
		for(Map.Entry<String, Integer> term: termsFreq.entrySet()) {
			//calculate IDF
			if((float)term.getValue() / numOfDocs > 0.3) {
				if(stopwords.contains(term.getKey()))
					continue;
				else
					stopwords.add(term.getKey());
				
			}
		}
		
		Files.write(Paths.get("stopwords.txt"), stopwords, StandardOpenOption.TRUNCATE_EXISTING);
			
	}
	
	public void resetStopwordsList() throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(new File("stopwords.txt"));
		writer.print("");
		writer.close();
	}
	
	public Set<String> getStopwords() throws FileNotFoundException{
		Set<String> stopwords = new HashSet<>();
		File swfile = new File("stopwords.txt");
		Scanner sw = new Scanner(swfile);
		while(sw.hasNext()) {
			stopwords.add(sw.next());
		}
		
		sw.close();
	
		return stopwords;
	}
	

		
	/*
	 * Add to Map termsFreq:
	 * key: every term in the files List
	 * value: the number of files that this term occurs
	 */
	Map<String, Integer> termsFreq() throws FileNotFoundException{
		Map<String, Integer> termsFreq = new HashMap<>();
		
		Set<String> fileTerms;
		for(File file: files) {
			fileTerms = termsInFile(file);
			for(String term: fileTerms) {
				termsFreq.put(term, termsFreq.getOrDefault(term, 0) + 1);
			}
		}
		return termsFreq;
	}
	
	
	Set<String> termsInFile(File file) throws FileNotFoundException{ 
		Set<String> termsInDoc = new HashSet<>();
		
		String[] tokens = tokensFromFile(file);
		for (String token: tokens) {
			termsInDoc.addAll(splitToken(token));
		}
		return termsInDoc;
		
	}
	
	
	//get tokens from file using as delimiter whitespaces
	String[] tokensFromFile(File file) throws FileNotFoundException{
		List<String> tokens = new ArrayList<>();
		
		Pattern wordPattern = Pattern.compile("\\w{3,}([\\S]+\\w{3,})*");
		Matcher m = wordPattern.matcher(" ");
		Scanner in = new Scanner(file);
		String line;
		
		while(in.hasNextLine()) {
			line = in.nextLine();
			m.reset(line);
			
			while(m.find()) {
				tokens.add(m.group());
				
			}
		}
		return (String[]) tokens.toArray(new String[0]);
	}
	
	
	//split tokens using as delimiter non word characters and camel case syntax
	List<String> splitToken(String str) {
		
		String[] s1 = str.split("\\W");
		ArrayList<String> strList = new ArrayList<String>();
		String[] words;
		for(String s2 : s1) {
			words = s2.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
			for(String word: words) {
				strList.add(word.toLowerCase());
			}
		}
		return strList;
	}
	
	
}
