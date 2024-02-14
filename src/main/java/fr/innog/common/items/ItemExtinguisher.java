package fr.innog.common.items;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import fr.innog.common.ModCore;
import fr.innog.common.items.interact.IItemPress;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemExtinguisher extends Item implements IItemPress {
	
	public static int maxMoneyEarn = 0;
	public static int minMoneyEarn = 0;

	public ItemExtinguisher(int maxItemUseDuration)
	{
		setMaxStackSize(1);
		setMaxDamage(maxItemUseDuration);
	}


	@Override
	public void onItemUsing(EntityPlayer player, World worldObj, ItemStack is, int itemPressedTicksCount)
	{
		if(player.getHeldItemMainhand().getItem() == this)
		{

			if(player.world.isRemote)
			{
				Vec3d dir = player.getLookVec();
						
				player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX-0.5f+dir.x, player.posY+player.eyeHeight-0.2+dir.y, player.posZ+dir.z, dir.x*0.6f, dir.y*1f, dir.z*0.6f);
		    	player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX+dir.x, player.posY+player.eyeHeight+dir.y, player.posZ+dir.z, dir.x*0.6f, dir.y*1f, dir.z*0.6f);
		    	player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX+dir.x, player.posY+player.eyeHeight-0.2+dir.y, player.posZ+dir.z, dir.x*0.6f, dir.y*1f, dir.z*0.6f);
		    	player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX+dir.x, player.posY+player.eyeHeight-0.2+dir.y, player.posZ+0.5+dir.z, dir.x*0.6f, dir.y*1f, dir.z*0.6f);
		    	player.world.spawnParticle(EnumParticleTypes.CLOUD, player.posX+dir.x, player.posY+player.eyeHeight-0.4+dir.y, player.posZ+dir.z, dir.x*0.6f, dir.y*1f, dir.z*0.6f);
			}
			else
			{
				
		    	if(player.ticksExisted % 10 == 0)
		    	{
			    	
				    RayTraceResult raytrace = MinecraftUtils.rayTraceServer(player, 5.0F, 0.0F);;
				    	

				    if(raytrace == null) return;
						
				    if(raytrace.typeOfHit == Type.ENTITY)
					{
						Entity entity = raytrace.entityHit;
						if(entity.isBurning())
						{
							int rand = MathHelper.getInt(new Random(), 0, 100);
							if(rand >= 40 && rand <= 60)
							{
								entity.extinguish();
								if(ServerUtils.isPompier(player))
								{
									if(ServerUtils.isPompier(player))
									{
						    			 int money = MathHelper.getInt(player.getRNG(), minMoneyEarn, maxMoneyEarn);
						    			 ModCore.getEssentials().giveMoney(player, money);
						    			 MinecraftUtils.sendMessage(player, "§6Vous avez gagné §e" + money + "$ §6pour avoir éteint une flamme.");
									}			
									ModCore.getEssentials().giveMoney(player, MathHelper.getInt(player.getRNG(), minMoneyEarn, maxMoneyEarn));
								}
							}
						}
					}
				    else if(raytrace.typeOfHit == Type.BLOCK)
				    {
				    	 IBlockState state = player.world.getBlockState(raytrace.getBlockPos());
				              
				    	 int rand = MathHelper.getInt(new Random(), 0, 10);
					     if (state.getBlock() == Blocks.FIRE)
					     {

					    	 if (rand >= 1 && rand <= 7)
					         {
					    		 worldObj.setBlockToAir(raytrace.getBlockPos());
					    		 if(ServerUtils.isPompier(player))
								 {
					    			 int money = MathHelper.getInt(player.getRNG(), minMoneyEarn, maxMoneyEarn);
					    			 ModCore.getEssentials().giveMoney(player, money);
					    			 MinecraftUtils.sendMessage(player, "§6Vous avez gagné §e" + money + "$ §6pour avoir éteint une flamme.");
								 }					        
					    	 }  
					     }
					              
					     state = player.world.getBlockState(raytrace.getBlockPos().up());
     
					     if (state.getBlock() == Blocks.FIRE)
					     {
						       if (rand >= 1 && rand <= 7)
						       {
						        	 worldObj.setBlockToAir(raytrace.getBlockPos().up());
						        	 if(ServerUtils.isPompier(player))
									 {
						    			 int money = MathHelper.getInt(player.getRNG(), minMoneyEarn, maxMoneyEarn);
						    			 ModCore.getEssentials().giveMoney(player, money);
						    			 MinecraftUtils.sendMessage(player, "§6Vous avez gagné §e" + money + "$ §6pour avoir éteint une flamme.");
									 }									       
						        }  
					     }
				              
				    }
			    	player.world.playSound(null,player.getPosition(), new SoundEvent(new ResourceLocation("craftyourliferp:extinguisher")),SoundCategory.PLAYERS, 1.0F, 1.0F);
			    }
		    }
		 	
			
		}
		
	}


	@Override
	public void onItemRightClicked(EntityPlayer player, World worldObj, ItemStack heldItem) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onItemStopUsing(EntityPlayer player, World worldObj, ItemStack heldItem, int itemPressedTicksCount) {
		// TODO Auto-generated method stub
		
	}









}