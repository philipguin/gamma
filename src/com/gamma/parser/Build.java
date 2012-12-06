package com.gamma.parser;

import java.util.Locale;

import android.util.Log;

import com.gamma.parser.Parser.ASTLeaf;
import com.gamma.parser.Parser.ASTLeafConstant;
import com.gamma.parser.Parser.ASTNode;
import com.gamma.parser.Parser.ASTPart;
import com.gamma.parser.Tokenizer.ParseException;


public class Build
{
	public IEvaluator<Double> build(ASTPart d) {
		if (d instanceof ASTNode)
		{
			ASTNode node = (ASTNode)d;
			switch (node.type) {
			case PLUS: 
				return new IEvaluator.AdditionDouble(build(node.children[0]), build(node.children[1]));
			case MINUS:
				return new IEvaluator.SubtractionDouble(build(node.children[0]), build(node.children[1]));
			case TIMES:
				return new IEvaluator.MultDouble(build(node.children[0]), build(node.children[1]));
			case DIVIDE:
				return new IEvaluator.DivDouble(build(node.children[0]), build(node.children[1]));
			case NEGATE:
				return new IEvaluator.NegateDouble(build(node.children[0]));
			default:
				return null;
			}
		}
		else if (d instanceof ASTLeaf)
		{
			switch (((ASTLeaf)d).type) {
			case VARIABLE:
				String identifier;
				try {
					identifier = d.getIdentifier().toLowerCase(Locale.US);
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
					Log.e("parse", "Had an error!", e);
					return null;
				}
			case CONSTANT:
				@SuppressWarnings("unchecked")
				ASTLeafConstant<Double> constantLeaf = (ASTLeafConstant<Double>)d;
				return new IEvaluator.EvalNumber(constantLeaf.value);
			default:
				return null;
			}
		}
		else
			return null;
	}
}
