package fr.innog.commands;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.common.ModCore;
import fr.innog.ui.remote.RemoteGarageUI;
import fr.innog.ui.remote.data.RemoteUICache;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandVehicle extends ExtendedCommandBase {

	public CommandVehicle(String commandName) {
		super(commandName);
	}

	@Override
	protected void buildCommand() {
		try {
			addCommandArgument(new CommandStructure("Permet de s'ajouter des vcoins").addPredefinedArg("vcoins").addPredefinedArg("add").addArg("[<count>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server, ICommandSender sender, Object... value) 
				{
					if(sender instanceof EntityPlayer)
					{
						int vcoins = (int) value[2];
						EntityPlayer player = (EntityPlayer) sender;
						IPlayer Iplayer = player.getCapability(PlayerProvider.PLAYER_CAP, null);
						Iplayer.getVehicleDatas().addVcoins(vcoins);
						MinecraftUtils.sendMessage(sender,"§aVous venez de vous ajoutez §b" + vcoins + "§a vcoins!");
						RemoteUICache.setDirtyForPlayer(player, RemoteGarageUI.class.getSimpleName(), "VCoins");
					}
					else
					{
						MinecraftUtils.sendMessage(sender, "§cCette commande peut seulement être utilisé en jeu");
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Permet de se retirer des vcoins").addPredefinedArg("vcoins").addPredefinedArg("remove").addArg("[<count>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(sender instanceof EntityPlayer)
					{
						int vcoins = (int) value[2];
						EntityPlayer player = (EntityPlayer) sender;
						IPlayer Iplayer = player.getCapability(PlayerProvider.PLAYER_CAP, null);
						Iplayer.getVehicleDatas().removeVcoins(vcoins);
						MinecraftUtils.sendMessage(sender,"§aVous venez de vous enlever §b" + vcoins + " §a vcoins!");
						RemoteUICache.setDirtyForPlayer(player, RemoteGarageUI.class.getSimpleName(), "VCoins");
					}
					else
					{
						MinecraftUtils.sendMessage(sender, "§cCette commande peut seulement être utilisé en jeu");
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Permet d'ajouter des vcoins à un joueur").addPredefinedArg("vcoins").addPredefinedArg("add").addArg("[<username>]", String.class).addArg("[<count>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String username = value[2].toString();
					int vcoins = (int) value[3];
						
					EntityPlayerMP targetPlayer = server.getPlayerList().getPlayerByUsername(username);
						
					if(targetPlayer == null)
					{
						MinecraftUtils.sendMessage(sender,"§cCe joueur n'existe pas.");
						return;
					}
						
						
					IPlayer Iplayer = targetPlayer.getCapability(PlayerProvider.PLAYER_CAP, null);
					Iplayer.getVehicleDatas().addVcoins(vcoins);
					MinecraftUtils.sendMessage(sender, "§aVous venez d'ajouter §b" + vcoins + " §aà §6" + username);
					RemoteUICache.setDirtyForPlayer(targetPlayer, RemoteGarageUI.class.getSimpleName(), "VCoins");
	
				}
			}));
			
			addCommandArgument(new CommandStructure("Permet d'enlever des vcoins à un joueur").addPredefinedArg("vcoins").addPredefinedArg("remove").addArg("[<username>]", String.class).addArg("[<count>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(sender instanceof EntityPlayer)
					{
						String username = value[2].toString();
						int vcoins = (int) value[3];
						
						EntityPlayerMP targetPlayer = server.getPlayerList().getPlayerByUsername(username);
						
						if(targetPlayer == null)
						{
							MinecraftUtils.sendMessage(sender,"§cCe joueur n'existe pas.");
							return;
						}
						
						
						IPlayer Iplayer = targetPlayer.getCapability(PlayerProvider.PLAYER_CAP, null);
						Iplayer.getVehicleDatas().removeVcoins(vcoins);
						MinecraftUtils.sendMessage(sender, "§aVous venez d'enlever §b" + vcoins + " §aà §6" + username);
						RemoteUICache.setDirtyForPlayer(targetPlayer, RemoteGarageUI.class.getSimpleName(), "VCoins");
					}
					else
					{
						MinecraftUtils.sendMessage(sender, "§cCette commande peut seulement être utilisé en jeu");
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Permet de définir les vcoins d'un joueur").addPredefinedArg("vcoins").addPredefinedArg("set").addArg("[<username>]", String.class).addArg("[<count>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(sender instanceof EntityPlayer)
					{
						String username = value[2].toString();
						int vcoins = (int) value[3];
						
						EntityPlayerMP targetPlayer = server.getPlayerList().getPlayerByUsername(username);
						
						if(targetPlayer == null)
						{
							MinecraftUtils.sendMessage(sender,"§cCe joueur n'existe pas.");
							return;
						}
						
						
						IPlayer Iplayer = targetPlayer.getCapability(PlayerProvider.PLAYER_CAP, null);
						Iplayer.getVehicleDatas().setVcoins(vcoins);
						MinecraftUtils.sendMessage(sender, "§aVous venez de definir les vcoins de §6" + username + " §aà §b" + vcoins);
						RemoteUICache.setDirtyForPlayer(targetPlayer, RemoteGarageUI.class.getSimpleName(), "VCoins");
					}
					else
					{
						MinecraftUtils.sendMessage(sender, "§cCette commande peut seulement être utilisé en jeu");
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Permet de voir les vcoins d'un joueur").addPredefinedArg("vcoins").addArg("[<username>]", String.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(sender instanceof EntityPlayer)
					{
						String username = value[1].toString();
						
						EntityPlayerMP targetPlayer = server.getPlayerList().getPlayerByUsername(username);
						
						if(targetPlayer == null)
						{
							MinecraftUtils.sendMessage(sender,"§cCe joueur n'existe pas.");
							return;
						}
						
						
						IPlayer Iplayer = targetPlayer.getCapability(PlayerProvider.PLAYER_CAP, null);

						MinecraftUtils.sendMessage(sender, "§6" + username + "§a a §b" + Iplayer.getVehicleDatas().getVcoins() + "§a vcoins.");
					}
					else
					{
						MinecraftUtils.sendMessage(sender, "§cCette commande peut seulement être utilisé en jeu");
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("test").addPredefinedArg("test").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(sender instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) sender;
						for(int x = (int)player.posX; x < 10; x++)
						{
							for(int z = (int) player.posZ; z < 10; z++)
							{
								player.world.setBlockState(new BlockPos(x,(int)player.posY+1, z), Blocks.BEDROCK.getDefaultState());
							}
						}
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
