package package1;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



public class Class1{
	
	private static ArrayList<Library> libraries = new ArrayList<Library>();
	private static ArrayList<Keyword> keywords = new ArrayList<Keyword>();
	
	static public ArrayList<Library> getLibraries(){
		return libraries;
	}
	
	static public ArrayList<Keyword> getKeywords(){
		return keywords;
	}
	
	public static void main(String[] args) throws IOException{
		
		FindComponents find = new FindComponents("stopwords.txt", libraries, keywords);
		
		//list of all .java files in directory
		File[] files = (new FilesList("Java-master")).getFiles();
		
		for(File file : files){
			CodeFile codefile = new CodeFile(file, find);
			codefile.getComponents();
		}
	}
}











