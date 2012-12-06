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
		int fitness = 0;
		//TODO Use evaluator to get fitness
		return fitness;
	}
}
