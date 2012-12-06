package com.gamma;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class FitnessCalculator {
	private String algorithm;
	private List<Creature> creatures;
	private Activity context;
	
	public FitnessCalculator(Activity contextActivity) {
		context = contextActivity;
	}
	
	public void refreshAlgorithm() {
		try {
    		//Open reader
			String dirName = context.getFilesDir().getAbsolutePath();
			String fileName = dirName + "/fitness_algorithm.txt";
    		BufferedReader input = new BufferedReader(new FileReader(fileName));
    		
    		//Read the algorithm from the file
    		algorithm = input.readLine();
    		input.close();
    	} catch (FileNotFoundException e) {
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
	}
	
	public void setCreatures(List<Creature> inputCreatures) {
		creatures = inputCreatures;
	}
	
	public void calculateFitnesses() {
		for (Creature creature : creatures) {
			//TODO Use evaluator to get fitness
			int fitness = 0; //0 is a placeholder
			creature.setFitness(fitness);
		}	
	}
}
