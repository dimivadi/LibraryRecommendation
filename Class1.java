import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



class Library{
	String libraryName;
	
	Library(String libraryName){
		this.libraryName = libraryName;
	}
	
	String getLibrary() {
		return this.libraryName;
	}
}


class Keyword{
	String keywordName;
	ArrayList<Library> libraries;
	
	Keyword(String keywordName){
		this.keywordName = keywordName;
	}
	
	String getKeyword() {
		return keywordName;
	}
	
	void setConnectedLibraries(ArrayList<Library> libraries) {
		this.libraries = libraries;
	}
	
	ArrayList<Library> getConnectedLibraries(){
		return libraries;
	}
	
}

class CodeFile{
	
	File codefile; 
	
	CodeFile(String filename){
		this.codefile = new File(filename);
		
	}
	
	File getFile() {
		return codefile;
	}
	
	
	
}

class FindComponents{
	
	ArrayList<String> stopwords = new ArrayList<String>();
	
	FindComponents(String stopwordsFile) throws FileNotFoundException{
		File swfile = new File("stopwords.txt");
		Scanner sw = new Scanner(swfile);
		while(sw.hasNext()) {
			this.stopwords.add(sw.next());
		}
		
		sw.close();
	}
	
	ArrayList<Library> findLibraries(CodeFile codefile, ArrayList<Library> libraries) throws FileNotFoundException {
		
		Pattern libPattern = Pattern.compile("(?<=(^\\s*import\\s))[\\w+\\.]+");
		Scanner in = new Scanner(codefile.getFile());
		String line;
		ArrayList<Library> librariesInFile = new ArrayList<Library>();
		
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
	
	void findKeywords(CodeFile codeFile, ArrayList<Keyword> keywords, ArrayList<Library> connectedLibraries) throws FileNotFoundException {
		
		
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
		
	}
}


public class Class1{
	
	static ArrayList<Library> libraries = new ArrayList<Library>();
	static ArrayList<Keyword> keywords = new ArrayList<Keyword>();
	
	public static void main(String[] args) throws IOException{
		CodeFile codefile = new CodeFile("ConnectedComponent.java");
		FindComponents find = new FindComponents("stopwords.txt");
		ArrayList<Library> librariesInFile = find.findLibraries(codefile, libraries);//findLiraries static?
		find.findKeywords(codefile, keywords, librariesInFile);
		
	}
}
