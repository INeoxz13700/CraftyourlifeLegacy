package fr.innog.common.items;

import java.util.List;

import javax.annotation.Nullable;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWaterBottle extends Item {
	
	private int waterGive;
	
	private float waterLiter;
		
	public ItemWaterBottle(float waterLiter, int waterGive)
	{
        this.setHasSubtypes(true);
		this.setMaxDamage((int) (waterLiter*1000));
		maxStackSize = 1;
		this.waterGive = waterGive;
		this.waterLiter = waterLiter;
	}
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {	
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if(handIn == EnumHand.OFF_HAND)  return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);

		if(ModControllers.thirstController.playerLookWater(playerIn, worldIn))
		{
			itemstack.setItemDamage(0);
		}
    	else if(itemstack.getItemDamage() <= waterLiter*1000)
    	{
    		playerIn.setActiveHand(handIn);
    	}
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
	
	@Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
    {   
	    if (!(entityLiving instanceof EntityPlayer))
	    {
	    	return stack;
	    }
	    
        if (!world.isRemote)
        {
            EntityPlayer player = (EntityPlayer)entityLiving;

		    IPlayer exPlayer = MinecraftUtils.getPlayerCapability(player);
        	
		    exPlayer.getThirstStats().setThirst(exPlayer.getThirstStats().getThirst() + (float)waterGive/10F);

		    stack.damageItem(waterGive, player); 
		    
		    if(stack.getItemDamage() >= stack.getMaxDamage())
		    {
			    stack.damageItem(1, player); 
		    }
        }

        return stack;
    }
	
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 20*3;
    }
    
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	tooltip.add("§bContient " + waterLiter + "L d'eau");
    	
    	
    	float literLeft = (float) (stack.getMaxDamage() - stack.getItemDamage()) / 1000;
    	tooltip.add(literLeft + "L d'eau restant");
    }
  
}
