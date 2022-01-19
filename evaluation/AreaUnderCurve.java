package evaluation;


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

	
	private ComponentMiner componentMiner;
	private Map<Set<Component>, Set<Component>> existingConnections;
	
	
	
	double[] x; //ranking of libraries that the system should recommend, according to testingSet
	double[] y; //ranking of unwanted libraries
	int i;
	int j;
	MannWhitneyUTest mw = new MannWhitneyUTest();

	public AreaUnderCurve(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections) {
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		
	}
	
	
//	public AreaUnderCurve(EvaluationDataSource evaluationDataSource) {
//		this.componentMiner = evaluationDataSource.getComponentMiner();
//		this.existingConnections = evaluationDataSource.getExistingConnections();
//	}

	@Override
	public void run() {
		
		Map<Component, Double> rankedComponents;
		
		double sumOfAUC = 0;
		int N = 0;
		
		for(Entry<Set<Component>, Set<Component>> existingConnection: existingConnections.entrySet()) {
			
			if(existingConnection.getKey().isEmpty() || existingConnection.getValue().isEmpty()) {
				continue;
			}
			
			rankedComponents = componentMiner.componentMining(existingConnection.getKey());
			
			x = new double[existingConnection.getValue().size()];
			y = new double[rankedComponents.size()];
			i = 0;
			j = 0;
//			System.out.println("Key: "+ existingConnection.getKey());
//			System.out.println("value: "+ existingConnection.getValue());
			for(Map.Entry<Component, Double> rankedEntry: rankedComponents.entrySet()) {
				if(existingConnection.getValue().contains(rankedEntry.getKey())) {
					x[i++] = rankedEntry.getValue();
				}else {
					y[j++] = rankedEntry.getValue();
				}
			}
			
			double U = mw.mannWhitneyU(x, y);
			double auc = U/(x.length * y.length);
			sumOfAUC += auc;
			N++;
//			System.out.println("AUC for keyword \""+existingConnection.getKey()+"\":  "+ auc);
			System.out.println("AUC for seed "+existingConnection.getKey()+ ": " +auc);
		}
		System.out.println("AUC mean: "+ sumOfAUC/N);
	}	
}
