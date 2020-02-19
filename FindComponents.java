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
		
		Pattern libPattern = Pattern.compile("(?<=(^\\s*import\\s))[\\w+\\.]+");
		Scanner in = new Scanner(codefile.getFile());
		String line;
		
		
		while(in.hasNextLine()) {			
			line = in.nextLine();			
			Matcher m = libPattern.matcher(line);			
			while(m.find()) {				
				String token = m.group();				
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
		
		Pattern wordPattern = Pattern.compile("[a-zA-Z]{3,}");
		
		Scanner in = new Scanner(codeFile.getFile());
		String line;
		

		while(in.hasNextLine()) {
			line = in.nextLine();
			Matcher m = wordPattern.matcher(line);
			while(m.find()) {
				String token = m.group();
				
				if(!stopwords.contains(token)) {
					Keyword newKeyword = new Keyword(token);
					newKeyword.setConnectedLibraries(connectedLibraries);
					
					if(!newKeyword.keywordExistsInArrayList(keywordsInFile)) {
						keywordsInFile.add(newKeyword);
						System.out.println("Keyword: " + newKeyword.getKeywordName());
						
						if(!newKeyword.keywordExistsInArrayList(keywords)) {
							keywords.add(newKeyword);
						}else {
							Keyword kw;
							kw = getKeywordByName(keywords, newKeyword.getKeywordName());
							for(Library lib : connectedLibraries) {
								if(!lib.libraryExistsInArrayList(kw.getConnectedLibraries())) {
									kw.addConnectedLibrary(lib);
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
	
	Keyword getKeywordByName(ArrayList<Keyword> keywords, String keywordName) {
		for (Keyword kw: keywords)
			if(kw.getKeywordName() == keywordName)
				return kw;
		Keyword k = new Keyword("");
		return k;
	}
}
