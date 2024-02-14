package fr.innog.commands;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class CommandPenalty extends ExtendedCommandBase {

	public CommandPenalty(String commandName) {
		super(commandName);
	}
	
	@Override
	protected void buildCommand() {
		
		
		try {
			addCommandArgument(new CommandStructure("Ajouter une amende").addPredefinedArg("add").addArg("[<Pseudo cible>]", String.class, getPlayerTabulation()).addArg("[<Pseudo expéditeur>]", String.class, getPlayerTabulation()).addArg("[<Prix>]", int.class).addArg("[<Tresorerie>]", String.class).addArg("[<Raison>]", String.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					EntityPlayer targetPlayer = sender.getEntityWorld().getPlayerEntityByName(value[1].toString());
					EntityPlayer expeditorPlayer = sender.getEntityWorld().getPlayerEntityByName(value[2].toString());
					
					 if(targetPlayer == null)
					 {
						 MinecraftUtils.sendMessage(sender,"[Penalty] Utilisateur cible introuvable");
						 return;
					 }
					 
					 if(expeditorPlayer == null)
					 {
						 MinecraftUtils.sendMessage(sender, "[Penalty] Utilisateur expéditeur introuvable");
						 return;
					 }
					 
					 if(!ServerUtils.canPutPenalty(expeditorPlayer))
					 {
						 MinecraftUtils.sendMessage(expeditorPlayer, "§cVous n'avez pas le grade nécessaire pour mettre des amendes");
						 return;
					 }
					 
					 int price = (int)value[3];
					 String treasury = value[4].toString();
					 if(value[4].toString().equals("Aucun"))
					 {
						 treasury = null;
					 }
					 String reason = value[5].toString().replace("_", " ");

					 IPlayer targetData = MinecraftUtils.getPlayerCapability(targetPlayer);
					 targetData.getPenaltyManager().addPenalty(price, reason,treasury);
					 System.out.println("Penalty added to " + targetPlayer.getName());
				}

			}));
			
			addCommandArgument(new CommandStructure("Supprimer une amende").addPredefinedArg("remove").addArg("[<Pseudo>]", String.class, getPlayerTabulation()).addArg("[<Id>]", String.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					EntityPlayer targetPlayer = sender.getEntityWorld().getPlayerEntityByName(value[1].toString());
					
					 
					 if(targetPlayer == null)
					 {
						 MinecraftUtils.sendMessage(sender, "[Penalty] Utilisateur introuvable");
						 return;
					 }
					 

					 
					 String id = value[2].toString();

					 IPlayer targetData = MinecraftUtils.getPlayerCapability(targetPlayer);
					 targetData.getPenaltyManager().removePenalty(id);
					 System.out.println("Penalty removed for " + targetPlayer.getName());
				}

			}));
			
			addCommandArgument(new CommandStructure("Ouvrir interface à un joueur").addPredefinedArg("gui").addArg("[<Pseudo>]", String.class, getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					EntityPlayer targetPlayer = sender.getEntityWorld().getPlayerEntityByName(value[1].toString());
					
					 
					 if(targetPlayer == null)
					 {
						 MinecraftUtils.sendMessage(sender, "[Penalty] Utilisateur introuvable");
						 return;
					 }
					 
					 ModControllers.uiController.displayUI(targetPlayer, 7);
				}

			}));
			
			
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
