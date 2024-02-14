package fr.innog.common.tiles;

import net.minecraft.entity.EntityLivingBase;

public interface IStealingTileEntity {

	public int getStealingTime();
	
	public String getDisplayMessageInLook();
	
	public EntityLivingBase getThiefEntity();
	
	public long getStealingStartedTime();
	
	public void resetStealing();
	
	public void setStealingEntity(EntityLivingBase entity);
	
	public void finalizeStealing();


}