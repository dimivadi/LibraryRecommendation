package miners;
import java.util.Collection;
import java.util.Map;

import datatypes.Component;

public class Evaluation {
	
	private final double threshold = 0.001;
	private Map<Component, Double> scores;
	private Collection<Component> testFileLibraries;
	int foundLibraries = 0;
	
	public Evaluation(Map<Component, Double> scores, Collection<Component> testFileLibraries){
		this.scores = scores;
		this.testFileLibraries = testFileLibraries;
		
		
		for(Component component: testFileLibraries) {
			if(scores.get(component) != null) {
				System.out.println("Library found: "+ component + " score: "+scores.get(component));
				foundLibraries++;
			}
		}
		
	}
	
	public long[] results() {
		
		long[] results = new long[2];
		results[0] = foundLibraries;
		results[1] = scores.size();
		return results;
		
	}


}
