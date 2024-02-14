package fr.innog.common.items;

import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketOpenTelephone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemKamsung extends Item {

	public ItemKamsung()
	{
		setMaxStackSize(1);
	}
	
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
    	if(worldIn.isRemote) return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));

    	ModCore.getPackethandler().sendTo(new PacketOpenTelephone(),(EntityPlayerMP) playerIn);
    	return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    }
	
}
