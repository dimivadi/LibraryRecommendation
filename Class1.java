package package1;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Class1{
	

	
	public static void main(String[] args) throws IOException{
		
		
		List<CodeFile> codefiles = new ArrayList<CodeFile>();
		Connections connections;
		
		List<File> files = FilesList.listAllFiles("Java-master", "java");
		
		for(File file : files){
			CodeFile codefile = new CodeFile(file);
			codefiles.add(codefile);
			//
		}
		
	

		
	}
}









