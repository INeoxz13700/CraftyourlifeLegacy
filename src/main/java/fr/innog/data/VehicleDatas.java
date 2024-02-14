package fr.innog.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import fr.dynamx.common.items.ItemModularEntity;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.utils.DataUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class VehicleDatas {

    private static final int startUnlockedSlot = 2;
    public static final int maxSlot = 15;
    public static final int slotPriceMultiplier = 12;

	public int vcoins;
		
	private HashMap<String, List<VehicleStack>> vehicles;

	private int unlockedSlot;
	
	public VehicleDatas()
	{
		setUnlockedSlot(startUnlockedSlot);
		vcoins = 0;
		vehicles = new HashMap<String, List<VehicleStack>>();
	}
	

	public void setVcoins(int count) {
		vcoins = count;
	}

	public int getVcoins() {
		return vcoins;
	}

	public void addVcoins(int count) {
		vcoins += count;
	}

	public void removeVcoins(int count) {
		vcoins -= count;
	}
	
	public HashMap<String, List<VehicleStack>> getVehicles() {
		return vehicles;
	}
	
	public List<VehicleStack> getVehicles(Class<? extends ItemModularEntity> vehicleType)
	{
		if(vehicles.containsKey(vehicleType.getSimpleName()))
		{
			return vehicles.get(vehicleType.getSimpleName());
		}
		return null;
	}


	public int getUnlockedSlot() {
		return unlockedSlot;
	}


	public void setUnlockedSlot(int unlockedSlot) {
		this.unlockedSlot = unlockedSlot;
	}
	
	public void addVehicle(VehicleStack stack, Class<? extends ItemModularEntity> vehicleType)
	{
		List<VehicleStack> vehiclesIs = new ArrayList<>();

		if(!vehicles.containsKey(vehicleType.getSimpleName()))
		{
			vehicles.put(vehicleType.getSimpleName(), vehiclesIs);
		}
		else
		{
			vehiclesIs = vehicles.get(vehicleType.getSimpleName());
		}
				
		
		vehiclesIs.add(stack);
	}
	
	public static void updatePlayerVehicleData(EntityPlayer player, VehicleStack stack)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		ItemModularEntity modularEntity = (ItemModularEntity)stack.getItem();
		String vehicleType = modularEntity.getClass().getSimpleName();
		if(playerData.getVehicleDatas().getVehicles().containsKey(vehicleType))
		{
			List<VehicleStack> vehiclesStack = playerData.getVehicleDatas().getVehicles().get(vehicleType);
			for(int i = 0; i < vehiclesStack.size(); i++)
			{
				
				VehicleStack vehicleStack = vehiclesStack.get(i);
				
				if(!vehicleStack.hasVehicleData()) continue;
				
				NBTTagCompound compound = vehicleStack.getVehicleDatas();

				if(!compound.hasKey("EntityPersistentId")) continue;
				
				if(compound.getString("EntityPersistentId").equals(stack.getVehicleDatas().getString("EntityPersistentId")))
				{
					vehicleStack.setVehicleData(stack.getVehicleDatas());
				}
			}
		}
	}
	
	public static void updateOfflinePlayerVehicleData(UUID owner, VehicleStack stack)
	{

		NBTTagCompound playerNBT = null;
		try {
			playerNBT = DataUtils.getDataOfflinePlayer(owner);
		} catch (IOException e) {
			e.printStackTrace();
			ModCore.log("Data of offline player UUID: " + owner + " not found");
			return;
		}
				
		IPlayer playerData = DataUtils.parsePlayerTag(playerNBT);
		
		if(playerData != null)
		{
			ItemModularEntity modularEntity = (ItemModularEntity)stack.getItem();
			String vehicleType = modularEntity.getClass().getSimpleName();
			if(playerData.getVehicleDatas().getVehicles().containsKey(vehicleType))
			{
				List<VehicleStack> vehiclesStack = playerData.getVehicleDatas().getVehicles().get(vehicleType);
				for(int i = 0; i < vehiclesStack.size(); i++)
				{
					
					VehicleStack vehicleStack = vehiclesStack.get(i);
					
					if(!vehicleStack.hasVehicleData()) continue;
					
					NBTTagCompound compound = vehicleStack.getVehicleDatas();

					if(!compound.hasKey("EntityPersistentId")) continue;
					
					if(compound.getString("EntityPersistentId").equals(stack.getVehicleDatas().getString("EntityPersistentId")))
					{
						vehicleStack.setVehicleData(stack.getVehicleDatas());
					}
				}
			}
			
			try {
				DataUtils.writeDataOfflinePlayer(owner, playerNBT, playerData);
			} catch (IOException e) {
				e.printStackTrace();
				ModCore.log("Data of offline player UUID: " + owner + " can't be saved");
			}
		}
	}

}
