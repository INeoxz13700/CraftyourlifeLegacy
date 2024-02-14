package fr.innog.common.items;

import java.util.List;

import javax.annotation.Nullable;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.Int;

public class ItemAlcohol extends ItemWaterBottle {
	
	private float gPerLAlcol;

	public ItemAlcohol(float waterLiter, int waterGive, float gPerLAlcol)
	{
		super(waterLiter, waterGive);
		this.gPerLAlcol = gPerLAlcol;
		this.setMaxStackSize(1);
	}
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {	
		ItemStack itemstack = playerIn.getHeldItem(handIn);

		if(handIn == EnumHand.OFF_HAND)  return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);

		
		 playerIn.setActiveHand(handIn);
         return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
	
	@Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
    {   
	    if (!(entityLiving instanceof EntityPlayer))
	    {
	    	return stack;
	    }
	    
        EntityPlayer player = (EntityPlayer)entityLiving;

        if (!world.isRemote)
        {

            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP)player, stack);

	        super.onItemUseFinish(stack, world, entityLiving);
	        
	        IPlayer exPlayer = MinecraftUtils.getPlayerCapability(player);

		    exPlayer.getHealthData().setAlcolInBlood(exPlayer.getHealthData().getAlcolInBlood() + gPerLAlcol);
		    if(exPlayer.getHealthData().shouldBeInEthylicComa())
		    {
		    	MinecraftUtils.sendMessage(player, "§cVous êtes sur le point de faire un coma éthylique, allez vite à l'hôpital ou appelez les urgences!");
		    }		
		    
		    float pv = gPerLAlcol*3;
		    MinecraftUtils.sendMessage(player, "L'alcool soulage vos douleurs vous gagnez " + pv + " PV. (Attention à ne pas en abuser)");
		    player.setHealth(player.getHealth()+pv);
		    
		    stack.damageItem(stack.getMaxDamage()+1, entityLiving);
		
        }

        return stack;
    }
	
    public int getMaxItemUseDuration(ItemStack stack)
    {
        return 20*4;
    }
    
    public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
    	tooltip.add(new String("§61 verre = §e" + this.gPerLAlcol + "g dans le sang"));
   
    	tooltip.add(new String(""));
    	super.addInformation(stack, worldIn, tooltip, flagIn);
    }
  
}
