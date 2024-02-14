package fr.innog.client.blocks;

import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface ICustomModel {
	@SideOnly(Side.CLIENT)
	public void registerCustomRender(Item item);
}
