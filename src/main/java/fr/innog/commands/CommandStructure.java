package fr.innog.commands;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class CommandStructure {

	private List<ArgumentInfo> argsList = new ArrayList<ArgumentInfo>();
	
	private ICommandCallback commandCallback;
	
	private String description;

	
	public CommandStructure(String commandDescription)
	{
		this.description = commandDescription;
	}
	
	
	
	public CommandStructure addPredefinedArg(String argName) throws Exception
	{
		argsList.add(new ArgumentInfo(argName));
		return this;
	}
	
	public CommandStructure addArg(String argName, Class<?> argValueType) throws Exception
	{
		return addArg(argName, argValueType, null);
	}
	
	public CommandStructure addArg(String argName, Class<?> argValueType, @Nullable ITabulationCompletion tabCompletionCallback) throws Exception
	{
		if(!argValueType.isPrimitive() && argValueType != String.class)
		{
			throw new Exception("Only primitive value or String are allowed");
		}
		
		
		ArgumentInfo argumentInfo = new ArgumentInfo(argName, argValueType);
		
		if(tabCompletionCallback != null)
		{
			argumentInfo.addTabulationCompletions(tabCompletionCallback);
		}
		
		argsList.add(argumentInfo);
		
		return this;
	}
	
	
	public CommandStructure setCommandCallback(ICommandCallback callback)
	{
		this.commandCallback = callback;
		return this;
	}
	
	public int getArgCount()
	{
		return argsList.size();
	}
	
	public ArgumentInfo getArgInfo(int index)
	{
		return argsList.get(index);
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public ICommandCallback getCommandCallback()
	{
		return commandCallback;
	}
}
