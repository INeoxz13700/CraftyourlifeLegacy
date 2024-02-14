package fr.innog.common.world.hospitals;

import java.util.HashMap;
import java.util.Map;

import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.ISaveHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class HospitalBed implements ISaveHandler {

	private HashMap<BlockPos, String> hospitalBeds = new HashMap<>(); 
		
	public void clearHospitalBeds(World world) 
	{
		WorldDataManager worldData = WorldDataManager.get(world);
		
		hospitalBeds.clear();
		
		worldData.setDirty(true);
	}
	
	public String getRoom(BlockPos pos)
	{
		if(hospitalBeds.containsKey(pos))
		{
			return hospitalBeds.get(pos);
		}
		return "";
	}
	
	public boolean removeHospitalBed(World world, BlockPos coordinates)
	{
		IBlockState state = world.getBlockState(coordinates);
		Block block = state.getBlock();
		
		if(block == Blocks.BED)
		{			
			if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD)
            {
				coordinates = coordinates.offset((EnumFacing)state.getValue(BlockBed.FACING));
                state = world.getBlockState(coordinates);

                if (state.getBlock() != Blocks.BED)
                {
                    return false;
                }
            }
		}
		else
		{
			return false;
		}
		
		if(!hospitalBeds.containsKey(coordinates)) return false;
		
		hospitalBeds.remove(coordinates);
		
		WorldDataManager worldData = WorldDataManager.get(world);

		worldData.setDirty(true);
		return true;
	}
	
	public boolean addHospitalBed(World world, BlockPos coordinates, String name)
	{				
		IBlockState state = world.getBlockState(coordinates);
		Block block = state.getBlock();
		
		if(block == Blocks.BED)
		{			
			if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD)
            {
				coordinates = coordinates.offset((EnumFacing)state.getValue(BlockBed.FACING));
                state = world.getBlockState(coordinates);

                if (state.getBlock() != Blocks.BED)
                {
                    return false;
                }
            }
		}
		else
		{
			return false;
		}
		
		if(hospitalBeds.containsKey(coordinates)) return false;
		
		hospitalBeds.put(coordinates,name);
		
		WorldDataManager.get(world).setDirty(true);
		
		return true;
	}
	
	public int getHospitalCount()
	{
		return hospitalBeds.size();
	}
	
	public BlockPos getRandomHospitalBed(World world)
	{
		return (BlockPos) hospitalBeds.keySet().toArray()[(MathHelper.getInt(world.rand, 0, hospitalBeds.size()-1))];
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList tagsList = new NBTTagList();
		for(Map.Entry<BlockPos, String> coordinates : hospitalBeds.entrySet())
		{
			NBTTagCompound bedTag = new NBTTagCompound();
			bedTag.setInteger("x", coordinates.getKey().getX());
			bedTag.setInteger("y", coordinates.getKey().getY());
			bedTag.setInteger("z", coordinates.getKey().getZ());
			bedTag.setString("Name", coordinates.getValue());
			tagsList.appendTag(bedTag);
		}	
		
		compound.setTag("HospitalBeds", tagsList);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		
		if(!compound.hasKey("HospitalBeds")) return;
		
		NBTTagList tagsList = (NBTTagList) compound.getTag("HospitalBeds");
		if(tagsList != null)
		{
			for(int i = 0; i < tagsList.tagCount(); i++)
			{
				NBTTagCompound bedTag = tagsList.getCompoundTagAt(i);
				BlockPos coordinates = new BlockPos(bedTag.getInteger("x"),bedTag.getInteger("y"),bedTag.getInteger("z"));
				String name = bedTag.getString("Name");
				hospitalBeds.put(coordinates, name);
			}
		}		
	}

}
