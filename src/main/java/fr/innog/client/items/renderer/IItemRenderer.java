package fr.innog.client.items.renderer;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IItemRenderer {

	@SideOnly(Side.CLIENT)
	void renderItemFirstPerson(EntityPlayer itemUser, ItemStack heldItemStack, long deltaFrame);
	
	@SideOnly(Side.CLIENT)
	void setupRenderPlayerHeldItem(EntityPlayer player, ModelPlayer playerModel, ItemStack heldItemStack, long deltaFrame);

	
}
