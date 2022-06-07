package evaluation;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
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
import miners.NoSuchKeywordsExistException;

public class Metrics{
	
	private ComponentMiner componentMiner;
	private Map<Set<Component>, Set<Component>> existingConnections;
	private boolean sweepRatio;
	private double dampingFactor;
	private String normalization;
	private FileWriter fileWriter;
	private double[] weightValues;
	
	public Metrics(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections
			, boolean sweepRatio, double dampingFactor, String normalization, double[] weightValues, FileWriter fileWriter)	{
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		this.sweepRatio = sweepRatio;
		this.dampingFactor = dampingFactor;
		this.normalization = normalization;
		this.fileWriter = fileWriter;
		this.weightValues = weightValues;
	}
	
	public Metrics(ComponentMiner componentMiner, Map<Set<Component>, Set<Component>> existingConnections
			, boolean sweepRatio, double dampingFactor, String normalization, double[] weightValues)	{
		this.componentMiner = componentMiner;
		this.existingConnections = existingConnections;
		this.sweepRatio = sweepRatio;
		this.dampingFactor = dampingFactor;
		this.normalization = normalization;
		this.fileWriter = fileWriter;
		this.weightValues = weightValues;
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
		double recall3 = 0;
		double precision3 = 0;
		double meanPrecision3;
		double meanRecall3;
		double f1Score3;
		double recall5 = 0;
		double precision5 = 0;
		double meanPrecision5;
		double meanRecall5;
		double f1Score5;
		double recall10 = 0;
		double precision10 = 0;
		double meanPrecision10;
		double meanRecall10;
		double f1Score10;
		int hits3 = 0;
		int hits5 = 0;
		int hits10 = 0;
		double sumOfAUC = 0;
		int sizeOfTestingSet = existingConnections.size();
		
//		System.out.println("size of testing set: "+sizeOfTestingSet);
		
//		Similarity similarity = new Similarity(componentMiner, true);
		
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
			
			Map<Component, Double> rankedComponents = null;
			try {
				rankedComponents = componentMiner.componentMining(existingConnection.getKey(), sweepRatio, dampingFactor, normalization, weightValues);
			} catch (NoSuchKeywordsExistException e) {
//				System.out.println("No such keywords exist. Moving on with the next test");
				continue;
			}
//			Map<Component, Double> rankedComponents = componentMiner.componentMining(existingConnection.getKey());
//			rankedComponents = similarity.getLibrarySimilarity(existingConnection.getKey());
			
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
			///-------------
			Map<Component, Double> top3Components = rc.getTopComponents(3);
			Map<Component, Double> top5Components = rc.getTopComponents(5);
			Map<Component, Double> top10Components = rc.getTopComponents(10);
			
			coverage.addToRecommendedComponents(top3Components, 3);
			coverage.addToRecommendedComponents(top5Components, 5);
			coverage.addToRecommendedComponents(top10Components, 10);
			
			//Recall
			//@3
			int count3 = 0;
			for(Component existingLibrary: existingConnection.getValue()) 
				if(top3Components.containsKey(existingLibrary)) 
					count3++;	
			recall3 += (double) count3 / existingConnection.getValue().size();
			
			//@5
			int count5 = 0;
			for(Component existingLibrary: existingConnection.getValue()) 
				if(top5Components.containsKey(existingLibrary)) 
					count5++;
			recall5 += (double) count5 / existingConnection.getValue().size();
			
			//@10
			int count10 = 0;
			for(Component existingLibrary: existingConnection.getValue()) 
				if(top10Components.containsKey(existingLibrary)) 
					count10++;
			recall10 += (double) count10 / existingConnection.getValue().size();
			
			//Precision
			//@3
			count3 = 0;
			for(Map.Entry<Component, Double> recommendedComp : top3Components.entrySet()) 
				if(existingConnection.getValue().contains(recommendedComp.getKey())) 
					count3++;
			precision3 += (double) count3 / 3;
			
			//@5
			count5 = 0;
			for(Map.Entry<Component, Double> recommendedComp : top5Components.entrySet()) 
				if(existingConnection.getValue().contains(recommendedComp.getKey())) 
					count5++;
			precision5 += (double) count5 / 5;
			
			//@10
			count10 = 0;
			for(Map.Entry<Component, Double> recommendedComp : top10Components.entrySet()) 
				if(existingConnection.getValue().contains(recommendedComp.getKey())) 
					count10++;
			precision10 += (double) count10 / 10;
			
			// HitRate
			//@3
			for(Map.Entry<Component, Double> recommendedComp : top3Components.entrySet()) {

				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					hits3++;
					break;
				}
			}
			
