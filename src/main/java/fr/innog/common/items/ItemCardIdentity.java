package fr.innog.common.items;

import fr.innog.network.PacketCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ItemCardIdentity extends Item {

	public ItemCardIdentity()
	{
		this.setMaxStackSize(1);
	}
	
	
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
    	if(handIn == EnumHand.OFF_HAND) new ActionResult<ItemStack>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    	
    	if(worldIn.isRemote)
    	{
    		PacketCollection.openCardIdentityOf(playerIn);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
    	}
        return new ActionResult<ItemStack>(EnumActionResult.PASS, playerIn.getHeldItem(handIn));
    }
}
