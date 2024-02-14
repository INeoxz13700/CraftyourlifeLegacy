package fr.innog.handler;

import fr.innog.client.ui.CheckUpdateUI;
import fr.innog.client.ui.ingame.DeathUI;
import fr.innog.client.ui.ingame.InventoryUI;
import fr.innog.client.ui.ingame.PauseUI;
import fr.innog.client.ui.ingame.SleepingUI;
import fr.innog.common.ModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class UIHandler implements IGuiHandler {

	
	@SubscribeEvent
	public void onGuiOpen(GuiOpenEvent event) {
	   
	   Minecraft mc = Minecraft.getMinecraft();
	
	   if (event.getGui() instanceof net.minecraft.client.gui.GuiMainMenu)
	   {
		  event.setGui((GuiScreen)new CheckUpdateUI());
	   }
	   else if (event.getGui() instanceof net.minecraft.client.gui.GuiIngameMenu)
	   {
		  if(!ModCore.debugMode)  event.setGui(new PauseUI());
	   }
       else if(event.getGui() instanceof GuiGameOver)
       {
    	   event.setCanceled(true);
           mc.displayGuiScreen(new DeathUI());
       }
       else if(event.getGui() instanceof GuiSleepMP)
       {
    	   event.setCanceled(true);
           mc.displayGuiScreen(new SleepingUI());
       }
       else if(event.getGui() instanceof GuiInventory)
       {
    	   event.setCanceled(true);
    	   mc.displayGuiScreen(new InventoryUI(mc.player));
       }
	    
	}
	
	@SubscribeEvent
	public void textureStich(TextureStitchEvent.Pre event) {

	    event.getMap().registerSprite(new ResourceLocation("craftyourliferp:items/empty_armor_slot_gilet"));
	    event.getMap().registerSprite(new ResourceLocation("craftyourliferp:items/empty_armor_slot_braid"));
	}
	
	@SubscribeEvent
	public void onSleepingUpdate(ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.player == null) return;
		   
		if(mc.currentScreen instanceof SleepingUI)
		{
			if(!mc.player.isPlayerSleeping() || !mc.player.isEntityAlive())
			{
				mc.displayGuiScreen(null);
			}
			
			
			mc.gameSettings.thirdPersonView = 0;
		}
	}
	

		 
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}
    
	
}
