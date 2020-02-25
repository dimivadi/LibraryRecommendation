package package1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




class FindComponents{
	
	private ArrayList<String> stopwords = new ArrayList<String>();
	private ArrayList<Component> libraries = new ArrayList<Component>();
	private ArrayList<Component> keywords = new ArrayList<Component>();
	private ArrayList<Component> components = new ArrayList<Component>();
	
	
	FindComponents() throws FileNotFoundException{
		File swfile = new File("stopwords.txt");
		Scanner sw = new Scanner(swfile);
		while(sw.hasNext()) {
			this.stopwords.add(sw.next());
		}
		
		sw.close();
		
	}
	
	public ArrayList<Component> findLibraries(CodeFile codefile) throws FileNotFoundException {
		
		
		if(libraries.isEmpty()) {
			Pattern libPattern = Pattern.compile("\\s*import\\s+([\\w+\\.]+)");
			
			Scanner in = new Scanner(codefile.getFile());
			String line;
			
			while(in.hasNextLine()) {			
				line = in.nextLine();			
				Matcher m = libPattern.matcher(line);			
				while(m.find()) {				
					String token = m.group(1);
					System.out.println(token);
					//token = "java.util.Set";
					Library newLibrary = new Library(token);						
					if(!newLibrary.existsInArrayList(libraries)) {					
						libraries.add(newLibrary);					
						System.out.println("Library:"+ newLibrary.getName());
					}
					
				}
			}
			
			
			in.close();
		}
		
		return libraries;
	}
	
	public ArrayList<Component> findKeywords(CodeFile codeFile) throws FileNotFoundException {
		
		if(keywords.isEmpty()) {
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
						if(!stopwords.contains(part)) {
							Keyword newKeyword = new Keyword(part);
							//newKeyword.setConnectedLibraries(connectedLibraries);
							
							if(!newKeyword.existsInArrayList(keywords)) {
								keywords.add(newKeyword);
								System.out.println("Keyword: " + newKeyword.getName());
							}
						}
					}
				}
			}

			
			in.close();
		}
		
		
		return keywords;
		
	}
	
	public ArrayList<Component> findComponents(CodeFile codefile) throws FileNotFoundException {
		
		if(components.isEmpty()) {
			if(libraries.isEmpty())
				findLibraries(codefile);
			if(keywords.isEmpty())
				findKeywords(codefile);
			components.addAll(libraries);
			components.addAll(keywords);
		}
		
		return components;
		
	}
	
	public String[] splitTokenInStrings(String str) {
		
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

