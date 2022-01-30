package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindKeywords extends FindComponents{

	private ArrayList<String> stopwords = new ArrayList<>();
	Set<Component> keywords;
	
	public FindKeywords() throws FileNotFoundException{
		
		File swfile = new File("stopwords.txt");
		Scanner sw = new Scanner(swfile);
		while(sw.hasNext()) {
			this.stopwords.add(sw.next());
		}
		
		sw.close();
		
	}
	
	
	public Set<Component> findComponents(CodeFile codefile) throws FileNotFoundException{
		
		keywords = new HashSet<Component>();
//		Pattern wordPattern = Pattern.compile("\\w{3,}([\\S]+\\w{3,})*");
		Pattern wordPattern = Pattern.compile("[a-zA-Z0-9]{3,}");
		Matcher matcher = wordPattern.matcher(" ");
		Scanner in = new Scanner(codefile.getFile());
		String line;
		

		while(in.hasNextLine()) {
			line = in.nextLine();
			if(line.matches("^\\s*import\\s+.*"))
					continue;
			
			matcher.reset(line);
			while(matcher.find()) {
				String token = matcher.group();
			
				String[] parts = splitTokenInStrings(token);
				
				for(String part : parts) {
					//skip if string is a number
					if(part.matches("^[0-9]+$") || part.matches("0x[0-9]+$"))
						continue;
					if(!stopwords.contains(part)) {
						Keyword newKeyword = new Keyword(part);
						keywords.add(newKeyword);
					}
				}
			}
		}

		
		in.close();
		return keywords;
	
	}
	
	private String[] splitTokenInStrings(String str) {
		
		//split words written in camel case syntax
		ArrayList<String> strList = new ArrayList<String>();
		String[] words;
		
		words = str.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
		for(String word: words) {
			strList.add(word.toLowerCase());
		}
		
		return (String[]) strList.toArray(new String[0]);
		
	}
//	 private String[] splitTokenInStrings(String str) {
//		
//		//split words using as delimiter non word characters and camel case syntax
//		String[] s1 = str.split("[^a-zA-Z0-9]");
//		ArrayList<String> strList = new ArrayList<String>();
//		String[] words;
//		for(String s2 : s1) {
//			words = s2.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
//			for(String word: words) {
//				strList.add(word.toLowerCase());
//			}
//		}
//		return (String[]) strList.toArray(new String[0]);
//		
//	}
	
}
