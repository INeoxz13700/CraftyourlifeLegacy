package fr.innog.common.controllers;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.event.DataSyncEvent;
import fr.innog.common.ModCore;
import fr.innog.common.thirst.ThirstStats;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class ThirstController {

    //ClientSide
    public boolean playerLookWater(EntityPlayer player, World worldObj)
    {
    	if(worldObj.isRemote)
    	{
		    RayTraceResult mop = (Minecraft.getMinecraft()).player.rayTrace(2.0D, 0.0F);
		    
		    if (mop != null) {

			      Block blockLookingAt = (Minecraft.getMinecraft()).world.getBlockState(mop.getBlockPos()).getBlock();
			      if (blockLookingAt instanceof BlockStaticLiquid) 
			      {
			    	  return true;
			      }
		    }
    	}
    	else
    	{
    	    RayTraceResult mop = MinecraftUtils.rayTraceServer(player, 2.0D, 0.0f);
		    if (mop != null) {
		      Block blockLookingAt = player.world.getBlockState(mop.getBlockPos()).getBlock();
		      if (blockLookingAt instanceof BlockStaticLiquid) 
		      {
		    	  return true;
		      }
		    }
    	}
	    return false;
   }
    
   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event)
   {
		if(event.phase == Phase.START)
		{
			if(!event.player.world.isRemote)
			{
				IPlayer playerData = MinecraftUtils.getPlayerCapability(event.player);

				playerData.getThirstStats().onUpdate(event.player);
			}
		}
   }
	
   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event)
   {
		EntityPlayer player = event.player;
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		playerData.getThirstStats().setThirst(ThirstStats.initialThirst);
   }
   
   @SubscribeEvent
   public static void onThirstSync(DataSyncEvent event)
   {
	    if(event.getKey().equals("ThirstData"))
	    {
			EntityPlayer player = event.getPlayer();
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			playerData.getThirstStats().setThirst((Float)event.getData().getData());
	    }
   }
	
}
