package fr.innog.common.fire;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.controllers.FireController;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Fire 
{
	
	public final int maxFirePoint;
	
	public String cause;
	
	private int x;
	private int z;
	
	public long fireStartTime;
	
	public List<FirePoint> points = new ArrayList<>();

	public Fire(String cause, int maxFirePoint, int x, int z)
	{
		this.maxFirePoint = maxFirePoint;
		this.cause = cause;
		
		this.x = x;
		this.z = z;
		
		this.fireStartTime = System.currentTimeMillis();
	}
	
	public boolean addPointFrom(World world, FirePoint point)
	{
		if(world.isRemote)
		{
			return false;
		}
		
		
		int i = 0;
		
		if(points.size()+4 >= maxFirePoint)
		{
			return false;
		}

		int x = point.x;
		int y = point.y;
		int z = point.z;
		
		FireController fireController = ModControllers.fireController;
		
		for(i = -2; !FireController.canPlaceFire(world,x,y+i,z+1) && i < 2;i++)  
		{
			
		}
				
		if((Blocks.FIRE.getFlammability(world.getBlockState(new BlockPos(x,y+i-1,z+1)).getBlock()) > 0) || world.getBlockState(new BlockPos(x,y+i-1,z+1)).getBlock() == Blocks.GRASS) 
			if(!alreadyFirePoint(x,y+i,z+1)) points.add(new FirePoint(this,x,y+i,z+1,point));
		
		
		for(i = -2; !FireController.canPlaceFire(world,x-1,y+i,z) && i < 2;i++)  
		{
			
		}
		
		if(FireController.canPlaceFire(world,x-1,y+i,z) && (Blocks.FIRE.getFlammability(world.getBlockState(new BlockPos(x-1,y+i-1,z)).getBlock()) > 0) || world.getBlockState(new BlockPos(x-1,y+i-1,z)).getBlock() == Blocks.GRASS)
			if(!alreadyFirePoint(x-1,y+i,z)) points.add(new FirePoint(this,x-1,y+i,z,point));

		for(i = -2; !FireController.canPlaceFire(world,x+1,y+i,z) && i < 2;i++) 
		{
			
		}
		
		if(FireController.canPlaceFire(world,x+1,y+i,z) && (Blocks.FIRE.getFlammability(world.getBlockState(new BlockPos(x+1,y+i-1,z)).getBlock()) > 0) || world.getBlockState(new BlockPos(x+1,y+i-1,z)).getBlock() == Blocks.GRASS)
			if(!alreadyFirePoint(x+1,y+i,z)) points.add(new FirePoint(this,x+1,y+i,z,point));
		
		for(i = -2; !FireController.canPlaceFire(world,x,y+i,z-1) && i < 2;i++) 
		{
			
		}
		
		if(FireController.canPlaceFire(world,x,y+i,z-1) && (Blocks.FIRE.getFlammability(world.getBlockState(new BlockPos(x,y+i-1,z-1)).getBlock()) > 0) || world.getBlockState(new BlockPos(x,y+i-1,z-1)).getBlock() == Blocks.GRASS)
			if(!alreadyFirePoint(x,y+i,z-1)) points.add(new FirePoint(this,x,y+i,z-1,point));
		
		return true;
	}
	
	public boolean alreadyFirePoint(int x, int y, int z)
	{
		for(FirePoint fire : points)
		{
			if(fire.x == x && fire.y == y && fire.z == z)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public void removeFirePoint(World world, int x, int y, int z)
	{
		FirePoint point = null;
		
		for(FirePoint firePoint : points)
		{
			if(firePoint.x == x && firePoint.y == y && firePoint.z == z)
			{
				point = firePoint;
				break;
			}
		}
		
		points.remove(point);

		if(world.getBlockState(new BlockPos(x, y, z)) == Blocks.FIRE)
		{
			world.setBlockToAir(new BlockPos(x, y, z));
		}
		
	}
		
	public void tick(World world)
	{
		for(int i = 0; i < points.size(); i++)
		{
			FirePoint point = points.get(i);

			point.tick(world);
		}
				
		if((System.currentTimeMillis() - fireStartTime) / 1000 >= 60*5)
		{
			fireStartTime = System.currentTimeMillis();
			
			MinecraftUtils.broadcastMessage("§4[Attention] §cle feu aux coordonnées §f(" + getX() + "," + z+") §cest toujours en cours");
		}
		
		if(points.size() == 0)
		{
			ModControllers.fireController.removeFire(this);
		}
	}

	public int getX() 
	{
		return x;
	}


	public int getZ() 
	{
		return z;
	}
	
	
	
}