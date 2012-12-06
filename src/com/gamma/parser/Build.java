package com.gamma.parser;

import com.gamma.parser.Parser.ASTLeaf;
import com.gamma.parser.Parser.ASTNode;
import com.gamma.parser.Parser.LeafType;
import com.gamma.parser.Parser.NodeType;
import com.gamma.parser.Tokenizer.ParseException;


public class Build {
	public IEvaluator<Float> build(ASTLeaf d){
		switch (d.type) {
		case VARIABLE:
			{
			String identifier;
			try {
				identifier = d.getIdentifier().toLowerCase();
				if (identifier=="speed") { 
					return new IEvaluator.IDspeed();
				} else if (identifier=="strength") { 
					return new IEvaluator.IDstrength();
				} else if (identifier=="stepheight") { 
					return new IEvaluator.IDstepHeight();
				} else if (identifier=="maxenergy") {
					return new IEvaluator.IDmaxEnergy();
				} /*case "red": 
					return IDred();
				case "blue": 
					return IDblue();
				case "green": 
					return IDgreen();
				case "kills": 
					return IDkills();
				case "deaths": 
					return IDdeaths();
				case "totaldamageoutput":
					return IDtotalDamageOutput();
				case "totaldamagetaken":
					return IDtotalDamageTaken();
				}
				break;
				*/
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		case CONSTANT : break;
		}
			return new IEvaluator.IDspeed();
	}
}
