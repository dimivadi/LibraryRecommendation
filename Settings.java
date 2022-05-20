import java.util.Scanner;

public class Settings {
	
	String normalization;
	double dampingFactor;
	boolean sweepRatio;
	double[] weightValues;
	int numOfRecommendations;
	
	Settings(){
		normalization = "original";
		dampingFactor = 0.85;
		sweepRatio = false;
		weightValues = null;
		numOfRecommendations = 10;
	}
	
	public void setDampingFactor(double dampingFactor) {
		this.dampingFactor = dampingFactor;
	}
	
	public void setNormalization(String normalization) {
		this.normalization = normalization;
	}
	
	public void setSweepRatio(boolean sweepRatio) {
		this.sweepRatio = sweepRatio;
	}
	
	public void setNumOfRecommendations(int numOfRecommendations) {
		this.numOfRecommendations = numOfRecommendations;
	}
	
	public void setWeightValues(double[] weightValues) {
		this.weightValues = weightValues;
	}
	
	public double getDampingFactor() {
		return this.dampingFactor;
	}
	
	public String getNormalization() {
		return this.normalization;
	}
	
	public boolean getSweepRatio() {
		return this.sweepRatio;
	}
	
	public double[] getWeightValues() {
		return this.weightValues;
	}
	
	public int getNumOfRecommendations() {
		return this.numOfRecommendations;
	}
	
	
}
