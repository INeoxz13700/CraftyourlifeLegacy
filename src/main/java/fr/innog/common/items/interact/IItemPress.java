package fr.innog.common.items.interact;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IItemPress 
{	
	/**Called when player started using item**/
	public void onItemRightClicked(EntityPlayer player, World worldObj, ItemStack heldItem);
	
	/**Called every tick when player using item**/
	public void onItemUsing(EntityPlayer player, World worldObj, ItemStack heldItem, int itemPressedTicksCount);
	
	/**Called every tick when player stopped using item**/
	public void onItemStopUsing(EntityPlayer player, World worldObj, ItemStack heldItem, int itemPressedTicksCount);
}