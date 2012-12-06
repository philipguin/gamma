package com.gamma.parser;

import com.gamma.parser.Parser.ASTLeaf;
import com.gamma.parser.Parser.ASTLeafConstant;
import com.gamma.parser.Parser.ASTNode;
import com.gamma.parser.Tokenizer.ParseException;


public class Build {
	public IEvaluator<Double> build(ASTLeaf d){
		switch (d.type) {
		case VARIABLE:
			String identifier;
			try {
				identifier = d.getIdentifier().toLowerCase();
				if (identifier.equals("speed")) { 
					return new IEvaluator.IDspeed();
				} else if (identifier.equals("strength")) { 
					return new IEvaluator.IDstrength();
				} else if (identifier.equals("stepheight")) { 
					return new IEvaluator.IDstepHeight();
				} else if (identifier.equals("maxenergy")) {
					return new IEvaluator.IDmaxEnergy();
				} else if (identifier.equals("red")) {
					return new IEvaluator.IDred();
				} else if (identifier.equals("blue")) { 
					return new IEvaluator.IDblue();
				} else if (identifier.equals("green")) {
					return new IEvaluator.IDgreen();
				} else if (identifier.equals("kills")) { 
					return new IEvaluator.IDkills();
				} else if (identifier.equals("deaths")) { 
					return new IEvaluator.IDdeaths();
				} else if (identifier.equals("totaldamageoutput")) {
					return new IEvaluator.IDtotalDamageOutput();
				} else if (identifier.equals("totaldamagetaken")) {
					return new IEvaluator.IDtotalDamageTaken();
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return new IEvaluator.IDkills();
		case CONSTANT:
			@SuppressWarnings("unchecked")
			ASTLeafConstant<Double> constantLeaf = (ASTLeafConstant<Double>)d;
			return new IEvaluator.EvalNumber(constantLeaf.value);
		default:
			return new IEvaluator.EvalNumber(1);
		}
	}

	public IEvaluator<Double> build(ASTNode d){
		switch (d.type) {
		case PLUS: 
			return new IEvaluator.AdditionDouble(build((ASTLeaf) d.children[0]),build((ASTLeaf) d.children[1]));
		case MINUS:
			return new IEvaluator.SubtractionDouble(build((ASTLeaf) d.children[0]),build((ASTLeaf) d.children[1]));
		case TIMES:
			return new IEvaluator.MultDouble(build((ASTLeaf) d.children[0]),build((ASTLeaf) d.children[1]));
		case DIVIDE:
			return new IEvaluator.DivDouble(build((ASTLeaf) d.children[0]),build((ASTLeaf) d.children[1]));
		case NEGATE:
			return new IEvaluator.NegateDouble(build((ASTLeaf) d.children[0]));
		default:
			return new IEvaluator.EvalNumber(1);
		}
	}
}
