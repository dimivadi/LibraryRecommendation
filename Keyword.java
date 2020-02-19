package package1;

import java.util.ArrayList;

class Keyword{
	private String keywordName;
	private ArrayList<Library> libraries;
	
	Keyword(String keywordName){
		this.keywordName = keywordName;
	}
	
	public String getKeyword() {
		return keywordName;
	}
	
	void setConnectedLibraries(ArrayList<Library> libraries) {
		this.libraries = libraries;
	}
	
	public ArrayList<Library> getConnectedLibraries(){
		return libraries;
	}
	
}