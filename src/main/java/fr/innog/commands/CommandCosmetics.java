package fr.innog.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.ui.remote.RemoteCosmeticUI;
import fr.innog.ui.remote.data.RemoteUICache;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandCosmetics extends ExtendedCommandBase {

	public CommandCosmetics(String commandName) {
		super(commandName);
	}
	
	@Override
	protected void buildCommand() {
		
		ITabulationCompletion cosmeticIdTabCompletion = new ITabulationCompletion()
		{

			@Override
			public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args,
					BlockPos targetPos) {

				List<String> cosmeticsId = new ArrayList<>();
				
				String currentArg = args[args.length-1].toLowerCase();
				

				for(CosmeticObject cosmetic : ModCore.getCosmeticsManager().getCosmetics())
				{
					if((cosmetic.getId() + "").startsWith(currentArg));
					{
						cosmeticsId.add(cosmetic.getId() + "");
					}
				}
				
				return cosmeticsId;
			}
			
		};
		
		try {
			addCommandArgument(new CommandStructure("Débloque un cosmétique").addPredefinedArg("unlock").addArg("[<pseudo>]", String.class, getPlayerTabulation()).addArg("[<id>]", int.class, cosmeticIdTabCompletion).setCommandCallback(new ICommandCallback()
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
    					CosmeticObject cosmetic = CosmeticObject.setCosmetiqueUnlocked(player, id);
        				MinecraftUtils.sendMessage(sender, "§aVous avez débloque le cosmetique §b" + cosmetic.getName() + "§a pour " + player.getName());
        				RemoteUICache.setDirtyForPlayer(player, RemoteCosmeticUI.class.getSimpleName(), "Cosmetics");
    				}
    				else
    				{
    					MinecraftUtils.sendMessage(sender,"§cUtilisateur introuvable!");
    				}
				}
			}));
			
			addCommandArgument(new CommandStructure("Retire un cosmétique").addPredefinedArg("lock").addArg("[<pseudo>]", String.class,getPlayerTabulation()).addArg("[<id>]", int.class, cosmeticIdTabCompletion).setCommandCallback(new ICommandCallback()
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
    					CosmeticObject cosmetic = CosmeticObject.setCosmetiqueLocked(player, id);
        				MinecraftUtils.sendMessage(sender, "§cVous avez bloqué le cosmetique §b" + cosmetic.getName() + "§c pour " + player.getName());
        				RemoteUICache.setDirtyForPlayer(player, RemoteCosmeticUI.class.getSimpleName(), "Cosmetics");
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
