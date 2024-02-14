package fr.dynamx.addons.basics.common.event;

import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.common.modules.InteractionKeyModule;
import fr.dynamx.addons.basics.utils.VehicleKeyUtils;
import fr.dynamx.api.events.ContentPackSystemEvent;
import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.innog.common.ModCore;
import fr.innog.common.vehicles.VehicleEntityManager;
import fr.innog.common.vehicles.VehicleEntityReference;
import fr.innog.common.world.WorldDataManager;
import fr.innog.utils.MinecraftUtils;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = "dynamx_basics")
public class BasicsAddonEventHandler {
	
  @SubscribeEvent
  public static void initVehicleModules(PhysicsEntityEvent.CreateModules<BaseVehicleEntity> event) {
    BaseVehicleEntity<?> entity = (BaseVehicleEntity<?>)event.getEntity();
    BasicsAddonInfos info = (BasicsAddonInfos)((ModularVehicleInfo)entity.getPackInfo()).getSubPropertyByType(BasicsAddonInfos.class);
    event.getModuleList().add(new BasicsAddonModule(entity, info));
    event.getModuleList().add(new InteractionKeyModule(entity));
  }
  

  @SubscribeEvent
  public static void interactWithCar(VehicleEntityEvent.PlayerInteract event) {
	  BaseVehicleEntity<?> entity = event.getEntity();
	  
	  VehicleEntityManager vehicleManager = WorldDataManager.get(entity.getEntityWorld()).getVehicleManager();
	  
	  VehicleEntityReference reference = vehicleManager.getVehicleReference(entity);

	  if (event.getPart() instanceof fr.dynamx.common.contentpack.parts.PartSeat || event.getPart() instanceof fr.dynamx.common.contentpack.parts.PartStorage || event.getPart() instanceof fr.dynamx.common.contentpack.parts.PartDoor) {
	      BasicsAddonModule module = (BasicsAddonModule)event.getEntity().getModuleByType(BasicsAddonModule.class);
	      if (VehicleKeyUtils.isKeyItem(event.getPlayer().getHeldItemMainhand())) {
		        if (!reference.getOwnerUuid().toString().equals(event.getPlayer().getPersistentID().toString())) 
		        {
		        	MinecraftUtils.sendMessage(event.getPlayer(), "§cCe véhicule ne vous appartient pas.");
		        } 
		        else
		        {
		        	if(module.isLocked())
		        	{
			        	MinecraftUtils.sendMessage(event.getPlayer(), "§aVéhicule dévérouillé");
		        	}
		        	else
		        	{
			        	MinecraftUtils.sendMessage(event.getPlayer(), "§cVéhicule vérouillé");
		        	}
		        	module.setLocked(!module.isLocked());
		        }
		       	event.setCanceled(true);
	      }
	      else
	      {
	    	  if(module.isLocked())
	    	  {
		         MinecraftUtils.sendMessage(event.getPlayer(), "§cCe véhicule est vérouillé.");
	    		 event.setCanceled(true);
	    	  }
	      }
	  }
  }
}
