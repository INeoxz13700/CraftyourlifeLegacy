package fr.innog.handler;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.common.ModCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CapabilityHandler {

	public static final ResourceLocation CAP_ID = new ResourceLocation(ModCore.MODID, "player_data");
	public static final ResourceLocation INVENTORY_CAP_ID = new ResourceLocation(ModCore.MODID, "custom_inventory");

	 @SubscribeEvent
	 public void attachCapability(AttachCapabilitiesEvent<Entity> event)
	 {
		 if (!(event.getObject() instanceof EntityPlayer)) return;


		 event.addCapability(CAP_ID, new PlayerProvider((EntityPlayer)event.getObject()));
	 }
	 
	 @SubscribeEvent
	 public void onPlayerClone(PlayerEvent.Clone event)
	 {
		  EntityPlayer player = event.getEntityPlayer();
		  
		  if(event.getOriginal().hasCapability(PlayerProvider.PLAYER_CAP, null))
		  {
			  IPlayer newPlayerdata = player.getCapability(PlayerProvider.PLAYER_CAP, null);
			  IPlayer oldPlayerdata = event.getOriginal().getCapability(PlayerProvider.PLAYER_CAP, null);
			  newPlayerdata.permute(oldPlayerdata);
		  }
	 }
	
}
