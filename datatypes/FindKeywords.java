package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindKeywords {

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
		Pattern wordPattern = Pattern.compile("\\w{3,}([\\S]+\\w{3,})*");
		Matcher m = wordPattern.matcher(" ");
		Scanner in = new Scanner(codefile.getFile());
		String line;
		

		while(in.hasNextLine()) {
			line = in.nextLine();
			if(line.matches("\\s*import\\s+.*"))
					continue;
			
			m.reset(line);
			while(m.find()) {
				String token = m.group();
		
				String[] parts = splitTokenInStrings(token);
				
				for(String part : parts) {
					if(part.length() < 3)
						continue;
					if(!stopwords.contains(part)) {
						Keyword newKeyword = new Keyword(part);
						
						if(!keywords.contains(newKeyword)) {
							keywords.add(newKeyword);
							//System.out.println("Keyword: " + newKeyword.getName());
						}
					}
				}
			}
		}

		
		in.close();
		return keywords;
	
	}
	
	/*
	public String[] splitTokenInStrings(String str) {
		
		String[] s = str.split("\\W");
		
		String[] arr;
		
		ArrayList<String> strList = new ArrayList<String>();
		ArrayList<String> strList2 = new ArrayList<String>();
		for (String temp : s) {
			arr = temp.split("(?<=[a-z])(?=[A-Z])");
			for(String c : arr) {
				strList.add(c.toLowerCase());
			}
			strList2.add(String.join(" ", arr));
			strList.add(String.join(" ", arr));
		}
		
		strList.add(String.join(" ",strList2.toArray(new String[0])));
		return (String[]) strList.toArray(new String[0]);	
		
	}
	*/
	
	 String[] splitTokenInStrings(String str) {
		
		//split words separated by non word characters
		String[] s1 = str.split("\\W");
		ArrayList<String> strList = new ArrayList<String>();
		String[] words;
		for(String s2 : s1) {
			words = s2.split("(?<=[a-z])(?=[A-Z])|(?<=[A-Z])(?=[A-Z][a-z])");
			for(String word: words) {
				strList.add(word.toLowerCase());
			}
		}
		return (String[]) strList.toArray(new String[0]);
		
	}
	
}
