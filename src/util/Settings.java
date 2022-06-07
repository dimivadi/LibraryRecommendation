package util;
import java.util.Scanner;

public class Settings {
	
	String normalization;
	double dampingFactor;
	boolean sweepRatio;
	double[] weightValues;
	int numOfRecommendations;
	String methodShortname;
	boolean cosSim;

	
	Settings(){
		normalization = "original";
		dampingFactor = 0.85;
		sweepRatio = false;
		weightValues = null;
		numOfRecommendations = 20;
		cosSim = false;
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
	
	public String setMethodShortname(String methodShortname) {
		this.methodShortname = methodShortname;
		return this.methodShortname;
	}
	
	public void setCosSim(boolean cosSim) {
		this.cosSim = cosSim;
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
	
	public String getMethodShortname() {
		return methodShortname;
	}
	
	public boolean getCosSim() {
		return this.cosSim;
	}
	
}
