package evaluation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import datatypes.Component;

public class AreaUnderCurve extends Evaluate{

	/*
	 * 
	 * Calculate the U statistic of Mann Whitney U Test, 
	 * using as samples i)the ranking of the libraries that the system should recommend in the testing set
	 * and ii) the ranking of the remaining libraries
	 * Then calculate the AUC according to the rule AUC = U/n1*n2
	 * 
	 */
	
	double[] x; //ranking of libraries that the system should recommend, according to testingSet
	double[] y; //ranking of unwanted libraries
	int i;
	int j;
	MannWhitneyUTest mw = new MannWhitneyUTest();
//	List<Double> x = new ArrayList<Double>(); //ranking of libraries that the system should recommend, according to testingSet
//	List<Double> y = new ArrayList<Double>(); //ranking of unwanted libraries
	
	public AreaUnderCurve(String trainingSet, String testingSet, String filesExtensions) throws IOException {
		super(trainingSet, testingSet, filesExtensions);
		
	}

	@Override
	public void run() {
		
		Map<Component, Double> rankedComponents;
		
		for(Entry<Set<Component>, Set<Component>> entry: existingConnections.entrySet()) {
			rankedComponents = componentMiner.componentMining(entry.getKey());
			x = new double[entry.getValue().size()];
			y = new double[rankedComponents.size()];
			i = 0;
			j = 0;
			for(Map.Entry<Component, Double> rankedEntry: rankedComponents.entrySet()) {
				if(entry.getValue().contains(rankedEntry.getKey())) {
					x[i++] = rankedEntry.getValue();
				}else {
					y[j++] = rankedEntry.getValue();
				}
			}
			
			double U = mw.mannWhitneyU(x, y);
			
			System.out.println("AUC for keyword \""+entry.getKey()+"\":  "+ U/(x.length * y.length));
		}
		
		
		
		
	}
	
	
	
	
//	MannWhitneyUTest mw = new MannWhitneyUTest();
//	
//	double run(double[] x, double[] y) {
//		
//		double U = mw.mannWhitneyU(x, y);
//		return U/(x.length * y.length);
//		
//	}
//	
	
	
}
