package package1;

import java.util.ArrayList;

class Keyword{
	private String keywordName;
	private ArrayList<Library> libraries;
	
	Keyword(String keywordName){
		this.keywordName = keywordName;
	}
	
	public String getKeywordName() {
		return keywordName;
	}
	
	void setConnectedLibraries(ArrayList<Library> libraries) {
		this.libraries = libraries;
	}
	
	void addConnectedLibrary(Library library) {
		libraries.add(library);
	}
	
	public ArrayList<Library> getConnectedLibraries(){
		return libraries;
	}
	
	boolean keywordExistsInArrayList(ArrayList<Keyword> keywords) {
		for (Keyword kw : keywords) {
			if(kw.getKeywordName() == this.keywordName) {
				return true;
			}
		}
		return false;
	}
}