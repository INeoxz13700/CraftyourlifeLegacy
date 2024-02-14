package fr.innog.common.controllers;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.common.fire.Fire;
import fr.innog.common.fire.FirePoint;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.FireData;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class FireController {

	public static List<Fire> fireActiveCount = new ArrayList<Fire>();

	public final static int maxFiresInWorld = 1;
	
	public static void startFireFromCoordinates(World world, int x, int y, int z, String cause, int maxFirePoint)
	{
		if(world.isRemote)
		{
			return;
		}
		
		if(getFireInCoordinates(x,z) != null)
		{
			return;
		}
		
		if(fireActiveCount.size() > maxFiresInWorld)
		{
			return;
		}
	
				
		if(canPlaceFire(world,x,y,z))
		{
			Fire fire = new Fire(cause,maxFirePoint,x,z);
			
			FirePoint mainPoint = new FirePoint(fire,x,y,z, null);
			fire.points.add(mainPoint);
			
			world.setBlockState(new BlockPos(x,y,z), Blocks.FIRE.getDefaultState());
			mainPoint.wasFire = true;

			fire.addPointFrom(world, mainPoint);

			fireActiveCount.add(fire);
			
			MinecraftUtils.broadcastMessage("§4[Attention] §cUn incendie à débuté dans les coordonnées §f(" + x + "," + z+")");
		}
	}
	
	public static void removeFire(Fire fire)
	{
		fireActiveCount.remove(fire);
		MinecraftUtils.broadcastMessage("§4[Attention] §cle feu aux coordonnées §f(" + fire.getX() + "," + fire.getZ() +") §cest sous contrôle");
	}
	
	public static boolean canPlaceFire(World world, int x, int y, int z)
	{
		Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
		if(block == Blocks.AIR || (block instanceof BlockBush && !(block instanceof BlockFlower)))
		{
			return true;
		}
		return false;
	}
	
	public static Fire getFireInCoordinates(int x, int z)
	{
		for(Fire fire : fireActiveCount)
		{
			if(fire.getX() == x && fire.getZ() == z)
			{
				return fire;
			}
		}
		return null;
	}
	
	public static boolean blockIsInflammable(World world, int x, int y, int z)
	{
		return world.getBlockState(new BlockPos(x, y, z)).isNormalCube();
	}
		

	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event)
	{
		if(event.phase == TickEvent.Phase.END)
		{
			if(!event.world.isRemote)
			{	
				int count = ServerUtils.getPlayerCountForJob(event.world, "pompier");
				if(count > 0)
				{
					if(event.world == FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld())
					{
						for(int i = 0; i < fireActiveCount.size(); i++)
						{
							Fire fire = fireActiveCount.get(i);
									
							fire.tick(event.world);
							
						}

						WorldDataManager worldData = WorldDataManager.get(event.world);
						
						FireData fireData = worldData.getFireData();
					
						int everyTime = 20*60*60;
						
						if(event.world.getTotalWorldTime() - fireData.lastFireNaturalTime >= everyTime)
						{
							if(fireData.getFires().size() == 0) return;
							
							BlockPos point = fireData.getFires().get(MathHelper.getInt(event.world.rand, 0, fireData.getFires().size()-1));
														
							int x = (int)point.getX();
							int z = (int)point.getZ();
							int y = getTopBlock(event.world, x, z);
	
							Block block = event.world.getBlockState(new BlockPos((int)x, (int)y, (int)z)).getBlock();
							if(block == Blocks.GRASS)
							{
								fireData.setLastFireTime(event.world, event.world.getTotalWorldTime(),1);
		
								startFireFromCoordinates(event.world, (int)x, (int) y+1,  (int)z, "Catastrophe naturelle",1000);
							}
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(WorldTickEvent event)
	{
		if(ServerUtils.getPlayerCountForJob(event.world, "pompier") > 0)
		{
			if(!event.world.isRemote && event.phase == TickEvent.Phase.END)
			{
				if(event.world.isRaining())
				{
						WorldDataManager worldData = WorldDataManager.get(event.world);
						

						if(worldData.getFireData().getFires().size() == 0) return;
						
						BlockPos pos = worldData.getFireData().getFires().get(MathHelper.getInt(event.world.rand, 0, worldData.getFireData().getFires().size()-1));
						int x = (int)pos.getX();
						int z = (int)pos.getZ();
						int y = getTopBlock(event.world, x, z);
	
						float flammability = 0f;
						if(event.world.getBlockState(new BlockPos(x, y, z)).getBlock() == Blocks.GRASS)
						{
							flammability = 0.001f;
						}
						else
						{
							flammability = (Blocks.FIRE.getFlammability(event.world.getBlockState(new BlockPos(x, y, z)).getBlock()) / 100f);
	
						}
						if(flammability > 0)
						{
							WorldDataManager data = WorldDataManager.get(event.world);
							
							if(event.world.getTotalWorldTime() - data.getFireData().lastFireThunderTime < 20 * 60 * 30)
							{
								return;
							}
							
							data.getFireData().setLastFireTime(event.world,event.world.getTotalWorldTime(), 2);
							double randomDouble = MinecraftUtils.getRandomDoubleInRange(event.world.rand, 0, 1);
							if(randomDouble >= 0.5 - flammability && randomDouble <= 0.5 + flammability)
							{

								if(canPlaceFire(event.world,x, y+1, z))
								{
									event.world.spawnEntity(new EntityLightningBolt(event.world,x, y,z, true));
									startFireFromCoordinates(event.world, x, y+1, z,"orage",1000);
								}
							}
							else
							{
								if(event.world.getBlockState(new BlockPos(x, y+1, z)) == Blocks.FIRE)
								{
									event.world.setBlockToAir(new BlockPos(x, y+1, z));
								}
							}
						}
					}
			}
		}
	}
	
	public static int getTopBlock(World world, int x, int z)
	{
		for (int j=255; j != 0; j--)
		{
			if(!canPlaceFire(world, x, j, z))
			{
				return j;
			}
		}

		return 0;
	}
}
