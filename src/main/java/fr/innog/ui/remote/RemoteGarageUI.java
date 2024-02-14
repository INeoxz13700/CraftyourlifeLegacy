package fr.innog.ui.remote;

import java.util.HashMap;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.client.ui.ingame.GarageUI;
import fr.innog.common.ModControllers;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.data.VehicleDatas;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.RemoteMethod;
import fr.innog.ui.remote.data.RemoteMethodCallback.ActionResult;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class RemoteGarageUI extends RemoteUIProcessor {
	

	public RemoteGarageUI(EntityPlayer player) {
		super(player);
	}
	
	@Override
	public void initRemoteUI(CacheData cachedData)
	{
		super.initRemoteUI(cachedData);
		
		IPlayer playerData = player.getCapability(PlayerProvider.PLAYER_CAP, null);
		
		VehicleDatas vehicleDatas = playerData.getVehicleDatas();
		
		cachedData.setCached("VCoins", new SyncStruct<Integer>(vehicleDatas.getVcoins()));
		cachedData.setCached("UnlockedSlot", new SyncStruct<Integer>(vehicleDatas.getUnlockedSlot()));
		cachedData.setCached("Vehicles", new SyncStruct<HashMap<String, List<VehicleStack>>>(vehicleDatas.getVehicles()));
	}
	
	public void displayMessage(String message)
	{
		if(player.world.isRemote)
		{
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.currentScreen instanceof GarageUI)
			{
				GarageUI gui = (GarageUI) Minecraft.getMinecraft().currentScreen;
				gui.displayMessage(message);
			}
		}
		else
		{
			executeMethod("displayMessage", new Object[] { message });
		}
	}
	
	
	public int getSlotPrice()
	{
		int unlockedSlot = 0;
		if(player.world.isRemote)
		{
			unlockedSlot = (Integer) this.cachedData.getData("UnlockedSlot").getData();
		}
		else
		{
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			unlockedSlot = playerData.getVehicleDatas().getUnlockedSlot();
		}
		
		return VehicleDatas.slotPriceMultiplier * unlockedSlot;
	}
	
	
	@RemoteMethod
	public ActionResult extendGarage()
	{
		if(!player.world.isRemote)
		{
			return ModControllers.garageController.extendGarage(this, player);
		}
		
		return ActionResult.FAILURE;
	}
	
	@RemoteMethod
	public ActionResult transferToInventory(String itemVehicleType, Integer vehicleIndex)
	{
		if(!player.world.isRemote)
		{
			return ModControllers.garageController.transferToInventory(this,itemVehicleType, vehicleIndex, player);
		}
		return ActionResult.FAILURE;
	}
	
	@RemoteMethod
	public void spawnVehicle(String itemVehicleType, Integer vehicleIndex)
	{
		if(!player.world.isRemote)
		{
			ModControllers.garageController.spawnVehicle(itemVehicleType, vehicleIndex, player);
		}
	}
	
}
