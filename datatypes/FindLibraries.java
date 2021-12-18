package datatypes;

import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindLibraries extends FindComponents{
	
	Set<Component> libraries;
	public Set<Component> findComponents(CodeFile codefile) {
		
		
		libraries = new HashSet<Component>();
		Pattern libPattern = Pattern.compile("\\s*import\\s+(?:static\\s+)?([\\w+\\.]+)");
		
		Scanner in;
		try {
			in = new Scanner(codefile.getFile());
		
			String line;
		
			while(in.hasNextLine()) {			
				line = in.nextLine();			
				Matcher m = libPattern.matcher(line);			
				while(m.find()) {				
					String token = m.group(1);
					Library newLibrary = new Library(token);						
					if(!libraries.contains(newLibrary)) {					
						libraries.add(newLibrary);					
						//System.out.println("Library:"+ newLibrary.getName());
					}
				
				}
			}
		
		
			in.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		return libraries;
		
	}

}
