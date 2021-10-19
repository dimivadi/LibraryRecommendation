package datatypes;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FileNameToKeywords {
	
	/*
	 * Class to split a File name and return the words as components
	 */
	
	public Set<Component> getKeywords(File file){
		
		String fileName = file.getName().split("\\.")[0];
		String[] arr;
		Set<Component> keywords = new HashSet<>();
		
		arr = fileName.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");

		for(String s: arr) {
			keywords.add(new Keyword(s.toLowerCase()));
		}
		return keywords;
	
	}
	
//	public Set<Component> getKeywordsWithoutStopwords(File file){
//		Set<Component> keywordsWithoutStopwords;
//		keywordsWithoutStopwords = this.getKeywords(file);
//		Stopwords stopwords = new Stopwords();
//		
//	}
		
}