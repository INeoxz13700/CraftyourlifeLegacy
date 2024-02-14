package fr.innog.common.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.SeatsModule;
import fr.dynamx.common.items.DynamXItem;
import fr.dynamx.common.items.ItemModularEntity;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.vehicles.VehicleEntityManager;
import fr.innog.common.vehicles.VehicleEntityReference;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.VehicleDatas;
import fr.innog.server.adapter.datas.ProtectedRegion;
import fr.innog.ui.remote.RemoteGarageUI;
import fr.innog.ui.remote.data.RemoteMethodCallback.ActionResult;
import fr.innog.ui.remote.data.RemoteUICache;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class GarageController {

	public ActionResult extendGarage(RemoteGarageUI remoteUI, EntityPlayer player)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		if(playerData.getVehicleDatas().getUnlockedSlot() == VehicleDatas.maxSlot)
		{
			remoteUI.displayMessage("Vous avez agrandit votre garage au maximum.");
			return ActionResult.FAILURE;
		}
		
		int slotPrice = remoteUI.getSlotPrice();
				
		if(playerData.getVehicleDatas().getVcoins() >= slotPrice) {
			playerData.getVehicleDatas().removeVcoins(slotPrice);
			playerData.getVehicleDatas().setUnlockedSlot(playerData.getVehicleDatas().getUnlockedSlot() + 1);
        	
			remoteUI.displayMessage("Votre achat a été pris en compte!");
			return ActionResult.SUCCESS;
		}
        else 
        {
        	remoteUI.displayMessage("Vous n'avez pas suffisamment de VCoins.");
			return ActionResult.FAILURE;
        }
	}
	
	public ActionResult transferToInventory(RemoteGarageUI remoteUI, String itemVehicleType, int vehicleIndex, EntityPlayer player)
	{
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		try {
			Class<? extends ItemModularEntity> vehicleTypeClass = (Class<? extends ItemModularEntity>) Class.forName(itemVehicleType);
	
			List<VehicleStack> vehicles = playerData.getVehicleDatas().getVehicles(vehicleTypeClass);
					
			VehicleStack vehicleStack = vehicles.get(vehicleIndex);
			
			VehicleEntityManager entityManager = WorldDataManager.get(player.world).getVehicleManager();

						
			if(vehicleStack.hasVehicleData())
			{
				String vehicleUuid = vehicleStack.getVehicleDatas().getString("EntityPersistentId");
				if(entityManager.isVehicleInWorld(vehicleUuid))	
				{
					remoteUI.displayMessage("Vous devez rentrer votre véhicule dans le garage avant de pouvoir le transférer dans votre inventaire.");
					return ActionResult.FAILURE;
				}
			}

			ItemStack is = new ItemStack(vehicleStack.getItem(), 1, vehicleStack.getMeta());
			
			is.setTagCompound(vehicleStack.getVehicleDatas());
			
			if(!player.inventory.addItemStackToInventory(is))
			{
				MinecraftUtils.sendMessage(player, "§cLe véhicule §a" + is.getDisplayName() + " §cn'a pas pu être ajouté à votre inventaire.");
				player.closeScreen();
				return ActionResult.FAILURE;
			}  
			else
			{
				vehicles.remove(vehicleIndex);
				remoteUI.displayMessage("Le véhicule a bien été transféré dans votre inventaire.");
				return ActionResult.SUCCESS;
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			return ActionResult.FAILURE;
		}
	}
		
	@SuppressWarnings("unchecked")
	public void spawnVehicle(String itemVehicleType, int vehicleIndex, EntityPlayer player)
	{
		try 
		{
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			
			Class<? extends ItemModularEntity> vehicleTypeClass = (Class<? extends ItemModularEntity>) Class.forName(itemVehicleType);
	
			List<VehicleStack> vehicles = playerData.getVehicleDatas().getVehicles(vehicleTypeClass);
					
			VehicleStack vehicleIs = vehicles.get(vehicleIndex);
			
			
			VehicleEntityManager entityManager = WorldDataManager.get(player.world).getVehicleManager();
			
			if(vehicleIs.hasVehicleData())
			{
				if(vehicleIs.getVehicleDatas().hasKey("EntityPersistentId"))
				{
					String vehicleUid = vehicleIs.getVehicleDatas().getString("EntityPersistentId");
					
					if(entityManager.isVehicleInWorld(vehicleUid))
					{
						VehicleEntityReference ref = entityManager.getVehicleReference(vehicleUid);
						

						if(player.getDistance(ref.getLastPos().getX(), ref.getLastPos().getY(), ref.getLastPos().getZ()) > 32)
						{
							MinecraftUtils.sendMessage(player, "§cRapprochez-vous de votre véhicule. §aCoordonnées : " + ref.getLastPos());
							return;
						}
						
						if(!isEntityInWorld(ref, player.world))
						{
							entityManager.removeVehicle(player.world, vehicleUid);
							MinecraftUtils.sendMessage(player, "§aLe véhicule a été retiré du monde.");
							return;
						}
						
						BaseVehicleEntity<?> vehicleEntity = entityManager.tryFindEntity(player.world, vehicleUid);

						if(vehicleEntity != null)
						{
							if(vehicleEntity.getModuleByType(SeatsModule.class).getControllingPassenger() != null)
							{
								MinecraftUtils.sendMessage(player, "§cUne personne utilise le véhicule vous ne pouvez pas le rentrer dans votre garage.");
								return;
							}
						}
						
						entityManager.unspawnVehicle(vehicleUid, player);
						return;
					} 
				}
			}
			
			entityManager.spawnVehicle(vehicleIs, player, false);
			
				
		} catch (ClassNotFoundException  | SecurityException | IllegalArgumentException  e) {
			e.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public static void onVehicleSpawn(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getHand() == EnumHand.MAIN_HAND)
		{
			
			if(event.getWorld().isRemote) return;
			
			
			ItemStack is = event.getItemStack();
			
			if(!(is.getItem() instanceof ItemModularEntity))
			{
				return;
			}
			
			EntityPlayer player = event.getEntityPlayer();
			
			VehicleEntityManager entityManager = WorldDataManager.get(player.world).getVehicleManager();
			try {
				if(!GarageController.canSpawnVehicle(player, new Vec3d(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))) 
				{
					event.setCanceled(true);
					player.addItemStackToInventory(is);
					return;
				}
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
				player.addItemStackToInventory(is);
				event.setCanceled(true);
				return;
			}
			
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			
			List<VehicleStack> vehicles = playerData.getVehicleDatas().getVehicles(((ItemModularEntity)is.getItem()).getClass());

			if(vehicles != null && vehicles.size()+1 > playerData.getVehicleDatas().getUnlockedSlot())
			{
				MinecraftUtils.sendMessage(player,"§cVotre garage est plein agrandissez le!");
				player.addItemStackToInventory(is);
				event.setCanceled(true);
				return;
			}

			VehicleStack vehicleStack = null;
			
			if(is.hasTagCompound()) vehicleStack = new VehicleStack((DynamXItem)is.getItem(), (byte)is.getMetadata(), is.getTagCompound());
			else vehicleStack = new VehicleStack((DynamXItem)is.getItem(), (byte)is.getMetadata());
			
			playerData.getVehicleDatas().addVehicle(vehicleStack, ((ItemModularEntity)is.getItem()).getClass());
			
			entityManager.spawnVehicle(vehicleStack, player, true);
			
			MinecraftUtils.sendMessage(player, "§aLe véhicule a été ajouté dans votre garage.");
			RemoteUICache.setDirtyForPlayer(player, RemoteGarageUI.class.getSimpleName(), "Vehicles");
		}
	}	
	
	
	public static boolean canSpawnVehicle(EntityPlayer player, Vec3d pos) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		if(player.getServer() != null && player.getServer().isDedicatedServer())
		{
			List<ProtectedRegion> regions = ModCore.getWorldGuard().getApplicableRegions(pos);
				
			boolean isInGarageRegion = false;
			boolean flag = false;
				
			for(ProtectedRegion region : regions) {
				if(region.getId().contains("garage_publique") || region.getId().contains("parking"))
				{
					isInGarageRegion = true;
					break;
				}
				else if(region.getId().contains("garage"))
				{
					if(isPlayerGarage(region.getId(), player))
					{
						isInGarageRegion = true;
						break;
					}
					else
					{
						flag = true;
					}
				}
			}
				
			if(!isInGarageRegion)
			{
				if(flag) 
				{
					MinecraftUtils.sendMessage(player, "§cCe n'est ni votre garage ni un garage public.");
				}
				else
				{
					MinecraftUtils.sendMessage(player, "§cLes véhicules peuvent seulement être posés/rentrés dans des garages publics, privés et parking.");
				}		
				return false;
			}

		}
		return true;
	}
	
	public static boolean isPlayerGarage(String regionId, EntityPlayer player)
	{
		return regionId.contains("garage_" + player.getName().toLowerCase());
	}
	
	public static boolean isEntityInWorld(VehicleEntityReference ref, World world)
	{
		for(Entity entity : world.loadedEntityList)
		{
			if(entity.getPersistentID().toString().equals(ref.getEntityUuid().toString()))
			{
				return true;
			}
		}
		return false;
	}
	
}
