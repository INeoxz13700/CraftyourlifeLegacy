package fr.innog.common.controllers;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.event.DataSyncEvent;
import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class ShieldController {

   @SubscribeEvent
   public static void onShieldSync(DataSyncEvent event)
   {
	    if(event.getKey().equals("ShieldData"))
	    {
			EntityPlayer player = event.getPlayer();
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			playerData.getShieldStats().setShield((Float)event.getData().getData(), false);
	    }
   }
   
   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event)
   {
	   IPlayer playerData = MinecraftUtils.getPlayerCapability(event.player);
	   
	   playerData.getShieldStats().setShield(0, true);
   }
	
}
