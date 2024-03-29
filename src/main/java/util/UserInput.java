package util;
import java.util.HashSet;
import java.util.Set;

import datatypes.Component;
import datatypes.Keyword;

public class UserInput {

	
	public Set<Component> stringToKeywords(String input){
		
		Set<Component> inputSet = new HashSet<Component>();
		String[] inputs = input.split("\\W");
		for(String i: inputs) {
			inputSet.add(new Keyword(i));
		}
		
		return inputSet;
	}
	
	public Set<Component> stringArrayToKeywords(String[] input){
		Set<Component> inputSet = new HashSet<Component>();
		for(String i: input) {
			inputSet.add(new Keyword(i));
		}
		
		return inputSet;
	}
}
