package fr.innog.common.world;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.data.BlockData;
import fr.innog.data.BlocksBackup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class WorldHandler {
	
	 @SubscribeEvent
	 public static void onExplosion(ExplosionEvent.Detonate event) 
	 {
		 World world = event.getWorld();
		 if(!world.isRemote)
	     {
			 WorldDataManager data = WorldDataManager.get(world);
		    	
			 
		     List<BlockPos> affectedBlocks = event.getAffectedBlocks();
		     List<BlockPos> toRemove = new ArrayList<>();
		     for(BlockPos pos : affectedBlocks)
	    	 {
		    	 IBlockState state = world.getBlockState(pos);
	    		 Block block = state.getBlock();
	    		 
	    		 if(block instanceof BlockDoor)
	    		 {
	    			 if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER)
	    			 {
	    				 pos = pos.down();
	    				 state = world.getBlockState(pos);
	    			 }
	    		     int metadata = block.getMetaFromState(state);
	    			 
	    			 data.getBlocksBackup().addBlock(world, new BlockData(Block.getIdFromBlock(block),metadata,pos,BlocksBackup.updateTime));
		    		 world.setBlockToAir(pos); 
	    		 }
	    		 else if(block instanceof BlockTrapDoor || block instanceof BlockGlass || block instanceof BlockStainedGlass || block instanceof BlockPane)
	    		 {
	    			data.getBlocksBackup().addBlock(world, new BlockData(Block.getIdFromBlock(block),block.getMetaFromState(state),pos,BlocksBackup.updateTime));
	    			world.setBlockToAir(pos);
	    		 }
	    		 else
	    		 {
	    			 toRemove.add(pos);
	    		 }
	    	 }
		     
		     affectedBlocks.removeAll(toRemove);
		     

	     }	
	    	
	    	
	  }
	 
	  @SubscribeEvent
	  public static void onWorldTick(WorldTickEvent event)
	  {
	    	if(event.phase == Phase.START)
	    	{
		    	if(!event.world.isRemote)
		    	{
		    		if(!event.world.getWorldInfo().getWorldName().equalsIgnoreCase("cyl")) return;
		    		
		    		WorldDataManager worldData = WorldDataManager.get(event.world);
			    	
			    	if(event.world.getTotalWorldTime() % (20 * BlocksBackup.updateTime) == 0)
			    	{
			    		worldData.getBlocksBackup().update(event.world);
			    	}

		    	}
	    	}
	  }

}
