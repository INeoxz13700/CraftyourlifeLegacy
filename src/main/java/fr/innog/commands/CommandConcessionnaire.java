package fr.innog.commands;

import java.util.ArrayList;
import java.util.List;

import fr.dynamx.common.items.vehicle.ItemCar;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.data.Concessionnaire;
import fr.innog.utils.structural.Tuple;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandConcessionnaire extends ExtendedCommandBase {

	public CommandConcessionnaire(String commandName) {
		super(commandName);
	}
	
	@Override
	protected void buildCommand() {
		
		ITabulationCompletion concessionnaireNameTabCompletion = new ITabulationCompletion()
		{

			@Override
			public List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args,
					BlockPos targetPos) {

				List<String> concessionnairesName = new ArrayList<>();
				
				String currentArg = args[args.length-1].toLowerCase();
				
				for(Concessionnaire concessionnaire : ModControllers.concessionnaireController.getConcessionnaireList())
				{
					if(concessionnaire.getName().toLowerCase().startsWith(currentArg))
					{
						concessionnairesName.add(concessionnaire.getName().replace(" ", "_"));
					}
				}
				
				return concessionnairesName;
			}
			
		};
		
		try {
			addCommandArgument(new CommandStructure("Affiche la liste des concessionnaires").addPredefinedArg("list").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					sender.sendMessage(new TextComponentString("§cListe des concessionnaires :"));
					sender.sendMessage(new TextComponentString(""));
					
					if(ModControllers.concessionnaireController.getConcessionnaireList().size() == 0)
					{
						sender.sendMessage(new TextComponentString("§cIl n'y a pas encore de concessionnaire défini."));
					}
					else
					{
						for(Concessionnaire concessionnaire : ModControllers.concessionnaireController.getConcessionnaireList())
						{
							sender.sendMessage(new TextComponentString(" §b- " + concessionnaire.getName()));
						}
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Créer un concessionnaire").addPredefinedArg("create").addArg("[<nom>]", String.class, concessionnaireNameTabCompletion).setCommandCallback(new ICommandCallback()
					
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String c_name = value[1].toString().replaceAll("_", " ");

					Tuple<Boolean, String> result = ModControllers.concessionnaireController.createConcessionnaire(c_name);
				
					if(!result.getItem1())
					{
						sender.sendMessage(new TextComponentString("§c" + result.getItem2()));
					}
					else
					{
						sender.sendMessage(new TextComponentString("§aLe concessionnaire §b" + c_name + "§a a été crée avec succès"));
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Ajouter un véhicule au concessionnaire").addPredefinedArg("add").addArg("[<nom>]", String.class, concessionnaireNameTabCompletion).addArg("[<Id véhicule>]", String.class).addArg("[<Prix>]", double.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{					
					String c_name = value[1].toString().replace("_", " ");
					String vehicleId = value[2].toString();
					double price = (double)value[3];
					
					ItemCar vehicleItem = null;
					if(vehicleId.contains(":"))
					{
						String[] splittedVehicleId = vehicleId.split(":");
						Item item = Item.getByNameOrId(splittedVehicleId[0]);
						
						if(!(item instanceof ItemCar))
						{
							sender.sendMessage(new TextComponentString("§cL'item id saisi n'existe pas ou alors n'est pas un véhicule."));
							return;
						}
						vehicleItem = (ItemCar) item;
						
						if(Integer.parseInt(splittedVehicleId[1]) > vehicleItem.getMaxMeta()-1)
						{
							sender.sendMessage(new TextComponentString("§cLe véhicule avec le metadata " + splittedVehicleId[1] + " nexiste pas!"));
							return;
						}
						
						if(splittedVehicleId[1].equals("0"))
						{
							vehicleId = splittedVehicleId[0];
						}
					}
					else
					{
						Item item = Item.getByNameOrId(vehicleId);
						
						if(!(item instanceof ItemCar))
						{
							sender.sendMessage(new TextComponentString("§cL'item id saisi n'existe pas ou alors n'est pas un véhicule."));
							return;
						}

						vehicleItem = (ItemCar) item;
					}
				
				
					Tuple<Boolean, String> result = ModControllers.concessionnaireController.addVehicleToConcessionnaire(c_name, vehicleId, price, sender);
				
					if(!result.getItem1())
					{
						sender.sendMessage(new TextComponentString("§c" + result.getItem2()));
					}
					else
					{
						sender.sendMessage(new TextComponentString("§aLe véhicule avec l'id §b" + vehicleId + " §avient d'être ajouté dans le concessionnaire §b" + c_name));
					}
					
				}
			}));
			
			addCommandArgument(new CommandStructure("Vider les données d'un concessionnaire").addPredefinedArg("clear").addArg("[<nom>]", String.class, concessionnaireNameTabCompletion).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String c_name = value[1].toString().replace("_", " ");

					Tuple<Boolean, String>  result = ModControllers.concessionnaireController.clearConcessionnaire(c_name, sender);
					
					if(!result.getItem1())
					{
						sender.sendMessage(new TextComponentString("§c" + result.getItem2()));
					}
					else
					{
						sender.sendMessage(new TextComponentString("§a" + result.getItem2()));
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Retirer d'un concessionnaire la vente d'un véhicule").addPredefinedArg("remove").addArg("[<nom>]", String.class,concessionnaireNameTabCompletion).addArg("[<id>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String c_name = value[1].toString().replace("_", " ");
					int id = (int)value[2];
					
					Tuple<Boolean, String>  result = ModControllers.concessionnaireController.removeVehicleFromConcessionnaire(c_name, id, sender);
				
					if(!result.getItem1())
					{
						sender.sendMessage(new TextComponentString("§c" + result.getItem2()));
					}
					else
					{
						sender.sendMessage(new TextComponentString("§a" + result.getItem2()));
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Supprimer un concessionnaire").addPredefinedArg("remove").addArg("[<nom>]", String.class, concessionnaireNameTabCompletion).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String c_name = value[1].toString().replace("_", " ");

					Tuple<Boolean, String> result = ModControllers.concessionnaireController.removeConcessionnaire(c_name, sender);
				
					if(!result.getItem1())
					{
						sender.sendMessage(new TextComponentString("§c" + result.getItem2()));
					}
					else
					{
						sender.sendMessage(new TextComponentString("§a" + result.getItem2()));
					}
				}
			}));
			
			addCommandArgument(new CommandStructure("Afficher la liste des véhicules en vente dans un concessionnaire.").addPredefinedArg("vehicles").addArg("[<nom>]", String.class, concessionnaireNameTabCompletion).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String c_name = value[1].toString().replace("_", " ");

					ModControllers.concessionnaireController.displayVehiclesInConcessionnaire(sender, c_name);
				}
			}));
			
			addCommandArgument(new CommandStructure("Ouvrir l'inventaire du concessionnaire à un joueur").addPredefinedArg("open").addArg("[<nom>]", String.class, concessionnaireNameTabCompletion).addArg("[<joueur>]", String.class,new ITabulationCompletion()
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
				
			}).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					ModCore.debug("Une commande pour ouvrir l'ui concessionnaire a été éxécuté");

					String c_name = value[1].toString().replace("_", " ");

					if(!ModControllers.concessionnaireController.concessionnaireNameExist(c_name))
					{
						sender.sendMessage(new TextComponentString("§cCe concessionnaire n'existe pas!"));
						return;
					}
					
					EntityPlayer targetPlayer = sender.getEntityWorld().getPlayerEntityByName(value[2].toString());
					
					ModControllers.concessionnaireController.displayConcessionnaireUI(targetPlayer, c_name);
				}
			}));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
