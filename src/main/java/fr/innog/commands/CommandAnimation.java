package fr.innog.commands;

import java.util.Arrays;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.common.animations.PlayerAnimator;
import fr.innog.ui.remote.RemoteAnimationUI;
import fr.innog.ui.remote.data.RemoteUICache;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandAnimation extends ExtendedCommandBase {

	public CommandAnimation(String commandName) {
		super(commandName);
	}
	
	@Override
	protected void buildCommand() {
		
		
		try {
			addCommandArgument(new CommandStructure("Débloque une animation à un joueur").addPredefinedArg("unlock").addArg("[<pseudo>]", String.class, getPlayerTabulation()).addArg("[<id>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(value[1].toString());
    				if(player != null)
    				{
    					int id = 0;
    					try
    					{
    						id = (int)value[2];
    					}
    					catch(Exception e)
    					{
    						e.printStackTrace();
    						return;
    					}
    					
    					PlayerAnimator animator = ModCore.getAnimationManager().unlockAnimation(player, id);
    					if(animator != null)
    					{
            				MinecraftUtils.sendMessage(sender, "§aVous avez débloque l'animation §b" + animator.getAnimationId() + "§a pour " + player.getName());
            				RemoteUICache.setDirtyForPlayer(player, RemoteAnimationUI.class.getSimpleName(), "UnlockedAnimations");
    					}
    				}
    				else
    				{
    					MinecraftUtils.sendMessage(sender,"§cUtilisateur introuvable!");
    				}
				}
			}));
			
			addCommandArgument(new CommandStructure("Retire une animation premium à un joueur").addPredefinedArg("lock").addArg("[<pseudo>]", String.class, getPlayerTabulation()).addArg("[<id>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(value[1].toString());
    				if(player != null)
    				{
    					int id = 0;
    					try
    					{
    						id = (int)value[2];
    					}
    					catch(Exception e)
    					{
    						e.printStackTrace();
    						return;
    					}
    					
    					PlayerAnimator animator = ModCore.getAnimationManager().lockAnimation(player, id);
    					if(animator != null)
    					{
            				MinecraftUtils.sendMessage(sender, "§aVous avez bloqué l'animation §b" + animator.getAnimationId() + "§a pour " + player.getName());
            				RemoteUICache.setDirtyForPlayer(player, RemoteAnimationUI.class.getSimpleName(), "UnlockedAnimations");
    					}
    				}
    				else
    				{
    					MinecraftUtils.sendMessage(sender,"§cUtilisateur introuvable!");
    				}
				}
			}));
			
			addCommandArgument(new CommandStructure("Joue un animation à un joueur").addPredefinedArg("play").addArg("[<pseudo>]", String.class, getPlayerTabulation()).addArg("[<id>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(value[1].toString());
    				if(player != null)
    				{
    					int id = 0;
    					try
    					{
    						id = (int)value[2];
    					}
    					catch(Exception e)
    					{
    						e.printStackTrace();
    						return;
    					}
    					
    					ModCore.getAnimationManager().playAnimation(player, id);
            			MinecraftUtils.sendMessage(sender, "§aVous avez jouer l'animation §b" + id + "§a pour " + player.getName());
    					
    				}
    				else
    				{
    					MinecraftUtils.sendMessage(sender,"§cUtilisateur introuvable!");
    				}
				}
			}));
			
			addCommandArgument(new CommandStructure("Stop un animation pour un joueur").addPredefinedArg("stop").addArg("[<pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					EntityPlayer player = server.getEntityWorld().getPlayerEntityByName(value[1].toString());
    				if(player != null)
    				{
    					ModCore.getAnimationManager().stopAnimation(player);
            			MinecraftUtils.sendMessage(sender, "§aVous avez stop l'animation en cours pour " + player.getName());
    				}
    				else
    				{
    					MinecraftUtils.sendMessage(sender,"§cUtilisateur introuvable!");
    				}
				}
			}));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> getAliases() 
	{
		return Arrays.asList(new String[] {"cm"});
	}
	
	
}
