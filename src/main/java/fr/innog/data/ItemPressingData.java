package fr.innog.data;

import fr.innog.network.PacketCollection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemPressingData {

	public ItemStack usedItem;
	
	public int itemPressTicks;
	
	private EntityPlayer player;
	
	public ItemPressingData(EntityPlayer player)
	{
		this.player = player;
	}
	
	public boolean isUsingItem()
	{
		return usedItem != null && !usedItem.isEmpty();
	}

	public void setItemReleased()
	{
		 usedItem = null;
		 itemPressTicks = 0;
		 
		 PacketCollection.notifyClientsUsingItem(player);
	}
	

	public void pressItem(ItemStack is) {
		usedItem = is;
		itemPressTicks = 0;
	}

	
}
