package fr.innog.data;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.world.WorldDataManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FireData implements ISaveHandler {
	
	private List<BlockPos> fireRegion = new ArrayList<>();

	public long lastFireInteractionTime;
	public long lastFireThunderTime;
	public long lastFireNaturalTime;
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		
		NBTTagList tagsList = new NBTTagList();

		for(BlockPos region : fireRegion)
		{
			NBTTagCompound regionTag = new NBTTagCompound();

			regionTag.setInteger("X", region.getX());
			regionTag.setInteger("Y", region.getY());
			regionTag.setInteger("Z", region.getZ());

			tagsList.appendTag(regionTag);
		}
		
		compound.setTag("FireRegions", tagsList);
		
		compound.setLong("LastFireInteractionTime", lastFireInteractionTime);
		compound.setLong("LastFireThunderTime",lastFireThunderTime);
		compound.setLong("LastFireNaturalTime",lastFireNaturalTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList tagsList = (NBTTagList) compound.getTag("FireRegions");
		if(tagsList != null)
		{
			for(int i = 0; i < tagsList.tagCount(); i++)
			{
				NBTTagCompound fireRegionTag = tagsList.getCompoundTagAt(i);

				fireRegion.add(new BlockPos(fireRegionTag.getInteger("X"), fireRegionTag.getInteger("Y"), fireRegionTag.getInteger("Z")));	
			}
			
		}	
		lastFireInteractionTime = compound.getLong("LastFireInteractionTime");
		lastFireThunderTime = compound.getLong("LastFireThunderTime");
		lastFireNaturalTime = compound.getLong("LastFireNaturalTime");
	}
	
	public List<BlockPos> getFires()
	{
		return fireRegion;
	}
	
	public boolean addFireRegion(World world, BlockPos pos)
	{
		if(fireRegion.contains(pos))
		{
			return false;
		}
		
		fireRegion.add(pos);
		WorldDataManager.get(world).setDirty(true);

		return true;
	}
	
	public boolean removeFireRegion(World world, int index)
	{
		if(index > fireRegion.size()-1)
		{
			return false;
		}
		
		fireRegion.remove(index);
		
		WorldDataManager.get(world).setDirty(true);

		return true;
	}
	
	public void setLastFireTime(World world, long time,int type)
	{
		if(type == 1)
		{
			lastFireNaturalTime = time;
		}
		else if(type == 2)
		{
			lastFireThunderTime = time;
		}
		WorldDataManager.get(world).setDirty(true);
	}

}
