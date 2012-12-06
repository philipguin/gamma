package com.gamma.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class SpellscriptTokenizer
{
	private final int[] charBuffer = new int[4096];
	
	public static class ParseException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public ParseException(String str)
		{
			super(str);
		}
	}
	
	public static enum TokenType
	{
		PLUS, MINUS, TIMES, DIVIDE, EXPONIENTATION,
		
		IDENTIFIER,
		
		INTEGER, DOUBLE, FLOAT,
		
		PARENS_L, PARENS_R,
		
		//LOGIC_EQUAL, LOGIC_NOT_EQUAL, LOGIC_LT, LOGIC_LT_OR_EQUAL, LOGIC_GT, LOGIC_GT_OR_EQUAL, LOGIC_OR, LOGIC_AND, LOGIC_EXOR,
		
		END_OF_FILE,
	}
	
	public static class Token
	{
		public TokenType type;
		
		public Token(TokenType Type)
		{
			type = Type;
		}
		
		@Override
		public String toString()
		{
			return type.name();
		}
	}
	
	public static final class TokenValued<T> extends Token
	{
		public T value;
		
		public TokenValued(TokenType Type, T content)
		{
			super(Type);
			value = content;
		}

		@Override
		public String toString()
		{
			return type.name() + " ("+value.toString()+")";
		}
	}
	
	public List<Token> tokenize(Reader input) throws IOException, ParseException
	{
		List<Token> result = new LinkedList<Token>();
		
		int lastC = -1, c = '\r';
		Stack<Integer> indentLevels = new Stack<Integer>();
		indentLevels.push(0);
		
		int indentLevel = 0;
		int lineNumber = 0;
		
		int charCount;
		
		//break; = read character
		//continue parseLoop; = use current character
		
		parseLoop:
		while (true)
		{
			switch (c)
			{
			case -1:
				break parseLoop;
			
			//Whitespace
			case ' ':
			case '\t':
				break;
					

			//Line breaks
			case '\n':
				if (lastC == '\r')
					break;
				
			case '\r':
				
				++lineNumber;
				indentLevel = 0;
				//result.add(new Token(TokenType.ENDLINE));
				
				while (true)
				{
					lastC = c;
					c = input.read();
					
					switch (c)
					{
					case ' ':
						++indentLevel;
						continue;
						
					case '\t':
						indentLevel += 4;
						continue;
						
					case '#': //Comment
						while (true)
						{
							lastC = c;
							c = input.read();
							
							switch (c)
							{
							default:
								continue;
							
							case '\n':
							case '\r':
							}
							break;
						}

					case '\n':
						if (lastC == '\r')
							continue;
						
					case '\r':
						++lineNumber;
						indentLevel = 0;
						//result.add(new Token(TokenType.ENDLINE));
						continue;
						
					case -1:
						indentLevel = 0;
						break;
					}
					break;
				}
					
				/*if (indentLevel > indentLevels.peek())
				{
					indentLevels.push(indentLevel);
					result.add(new Token(TokenType.INDENT));
				}
				else if (indentLevel < indentLevels.peek())
				{
					do
					{
						indentLevels.pop();
						result.add(new Token(TokenType.UNINDENT));
						
						if (indentLevel > indentLevels.peek())
							throw new ParseException("Error: Inconsistent indentation at line " + lineNumber);
					}
					while (indentLevel < indentLevels.peek());
				}
				else //same indentation
				{
					
				}*/
				
				continue parseLoop;
				
			//Comment
			case '#':
				
				while (true)
				{
					lastC = c;
					c = input.read();
					
					switch (c)
					{
					default:
						continue;
					
					case '\n':
					case '\r':
					}
					break;
				}
				
				continue parseLoop;
				
			//Punctuation
			case '(': result.add(new Token(TokenType.PARENS_L)); break;
			case ')': result.add(new Token(TokenType.PARENS_R)); break;
				
			//Assignment, Logic, Not
			case '=':

				lastC = c;
				c = input.read();

				if (c != '=')
					throw new ParseException("Error: expected '=' after '=' at line " + lineNumber);
				
				result.add(new Token(TokenType.LOGIC_NOT_EQUAL));
				break;
				
				
			case '!':

				lastC = c;
				c = input.read();
				
				if (c != '=')
					throw new ParseException("Error: expected '=' after '!' at line " + lineNumber);
				
				result.add(new Token(TokenType.LOGIC_EQUAL));
				break;
				
			case '<':

				lastC = c;
				c = input.read();
				
				if (compare(c, '=', TokenType.LOGIC_LT_OR_EQUAL, TokenType.LOGIC_LT, result))
					break;
				
				continue parseLoop;
				
			case '>':

				lastC = c;
				c = input.read();
				
				if (compare(c, '=', TokenType.LOGIC_GT_OR_EQUAL, TokenType.LOGIC_GT, result))
					break;
				
				continue parseLoop;
				
			//Compound assignment and binary operators
			case '+':
			case '-':
			case '*':
			case '/':

				lastC = c;
				c = input.read();
				
				//Binary
				switch (lastC)
				{
					case '+': result.add(new Token(TokenType.PLUS));   break;
					case '-': result.add(new Token(TokenType.MINUS));  break;
					case '*': result.add(new Token(TokenType.TIMES));  break;
					case '/': result.add(new Token(TokenType.DIVIDE)); break;
				}
				continue parseLoop;
				
			//Identifiers
			case '_':
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z':
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z':

				charBuffer[0] = c;
				charCount = 1;

				lastC = c;
				c = input.read();
				
				while (true)
				{
					if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '_')
					{
						charBuffer[charCount] = c;
						++charCount;
						
						lastC = c;
						c = input.read();
						continue;
					}
					break;
				}
				
				result.add(new TokenValued<String>(TokenType.IDENTIFIER, new String(charBuffer, 0, charCount)));
				
				continue parseLoop;
				
			//Numeric literals
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
			case '.':
				
				charBuffer[0] = c;
				charCount = 1;
				
				boolean isFinite = c != '.';
                
                while (true)
                {
    				lastC = c;
    				c = input.read();
    				
                	switch (c)
                	{
                	case '.':
                		if (isFinite)
                			isFinite = false;
                		else
                			throw new ParseException("Error: expected no more than 1 '.' at line " + lineNumber);
                		
                	case '0':
                	case '1':
                	case '2':
                	case '3':
                	case '4':
                	case '5':
                	case '6':
                	case '7':
                	case '8':
                	case '9':
                		
	    				charBuffer[charCount] = c;
	    				++charCount;
	    				continue;
                	}
                	break;
                }
                
                if (!isFinite && charCount == 1)
                {
                	throw new ParseException("Error: found unexpected '.' at line " + lineNumber);
                	//result.add(new Token(TokenType.PERIOD));
                	//continue parseLoop;
                }
                
                switch (c)
                {
                case 'f':
                case 'F':
                	result.add(new TokenValued<Float>(TokenType.FLOAT, Float.parseFloat(new String(charBuffer, 0, charCount))));
                	break;
                	
                case 'd':
                case 'D':
                	result.add(new TokenValued<Double>(TokenType.DOUBLE, Double.parseDouble(new String(charBuffer, 0, charCount))));
                	break;
                	
                default:
                    if (isFinite)
                    	result.add(new TokenValued<Integer>(TokenType.INTEGER, Integer.parseInt(new String(charBuffer, 0, charCount))));
                    else
                    	result.add(new TokenValued<Double>(TokenType.DOUBLE, Double.parseDouble(new String(charBuffer, 0, charCount))));
                    
                	continue parseLoop;
                }
                break;
			}

			lastC = c;
			c = input.read();
		}
		
		result.remove(0);
		
		/*result.add(new Token(TokenType.ENDLINE));
		
		while (indentLevels.size() > 1)
		{
			result.add(new Token(TokenType.UNINDENT));
			indentLevels.pop();
		}*/
		
		result.add(new Token(TokenType.END_OF_FILE));
		
		return result;
	}
	
	private static final boolean compare(int c, int toCompare, TokenType ifTrue, TokenType ifFalse, List<Token> result)
	{
		if (c == toCompare)
		{
			if (ifTrue != null)
				result.add(new Token(ifTrue));
			return true;
		}
		else
		{
			if (ifFalse != null)
				result.add(new Token(ifFalse));
			return false;
		}
	}
}
