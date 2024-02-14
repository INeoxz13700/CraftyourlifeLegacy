package fr.innog.commands;

import javax.annotation.Nullable;

import fr.innog.utils.structural.TypeUtils;

public class ArgumentInfo {

	private String argumentName;
	
	private boolean isPredefinedArg;
	
	private Class<?> argumentType;
	
	private ITabulationCompletion tabulationCompletionCallback;
	
	
	public ArgumentInfo(String argumentName, Class<?> argumentType)
	{
		this.argumentName = argumentName;
		this.argumentType = argumentType;
	}
	
	public ArgumentInfo(String argumentName)
	{
		this.argumentName = argumentName;
		isPredefinedArg = true;
	}
	
	public ArgumentInfo addTabulationCompletions(ITabulationCompletion callback)
	{
		this.tabulationCompletionCallback = callback;
		return this;
	}
	
	public boolean hasTabulationCompletion()
	{
		return this.tabulationCompletionCallback != null;
	}
	
	public boolean isPredefinedArgument()
	{
		return this.isPredefinedArg;
	}
	
	public Class<?> getArgumentType()
	{
		return argumentType;
	}
	
	public String getArgumentName()
	{
		return argumentName;
	}
	
	public ITabulationCompletion getTabulationCompletionCallback()
	{
		return tabulationCompletionCallback;
	}
	

	@Nullable
	public Object parse(String arg)
	{		
		if(String.class.isAssignableFrom(argumentType)) return arg;

	    if(boolean.class.isAssignableFrom(argumentType) || Boolean.class.isAssignableFrom(argumentType)) return TypeUtils.isBoolean(arg);
	    
	    if(byte.class.isAssignableFrom(argumentType) || Byte.class.isAssignableFrom(argumentType))
	    {
	    	try
	    	{
		    	return Byte.parseByte(arg);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		return null;
	    	}
	    }
	       		
	    if(short.class.isAssignableFrom(argumentType) || Short.class.isAssignableFrom(argumentType))
	    {
	    	try
	    	{
		    	return Short.parseShort(arg);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		return null;
	    	}	    
	    }
	    
	    if(int.class.isAssignableFrom(argumentType) || Integer.class.isAssignableFrom(argumentType)) 
	    {
	    	try
	    	{
		    	return Integer.parseInt(arg);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		return null;
	    	}	
	    }
	    
	    if(long.class.isAssignableFrom(argumentType) || Long.class.isAssignableFrom(argumentType)) 
	    {
	    	try
	    	{
	    		return Long.parseLong(arg);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		return null;
	    	}	
	    }
	    
	    if(float.class.isAssignableFrom(argumentType) || Float.class.isAssignableFrom(argumentType)) 
	    {
	    	try
	    	{
	    		return Float.parseFloat(arg);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		return null;
	    	}	
	    }
	    
	    if(double.class.isAssignableFrom(argumentType) || Double.class.isAssignableFrom(argumentType)) 
	    {
	    	try
	    	{
	    		return Double.parseDouble(arg);
	    	}
	    	catch(NumberFormatException e)
	    	{
	    		return null;
	    	}	
	    }
	    
	    
	    return null;
	}
	
}
