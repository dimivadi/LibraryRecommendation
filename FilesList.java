package package1;

import java.io.File;
import java.io.FilenameFilter;

class FilesList {
	
	private File[] files;
	
	FilesList(String folderPath){
		
		File dir = new File(folderPath);
		files = dir.listFiles(new FilenameFilter()
		{
		  public boolean accept(File dir, String name)
		  {
		     return name.endsWith(".java");
		  }
		});
		
		
	}
	
	File[] getFiles() {
		return files;
	}
}
