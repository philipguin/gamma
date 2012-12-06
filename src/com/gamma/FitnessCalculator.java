package com.gamma;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;

import com.gamma.parser.Build;
import com.gamma.parser.IEvaluator;
import com.gamma.parser.Parser;
import com.gamma.parser.Parser.ASTNode;
import com.gamma.parser.Parser.ASTPart;
import com.gamma.parser.Tokenizer;
import com.gamma.parser.Tokenizer.ParseException;
import com.gamma.parser.Tokenizer.Token;

import android.app.Activity;

public class FitnessCalculator {
	private String algorithm;
	private List<Creature> creatures;
	private Activity context;
	private IEvaluator<Double> eTree;
	private String defaultAlgorithm = "kills";
	
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
    		
    		//If alrgorithm is empty, make it non-empty
    		if (algorithm.trim() == "") {
    			algorithm = defaultAlgorithm;
    		}
    		
    		//Read tokens from algorithm
    		input = new BufferedReader(new StringReader(algorithm));
    		Tokenizer tokenizer = new Tokenizer();
    		List<Token> tokens = tokenizer.tokenize(input);
    		
    		//Convert tokens to parse tree
    		Parser parser = new Parser(tokens);
    		ASTPart tree = parser.makeExpression();
    		
    		//Convert to evaluator tree
    		Build builder = new Build();
    		IEvaluator<Double> eTree = builder.build((ASTNode)tree);
    	} catch (FileNotFoundException e) {
    	    e.printStackTrace();
    	} catch (IOException e) {
    	    e.printStackTrace();
    	} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void setCreatures(List<Creature> inputCreatures) {
		creatures = inputCreatures;
	}
	
	public void calculateFitnesses() {
		for (Creature creature : creatures) {
			Float fitness = eTree == null ? null : eTree.resolve(creature).floatValue();
			
			if (fitness == null || fitness <= 0f) {
				fitness = 0.01f;
			}
			creature.setFitness(fitness);
		}	
	}
}
