package fr.innog.commands;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandIdentity extends ExtendedCommandBase {

	public CommandIdentity(String commandName) {
		super(commandName);
	}

	@Override
	protected void buildCommand() {
		try {
			addCommandArgument(new CommandStructure("Lance le processus de création d'identité").addPredefinedArg("reset").addArg("[<pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					EntityPlayer victim = server.getEntityWorld().getPlayerEntityByName(value[1].toString());
    				if(victim != null)
    				{
    					IPlayer playerData = MinecraftUtils.getPlayerCapability(victim);
        				MinecraftUtils.sendMessage(sender, "§aVous avez réintialisé l'identité de §b " + victim.getName());
        				playerData.getIdentityData().lastname = "";
        				
        				EntityPlayerMP victimMP = (EntityPlayerMP) victim;
        				
        				victimMP.connection.disconnect(new TextComponentString("§cChangez de nom RP!"));
    				}
    				else
    				{
    					MinecraftUtils.sendMessage(sender,"§cUtilisateur introuvable!");
    				}
				}
			}));
			
			addCommandArgument(new CommandStructure("Lance le processus de création d'identité").addPredefinedArg("resetpnj").addArg("[<pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					EntityPlayer victim = server.getEntityWorld().getPlayerEntityByName(value[1].toString());
    				if(victim != null)
    				{
    					IPlayer playerData = MinecraftUtils.getPlayerCapability(victim);
    					
        				int elapsedTimeInSeconds = (int)((System.currentTimeMillis() - playerData.getIdentityData().lastIdentityResetTime) / 1000.0f);

    					if(elapsedTimeInSeconds >= 86400)
    					{
            				playerData.getIdentityData().lastname = "";
            				playerData.getIdentityData().lastIdentityResetTime = System.currentTimeMillis();
            				EntityPlayerMP victimMP = (EntityPlayerMP) victim;
            				
            				victimMP.connection.disconnect(new TextComponentString("§cChangez de nom RP!"));
    					}
    					else
    					{
        					int leftTime = 86400 - elapsedTimeInSeconds;
        					MinecraftUtils.sendMessage(victim, "§cVous pouvez de nouveau changer d'identité dans §e" + leftTime + " §eseconde(s)");
    					}    				
    				}
    				else
    				{
    					MinecraftUtils.sendMessage(sender,"§cUtilisateur introuvable!");
    				}
				}
			}));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
