package evaluation;

import java.io.FileNotFoundException;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

public class AreaUnderCurve extends Evaluate{

	/*
	 * 
	 * Calculate the U statistic of Mann Whitney U Test, 
	 * using as samples i)the libraries that the system should recommend in the testing set
	 * and ii) the remaining libraries
	 * Then calculate the AUC according to the rule AUC = U/n1*n2
	 * 
	 */
	
	double[] x; //ranking of libraries that the system should recommend, according to testingSet
	double[] y; //ranking of unwanted libraries
	
	AreaUnderCurve(String trainingSet, String testingSet, String filesExtensions) throws FileNotFoundException {
		super(trainingSet, testingSet, filesExtensions);
		
	}

	@Override
	public void run() {
		
		
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
