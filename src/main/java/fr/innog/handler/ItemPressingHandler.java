package fr.innog.handler;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.items.interact.IItemPress;
import fr.innog.data.ItemPressingData;
import fr.innog.network.PacketCollection;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class ItemPressingHandler {

	@SubscribeEvent(priority=EventPriority.HIGHEST,receiveCanceled=false)
	public static void onPlayerPressItem(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getHand() == EnumHand.OFF_HAND) return;

		EntityPlayer player = event.getEntityPlayer();

		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		ItemPressingData pressing = playerData.getItemPressing();
		
		ItemStack is = player.getHeldItemMainhand();
		
		if(!pressing.isUsingItem())
		{
			if(is.getItem() instanceof IItemPress)
			{
				IItemPress item = (IItemPress)is.getItem();
				pressing.pressItem(is);
				item.onItemRightClicked(player,player.world,is);
			}
		}
	}
	
	@SubscribeEvent(priority=EventPriority.HIGHEST,receiveCanceled=false)
	public static void onPlayerPressItem(PlayerInteractEvent.RightClickItem event)
	{
		if(event.getHand() == EnumHand.OFF_HAND) return;
		
		EntityPlayer player = event.getEntityPlayer();

		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		ItemPressingData pressing = playerData.getItemPressing();
		
		ItemStack is = player.getHeldItemMainhand();
		
		if(!pressing.isUsingItem())
		{
			if(is.getItem() instanceof IItemPress)
			{
				IItemPress item = (IItemPress)is.getItem();
				pressing.pressItem(is);
				item.onItemRightClicked(player,player.world,is);
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerPressingItem(PlayerTickEvent event)
	{
		if(event.phase == Phase.END)
		{
			EntityPlayer player = event.player;
			
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			
			ItemPressingData pressing = playerData.getItemPressing();
			
			ItemStack is = player.getHeldItemMainhand();

			if(!is.isEmpty())
			{
				if(pressing.isUsingItem())
				{	
					if(!MinecraftUtils.areItemStacksEquals(is, pressing.usedItem))
					{			
						((IItemPress)pressing.usedItem.getItem()).onItemStopUsing(player,player.world,pressing.usedItem,pressing.itemPressTicks);
						pressing.setItemReleased();
					}
					else if(is.getItem() instanceof IItemPress)
					{
						((IItemPress)is.getItem()).onItemUsing(player, player.world, pressing.usedItem,pressing.itemPressTicks++);
						PacketCollection.notifyClientsUsingItem(player);
					}
				}
			}
			else
			{
				if(pressing.isUsingItem() )
				{
					((IItemPress)pressing.usedItem.getItem()).onItemStopUsing(player,player.world,pressing.usedItem,pressing.itemPressTicks);
					
					pressing.setItemReleased();
					
					PacketCollection.notifyClientsUsingItem(player);
				}
			}
		}
	}
	
}
