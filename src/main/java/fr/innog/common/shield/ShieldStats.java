package fr.innog.common.shield;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.data.ISaveHandler;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ShieldStats implements ISaveHandler {

    public static final float maxShield = 100f;
    public static final float initialShield = 0f;
    
    private EntityPlayer player;
    
    private float shield;
    
    public ShieldStats(EntityPlayer p) {
    	this.player = p;
    	shield = initialShield;
    }
    
    public float getShield() {
    	if(player != null && !player.world.isRemote)
    	{
    		return this.shield;
    	}
    	return this.shield;
    }
    
    public void setShield(float value, boolean sync) {
    	if(player != null && !player.world.isRemote)
    	{
    		this.shield = Math.min(value, maxShield);
    		
    		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

    		if(sync)playerData.syncShield();
    	}
    	else {
    		this.shield = Math.min(value, maxShield);
    		if(this.shield < 0)
    		{
    			this.shield = 0;
    		}
    	}
    }

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setFloat("Shield", shield);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		shield = compound.getFloat("Shield");
	}
	
}
