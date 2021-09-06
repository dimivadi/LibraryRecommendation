package evaluation;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

public class AreaUnderCurve {
	
	
	MannWhitneyUTest mw = new MannWhitneyUTest();
	
	double run(double[] x, double[] y) {
		
		double U = mw.mannWhitneyU(x, y);
		return U/(x.length * y.length);
		
	}
	
	
	
}
