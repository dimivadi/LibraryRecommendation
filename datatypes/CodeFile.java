package datatypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class CodeFile{
	
	private File codefile; 
	private String fileName;
	
	
	public CodeFile(String filename) throws FileNotFoundException{
		this.codefile = new File(filename);	
	}

	public CodeFile(File file) throws FileNotFoundException{
		this.codefile = file;	
		this.fileName = file.getName();
	}
	
	
	public File getFile() {
		return codefile;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public static List<CodeFile> getCodeFiles(List<File> files) throws FileNotFoundException {
		
		List<CodeFile> codefiles = new ArrayList<>();
		
		for(File file : files){
			CodeFile codefile = new CodeFile(file);
			codefiles.add(codefile);
		}
		return codefiles;
	}
	
}



