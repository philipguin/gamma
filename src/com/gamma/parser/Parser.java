package com.gamma.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import com.gamma.parser.Tokenizer.ParseException;
import com.gamma.parser.Tokenizer.Token;
import com.gamma.parser.Tokenizer.TokenType;
import com.gamma.parser.Tokenizer.TokenValued;

public class Parser
{
	public Parser(List<Token> tokens)
	{
		tokenIterator = tokens.listIterator();
		
		if (tokenIterator.hasNext())
			current = tokenIterator.next();
	}
	
	protected final ListIterator<Token> tokenIterator;
	protected Token current;
	protected int lineNumber = 1;
	
	protected static final HashMap<String, ResolveType.Type> typeMapping = new HashMap<String, ResolveType.Type>();
	protected static final HashSet<String> keywordMapping = new HashSet<String>();
	
	public static enum NodeType
	{	
		LOGIC_EXOR, LOGIC_OR, LOGIC_AND,
		
		LOGIC_EQUALITY, LOGIC_INEQUALITY, LOGIC_IS, LOGIC_ISNOT,
		
		LOGIC_LT, LOGIC_LT_OR_EQUAL, LOGIC_GT, LOGIC_GT_OR_EQUAL,
		
		PLUS, MINUS, TIMES, DIVIDE, EXPONIENTATION,
		
		NEGATE, NOT,
	}
	
	public static enum LeafType
	{	
		CONSTANT, VARIABLE,
	}

	public static enum Likelihood { wont, might, will } 
	
	public static abstract class ASTPart
	{
		public ResolveType resolve;
		public Likelihood returnness = Likelihood.wont, breakness = Likelihood.wont, continueness = Likelihood.wont;
		
		public ASTPart(ResolveType Resolve)
		{
			resolve = Resolve;
		}
		
		public String getIdentifier() throws ParseException
		{
			throw new ParseException("Error: invalid call to 'getIdentifier()', this ASTPart ain't got one.");
		}
	}
	
	public static class ASTNode extends ASTPart
	{
		public NodeType type;
		public final ASTPart[] children;
		
		public ASTNode(NodeType Type, ResolveType Resolve, ASTPart[] Children)
		{
			super(Resolve);
			type = Type;
			children = Children;
		}
	}
	
	public static final class ASTIdentifierNode extends ASTNode
	{
		public String identifier;
		
		public ASTIdentifierNode(NodeType Type, ResolveType Resolve, String Identifier, ASTPart[] Children)
		{
			super(Type, Resolve, Children);
			identifier = Identifier;
		}

		public String getIdentifier() throws ParseException
		{
			return identifier;
		}
	}
	
	public static class ASTLeaf extends ASTPart
	{
		public LeafType type;
		
		public ASTLeaf(LeafType Type, ResolveType Resolve)
		{
			super(Resolve);
			type = Type;
		}
	}
	
	public static class ASTIdentifierLeaf extends ASTLeaf
	{
		public String identifier;
		
		public ASTIdentifierLeaf(LeafType Type, ResolveType Resolve, String Identifier)
		{
			super(Type, Resolve);
			identifier = Identifier;
		}

		public String getIdentifier() throws ParseException
		{
			return identifier;
		}
	}
	
	public static final class ASTLeafConstant<T> extends ASTLeaf
	{
		public T value;
		
		public ASTLeafConstant(ResolveType Resolve, T Value)
		{
			super(LeafType.CONSTANT, Resolve);
			value = Value;
		}
	}
	
	static
	{   
	    keywordMapping.add("True");
	    keywordMapping.add("False");
	    
	    keywordMapping.add("not");
	    keywordMapping.add("or");
	    keywordMapping.add("and");
	    keywordMapping.add("exor");
	}
	
	public int getLineNumber()
	{
		return lineNumber;
	}
	
	public void incrementLineNumber()
	{
		++lineNumber;
	}
	
	protected final void back()
	{
		tokenIterator.previous();
		tokenIterator.previous();
		current = tokenIterator.next();
	}
	
	protected final void next()
	{
		current = tokenIterator.next();
	}
	
	protected final Token peek()
	{
		return current;
	}
	
	@SuppressWarnings("unchecked")
	protected final TokenValued<String> peekString()
	{
		return (TokenValued<String>)current;
	}
	
	protected final Token pop()
	{
		Token result = current;
		current = tokenIterator.next();
		return result;
	}
	
