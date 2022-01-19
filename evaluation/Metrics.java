package evaluation;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import datatypes.Component;
import miners.ComponentMiner;
import miners.RankedComponents;

public class Metrics implements Evaluate{
	
	ComponentMiner componentMiner;
	Map<Set<Component>, Set<Component>> existingConnections;
	int numOfRecommendedComponents;
	
	
	public Metrics(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections, int numOfRecommendedComponents){
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		this.numOfRecommendedComponents = numOfRecommendedComponents;
	}
	
	public void run() {
		double[] x; //ranking of libraries that the system should recommend, according to testingSet
		double[] y; //ranking of unwanted libraries
		int i;
		int j;
		MannWhitneyUTest mw = new MannWhitneyUTest();
		int N = 0;
		double recall = 0;
		double precision = 0;
		int hits = 0;
		double sumOfAUC = 0;
		int sizeOfTestingSet = existingConnections.size();
		
		// 	Coverage
		Coverage coverage = new Coverage();
		coverage.setGraph(componentMiner.getComponentGraph());
		
		for(Entry<Set<Component>, Set<Component>> existingConnection : existingConnections.entrySet()) {
			
			if(existingConnection.getKey().isEmpty() || existingConnection.getValue().isEmpty()) {
				sizeOfTestingSet--;
				continue;
			}
			Map<Component, Double> rankedComponents = componentMiner.componentMining(existingConnection.getKey());
			
			
			// AUC
			x = new double[existingConnection.getValue().size()];
			y = new double[rankedComponents.size()];
			i = 0;
			j = 0;
			System.out.println("Key: "+ existingConnection.getKey());
			System.out.println("value: "+ existingConnection.getValue());
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
		
			RankedComponents rc = new RankedComponents(rankedComponents);
//			Map<Component, Double> topComponents = rc.getTopComponents(numOfRecommendedComponents);
			Map<Component, Double> topComponentsH = rc.getTopComponents(3);
			Map<Component, Double> topComponentsR = rc.getTopComponents(5);
			Map<Component, Double> topComponentsP = rc.getTopComponents(10);
			
			
			coverage.addToRecommendedComponents(topComponentsR);
			
			//Recall
			int count = 0;
			
			for(Component existingLibrary: existingConnection.getValue()) {
				if(topComponentsR.containsKey(existingLibrary)) {
					count++;
				}
			}
			recall += (double) count / existingConnection.getValue().size();
			System.out.println("recall for "+existingConnection.getKey()+" for top "+topComponentsR.size()+" components: "+(double) count / existingConnection.getValue().size());
			
			//Precision
			count = 0;
			for(Map.Entry<Component, Double> recommendedComp : topComponentsP.entrySet()) {
				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					count++;
				}
			}
			precision += (double) count / (topComponentsP.size());

			System.out.println("precision for "+existingConnection.getKey()+" for top "+topComponentsP.size()+" components: "+(double) count / topComponentsP.size());
			
			
			// HitRate
			for(Map.Entry<Component, Double> recommendedComp : topComponentsH.entrySet()) {
				//System.out.println("recommendedComp: "+recommendedComp.getKey()+" for keyword: "+ entry.getKey());
				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					hits++;
					break;
				}
			}
		}
		System.out.println("AUC mean: "+ sumOfAUC/N);
		System.out.println("mean recall: " + (double) recall/sizeOfTestingSet);
		System.out.println("mean pricision: " + (double) precision/sizeOfTestingSet);
		System.out.println("Hit Rate: " + (float) hits/sizeOfTestingSet);
		System.out.println("Coverage: " + coverage.getCoverage());
	}

}