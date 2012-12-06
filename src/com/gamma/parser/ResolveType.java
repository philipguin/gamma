package com.gamma.parser;


public class ResolveType
{
	public static enum Type
	{
		FLOAT("float"), DOUBLE("double"), INTEGER("int"), BOOLEAN("bool");
		
		private final String name;
		
		private Type(String str)
		{
			name = str;
		}
		
		public final String toString()
		{
			return name;
		}
	}
	
	public final Type type;
	
	public ResolveType(Type t)
	{
		type = t;
	}
	
	public boolean matches(Type t)
	{
		return t == type;
	}
	
	public String toString()
	{
		if (type == null)
			return "void";
		
		return type.toString();
	}

	@Override
	public final boolean equals(Object other)
	{
		return other != null && (other instanceof ResolveType) && type == ((ResolveType)other).type;
	}
	
	public boolean isAssignableFrom(ResolveType r)
	{
		return type == r.type;
	}
}
