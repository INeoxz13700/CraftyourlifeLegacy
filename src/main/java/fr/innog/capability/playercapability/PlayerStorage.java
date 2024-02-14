package fr.innog.capability.playercapability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.dynamx.common.items.DynamXItem;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticManager;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.common.vehicles.datas.VehicleStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerStorage implements IStorage<IPlayer>{

	@Override
	public NBTBase writeNBT(Capability<IPlayer> capability, IPlayer instance, EnumFacing side) {
		
		NBTTagCompound compound = new NBTTagCompound();
		

		
	   	NBTTagCompound loginInfoTag = new NBTTagCompound();

		instance.getLoginInformation().writeToNBT(loginInfoTag);
			
	    compound.setTag("LoginInformation", loginInfoTag);
	    
	    
		
		NBTTagCompound vehicleDatas = new NBTTagCompound();
		
	    		
		vehicleDatas.setInteger("Vcoins", instance.getVehicleDatas().getVcoins());
		vehicleDatas.setInteger("UnlockedSlot", instance.getVehicleDatas().getUnlockedSlot());
		
		NBTTagCompound vehicleCompound = new NBTTagCompound();
		for (String vehicleType : instance.getVehicleDatas().getVehicles().keySet()) 
		{
			List<VehicleStack> stackList = instance.getVehicleDatas().getVehicles().get(vehicleType);
			if(stackList != null)
			{
				NBTTagList vehicleClassList = new NBTTagList();
				for(VehicleStack stack : stackList)
				{
					NBTTagCompound isCompound = new NBTTagCompound();
					
					stack.writeToNBT(isCompound);
					
					vehicleClassList.appendTag(isCompound);
				}
				
				vehicleCompound.setTag(vehicleType, vehicleClassList);
			}
		}

		vehicleDatas.setTag("Vehicles", vehicleCompound);
		
		compound.setTag("VehicleDatas", vehicleDatas);

		
    	NBTTagList cosmeticTags = new NBTTagList();
    	
    	for(CosmeticObject obj : instance.getCosmeticDatas().cosmeticsData)
    	{  	
    		NBTTagCompound cosmeticTag = new NBTTagCompound();
    		obj.writeToNBT(cosmeticTag);
    		cosmeticTags.appendTag(cosmeticTag);
    	}
    	
    	compound.setTag("Cosmetics", cosmeticTags);
    	
    	NBTTagCompound unlockedAnimationsTag = new NBTTagCompound();
    	int[] unlockedAnimation = new int[instance.getUnlockedAnimations().size()];
    	unlockedAnimationsTag.setIntArray("UnlockedAnimation", unlockedAnimation);
    			
    	compound.setTag("Animations", unlockedAnimationsTag);
    	
    	NBTTagCompound identityTag = new NBTTagCompound();
    	
    	if(instance.getIdentityData() != null) instance.getIdentityData().writeToNBT(identityTag);
    	
    	compound.setTag("Identity", identityTag);
    	
    	NBTTagCompound thirstTag = new NBTTagCompound();
    	
    	instance.getThirstStats().writeToNBT(thirstTag);
    	
    	compound.setTag("Thirst", thirstTag);

    	NBTTagCompound shieldTag = new NBTTagCompound();
    	
    	instance.getShieldStats().writeToNBT(shieldTag);
    	
    	compound.setTag("Shield", shieldTag);
    	
    	NBTTagCompound healthDataTag = new NBTTagCompound();

		instance.getHealthData().writeToNBT(healthDataTag);
		
    	compound.setTag("HealthData", healthDataTag);
    	
    	
    	NBTTagCompound phoneDataTag = new NBTTagCompound();

		instance.getPhoneData().writeToNBT(phoneDataTag);
		
    	compound.setTag("PhoneData", phoneDataTag);
    	
    	NBTTagCompound inventoryTag = instance.getInventory().serializeNBT();

    	compound.setTag("InventoryData", inventoryTag);
    	
		return compound;
	}

	@Override
	public void readNBT(Capability<IPlayer> capability, IPlayer instance, EnumFacing side, NBTBase nbt) {
		NBTTagCompound compound = (NBTTagCompound) nbt;

		if(compound.hasKey("LoginInformation"))
		{
			instance.getLoginInformation().readFromNBT(compound.getCompoundTag("LoginInformation"));
		}
		
		if(compound.hasKey("VehicleDatas"))
		{
			NBTTagCompound vehicleDatas = (NBTTagCompound) compound.getTag("VehicleDatas");

			NBTTagCompound oldVehicleCompound = (NBTTagCompound) vehicleDatas.getTag("VehiclesTags");
			NBTTagCompound vehicleCompound = (NBTTagCompound) vehicleDatas.getTag("Vehicles");

			NBTTagList vehicleTagList = null;
			

			if(oldVehicleCompound != null)
			{
				Iterator<String> iterator = oldVehicleCompound.getKeySet().iterator();
		
				while(iterator.hasNext())
				{
	
					String vehicleType = iterator.next();

					vehicleTagList = (NBTTagList) oldVehicleCompound.getTag(vehicleType);
							
					List<VehicleStack> stackList = new ArrayList<VehicleStack>();

					for(int i = 0; i < vehicleTagList.tagCount(); i++)
					{
						ItemStack is = new ItemStack(vehicleTagList.getCompoundTagAt(i));
							
						if(is.isEmpty()) continue;
						
						VehicleStack stack = new VehicleStack((DynamXItem<?>)is.getItem(), (byte)is.getMetadata());
								
						stackList.add(stack);
					}
							
					instance.getVehicleDatas().getVehicles().put(vehicleType, stackList);
					
				}
			}
			
			if(vehicleCompound != null)
			{
				Iterator<String> iterator = vehicleCompound.getKeySet().iterator();
		
				while(iterator.hasNext())
				{
	
					String vehicleType = iterator.next();
						
					List<VehicleStack> stackList = new ArrayList<VehicleStack>();
		
					if(instance.getVehicleDatas().getVehicles().containsKey(vehicleType))
					{
						stackList = instance.getVehicleDatas().getVehicles().get(vehicleType);
					}
					
					
					vehicleTagList = (NBTTagList) vehicleCompound.getTag(vehicleType);
							
		
					for(int i = 0; i < vehicleTagList.tagCount(); i++)
					{								
						VehicleStack stack = new VehicleStack();
						stack.readFromNBT(vehicleTagList.getCompoundTagAt(i));
						stackList.add(stack);
					}
						
					instance.getVehicleDatas().getVehicles().put(vehicleType, stackList);
				}
			}
			

			vehicleDatas.removeTag("VehiclesTags");
			
			instance.getVehicleDatas().setVcoins(vehicleDatas.getInteger("Vcoins"));
			instance.getVehicleDatas().setUnlockedSlot(vehicleDatas.getInteger("UnlockedSlot"));
		}
			
		if(compound.hasKey("Cosmetics"))
		{
			NBTTagList cosmeticTags = (NBTTagList) compound.getTag("Cosmetics");
				
			List<CosmeticObject> cosmetics = new ArrayList<CosmeticObject>();
				
			int j = 0;
			CosmeticManager manager = ModCore.getCosmeticsManager();
			for(CosmeticObject cosmeticObj : manager.getCosmetics())
			{
				CosmeticObject copy = manager.getCopy(cosmeticObj);
				cosmetics.add(copy);
					
		   		NBTTagCompound tag = cosmeticTags.getCompoundTagAt(j);
		   		copy.loadNBTData(tag);  
		    		
		   		++j;
			}
				
			instance.getCosmeticDatas().setCosmeticsData(cosmetics);
				
		}
			
		if(compound.hasKey("Animations"))
		{
			NBTTagCompound animationTag = (NBTTagCompound) compound.getTag("Animations");
			int[] unlockedAnimationsArray = animationTag.getIntArray("UnlockedAnimation");
			List<Integer> unlockedAnimations = new ArrayList<Integer>();
				
			for(int i = 0; i < unlockedAnimationsArray.length; i++)
			{
				int unlockedAnimation = unlockedAnimationsArray[i];
				unlockedAnimations.add(unlockedAnimation);
			}
			instance.setUnlockedAnimations(unlockedAnimations);
		}
			
		if(compound.hasKey("Identity"))
		{
			instance.getIdentityData().readFromNBT(compound.getCompoundTag("Identity"));
		}
			
		if(compound.hasKey("Thirst"))
		{
			instance.getThirstStats().readFromNBT(compound.getCompoundTag("Thirst"));
		}
			
		if(compound.hasKey("Shield"))
		{
			instance.getShieldStats().readFromNBT(compound.getCompoundTag("Shield"));
		}
			
		if(compound.hasKey("HealthData"))
		{
			instance.getHealthData().readFromNBT(compound.getCompoundTag("HealthData"));
		}
		
		if(compound.hasKey("PhoneData"))
		{
			instance.getPhoneData().readFromNBT(compound.getCompoundTag("PhoneData"));
		}
		
		if(compound.hasKey("InventoryData"))
		{
			instance.getInventory().deserializeNBT(compound.getCompoundTag("InventoryData"));
		}
	}

}
