package package1;

import java.util.ArrayList;

class Library{
	private String libraryName;
	
	Library(String libraryName){
		this.libraryName = libraryName;
	}
	
	public String getLibraryName() {
		return this.libraryName;
	}
	
	boolean libraryExistsInArrayList(ArrayList<Library> libraries) {
		for (Library lib : libraries) {
			if(lib.getLibraryName() == this.libraryName) {
				return true;
			}
		}
		return false;
	}
	
}
