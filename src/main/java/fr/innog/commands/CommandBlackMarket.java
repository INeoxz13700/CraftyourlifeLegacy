package fr.innog.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.MarketItem;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandBlackMarket  extends ExtendedCommandBase {

	public CommandBlackMarket(String commandName) {
		super(commandName);
	}

	@Override
	protected void buildCommand() {
		try {
			addCommandArgument(new CommandStructure("Ajouter un item au black market").addPredefinedArg("add").addArg("[<item id>]", String.class).addArg("[<price unit>]", double.class).addArg("[<probability>]", int.class).addArg("[<display name>]", String.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					if(sender instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) sender;
						WorldDataManager data = WorldDataManager.get(player.world);

						String id = value[1].toString();
						double priceInEuro = (double) value[2];
						int probability = (int) value[3];
						String displayName = value[4].toString();
						if(data.getPhoneAppData().addItemToBlackMarket(player.world,id,priceInEuro, probability, displayName)) MinecraftUtils.sendMessage(sender,"§aItem successfully added !");
    					else MinecraftUtils.sendMessage(player,"§aItem already exist !");	
					}
					
					
					
				}
			}));
			
			addCommandArgument(new CommandStructure("Ajouter un item au black market").addPredefinedArg("add").addArg("[<item id>]", String.class).addArg("[<price unit>]", double.class).addArg("[<probability>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					if(sender instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) sender;
						WorldDataManager data = WorldDataManager.get(player.world);

						String id = value[1].toString();
						double priceInEuro = (double) value[2];
						int probability = (int) value[3];
						if(data.getPhoneAppData().addItemToBlackMarket(player.world,id,priceInEuro, probability)) MinecraftUtils.sendMessage(sender,"§aItem successfully added !");
    					else MinecraftUtils.sendMessage(player,"§aItem already exist !");	
					
					}
					
					
					
				}
			}));
			
			addCommandArgument(new CommandStructure("Retirer un item du black market").addPredefinedArg("remove").addArg("[<item id>]", String.class).addArg("[<probability>]", int.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
					if(sender instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) sender;
						WorldDataManager data = WorldDataManager.get(player.world);

						String id = value[1].toString();
						int probability = (int) value[2];
    					if(data.getPhoneAppData().removeItemFromBlackMarket(player.world,id, probability)) MinecraftUtils.sendMessage(player, "§aItem successfully removed !");
    					else MinecraftUtils.sendMessage(player, "§aItem not found !");
					}
					
					
					
				}
			}));
			
			addCommandArgument(new CommandStructure("Donner les objets commandés au marché noir au joueur").addPredefinedArg("give").addPredefinedArg("items").addArg("[<pseudo>]", String.class, this.getPlayerTabulation()).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					
						String username = value[2].toString();
						
						EntityPlayer target = sender.getEntityWorld().getPlayerEntityByName(username);
						
						if(target == null)
						{
	    					MinecraftUtils.sendMessage(sender, "§aPlayer not found !");
	    					return;
						}
						
						IPlayer targetData = MinecraftUtils.getPlayerCapability(target);
						List<ItemStack> itemsToRemove = new ArrayList<>();
						
						if(targetData.getPhoneData().itemStockage.isEmpty())
						{
							MinecraftUtils.sendMessage(target,"§cStockage vide");
							return;
						}
						
						for(ItemStack is : targetData.getPhoneData().itemStockage)
						{
							if(!target.inventory.addItemStackToInventory(is))
							{
								if(target.inventory.getFirstEmptyStack() >= 0)
								{
									itemsToRemove.add(is);
								}
								else
								{
									MinecraftUtils.sendMessage(target, "§cVotre inventaire est plein");
									
									return;
								}
							}
							else 
							{
								itemsToRemove.add(is);
							}
						}
						targetData.getPhoneData().itemStockage.removeAll(itemsToRemove);
						
						MinecraftUtils.sendMessage(target, "§aStockage récupéré en entier");
				}
			}));
			
			addCommandArgument(new CommandStructure("Liste des objets enregistré sur le marché noir").addPredefinedArg("list").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					MinecraftUtils.sendMessage(sender,"§aRegistered Items :");
					MinecraftUtils.sendMessage(sender,"");
					
					WorldDataManager data = WorldDataManager.get(sender.getEntityWorld());

										
					HashMap<MarketItem, Integer> items = data.getPhoneAppData().getItemProbability().getRegisteredItems();
				   
					Iterator<Map.Entry<MarketItem, Integer>> it = items.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry<MarketItem, Integer> pair = (Map.Entry<MarketItem, Integer>)it.next();
				        
				        MarketItem marketItem = (MarketItem) pair.getKey();
				        int probability = (int)pair.getValue();
    					MinecraftUtils.sendMessage(sender,("- " + marketItem.getItem().getDisplayName() + " " + marketItem.getPriceInEuro() + " euro " + probability + "%"));
				    }
				}
			}));
			
			addCommandArgument(new CommandStructure("Mise à jour du marché noir").addPredefinedArg("update").setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					WorldDataManager data = WorldDataManager.get(sender.getEntityWorld());
					
					data.getPhoneAppData().updateMarket(data.getWorldObj());
					
  					MinecraftUtils.sendMessage(sender, "Market updated");
				}
			}));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
	    return true;
	}

}
