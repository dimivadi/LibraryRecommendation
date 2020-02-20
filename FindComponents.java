package package1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FindComponents{
	
	private ArrayList<String> stopwords = new ArrayList<String>();
	private ArrayList<Library> libraries = new ArrayList<Library>();
	private ArrayList<Keyword> keywords = new ArrayList<Keyword>();
	
	
	FindComponents(String stopwordsFile, ArrayList<Library> libraries,ArrayList<Keyword> keywords) throws FileNotFoundException{
		File swfile = new File("stopwords.txt");
		Scanner sw = new Scanner(swfile);
		while(sw.hasNext()) {
			this.stopwords.add(sw.next());
		}
		
		sw.close();
		this.libraries = libraries;
		this.keywords = keywords;
	}
	
	ArrayList<Library> findLibraries(CodeFile codefile) throws FileNotFoundException {
		
		ArrayList<Library> librariesInFile = new ArrayList<Library>();
		
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
				librariesInFile.add(newLibrary);				
				if(!newLibrary.libraryExistsInArrayList(libraries)) {					
					libraries.add(newLibrary);					
					System.out.println("Library:"+ newLibrary.getLibraryName());
				}
				
			}
		}
		
		
		in.close();
		
		return librariesInFile;
	}
	
	ArrayList<Keyword> findKeywords(CodeFile codeFile, ArrayList<Library> connectedLibraries) throws FileNotFoundException {
		
		ArrayList<Keyword> keywordsInFile = new ArrayList<Keyword>();
		
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
						newKeyword.setConnectedLibraries(connectedLibraries);
					
						if(!newKeyword.keywordExistsInArrayList(keywordsInFile)) {
							keywordsInFile.add(newKeyword);
							System.out.println("Keyword: " + newKeyword.getKeywordName());
						
							if(!newKeyword.keywordExistsInArrayList(keywords)) {
								keywords.add(newKeyword);
							}else {
								Keyword currentKeyword;
								currentKeyword = getKeywordByName(keywords, newKeyword.getKeywordName());
								for(Library lib : connectedLibraries) {
									if(!lib.libraryExistsInArrayList(currentKeyword.getConnectedLibraries())) {
										currentKeyword.addConnectedLibrary(lib);
									}
								}
							}
						}
					}
				}
			}
		}

		
		in.close();
		return keywordsInFile;
		
	}
	
	String[] splitTokenInStrings(String str) {
		
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
	
	Keyword getKeywordByName(ArrayList<Keyword> keywords, String keywordName) {
		for (Keyword kw: keywords)
			if(kw.getKeywordName() == keywordName)
				return kw;
		Keyword k = new Keyword("");
		return k;
	}
}

