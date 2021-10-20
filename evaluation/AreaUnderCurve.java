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
import miners.ComponentMiner;

/*
 * 
 * Calculate the U statistic of Mann Whitney U Test, 
 * using as samples i)the ranking of the libraries that the system should recommend in the testing set
 * and ii) the ranking of the remaining libraries
 * Then calculate the AUC according to the rule AUC = U/n1*n2
 * Inputs:
 * existingConnections: Map containing as keys the components of the testing set and as values the components they are connected to. 
 * componentMiner: Instance of ComponentMiner containing the graph between components created by the training set
 * 
 */

public class AreaUnderCurve implements Evaluate{

	
	ComponentMiner componentMiner;
	Map<Set<Component>, Set<Component>> existingConnections;
	
	double[] x; //ranking of libraries that the system should recommend, according to testingSet
	double[] y; //ranking of unwanted libraries
	int i;
	int j;
	MannWhitneyUTest mw = new MannWhitneyUTest();

	public AreaUnderCurve(EvaluationDataSource evaluationDataSource) {
		this.componentMiner = evaluationDataSource.getComponentMiner();
		this.existingConnections = evaluationDataSource.getExistingConnections();
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
			System.out.println("Key: "+ entry.getKey());
			System.out.println("value: "+ entry.getValue());
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
}
