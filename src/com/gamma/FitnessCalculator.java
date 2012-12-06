package com.gamma;

public class FitnessCalculator {
	private String algorithm;
	
	public FitnessCalculator(String alg) {
		algorithm = alg;
	}
	
	public void setAlgorithm(String alg) {
		algorithm = alg;
	}
	
	public int calculateFitness(Creature creature) {
		//Get stats from creature
		return 0; //Placeholder
	}
}
