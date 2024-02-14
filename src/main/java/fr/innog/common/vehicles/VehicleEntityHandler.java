package fr.innog.common.vehicles;

import java.util.UUID;

import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.api.events.PhysicsEvent;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.ModularPhysicsEntity;
import fr.dynamx.common.entities.PhysicsEntity;
import fr.dynamx.common.items.DynamXItemSpawner;
import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class VehicleEntityHandler {

	
	@SubscribeEvent
	public static void onVehicleUnSpawn(PhysicsEvent.PhysicsEntityRemoved event)
	{
		if(event.getPhysicsEntity() instanceof BaseVehicleEntity)
		{
			VehicleEntityManager entityManager = WorldDataManager.get(event.getPhysicsEntity().world).getVehicleManager();

			BaseVehicleEntity<?> vehicleEntity = (BaseVehicleEntity<?>) event.getPhysicsEntity();
			
			String uuid = vehicleEntity.getPersistentID().toString();
			
			if(!event.getPhysicsEntity().isDead)
			{
				if(entityManager.isVehicleInWorld(uuid))
				{
					VehicleEntityReference reference = entityManager.getVehicleReference(vehicleEntity);
					if(reference != null)
					{
						reference.setLastPos(vehicleEntity.getPosition());
					}
				}
				return;
			}

			if(entityManager.isVehicleInWorld(uuid))
			{
				VehicleEntityReference reference = entityManager.onVehicleUnspawn(vehicleEntity);
				
				UUID ownerUUID = reference.getOwnerUuid();
				
				EntityPlayer owner = vehicleEntity.world.getPlayerEntityByUUID(ownerUUID);
				if(owner != null)
				{
					MinecraftUtils.sendMessage(owner, "§cVotre véhicule personnel a été détruite.");
				}
			}
					
		}
	}
	
	
    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote) {
            if(event.getEntity() instanceof BaseVehicleEntity<?>)
            {
				WorldDataManager worldData = WorldDataManager.get(event.getWorld());
				ModularPhysicsEntity<?> physicsEntity = (ModularPhysicsEntity<?>)event.getEntity();
				VehicleEntityReference reference = worldData.getVehicleManager().getVehicleReference(physicsEntity);
				if(reference != null && reference.getEntity() == null)
				{
					reference.setEntity(physicsEntity);
				}
            }
        }
    }
	
	
	@SubscribeEvent
	public static void onVehicleSpawn(PhysicsEntityEvent.Spawn event) {
	    
		EntityPlayer spawner = event.getPlayer();
	    
	    DynamXItemSpawner<?> itemSpawner = event.getItemSpawner();
	    
	    PhysicsEntity<?> physicsEntity = event.getPhysicsEntity();
	    
	    if(physicsEntity instanceof BaseVehicleEntity<?>)
	    {
		    WorldDataManager worldData = WorldDataManager.get(spawner.world);
		    
		    if(worldData.getVehicleManager().onVehicleSpawn(event.getPos(), spawner, itemSpawner, (ModularPhysicsEntity<?>) physicsEntity))
		    	worldData.setDirty(true);
		    else
		    	physicsEntity.setDead();
	    }
	}
	
	@SubscribeEvent
	public static void onVehicleInit(PhysicsEntityEvent.Init event)
	{
		  ModularPhysicsEntity<?> entity = (ModularPhysicsEntity<?>)event.getEntity();
		  
		  
		  if(entity instanceof BaseVehicleEntity<?>)
		  {
			  WorldDataManager worldData = WorldDataManager.get(entity.world);

			  worldData.getVehicleManager().onVehicleInit(entity);
		  }
	}
	
	
	@SubscribeEvent
	public static void onEntityUnmountVehicle(VehicleEntityEvent.EntityDismount event)
	{
		BaseVehicleEntity<?> vehicle = event.getEntity();
		VehicleEntityManager entityManager = WorldDataManager.get(vehicle.world).getVehicleManager();

		if(entityManager.isVehicleInWorld(vehicle.getPersistentID().toString()))
		{
			VehicleEntityReference reference = entityManager.getVehicleReference(vehicle);
			if(reference != null)
			{
				reference.setLastPos(vehicle.getPosition());
			}
		}
	}
	
}
