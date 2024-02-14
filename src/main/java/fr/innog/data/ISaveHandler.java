package fr.innog.data;

import net.minecraft.nbt.NBTTagCompound;

public interface ISaveHandler {
	
	public void writeToNBT(NBTTagCompound compound);
	
	public void readFromNBT(NBTTagCompound compound);

}
