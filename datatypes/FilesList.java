package datatypes;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilesList {
	
	// Methods to be called by user
	// extensions: String of extensions separated by non-word characters
	public static List<File> listAllFiles(String startDir, String extensions) {
		String[] suffixes = toSuffixes(extensions);
		return recurseDir(new File(startDir), suffixes);
	}
	
	public static List<File> listAllFiles(File startDir, String extensions){
		String[] suffixes = toSuffixes(extensions);
		return recurseDir(startDir, suffixes);
	}
	
	// Recursive search in folders
	private static List<File> recurseDir(File startDir, String[] suffixes) {
		List<File> files = new ArrayList<File>();
		
		for(File item : startDir.listFiles()) {
			if(item.isDirectory()) {
				files.addAll(recurseDir(item, suffixes));
			}else {
				if(accept(item.getName(), suffixes))
					files.add(item);
			}
		}
		
		return files;
	}
	
	private static boolean accept(String fileName, String[] suffixes) {
		for (String suffix : suffixes) {
			if(fileName.endsWith(suffix))
				return true;
		}
		return false;
	}
	
	private static String[] toSuffixes(String extensions) {
		return extensions.split("\\W+");
	}
	
	
}
