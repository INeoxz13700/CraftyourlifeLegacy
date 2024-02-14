package fr.innog.common.thirst;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.data.ISaveHandler;
import fr.innog.utils.MathsUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;

public class ThirstStats implements ISaveHandler {
	
	  public static final float maxThirst = 20f;
	  public static final float initialThirst = 20f;
	    
	  private EntityPlayer player;
	    
	  private float thirst;
	  
	  public float prevThirst;
	  

	    public ThirstStats(EntityPlayer p) {
	    	this.player = p;
	    	this.setThirst(initialThirst);
	    }
	    
	    public float getThirst() 
	    {
	    	return this.thirst;
	    }
	    
	    public float getThirstNormalized() 
	    {
	    	return this.thirst  / maxThirst;
	    }
	    
	    public void setThirst(float value)
	    {
	    	if(player != null && !player.world.isRemote)
	    	{
	    		thirst = MathsUtils.Clamp(value, 0.0F, maxThirst);
	    		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
	    		if(playerData != null)
	    		{
	    			playerData.syncThirst();
	    		} 
	    	}
	    	else {
	    		thirst = MathsUtils.Clamp(value, 0.0F, maxThirst);
	    	}
	    }
	    
	    //Thirst logic
	    public void onUpdate(EntityPlayer p_75118_1_)
	    {
	        EnumDifficulty enumdifficulty = p_75118_1_.world.getDifficulty();
	        this.prevThirst = this.thirst;
	        int difficulty = enumdifficulty.ordinal();


	        if(difficulty == 0 || p_75118_1_.capabilities.isCreativeMode)
	        {
	        	return;
	        }
	        
	        
	        float ratioThirstGain = ((difficulty) * (p_75118_1_.isSprinting() ? 2 : 1)) * 0.25f;
	        if(MathHelper.getInt(p_75118_1_.getRNG(), 0, p_75118_1_.isSprinting() ? 300 : 1000) == 0)
	        {
		        setThirst(getThirst() - ratioThirstGain);
	        }
	        
	        if(thirst <= 5.0D)
	        {
	        	p_75118_1_.setSprinting(false);
	        }
	        
	        if(thirst <= 0 && p_75118_1_.ticksExisted % 20 == 0)
	        {
	        	p_75118_1_.attackEntityFrom(DamageSource.STARVE, 0.5F);
	        }
	    }

		@Override
		public void writeToNBT(NBTTagCompound compound) {
			compound.setFloat("Thirst", thirst);
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			thirst = compound.getFloat("Thirst");
		}
	    
	    



}