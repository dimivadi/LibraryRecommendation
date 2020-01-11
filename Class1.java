import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Class1 {
	

	public static void main(String[] args) throws IOException{
		
		ArrayList<String> libraries = new ArrayList<String>();
		ArrayList<String> keywords = new ArrayList<String>();
		
		// 
		ArrayList<String> stopwords = new ArrayList<String>();
		
		File swfile = new File("stopwords.txt");
		Scanner sw = new Scanner(swfile);
		while(sw.hasNext()) {
			stopwords.add(sw.next());
		}
		
		sw.close();
		
		//
		Pattern libPattern = Pattern.compile("(?<=(^\\s*import\\s))[\\w+\\.]+");
		
		Pattern wordPattern = Pattern.compile("[a-zA-Z]{3,}");
		
		File file = new File("ConnectedComponent.java");
		Scanner in = new Scanner(file);
		String line;
		

		while(in.hasNextLine()) {
			line = in.nextLine();
			Matcher m = wordPattern.matcher(line);
			while(m.find()) {
				String token = m.group();
				if(!stopwords.contains(token))
					if(!keywords.contains(token)) {
						keywords.add(token);
						}
			}
		}
		for (String word : keywords)
			System.out.println(word);
		
		
		in.close();
		

	}

}







