package fr.innog.data;

import fr.innog.utils.DataUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class BlockData implements ISaveHandler
{
		
		public int timeToBackup;
		
		public long timeSinceBlockWaiting;
		
		public BlockPos position;
		
		public int blockId;
		
		public int metaData;
		
		public BlockData()
		{
			
		}
		
		public BlockData(int blockId,int metaData, BlockPos position, int timeToBackup)
		{
			this.blockId = blockId;
			this.position = position;
			this.timeToBackup = timeToBackup;
			this.timeSinceBlockWaiting = System.currentTimeMillis();
			this.metaData = metaData;
		}

		@Override
		public void writeToNBT(NBTTagCompound compound)
		{
			compound.setInteger("TimeToRegen", timeToBackup);
			compound.setLong("TimeSinceBlockWaiting", timeSinceBlockWaiting);
			DataUtils.writeBlockPosToNBT("Position", position, compound);
			compound.setInteger("MetaData", metaData);
			compound.setInteger("BlockId", blockId);
		}

		@Override
		public void readFromNBT(NBTTagCompound compound) {
			timeToBackup = compound.getInteger("TimeToRegen");
			timeSinceBlockWaiting = compound.getLong("TimeSinceBlockWaiting");
			position = DataUtils.readBlockPosFromNBT("Position", compound);
			metaData = compound.getInteger("MetaData");
			blockId = compound.getInteger("BlockId");
		}
		
}
