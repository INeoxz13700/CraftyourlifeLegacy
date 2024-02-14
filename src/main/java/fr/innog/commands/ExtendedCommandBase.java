package fr.innog.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.server.permission.PermissionAPI;

public abstract class ExtendedCommandBase extends CommandBase {

	private String commandName;
	
	private List<CommandStructure> commandArgs = new ArrayList<CommandStructure>();
	
	public ExtendedCommandBase(String commandName)
	{
		this.commandName = commandName;
		
		buildCommand();
		
		try {
			addCommandArgument(new CommandStructure("Afficher la liste des commandes").addPredefinedArg("help").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server, ICommandSender sender, Object... args) {
					displayHelp(sender);
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected abstract void buildCommand();
	
	public ExtendedCommandBase addCommandArgument(CommandStructure commandStruct)
	{
		commandArgs.add(commandStruct);
		return this;
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
	    if (sender instanceof EntityPlayer) return PermissionAPI.hasPermission((EntityPlayer) sender, ModCore.MODID + ".command." + getName().toLowerCase());
	    return true;
	}
	
	@Override
	public String getName() {
		return commandName;
	}	

	@Override
	public String getUsage(ICommandSender sender) {
		return "§cCorrect usage : /" + getName() + " help";
	}
	
	private void displayHelp(ICommandSender sender)
	{
	    sender.sendMessage(new TextComponentString("§aListe des commandes : ")); 
	    sender.sendMessage(new TextComponentString(""));

	    for(CommandStructure struct : this.commandArgs)
	    {
	    	String args = "";
	    	for(int i = 0; i < struct.getArgCount(); i++)
		    {
	    		ArgumentInfo info = struct.getArgInfo(i);
	    		
	    		args += info.getArgumentName() + " ";
		    }
	    	args += " §f- §e" + struct.getDescription();
	    	
		    sender.sendMessage(new TextComponentString("§6/" + this.getName() + " " + args));
	    }
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		CommandStructure correctStructure = null;
		Object[] values = null;

		for(int i = 0; i < this.commandArgs.size(); i++)
		{
			CommandStructure structure = commandArgs.get(i);

			if(args.length == structure.getArgCount())
			{
				values = new Object[structure.getArgCount()];
				boolean flag = true;

				//ModCore.log("");
				for(int j = 0; j < structure.getArgCount(); j++)
				{
					ArgumentInfo argInfo = structure.getArgInfo(j);
					
					//ModCore.log("pour arg " + argInfo.getArgumentName());
					
					if(argInfo.isPredefinedArgument() && !argInfo.getArgumentName().equalsIgnoreCase(args[j]))
					{
						//ModCore.log("passe ici");
						flag = false;
						break;
					}
					else if(!argInfo.isPredefinedArgument())
					{
						//ModCore.log("passe ici 2");

						Object parsedValue = argInfo.parse(args[j]);
						if(parsedValue == null)
						{
							//ModCore.log("passe ici 3");

							flag = false;
							break;
						}

						values[j] = parsedValue;
					}
					else
					{
						//ModCore.log("passe ici 4");

						values[j] = args[j];
					}

				}
				
				if(flag)
			    {
					correctStructure = structure;
					break;
			    }
			}
		}
		
		//Commande trouvé
		if(correctStructure != null)
	    {
			correctStructure.getCommandCallback().execute(server, sender, values);
			return;
	    }
		
	    sender.sendMessage(new TextComponentString(getUsage(sender))); 
	}
	
	
	@Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
		List<String> possibleArgs = new ArrayList<>();
		if(args.length == 1)
		{
			String arg = args[0].toLowerCase();
			for(int i = 0; i < commandArgs.size(); i++)
			{
				CommandStructure commandStruct = commandArgs.get(i);
				ArgumentInfo info = commandStruct.getArgInfo(0);
				if(info.isPredefinedArgument())
				{ 
					if(info.getArgumentName().toLowerCase().startsWith(arg))
					{
						if(!possibleArgs.contains(info.getArgumentName())) possibleArgs.add(info.getArgumentName());
					}
				}
			}
			return possibleArgs;
		}
		
		if(args.length > 1)
		{			
			for(CommandStructure cmdStruct : this.commandArgs)
			{
				if(args.length > cmdStruct.getArgCount())
				{
					continue;
				}
				
				
				for(int i = 0; i < args.length; i++)
				{
					ArgumentInfo info = cmdStruct.getArgInfo(i);
					
					if(info.isPredefinedArgument())
					{
						if(!info.getArgumentName().equals(args[i]))
						{
							break;
						}
					}
					else
					{
						if(info.parse(args[i]) == null)
						{
							break;
						}
						else
						{
							if(i == args.length-1 && info.hasTabulationCompletion())
							{
								return info.getTabulationCompletionCallback().getTabCompletion(server, sender, args, targetPos);
							}
						}
					}
				}
			}

		
		}
		
		
		return Collections.emptyList();
    }
	
	public ITabulationCompletion getPlayerTabulation()
	{
		return  new ITabulationCompletion()
		{

			@Override
			public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args,
					BlockPos targetPos) {

				String currentArg = args[args.length-1].toLowerCase();
				List<String> possibleArgs = new ArrayList<String>();
				for(String playerName : server.getOnlinePlayerNames())
				{
					if(playerName.toLowerCase().startsWith(currentArg))
					{
						possibleArgs.add(playerName);
					}
				}
				return possibleArgs;
			}
			
		};
	}


	

	
	
}
