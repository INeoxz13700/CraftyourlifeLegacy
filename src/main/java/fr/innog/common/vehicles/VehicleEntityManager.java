package fr.innog.common.vehicles;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.ModularPhysicsEntity;
import fr.dynamx.common.entities.PhysicsEntity;
import fr.dynamx.common.entities.modules.SeatsModule;
import fr.dynamx.common.items.DynamXItemSpawner;
import fr.dynamx.common.items.ItemModularEntity;
import fr.dynamx.utils.DynamXUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.controllers.GarageController;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.VehicleDatas;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class VehicleEntityManager {

	private  HashMap<String, VehicleEntityReference> vehicleEntities = new HashMap<>();
	
	private  HashMap<DynamXItemSpawner<?>, VehicleStack> spawnMap = new HashMap<>();
				
	public boolean spawnVehicle(VehicleStack vehicleStack, EntityPlayer player, boolean fromInventory)
	{	
		ItemModularEntity modularEntity = (ItemModularEntity) vehicleStack.getItem();
		
		spawnMap.put((DynamXItemSpawner<?>)modularEntity,  vehicleStack);
		
		if(!fromInventory)
		{
			RayTraceResult raytraceresult = DynamXUtils.rayTraceEntitySpawn(player.world, player, EnumHand.MAIN_HAND);
	         if (raytraceresult == null) 
	         {
	        	 MinecraftUtils.sendMessage(player, "§cVous devez regardez un block");
	        	 spawnMap.remove(modularEntity);
	        	 return false;
	         }
	         
	         if (raytraceresult.typeOfHit == RayTraceResult.Type.BLOCK || raytraceresult.typeOfHit == RayTraceResult.Type.ENTITY) 
	         {
	        	 try {
					if(GarageController.canSpawnVehicle(player, raytraceresult.hitVec))
					 {			
						 vehicleStack.spawnEntity(player.world, player, raytraceresult.hitVec);
					 }
				} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException | InstantiationException e) {
					e.printStackTrace();
				}
	         }
	         else
	         {
	        	 spawnMap.remove(modularEntity);
	        	 MinecraftUtils.sendMessage(player, "§cVous devez regardez un block");
	        	 return false;
	         }
		}
	
		return true;
	}
	
	public void removeVehicle(World world, String uuid)
	{
		if(vehicleEntities.containsKey(uuid))
		{
			vehicleEntities.remove(uuid);
			WorldDataManager.get(world).setDirty(true);
		}
	}
	
	public List<VehicleEntityReference> getVehicles()
	{
		return new ArrayList<>(vehicleEntities.values());
	}
	
		
	public boolean unspawnVehicle(String uid, EntityPlayer player)
	{
		for(Entity entity : player.world.getEntities(PhysicsEntity.class, x -> x.getPersistentID().toString().equals(uid)))
		{
			if(entity.getPersistentID().toString().equals(uid))
			{
				onVehicleUnspawn((ModularPhysicsEntity<?>)entity);
				entity.setDead();
				return true;
			}
		}
		return false;
	}
	
	
	public boolean isVehicleInWorld(String uuid)
	{
		ModCore.log("Vehicle in world count "  + vehicleEntities.size());
		return vehicleEntities.containsKey(uuid);
	}
	
	public BaseVehicleEntity<?> tryFindEntity(World world, String uuid)
	{
		for(Entity entity : world.loadedEntityList)
		{
			if(entity.getPersistentID().toString().equals(uuid))
			{
				return (BaseVehicleEntity<?>) entity;
			}
		}
		return null;
	}

	public boolean onVehicleSpawn(Vec3d pos, EntityPlayer spawner, DynamXItemSpawner<?> itemSpawner, ModularPhysicsEntity<?> entity)
	{
		 VehicleStack stack = spawnMap.remove(itemSpawner);
		 
		 if(stack == null) return false;	 
			 
		 stack.getVehicleDatas().setString("EntityPersistentId", entity.getPersistentID().toString());

		 vehicleEntities.put(entity.getPersistentID().toString(), new VehicleEntityReference(entity.getPersistentID(), entity, spawner.getPersistentID(), stack));
		 
		 return true;
	}
	
	public void onVehicleInit(ModularPhysicsEntity<?> entity)
	{
		VehicleEntityReference reference = vehicleEntities.get(entity.getPersistentID().toString());
		
		if(reference == null) return;
		
		
		
		ModCore.log("Chargement des données du véhicule.");

		NBTTagCompound vehicleDataCompound = reference.getVehicleData();
		

		if(vehicleDataCompound != null)
		{
			List<IPhysicsModule<?>> modules = entity.getModules();
			 
			for(IPhysicsModule<?> module : modules)
			{
				if(module instanceof SeatsModule) continue;

				module.readFromNBT(vehicleDataCompound);
			}
		}
		
	}
	
	public VehicleEntityReference onVehicleUnspawn(ModularPhysicsEntity<?> entity)
	{
		VehicleEntityReference reference = vehicleEntities.remove(entity.getPersistentID().toString());
		
		List<IPhysicsModule<?>> modules = entity.getModules();
		ModCore.log("Sauvegarde des données du véhicule");

		NBTTagCompound vehicleDataCompound = reference.getVehicleData();

		for(IPhysicsModule<?> module : modules)
		{
			if(module instanceof SeatsModule) continue;

			try
			{
				module.writeToNBT(vehicleDataCompound);
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
				ModCore.log("Un problème a eu lieu au moment de la sauvegarde du véhicule");
				continue;
			}
		}
		
				
		EntityPlayer owner = entity.world.getPlayerEntityByUUID(reference.getOwnerUuid());
		if(owner == null)
		{
			VehicleDatas.updateOfflinePlayerVehicleData(reference.getOwnerUuid(), reference.getAttribuatedStack());
		}
		else
		{
			VehicleDatas.updatePlayerVehicleData(owner, reference.getAttribuatedStack());
		}
		
		
		WorldDataManager.get(entity.world).setDirty(true);
		
		return reference;
	}
	
	
	public VehicleEntityReference getVehicleReference(ModularPhysicsEntity<?> entity)
	{
		if(vehicleEntities.containsKey(entity.getPersistentID().toString())) return vehicleEntities.get(entity.getPersistentID().toString());
		return null;
	} 
	
	public VehicleEntityReference getVehicleReference(String uuid)
	{
		if(vehicleEntities.containsKey(uuid)) return vehicleEntities.get(uuid);
		return null;
	} 
	
	public void readFromNbt(NBTTagCompound tag) {
		if(tag.hasKey("VehicleEntities"))
		{
			NBTTagList vehicleEntityList = (NBTTagList) tag.getTag("VehicleEntities");
			
			for(int i = 0; i < vehicleEntityList.tagCount(); i++)
			{
				NBTTagCompound entityCompound = vehicleEntityList.getCompoundTagAt(i);
				VehicleEntityReference entityReference = new VehicleEntityReference();
				entityReference.readFromNbt(entityCompound);
				vehicleEntities.put(entityReference.getEntityUuid().toString(), entityReference);
			}
		}
	}

	public void writeToNbt(NBTTagCompound tag) {
		NBTTagList vehicleEntityList = new NBTTagList();

		for (Entry<String, VehicleEntityReference> entry : vehicleEntities.entrySet()) {
			NBTTagCompound entityCompound = new NBTTagCompound();
			entry.getValue().writeToNbt(entityCompound);
			vehicleEntityList.appendTag(entityCompound);
		}
		
		tag.setTag("VehicleEntities", vehicleEntityList);
	}
	
	
}
	
	

	


	

