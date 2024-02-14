package fr.innog.data;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.utils.DataUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlocksBackup implements ISaveHandler {

	
	private	List<BlockData> blocks = new ArrayList<>();
	
	public final static int updateTime = 60*15; //In seconds
	
	public BlocksBackup()
	{
		
	}
	
	public void addBlock(World world, BlockData blockData)
	{
		blocks.add(blockData);
		
	
		WorldDataManager.get(world).markDirty();
	}
	
	public void update(World world)
	{
		List<BlockData> toRemove = new ArrayList<BlockData>();
		
		for(BlockData bData : blocks)
		{
			if((System.currentTimeMillis() - bData.timeSinceBlockWaiting) / 1000 >= bData.timeToBackup)
			{
				toRemove.add(bData);

				regenBlock(world, bData);
			}
		}
		
		blocks.removeAll(toRemove);
	}
	
	private void regenBlock(World world, BlockData blockData)
	{
		Block block = Block.getBlockById(blockData.blockId);
		
		if(block instanceof BlockDoor)
		{
		    BlockDoor doorBlock = (BlockDoor) block;
		    IBlockState lowerDoorState = doorBlock.getStateFromMeta(blockData.metaData);
		    IBlockState upperDoorState = doorBlock.getDefaultState().withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER);
		    world.setBlockState(blockData.position, lowerDoorState);
		    world.setBlockState(blockData.position.up(), upperDoorState); 
		    
		    return;
		}
		world.setBlockState(blockData.position, block.getStateFromMeta(blockData.metaData));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList blockTags = new NBTTagList();
		
		for(BlockData block : blocks)
		{
			NBTTagCompound blockTag = new NBTTagCompound();
			block.writeToNBT(blockTag);
			blockTags.appendTag(blockTag);
		}
		
		compound.setTag("BlocksData", blockTags);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		NBTTagList blockTags = (NBTTagList) compound.getTag("BlocksData");
		
		for(int i = 0; i < blockTags.tagCount(); i++)
		{
			NBTTagCompound blockTag = blockTags.getCompoundTagAt(i);
			BlockData blockData = new BlockData();
			blockData.readFromNBT(blockTag);
			blocks.add(blockData);
		}
	}
	
	
	
}