			//@5
			for(Map.Entry<Component, Double> recommendedComp : top5Components.entrySet()) {

				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					hits5++;
					break;
				}
			}
			//@10
			for(Map.Entry<Component, Double> recommendedComp : top10Components.entrySet()) {

				if(existingConnection.getValue().contains(recommendedComp.getKey())) {
					hits10++;
					break;
				}
			}
		}
		
		meanRecall3 = (double) recall3/sizeOfTestingSet;
		meanPrecision3 = (double) precision3/sizeOfTestingSet;
		f1Score3 = (double) 2 / ((1/meanPrecision3) + (1/meanRecall3));
		double hitRate3 = (double) hits3/sizeOfTestingSet;
		
		meanRecall5 = (double) recall5/sizeOfTestingSet;
		meanPrecision5 = (double) precision5/sizeOfTestingSet;
		f1Score5 = (double) 2 / ((1/meanPrecision5) + (1/meanRecall5));
		double hitRate5 = (double) hits5/sizeOfTestingSet;
		
		meanRecall10 = (double) recall10/sizeOfTestingSet;
		meanPrecision10 = (double) precision10/sizeOfTestingSet;
		f1Score10 = (double) 2 / ((1/meanPrecision10) + (1/meanRecall10));
		double hitRate10 = (double) hits10/sizeOfTestingSet;
		
		System.out.println();
		System.out.println("damping factor: " + dampingFactor + "  sweepRatio: "+ sweepRatio + "  normalization: " + normalization + " weights: " + Arrays.toString(weightValues));
		System.out.println("AUC mean: "+ sumOfAUC/N);
		System.out.println("mean recall@{3,5,10}: " + meanRecall3 + ", " + meanRecall5 + ", " + meanRecall10);
		System.out.println("mean pricision@{3,5,10}: " + meanPrecision3 + ", " + meanPrecision5 + ", " + meanPrecision10);
		System.out.println("f1 score@{3,5,10}: " + f1Score3 + ", " + f1Score5 + ", " + f1Score10);
		System.out.println("Hit Rate@{3,5,10}: " + hitRate3 + ", " + hitRate5 + ", " + hitRate10);
		System.out.println("Coverage@{3,5,10}: " + Arrays.toString(coverage.getCoverage()));
		
//		String text = "damping factor: " + dampingFactor + "  sweepRatio: "+ sweepRatio + "  normalization: " + normalization + " weights: " + Arrays.toString(weightValues) + "\n" + 
//				"AUC mean: "+ sumOfAUC/N + "\n" + 
//				"mean recall@{3,5,10}: " + meanRecall3 + ", " + meanRecall5 + ", " + meanRecall10 + "\n" + 
//				"mean pricision@{3,5,10}: " + meanPrecision3 + ", " + meanPrecision5 + ", " + meanPrecision10 + "\n" + 
//				"f1 score@{3,5,10}: " + f1Score3 + ", " + f1Score5 + ", " + f1Score10 + "\n" + 
//				"Hit Rate@{3,5,10}: " + hitRate3 + ", " + hitRate5 + ", " + hitRate10 + "\n" + 
//				"Coverage@{3,5,10}: " + Arrays.toString(coverage.getCoverage()) + "\n" + "\n"; 
//		try {
//			fileWriter.append(text);
//		} catch (IOException e) {
////			e.printStackTrace();
//		}

				
	}

}
