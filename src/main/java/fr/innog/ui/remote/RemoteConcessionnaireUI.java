package fr.innog.ui.remote;

import java.util.HashMap;

import fr.dynamx.common.items.DynamXItem;
import fr.innog.client.ui.ingame.ConcessionnaireUI;
import fr.innog.common.ModControllers;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.data.Concessionnaire;
import fr.innog.data.ConcessionnaireItem;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.RemoteMethod;
import fr.innog.ui.remote.data.SyncStruct;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public class RemoteConcessionnaireUI extends RemoteUIProcessor 
{
	private Concessionnaire concessionnaire;
	
	private String concessionnaireName;
		
	
	public RemoteConcessionnaireUI(EntityPlayer player, String concessionnaireName)
	{
		super(player);
		this.concessionnaire = ModControllers.concessionnaireController.getConcessionnaireFromName(concessionnaireName);
		this.concessionnaireName = concessionnaireName;
		this.player = player;
	}

	@Override
	public String getIdentifier()
	{
		return super.getIdentifier() + "_" + concessionnaireName;
	}
	
	public String getConcessionnaireName()
	{
		return this.concessionnaireName;
	}
	
	@Override
	public void initRemoteUI(CacheData cacheData)
	{
		super.initRemoteUI(cacheData);

		SyncStruct<HashMap<Integer, ConcessionnaireItem>> struct = new SyncStruct<>(concessionnaire.getVehicles());
		
		cacheData.setCached("ConcessionnaireItems", struct);
	}
	
	public VehicleStack getVehicleStackById(String vehicleId)
	{
		DynamXItem vehicleItem = null;
		byte itemMeta = 0;
				
		if(vehicleId.contains(":"))
		{
			String[] splittedVehicleId = vehicleId.split(":");
			Item item = Item.getByNameOrId(splittedVehicleId[0]);
			
			if(!(item instanceof DynamXItem))
			{
				return null;
			}
			vehicleItem = (DynamXItem) item;
			
			itemMeta = Byte.valueOf(splittedVehicleId[1]);
			
			if(itemMeta > vehicleItem.getMaxMeta()-1)
			{
				return null;
			}
			
			if(splittedVehicleId[1].equals("0"))
			{
				vehicleId = splittedVehicleId[0];
			}
		}
		else
		{
			Item item = Item.getByNameOrId(vehicleId);
			
			if(!(item instanceof DynamXItem))
			{
				return null;
			}

			vehicleItem = (DynamXItem) item;
		}
		
		
		return new VehicleStack(vehicleItem, itemMeta);
	}
	
	public Concessionnaire getConcessionnaire()
	{
		return concessionnaire;
	}
	
	@RemoteMethod
	public void buyVehicle(Integer id)
	{
		ModControllers.concessionnaireController.buyVehicle(this, player, id);
	}
	
	public void displayMessage(String message)
	{
		if(player.world.isRemote)
		{
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.currentScreen instanceof ConcessionnaireUI)
			{
				ConcessionnaireUI gui = (ConcessionnaireUI) Minecraft.getMinecraft().currentScreen;
				gui.displayMessage(message);
			}
		}
		else
		{
			executeMethod("displayMessage", new Object[] { message });
		}
	}
}
