package evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import datatypes.Component;
import miners.ComponentMiner;
import miners.RankedComponents;

public class Metrics{
	
	private ComponentMiner componentMiner;
	private Map<Set<Component>, Set<Component>> existingConnections;
	private boolean sweepRatio;
	private double dampingFactor;
	private String normalization;
	private FileWriter fileWriter;
	private boolean isWeighted;
	
	public Metrics(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections, 
			boolean sweepRatio, double dampingFactor, String normalization, boolean isWeighted, FileWriter fileWriter){
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		this.sweepRatio = sweepRatio;
		this.dampingFactor = dampingFactor;
		this.normalization = normalization;
		this.fileWriter = fileWriter;
		this.isWeighted = isWeighted;
	}
	
	public Metrics(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections){
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
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
		double meanPrecision;
		double meanRecall;
		double f1Score;
		int hits = 0;
		double sumOfAUC = 0;
		int sizeOfTestingSet = existingConnections.size();
		
		System.out.println("size of testing set: "+sizeOfTestingSet);
		
		Similarity similarity = new Similarity(componentMiner, true);
	
		// 	Coverage
		Coverage coverage = new Coverage();
		coverage.setNumberOfCommonLibraries(componentMiner, existingConnections);
		for(Entry<Set<Component>, Set<Component>> existingConnection : existingConnections.entrySet()) {
			
			if(existingConnection.getKey().isEmpty() || existingConnection.getValue().isEmpty()) {
				sizeOfTestingSet--;
				continue;
			}
			
//			if(existingConnection.getValue().size() < 10) {
//				sizeOfTestingSet--;
//				continue;
//			}
			
			Map<Component, Double> rankedComponents = componentMiner.componentMining(existingConnection.getKey(), sweepRatio, dampingFactor, normalization, isWeighted);
//			Map<Component, Double> rankedComponents = componentMiner.componentMining(existingConnection.getKey());
//			Map<Component, Double> rankedComponents = similarity.getLibrarySimilarity(existingConnection.getKey());
			
			// AUC
			x = new double[existingConnection.getValue().size()];
			y = new double[rankedComponents.size()];
			i = 0;
			j = 0;

//			System.out.println("value: ("+existingConnection.getValue().size()+") "+ existingConnection.getValue());	//
//			System.out.println("value: ("+existingConnection.getValue().size()+") ");	

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

//			System.out.println("AUC: "+ auc);
		
			RankedComponents rc = new RankedComponents(rankedComponents);
			Map<Component, Double> top3Components = rc.getTopComponents(3);
			Map<Component, Double> top10Components = rc.getTopComponents(10);

			coverage.addToRecommendedComponents(top10Components);
			
			//Recall
			int count = 0;
			
			for(Component existingLibrary: existingConnection.getValue()) {
				if(top10Components.containsKey(existingLibrary)) {
					count++;
				}else {
//					rc.getLibraryPosition(existingLibrary);
				}
			}
			recall += (double) count / existingConnection.getValue().size();
//			System.out.println("recall@"+top10Components.size()+": "+ (double) count / existingConnection.getValue().size());
			
			//Precision
			count = 0;
			for(Map.Entry<Component, Double> recommendedComp : top10Components.entrySet()) {
				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					count++;
				}
			}
			precision += (double) count / (top10Components.size());
//			System.out.println("precision@"+top10Components.size()+": " + (double) count / top10Components.size());
			
			// HitRate
			for(Map.Entry<Component, Double> recommendedComp : top3Components.entrySet()) {

				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					hits++;
					break;
				}
			}

		}
//		coverage.setNumberOfCommonLibraries(componentMiner, currentTestLibs);

		meanRecall = (double) recall/sizeOfTestingSet;
		meanPrecision = (double) precision/sizeOfTestingSet;
		f1Score = (double) 2 / ((1/meanPrecision) + (1/meanRecall));
		
		System.out.println();
		System.out.println("damping factor: " + dampingFactor + "  sweepRatio: "+ sweepRatio + "  normalization: " + normalization);
		System.out.println("AUC mean: "+ sumOfAUC/N);
		System.out.println("mean recall: " + meanRecall);
		System.out.println("mean pricision: " + meanPrecision);
		System.out.println("f1 score: " + f1Score);
		System.out.println("Hit Rate: " + (double) hits/sizeOfTestingSet);
		System.out.println("Coverage: " + coverage.getCoverage());
		
		String text = "damping factor: " + dampingFactor + "  sweepRatio: "+ sweepRatio + "  normalization: " + normalization + "\n" + 
				"AUC mean: "+ sumOfAUC/N + "\n" + 
				"mean recall: " + meanRecall + "\n" + 
				"mean pricision: " + meanPrecision + "\n" + 
				"f1 score: " + f1Score + "\n" + 
				"Hit Rate: " + (double) hits/sizeOfTestingSet + "\n" + 
				"Coverage: " + coverage.getCoverage() + "\n" + "\n"; 
		try {
			fileWriter.append(text);
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}

}