package fr.innog.data;

import fr.innog.common.ModCore;
import fr.innog.network.PacketCollection;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MathsUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerHealthData implements ISaveHandler {

    private float gAlcolInBlood;
    
    public int tickAlcol;
    
	private boolean shouldBeReanimate = false;

	public int reanimationTick = 0;

	public String reanimatingPlayername;
	
	private EntityPlayer player;
			
	public long startTimeTimestamp = 0;
	
	public int sleepingTime;
	
	public PlayerHealthData(EntityPlayer player)
	{
		this.player = player;
	}


	public float getAlcolInBlood() {
		return gAlcolInBlood;
	}

	public void setAlcolInBlood(float alcol) {
    	if(player != null && !player.world.isRemote)
    	{
    		gAlcolInBlood = MathsUtils.Clamp(alcol, 0.0F, 4.0F);
    		
    		syncAlcol();
    		
    	}
    	else
    	{
    		this.gAlcolInBlood = alcol;
    	}
	}

	public void setShouldBeReanimate(boolean shouldBeReanimate) {
		this.shouldBeReanimate = shouldBeReanimate;
		if(player != null && !player.world.isRemote)
		{
			syncShouldBeReanimate();
		}		
	}

	public boolean getShouldBeReanimate() {
		return shouldBeReanimate;
	}

	public boolean shouldBeInEthylicComa() {
		return this.gAlcolInBlood >= 4f;
	}
	
	public void syncShouldBeReanimate()
	{
		PacketCollection.syncDataTo("ShouldBeReanimate", new SyncStruct<Boolean>(shouldBeReanimate), player);
	}
	
    public void syncAlcol()
    {
    	PacketCollection.syncDataTo("Alcol", new SyncStruct<Float>(gAlcolInBlood), player);
    }

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setFloat("gAlcolInBlood", gAlcolInBlood);
		compound.setBoolean("ShouldBeReanimate", shouldBeReanimate);
	}


	@Override
	public void readFromNBT(NBTTagCompound compound) {
		shouldBeReanimate = compound.getBoolean("ShouldBeReanimate");
        gAlcolInBlood = compound.getFloat("gAlcolInBlood");
	}



	
}

