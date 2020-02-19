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
				if(!stopwords.contains(token)) {
					Library newLibrary = new Library(token);
					if(!libraries.contains(newLibrary)) {
						libraries.add(newLibrary);
						librariesInFile.add(newLibrary);
						System.out.println("Library: "+ newLibrary.getLibrary());
						
					}
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
					if(!keywords.contains(newKeyword)) {
						newKeyword.setConnectedLibraries(connectedLibraries);
						keywords.add(newKeyword);
						System.out.println("Keyword: " + newKeyword.getKeyword());
						
					}
				}

			}
		}

		
		in.close();
		return keywordsInFile;
		
	}
}