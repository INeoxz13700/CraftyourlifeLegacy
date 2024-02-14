package fr.innog.handler;

import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.items.Items;
import fr.innog.common.items.interact.IItemPress;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.data.ItemPressingData;
import fr.innog.network.PacketCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InputHandler {

	@SubscribeEvent
	public void onKeyPress(KeyInputEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if(ClientProxy.keyBindings[0].isPressed())
		{			
			PacketCollection.askToServerOpenUI(2);
		}
		else if(ClientProxy.keyBindings[1].isPressed())
		{	
			EntityPlayer p = mc.player;
			
			
			if(p.isRiding()) return;

        	
        	if(ModControllers.thirstController.playerLookWater(p, p.world))
        	{
        		PacketCollection.drinkWater();
        	}
        	else
        	{

        		if(p.getHeldItemMainhand().getItem() == Items.identityCard)
            	{
        			PacketCollection.openCardIdentityOf(p);
            	}
        		else if(mc.objectMouseOver.typeOfHit == Type.BLOCK)
        		{
	                /*ItemStack itemstack = mc.player.inventory.getCurrentItem();
	        		int i = mc.objectMouseOver.blockX;
	                int j =  mc.objectMouseOver.blockY;
	                int k =  mc.objectMouseOver.blockZ; 
	        	    boolean result = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(p, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, i, j, k, mc.objectMouseOver.sideHit, mc.theWorld).isCanceled();
	                if (result && mc.playerController.onPlayerRightClick(mc.player, mc.world,itemstack, i, j, k, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec))
	                {
	                    mc.thePlayer.swingItem();
	                }*/
        		}
        		else if(mc.objectMouseOver.typeOfHit == Type.ENTITY)
        		{
        			if(Minecraft.getMinecraft().objectMouseOver.entityHit instanceof EntityPlayer)
	        		{
	  		            EntityPlayer target = (EntityPlayer) Minecraft.getMinecraft().objectMouseOver.entityHit;
	  		            PacketCollection.openCardIdentityOf(target);
	        		}
        		}
        	}
		}
		else if(ClientProxy.keyBindings[2].isPressed())
		{		
			PacketCollection.askToServerOpenUI(4);
		}
	}
	
	@SubscribeEvent
	public void onClientTick(ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.player != null && mc.currentScreen != null)
		{
			if(ClientProxy.modClient.getLocalPlayerCapability().getItemPressing().isUsingItem())
			{
					EntityPlayer player = Minecraft.getMinecraft().player;
					ItemPressingData pressingData = ClientProxy.modClient.getLocalPlayerCapability().getItemPressing();
					ItemStack useIs = pressingData.usedItem;
					IItemPress item = (IItemPress) useIs.getItem();
	                item.onItemStopUsing(player, player.world, useIs, pressingData.itemPressTicks);
	                ClientProxy.modClient.getLocalPlayerCapability().getItemPressing().setItemReleased();
					
	                PacketCollection.notifyServerMouseState();
	                return;
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.HIGHEST)
	@SideOnly(Side.CLIENT)
	public void onMouseRightClick(MouseEvent event)
	{
		if(event.getButton() == 1)
		{			
			if(ClientProxy.modClient.getLocalPlayerCapability().getItemPressing().isUsingItem())
			{
				if(!event.isButtonstate()) //is Released
				{
					EntityPlayer player = Minecraft.getMinecraft().player;
					ItemPressingData pressingData = ClientProxy.modClient.getLocalPlayerCapability().getItemPressing();
					ItemStack useIs = pressingData.usedItem;
					IItemPress item = (IItemPress) useIs.getItem();
	                item.onItemStopUsing(player, player.world, useIs, pressingData.itemPressTicks);
	                ClientProxy.modClient.getLocalPlayerCapability().getItemPressing().setItemReleased();
					
	                PacketCollection.notifyServerMouseState();
	                return;
				}
				
			}
			
			/*if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK)
    		{
                ItemStack itemstack = mc.thePlayer.inventory.getCurrentItem();
                if(itemstack != null && itemstack.getItem() == ModdedItems.itemCrowbar)
                {
	        		int i =  mc.objectMouseOver.blockX;
	                int j =  mc.objectMouseOver.blockY;
	                int k =  mc.objectMouseOver.blockZ; 
	                CraftYourLifeRPMod.packetHandler.sendToServer(new PacketCustomInterract(i, j, k, mc.objectMouseOver.sideHit));              
	            }
    		}*/
		}
	}
	
}
