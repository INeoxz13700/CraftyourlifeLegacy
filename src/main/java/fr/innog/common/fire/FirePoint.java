package fr.innog.common.fire;

import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FirePoint {

	public int x;
	public int y;
	public int z;
	
	public FirePoint linkedTo;


	
	private float inflammability; //between 0 and 1 more inflammability is high more fire progression is fast
	
	private float inflammation = 0.1f;
	
	private Fire fire;
	
	public boolean wasFire = false;
	
	
	
	public FirePoint(Fire fire,int x, int y, int z, FirePoint linkedTo)
	{		
		this.fire = fire;
		
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.linkedTo = linkedTo;
	}
	
	public void setInflammation(float value)
	{
		this.inflammation = value;
	}
	
	public void tick(World world)
	{
		IBlockState blockBelowState = world.getBlockState(new BlockPos(x, y-1, z));
		IBlockState blockState = world.getBlockState(new BlockPos(x, y, z));

		inflammability = Blocks.FIRE.getFlammability(blockBelowState.getBlock()) / 100f;
		
		
		if(blockState.getBlock() != Blocks.FIRE)
		{
			if(world.getTotalWorldTime() % 20 == 0)
			{
				inflammation += MinecraftUtils.getRandomDoubleInRange(world.rand, 0, (inflammability * 0.1f) + 0.050f);
			}
			
			if(linkedTo != null)
			{
				if(world.getBlockState(new BlockPos(linkedTo.x, linkedTo.y, linkedTo.z)).getBlock() != Blocks.FIRE)
				{
					fire.removeFirePoint(world, x, y, z);
				}
				
				if(inflammation >= 1f)
				{
					if(wasFire)
					{
						fire.removeFirePoint(world, x, y, z);
					}
					else
					{
						world.setBlockState(new BlockPos(x, y, z), Blocks.FIRE.getDefaultState());
						wasFire = true;
						fire.addPointFrom(world, this);
					}
				}
				
				
			}
			else //Main point
			{
				
				if(fire.points.size() == 1 && fire.points.get(0) == this && world.getBlockState(new BlockPos(x, y, z)) == Blocks.AIR.getDefaultState())
				{
					fire.removeFirePoint(world, x, y, z);
				}
			}
			
			
		}
		else
		{
			
			if(world.getTotalWorldTime() % 20 == 0) 
			{
				inflammation -= MinecraftUtils.getRandomDoubleInRange(world.rand, 0, 0.0000002510D);
			
				if(world.isRaining())
				{
					inflammation -= MinecraftUtils.getRandomDoubleInRange(world.rand, 0, 0.0142D);
					if(blockState.getBlock() == Blocks.AIR)
					{
						inflammation -= MinecraftUtils.getRandomDoubleInRange(world.rand, 0, 0.00845);
					}
				}
			}

			
			
			if(inflammation < 0)
			{

				if(blockState.getBlock() == Blocks.FIRE)
				{
					world.setBlockToAir(new BlockPos(x, y, z));
				}
				fire.removeFirePoint(world, x, y, z);
			}
		}
		
				
		
	}
	

}