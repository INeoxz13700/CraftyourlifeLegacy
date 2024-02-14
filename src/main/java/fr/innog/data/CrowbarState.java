package fr.innog.data;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CrowbarState {
	

	private BlockPos doorPosition;
	
	private BlockPos playerPosition;
	
	private int state = 0;
	
	public CrowbarState(BlockPos doorPosition, BlockPos playerPosition)
	{
		this.doorPosition = doorPosition;
		this.playerPosition = playerPosition;
	}
	
	public BlockPos getDoorPosition()
	{
		return doorPosition;
	}
	
	public int getState()
	{
		return state;
	}
	
	public void incrementState()
	{
		state++;
		
		if(state > 100)
		{
			state = 100;
		}
	}
	
	public void onDoorBreaked(World world, IPlayer playerData)
	{
		IBlockState state = world.getBlockState(playerData.getCrowbarState().getDoorPosition());
		
		if(state.getBlock() instanceof BlockDoor)
		{
			world.setBlockState(playerData.getCrowbarState().getDoorPosition(), state.withProperty(BlockDoor.OPEN, true));
		}
		else if(state.getBlock() instanceof BlockTrapDoor)
		{
			world.setBlockState(playerData.getCrowbarState().getDoorPosition(), state.withProperty(BlockTrapDoor.OPEN, true));
		}
				
		MinecraftUtils.sendHudMessage(playerData.getPlayer(), "§aVous avez forcé l'ouverture de la porte!");
				
	}
	
	public boolean playerMoved(BlockPos playerPosition)
	{
		return this.playerPosition.getX() != playerPosition.getX() || this.playerPosition.getY() != playerPosition.getY() || this.playerPosition.getZ() != playerPosition.getZ();
	}
	
	
	
	
	
}
