package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindLibrariesAndKeywords extends FindComponents{
	
	private ArrayList<String> stopwords = new ArrayList<String>();
	private Collection<Component> libraries;
	private Collection<Component> keywords;
	private Collection<Component> components;
	
	
	FindLibrariesAndKeywords() throws FileNotFoundException{
		File swfile = new File("stopwords.txt");
		Scanner sw = new Scanner(swfile);
		while(sw.hasNext()) {
			this.stopwords.add(sw.next());
		}
		
		sw.close();
		
	}
	
	
	public Collection<Component> findComponents(CodeFile codefile) {
			
		components = new ArrayList<Component>();
		
		try {
			findLibraries(codefile);
			findKeywords(codefile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		components.addAll(libraries);
		components.addAll(keywords);
		
		
		return components;
			
	}
	
	
	private void findLibraries(CodeFile codefile) throws FileNotFoundException {
		
		
		libraries = new ArrayList<Component>();
		Pattern libPattern = Pattern.compile("\\s*import\\s+([\\w+\\.]+)");
		
		Scanner in = new Scanner(codefile.getFile());
		String line;
		
		while(in.hasNextLine()) {			
			line = in.nextLine();			
			Matcher m = libPattern.matcher(line);			
			while(m.find()) {				
				String token = m.group(1);
				Library newLibrary = new Library(token);						
				if(!libraries.contains(newLibrary)) {					
					libraries.add(newLibrary);					
					System.out.println("Library:"+ newLibrary.getName());
				}
				
			}
		}
		
		
		in.close();
		
	}
	
	private void findKeywords(CodeFile codeFile) throws FileNotFoundException {
		
		keywords = new ArrayList<Component>();
		Pattern wordPattern = Pattern.compile("\\w{3,}([\\S]+\\w{3,})*");
		Matcher m = wordPattern.matcher(" ");
		Scanner in = new Scanner(codeFile.getFile());
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
							System.out.println("Keyword: " + newKeyword.getName());
						}
					}
				}
			}
		}

		
		in.close();
	
	}
	
	
	
	private String[] splitTokenInStrings(String str) {
		
		String[] s = str.split("\\W");
		
		String[] arr;
		ArrayList<String> strList = new ArrayList<String>();
		ArrayList<String> strList2 = new ArrayList<String>();
		for (String temp : s) {
			arr = temp.split("(?<=[a-z])(?=[A-Z])");
			for(String c : arr) {
				strList.add(c);
			}
			strList2.add(String.join(" ", arr));
			strList.add(String.join(" ", arr));
		}
		
		strList.add(String.join(" ",strList2.toArray(new String[0])));
		return (String[]) strList.toArray(new String[0]);	
		
	}

}
