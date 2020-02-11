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
}


class Keyword{
	String keywordName;
	
	Keyword(String keywordName){
		this.keywordName = keywordName;
	}
	
	String getKeyword() {
		return keywordName;
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
	
	void findLibraries(CodeFile codefile) {
		//
	}
	
	void findKeywords(CodeFile codeFile, ArrayList<Keyword> keywords) throws FileNotFoundException {
		
		
		Pattern libPattern = Pattern.compile("(?<=(^\\s*import\\s))[\\w+\\.]+");
		
		Pattern wordPattern = Pattern.compile("[a-zA-Z]{3,}");
		
		Scanner in = new Scanner(codeFile.getFile());
		String line;
		

		while(in.hasNextLine()) {
			line = in.nextLine();
			Matcher m = wordPattern.matcher(line);
			while(m.find()) {
				String token = m.group();
				if(!stopwords.contains(token))
					if(!keywords.contains(token)) {
						Keyword keyword = new Keyword(token);
						keywords.add(keyword);
						}
			}
		}
		for (Keyword word : keywords)
			System.out.println(word.getKeyword());
		
		
		in.close();
		
		
		
		
		
	}
}


public class Class1{
	
	static ArrayList<Library> libraries = new ArrayList<Library>();
	static ArrayList<Keyword> keywords = new ArrayList<Keyword>();
	
	public static void main(String[] args) throws IOException{
		CodeFile codefile = new CodeFile("ConnectedComponent.java");
		FindComponents find = new FindComponents("stopwords.txt");
		find.findLibraries(codefile);//findLiraries static?
		find.findKeywords(codefile, keywords);
		
	}
}

