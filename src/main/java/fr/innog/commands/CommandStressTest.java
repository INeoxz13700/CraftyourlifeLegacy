package fr.innog.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandStressTest extends ExtendedCommandBase {

	public CommandStressTest(String commandName) {
		super(commandName);
	}
	
	@Override
	protected void buildCommand() {
		
		
		/*try {
			addCommandArgument(new CommandStructure("Stress test").addPredefinedArg("spawn").addArg("[<count>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					int count = (int)value[1];
					for(int i = 0; i <count; i++)
					{
						StressTestHandler.addPlayer(true);
					}
				}
			}));
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	@Override
	public List<String> getAliases() 
	{
		return Arrays.asList(new String[] {"cm"});
	}
	
	
}