	protected final void throwExpressionException() throws ParseException
	{
        throw new ParseException("Error: expected variable, constant, function call, or an expression closed in parenthesis after '"+peek()+" at line "+getLineNumber());
	}

	protected static final boolean isKeyword(String value)
	{
	    return keywordMapping.contains(value);
	}
	
	protected final ASTIdentifierLeaf makeVariable(String identifier)
	{
		return new ASTIdentifierLeaf(LeafType.VARIABLE, null, identifier);
	}
	
	
	public final ASTPart makeExpression() throws ParseException
	{
		return checkGrammar_Exor();
	}

	protected final ASTPart checkGrammar_Exor() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_Or()) == null)
	    	return null;

	    while (peek().type == TokenType.IDENTIFIER && "exor".equals(peekString().value))
	    {
		    next();
		    
	        if ((current = checkGrammar_Or()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(NodeType.LOGIC_EXOR, null, new ASTPart[]{tree, current});
	    }
	    
        return tree;
	}

	protected final ASTPart checkGrammar_Or() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_And()) == null)
	    	return null;

	    while (peek().type == TokenType.IDENTIFIER && "or".equals(peekString().value))
	    {
		    next();
		    
	        if ((current = checkGrammar_And()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(NodeType.LOGIC_OR, null, new ASTPart[]{tree, current});
	    }
	    
        return tree;
	}

	protected final ASTPart checkGrammar_And() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_Is_Not()) == null)
	    	return null;

	    while (peek().type == TokenType.IDENTIFIER && "and".equals(peekString().value))
	    {
		    next();
		    
	        if ((current = checkGrammar_Is_Not()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(NodeType.LOGIC_AND, null, new ASTPart[]{tree, current});
	    }
	    
        return tree;
	}

	protected final ASTPart checkGrammar_Is_Not() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_Equality()) == null)
	    	return null;

	    NodeType type;

	    while (true)
	    {
		    if (peek().type != TokenType.IDENTIFIER || !"is".equals(peekString().value))
		    	return tree;
		    
		    next();

		    if (peek().type == TokenType.IDENTIFIER && "not".equals(peekString().value))
		    {
		    	next();
		    	type = NodeType.LOGIC_ISNOT;
		    }
		    else
		    	type = NodeType.LOGIC_IS;
		    
	        if ((current = checkGrammar_Equality()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(type, null, new ASTPart[]{tree, current});
	    }
	}
	
	protected final ASTPart checkGrammar_Equality() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_Comparison()) == null)
	    	return null;

	    NodeType type;

	    while (true)
	    {
		    if (peek().type == TokenType.LOGIC_EQUAL)
		    	type = NodeType.LOGIC_EQUALITY;
		    
		    else if (peek().type == TokenType.LOGIC_NOT_EQUAL)
		    	type = NodeType.LOGIC_INEQUALITY;
		    
		    else
		    	return tree;
	
		    next();
		    
	        if ((current = checkGrammar_Comparison()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(type, null, new ASTPart[]{tree, current});
	    }
	}
	
	protected final ASTPart checkGrammar_Comparison() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_Term()) == null)
	    	return null;

	    NodeType type;

	    while (true)
	    {
		    switch (peek().type)
		    {
		    default:
		    	return tree;
		    	
		    case LOGIC_LT:			type = NodeType.LOGIC_LT;			break;
		    case LOGIC_LT_OR_EQUAL:	type = NodeType.LOGIC_LT_OR_EQUAL;	break;
		    case LOGIC_GT:			type = NodeType.LOGIC_GT;			break;
		    case LOGIC_GT_OR_EQUAL:	type = NodeType.LOGIC_GT_OR_EQUAL;	break;
		    }
		    
		    next();
		    
	        if ((current = checkGrammar_Term()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(type, null, new ASTPart[]{tree, current});
	    }
	}

	protected final ASTPart checkGrammar_Term() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_Factor()) == null)
	    	return null;

	    NodeType type;
	    
	    while (true)
	    {
		    switch (peek().type)
		    {
		    default:
		    	return tree;
	
		    case PLUS:  type = NodeType.PLUS;  break;
		    case MINUS: type = NodeType.MINUS; break;
		    }
		    
		    next();
		    
	        if ((current = checkGrammar_Factor()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(type, null, new ASTPart[]{tree, current});
	    }
	}

	protected final ASTPart checkGrammar_Factor() throws ParseException
	{
		ASTPart current, tree = null;

	    if ((tree = checkGrammar_Unary()) == null)
	    	return null;

	    NodeType type;
	    
	    while (true)
	    {
		    switch (peek().type)
		    {
		    default:
		    	return tree;
	
		    case TIMES:  type = NodeType.TIMES;  break;
		    case DIVIDE: type = NodeType.DIVIDE; break;
		    }
		    
		    next();
		    
	        if ((current = checkGrammar_Unary()) == null)
	            throwExpressionException();
	        
	        tree = new ASTNode(type, null, new ASTPart[]{tree, current});
	    }
	}

	protected final ASTPart checkGrammar_Unary() throws ParseException
	{
		NodeType type;
		
	    switch (peek().type)
	    {
	    case MINUS:	type = NodeType.NEGATE;	break;
	    
	    case IDENTIFIER:
	    	
	    	if ("not".equals(peekString().value))
	    	{
		    	type = NodeType.NOT;
		    	break;
	    	}

	    default:
	    {
	    	ASTPart leaf;
	        if ((leaf = checkGrammar_Exponientation()) == null)
	            return null;
	        
            return leaf;
	    }
	    }
	    
	    next();
    	ASTPart node;
    	
        if ((node = checkGrammar_Exponientation()) == null)
            throwExpressionException();
        
        //if incremental ops included, validate child is identifier here

	    return new ASTNode(type, null, new ASTPart[]{node});
	}

	protected final ASTPart checkGrammar_Exponientation() throws ParseException
	{
		ASTPart left;
		
        if ((left = checkGrammar_Leaf()) == null)
            return null;
	    
        if (peek().type != TokenType.EXPONIENTATION)
        	return left;
        
        next();
    	ASTPart right;
    	
        if ((right = checkGrammar_Unary()) == null)//weird case for negative exponents
            throwExpressionException();

	    return new ASTNode(NodeType.EXPONIENTATION, null, new ASTPart[]{left, right});
	}

	@SuppressWarnings("unchecked")
	protected final ASTPart checkGrammar_Leaf() throws ParseException
	{
	    switch (peek().type)
	    {
	    case PARENS_L:
	    {
	    	//Parenthesized expression
	    	next();
	    	
	    	ASTPart node;
	        if ((node = makeExpression()) == null)
	            throw new ParseException("Error at line "+getLineNumber()+": Expected expression but found '"+peek()+"' instead.");
	        
	        if (peek().type != TokenType.PARENS_R)
	            throw new ParseException("Error at line "+getLineNumber()+": Expected ')' but found '"+peek()+"' instead.");
	        
	        next();
	        
	        return node;
	    }
    
	    case IDENTIFIER:
	    {
	    	String identifier = peekString().value;
	        
	        if (isKeyword(identifier) && !"self".equals(identifier)) //kinda hackish, but whatever
	        {
	        	//Keyword literals
	        	next();
	        	
		        if ("True".equals(identifier))
		        	return new ASTLeafConstant<Boolean>(new ResolveType(ResolveType.Type.BOOLEAN), Boolean.valueOf(true));
		        
		        if ("False".equals(identifier))
		        	return new ASTLeafConstant<Boolean>(new ResolveType(ResolveType.Type.BOOLEAN), Boolean.valueOf(false));
		        
		        if ("None".equals(identifier))
		        	return new ASTLeafConstant<Object>(null, null);
		        
		        throw new ParseException("Error at line "+getLineNumber()+": incorrectly placed keyword '" + identifier + "'.");
	        }
	        
	        if (typeMapping.containsKey(identifier))
	        	throw new ParseException("Error at line "+getLineNumber()+": incorrectly placed type-keyword '" + identifier + "'.");
	    	
	    	return assumeVariableOrFunction();
	    }
	    	
	    default:
	    	return null;

	    //Other literals
	    case INTEGER: return new ASTLeafConstant<Integer>(new ResolveType(ResolveType.Type.INTEGER), ((TokenValued<Integer>)pop()).value);
	    case FLOAT:   return new ASTLeafConstant<Float>  (new ResolveType(ResolveType.Type.FLOAT),   ((TokenValued<Float>  )pop()).value);
	    case DOUBLE:  return new ASTLeafConstant<Double> (new ResolveType(ResolveType.Type.DOUBLE),  ((TokenValued<Double> )pop()).value);
	    }
	}

	protected final ASTPart assumeVariableOrFunction() throws ParseException
	{
		String identifier = peekString().value;
    	next();
        return makeVariable(identifier);
	}
}
 