package package1;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



public class Class1{
	

	
	
	public static void main(String[] args) throws IOException{
		
		ArrayList<CodeFile> codefiles = new ArrayList<CodeFile>();
		File[] files = (new FilesList("Java-master")).getFiles();
		
		for(File file : files){
			CodeFile codefile = new CodeFile(file);
			codefiles.add(codefile);
			codefile.getComponents();

		}
		ArrayList<Connections> connectionsList	= new ArrayList<Connections>();
		for(CodeFile cf : codefiles) {
			Connections connections = new Connections(cf);
			connectionsList.add(connections);
		}
		
		//CodeFile codefile = new CodeFile("ConnectedComponent.java");
		//codefile.getComponents();
		
	}
}









