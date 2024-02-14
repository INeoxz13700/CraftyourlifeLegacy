package fr.innog.commands;

import java.io.IOException;
import java.util.UUID;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.common.controllers.ReanimationController;
import fr.innog.common.world.WorldDataManager;
import fr.innog.utils.DataUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandApi extends ExtendedCommandBase {

	public CommandApi(String commandName) {
		super(commandName);
	}

	@Override
	protected void buildCommand() {
		try {
			addCommandArgument(new CommandStructure("Affiche le nombre de lits d'hôpital").addPredefinedArg("bed").addPredefinedArg("count").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					WorldDataManager worldData = WorldDataManager.get(sender.getEntityWorld());

    				MinecraftUtils.sendMessage(sender, "§dNombre de chambres : §4" + worldData.getHospitalData().getHospitalCount());
				}
			}));
			
			addCommandArgument(new CommandStructure("Supprime tous les lits d'hôpital").addPredefinedArg("bed").addPredefinedArg("clear").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					WorldDataManager worldData = WorldDataManager.get(sender.getEntityWorld());
    				
					worldData.getHospitalData().clearHospitalBeds(sender.getEntityWorld());
					
					MinecraftUtils.sendMessage(sender, "§aToutes les chambres ont été effacées.");
				}
			}));
			
			addCommandArgument(new CommandStructure("Ajouter un point d'incendie").addPredefinedArg("fire").addPredefinedArg("create").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(!(sender instanceof EntityPlayer)) return;
					
					EntityPlayer player = (EntityPlayer) sender;
					
					WorldDataManager worldData = WorldDataManager.get(sender.getEntityWorld());
    				
					if(worldData.getFireData().addFireRegion(server.getEntityWorld(), player.getPosition())) MinecraftUtils.sendMessage(sender, "§aPosition incendie ajouté.");
					else MinecraftUtils.sendMessage(sender, "§cPosition incendie supprimé.");
				}
			}));
			
			addCommandArgument(new CommandStructure("Supprimer un point d'incendie").addPredefinedArg("fire").addPredefinedArg("remove").addArg("[<id>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(!(sender instanceof EntityPlayer)) return;
					
					EntityPlayer player = (EntityPlayer) sender;
					
					int id = (int) value[2];
					
					WorldDataManager worldData = WorldDataManager.get(sender.getEntityWorld());
    				
					if(worldData.getFireData().removeFireRegion(server.getEntityWorld(), id)) MinecraftUtils.sendMessage(sender, "§aPosition incendie supprimé.");
					else MinecraftUtils.sendMessage(sender, "§cPosition incendie inexistant.");
				}
			}));
			
			addCommandArgument(new CommandStructure("Liste des points d'incendies").addPredefinedArg("fire").addPredefinedArg("list").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(!(sender instanceof EntityPlayer)) return;
					
										
					WorldDataManager worldData = WorldDataManager.get(sender.getEntityWorld());
    				
					MinecraftUtils.sendMessage(sender, "§aListe des points d'incendies : ");
					int index = 0;
					for(BlockPos pos : worldData.getFireData().getFires())
					{
						MinecraftUtils.sendMessage(sender, "§c- " + pos + " §BID : " + index);
						index++;
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Démarrer un feu").addPredefinedArg("fire").addPredefinedArg("start").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					if(!(sender instanceof EntityPlayer)) return;
					
					ModControllers.fireController.startFireFromCoordinates(sender.getEntityWorld(), sender.getPosition().getX(), sender.getPosition().getY(), sender.getPosition().getZ(), "Catastrophe naturelle", Integer.MAX_VALUE);
					
				}
			}));
			
			addCommandArgument(new CommandStructure("voir les bitcoins d'un joueur").addPredefinedArg("bitcoin").addArg("[<pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					String username = value[1].toString();
					
					EntityPlayer player = server.getPlayerList().getPlayerByUsername(username);
					IPlayer playerData = null;
					if(player == null)
					{
						try {
							UUID persistentId = DataUtils.getUUIDFromName(sender.getEntityWorld(), username);
							if(persistentId == null)
							{
								MinecraftUtils.sendMessage(sender, "§cJoueur inexistant.");
								return;
							}
							playerData = DataUtils.parsePlayerTag(DataUtils.getDataOfflinePlayer(persistentId));
							MinecraftUtils.sendMessage(sender, "§aBitcoin du joueur : §b" + playerData.getPhoneData().bitcoin + "§a BTC");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						playerData = MinecraftUtils.getPlayerCapability(player);
						MinecraftUtils.sendMessage(sender, "§aBitcoin du joueur : §b" + playerData.getPhoneData().bitcoin + "§a BTC");
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("ajouter bitcoin à un joueur").addPredefinedArg("bitcoin").addPredefinedArg("add").addArg("[<pseudo>]", String.class, getPlayerTabulation()).addArg("[<nombre>]", double.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					String username = value[2].toString();
					int count = (int)value[3];
					
					EntityPlayer player = server.getPlayerList().getPlayerByUsername(username);
					IPlayer playerData = null;
					if(player == null)
					{
						MinecraftUtils.sendMessage(sender, "§cJoueur non connecté.");
					}
					else
					{
						playerData = MinecraftUtils.getPlayerCapability(player);
						playerData.getPhoneData().bitcoin += count;
						MinecraftUtils.sendMessage(sender, "§aVous avez ajouté §b" + count + "§a BTC à §b" + player.getName());
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("retirer bitcoin à un joueur").addPredefinedArg("bitcoin").addPredefinedArg("remove").addArg("[<pseudo>]", String.class, getPlayerTabulation()).addArg("[<nombre>]", double.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					String username = value[2].toString();
					int count = (int)value[3];
					
					EntityPlayer player = server.getPlayerList().getPlayerByUsername(username);
					IPlayer playerData = null;
					if(player == null)
					{
						try {
							UUID persistentId = DataUtils.getUUIDFromName(sender.getEntityWorld(), username);
							if(persistentId == null)
							{
								MinecraftUtils.sendMessage(sender, "§cJoueur inexistant.");
								return;
							}
							
							NBTTagCompound playerNBT = DataUtils.getDataOfflinePlayer(persistentId);
							playerData = DataUtils.parsePlayerTag(playerNBT);
							playerData.getPhoneData().bitcoin -= count;
							
							DataUtils.writeDataOfflinePlayer(persistentId, playerNBT, playerData);
							
							MinecraftUtils.sendMessage(sender, "§aVous avez retiré §b" + count + "§a BTC à §b" + username);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						playerData = MinecraftUtils.getPlayerCapability(player);
						playerData.getPhoneData().bitcoin -= count;
						MinecraftUtils.sendMessage(sender, "§aVous avez retiré §b" + count + "§a BTC à §b" + player.getName());
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("réinitialise les bitcoins d'un joueur").addPredefinedArg("bitcoin").addPredefinedArg("reset").addArg("[<pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					String username = value[2].toString();
					
					EntityPlayer player = server.getPlayerList().getPlayerByUsername(username);
					IPlayer playerData = null;
					if(player == null)
					{
						try {
							UUID persistentId = DataUtils.getUUIDFromName(sender.getEntityWorld(), username);
							if(persistentId == null)
							{
								MinecraftUtils.sendMessage(sender, "§cJoueur inexistant.");
								return;
							}
							
							NBTTagCompound playerNBT = DataUtils.getDataOfflinePlayer(persistentId);
							playerData = DataUtils.parsePlayerTag(playerNBT);
							playerData.getPhoneData().bitcoin = 0;
							
							DataUtils.writeDataOfflinePlayer(persistentId, playerNBT, playerData);
							
							MinecraftUtils.sendMessage(sender, "§aVous avez réinitialisé les bitcoins de §b" + username);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else
					{
						playerData = MinecraftUtils.getPlayerCapability(player);
						playerData.getPhoneData().bitcoin = 0;
						MinecraftUtils.sendMessage(sender, "§aVous avez réinitialisé les bitcoins de §b" + player.getName());
					}
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
