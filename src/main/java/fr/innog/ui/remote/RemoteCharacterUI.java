package fr.innog.ui.remote;

import java.time.Year;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.ui.ingame.CharacterUI;
import fr.innog.ui.remote.data.RemoteMethod;
import fr.innog.ui.remote.data.RemoteMethodCallback.ActionResult;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RemoteCharacterUI extends RemoteUIProcessor {

	public static String[] startItem;

	public RemoteCharacterUI(EntityPlayer player) {
		super(player);
	}
	
	@RemoteMethod
	public ActionResult confirmIdentity(String lastName, String name, String birthday, String gender)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

		if(!playerData.getIdentityData().waitingDataFromClient)
		{
			displayMessage("Une erreur s'est produite.");
			return ActionResult.FAILURE;
		}
		
		
		String[] birthdaysplit = birthday.split("/");
				
		if(lastName.isEmpty() || name.isEmpty() || birthday.isEmpty() || gender.isEmpty())
		{
			displayMessage("Veuillez remplir tous les champs");
			return ActionResult.FAILURE;
		}
		else if(!gender.equalsIgnoreCase("Masculin") && !gender.equalsIgnoreCase("Feminin"))
		{
			displayMessage("Veuillez entrer un genre valide Ex : M (Pour masculin)");
			return ActionResult.FAILURE;
		}
		else if(Integer.parseInt(birthdaysplit[2]) > Year.now().getValue())
		{
			displayMessage("Votre date de naissance est fausse > " + Year.now().getValue());
			return ActionResult.FAILURE;
		}
		else if(Integer.parseInt(birthdaysplit[2]) < 1900)
		{
			displayMessage("Votre date de naissance est fausse");
			return ActionResult.FAILURE;
		}
		else if(Integer.parseInt(birthdaysplit[1]) < 1 || Integer.parseInt(birthdaysplit[1]) > 12)
		{
			displayMessage("Votre mois de naissance doit être compris entre 1 et 12.");
			return ActionResult.FAILURE;
		}
		else if(Integer.parseInt(birthdaysplit[0]) < 1 || Integer.parseInt(birthdaysplit[0]) > 31)
		{
			displayMessage("Votre jour de naissance doit être compris entre 1 et 31.");
			return ActionResult.FAILURE;
		}
		else
		{
			MinecraftUtils.dispatchConsoleCommand("apidata identity " + player.getName() + " " + name + " " + lastName);
			
			playerData.getIdentityData().setData(lastName, name, birthday, gender);
			
			if(playerData.getLoginInformation().isFirstConnection())
			{
				if(startItem != null)
				{
					for(String i : startItem)
					{
						String[] splitedData = i.split("-");
						int id = Integer.parseInt(splitedData[0]);
						int quantity = Integer.parseInt(splitedData[1]);
						ItemStack it = new ItemStack(Item.getItemById(id), quantity);
						player.inventory.addItemStackToInventory(it);
					}	
				}
				
				playerData.getLoginInformation().setFirstConnection(false);
			}
						
			return ActionResult.SUCCESS;
		}
	}
	
	public void displayMessage(String message)
	{
		if(player.world.isRemote)
		{
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.currentScreen instanceof CharacterUI)
			{
				CharacterUI gui = (CharacterUI) Minecraft.getMinecraft().currentScreen;
				gui.displayMessage(message);
			}
		}
		else
		{
			executeMethod("displayMessage", new Object[] { message });
		}
	}

}
