package fr.innog.common.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import fr.innog.advancedui.guicomponents.UIButton.CallBackObject;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.items.Items;
import fr.innog.common.world.WorldDataManager;
import fr.innog.handler.TicksHandler;
import fr.innog.utils.DataUtils;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class ReanimationController {
	public static List<Integer[]> reanimationBeds = new ArrayList<Integer[]>();
	public final static List<Integer> itemsToNotRemove = Arrays.asList(6557,345,6561,4541,6215,6287,6297,6391,6561);
	public final static List<Integer> itemsToForceRemoveOnReanimation = Arrays.asList(6560,6514,6558,6575,6569,6571,6576,6566,6567,6572,6573,6564,6588,6589,6116,6590,6525,6587);
	public final static int deathAfterNotReanimatedTime = 60*5;
	public final static int deathSubitProbability = 60*5;

	public static final int reanimationDurationTicks = 20*60*1;
		
	public boolean canWakeupFromBed(EntityPlayer player)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		if(playerData.getHealthData().getShouldBeReanimate() || (playerData.getHealthData().shouldBeInEthylicComa() && playerData.getHealthData().reanimatingPlayername != null))
		{
			return false;
		}
		return true;
	}
	
	public boolean damageForceWakeupFromBed(EntityPlayer player)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		if(playerData.getHealthData().getShouldBeReanimate() || playerData.getHealthData().shouldBeInEthylicComa())
		{
			return false;
		}
		return true;
	}
	
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onDoctorInteractBed(PlayerInteractEvent.EntityInteract event)
	{
		if(event.getHand() == EnumHand.OFF_HAND) return;
		
		World world = event.getWorld();
		if(!world.isRemote)
		{
			if(event.getTarget() instanceof EntityPlayer)
			{
				EntityPlayer doctorPlayer = event.getEntityPlayer();
				
				EntityPlayer interactPlayer = (EntityPlayer) event.getTarget();
				
				IPlayer interactPlayerData = MinecraftUtils.getPlayerCapability(interactPlayer);
				if(interactPlayerData.getHealthData().getShouldBeReanimate() || interactPlayerData.getHealthData().reanimatingPlayername != null)
				{
					doctorPlayer.closeScreen();
				}

				IPlayer doctorData = MinecraftUtils.getPlayerCapability(doctorPlayer);
				doctorData.reanimate(interactPlayer);
			}
		}
	}
	
	@SubscribeEvent
	public static void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if(!event.getPlayer().capabilities.isCreativeMode) return;
		
		World world = event.getWorld();
		Block block = event.getState().getBlock();
		
		if(block != Blocks.BED) return;
		
		BlockPos pos = event.getPos();
		if(!world.isRemote)
		{
			IBlockState state = event.getState();
			if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD)
            {
                pos = pos.offset((EnumFacing)state.getValue(BlockBed.FACING));
                state = world.getBlockState(pos);

                if (state.getBlock() != Blocks.BED)
                {
                    return;
                }
            }
			 
		     Iterator<EntityPlayer> iterator = world.playerEntities.iterator();

			 while (iterator.hasNext())
		     {
		          EntityPlayer entityplayer2 = (EntityPlayer)iterator.next();
		          IPlayer extendedPlayer2 = MinecraftUtils.getPlayerCapability(entityplayer2);

		          if (entityplayer2.isPlayerSleeping())
		          {
		        	   
		        	  BlockPos bedLocation = entityplayer2.bedLocation;
		            	 		            	 
		              if (bedLocation.equals(pos))
		              {
		                	if(extendedPlayer2.getHealthData().getShouldBeReanimate() || extendedPlayer2.getHealthData().reanimatingPlayername != null)
		                	{
		                		MinecraftUtils.sendMessage(event.getPlayer(), "§cVous ne pouvez pas casser ce lit pour le moment.");
		                		event.setCanceled(true);
		                		return;
		                	}
		              }
		          }
		     }
			 
			 
			 WorldDataManager worldData = WorldDataManager.get(world);
			 if(worldData.getHospitalData().removeHospitalBed(world, pos)) MinecraftUtils.sendMessage(event.getPlayer(), "§aLit d'hôpital retiré");
		}
	}
	
	@SubscribeEvent
	public static void onPlayerReanimateProcess(PlayerTickEvent event)
	{
		World world = event.player.world;
		EntityPlayer thePlayer = event.player;
	
		
		if(world.isRemote) return;
		
		if(event.phase == Phase.END) return;
		
		
		IPlayer thePlayerExtended = MinecraftUtils.getPlayerCapability(thePlayer);
		
		if(thePlayerExtended.getHealthData().getShouldBeReanimate() || thePlayerExtended.getHealthData().shouldBeInEthylicComa()) //if player is in reanimation
		{		
			if(thePlayerExtended.getHealthData().getShouldBeReanimate() && thePlayerExtended.getHealthData().reanimatingPlayername == null)
			{
				int timeElapsedInSeconds = (int) ((System.currentTimeMillis() - thePlayerExtended.getHealthData().startTimeTimestamp) / 1000f);
				if(!thePlayer.isPlayerSleeping())
				{
					ModControllers.reanimationController.subitDeath(thePlayer);
				}
				else if(thePlayer.ticksExisted % (20*5) == 0)
				{
					int leftTime = deathAfterNotReanimatedTime - timeElapsedInSeconds;
					MinecraftUtils.sendHudMessage(thePlayer, "§cIl vous reste §e" + ModControllers.reanimationController.getLeftTimeDisplay(leftTime) + " §cavant de mourir!");
				}
				
				if(timeElapsedInSeconds >= deathAfterNotReanimatedTime)
				{
					ModControllers.reanimationController.subitDeath(thePlayer);
					return;
				}
			}
			
			if(thePlayerExtended.getHealthData().reanimatingPlayername != null)
			{
				
				EntityPlayer doctorPlayer = world.getPlayerEntityByName(thePlayerExtended.getHealthData().reanimatingPlayername);
				if(doctorPlayer == null)
				{
					thePlayerExtended.getHealthData().reanimatingPlayername = null;
					thePlayerExtended.getHealthData().reanimationTick = 0;
					MinecraftUtils.sendHudMessage(thePlayer, "§cRéanimation annulé votre docteur n'est plus la!");
					return;
				}
				
				IPlayer doctorExtendedPlayer = MinecraftUtils.getPlayerCapability(doctorPlayer);
				
				if(doctorPlayer.getHeldItemMainhand().isEmpty() || doctorPlayer.getHeldItemMainhand().getItem() != Items.syringe)
				{
					thePlayerExtended.getHealthData().reanimatingPlayername = null;
					thePlayerExtended.getHealthData().reanimationTick = 0;
					doctorExtendedPlayer.getHealthData().reanimatingPlayername = null;
					MinecraftUtils.sendHudMessage(doctorPlayer, "§cRéanimation annulé vous n'avez plus votre seringue en main!");
					return;
				}
				else if(doctorPlayer.getDistanceSq(thePlayer) > 5*5)
				{
					thePlayerExtended.getHealthData().reanimatingPlayername = null;
					thePlayerExtended.getHealthData().reanimationTick = 0;
					doctorExtendedPlayer.getHealthData().reanimatingPlayername = null;
					MinecraftUtils.sendHudMessage(doctorPlayer,"§cRéanimation annulé ne vous eloignez pas de votre patient!");
					return;
				}
				
				int reanimationPercentage = (int) ((thePlayerExtended.getHealthData().reanimationTick++ / (float)reanimationDurationTicks) * 100f);
	
				MinecraftUtils.sendHudMessage(doctorPlayer,"§cRéanimation en cours : §a" + reanimationPercentage + "%");
				MinecraftUtils.sendHudMessage(thePlayer,"§cRéanimation en cours : §a" + reanimationPercentage + "%");
	
				if(reanimationPercentage >= 100)
				{					
					ModCore.getEssentials().takeMoney(thePlayer, 100);
					ModCore.getEssentials().giveMoney(doctorPlayer, 100);

					
					MinecraftUtils.sendMessage(thePlayer, "§eLes frais d'hospitalisation vous en coûté §6100 euro");
					MinecraftUtils.sendMessage(doctorPlayer, "§eLa réanimation vous a rapporté §6100 euro");
	
					thePlayerExtended.getHealthData().reanimatingPlayername = null;
					doctorExtendedPlayer.getHealthData().reanimatingPlayername = null;
					
					thePlayerExtended.onReanimated();
										
					doctorPlayer.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					
					MinecraftUtils.sendHudMessage(thePlayer, "§aVous avez survécu ! Vous pouvez vous lever.");				
				}
			}
		}
		else if(thePlayerExtended.getHealthData().reanimatingPlayername != null) //if player is Doctor
		{
			EntityPlayer victimPlayer = world.getPlayerEntityByName(thePlayerExtended.getHealthData().reanimatingPlayername);
			if(victimPlayer == null)
			{
				MinecraftUtils.sendHudMessage(thePlayer, "§cRéanimation annulé votre patient s'est déconnecté.");
				thePlayerExtended.getHealthData().reanimatingPlayername = null;
			}
		}
		
	
	}
	
	public void placePlayerInReanimation(EntityPlayer player)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		World world = player.world;
		WorldDataManager worldData = WorldDataManager.get(world);
				
		clearInventoryInReanimation(player);
		
		if(worldData.getHospitalData().getHospitalCount() == 0)
		{
			subitDeath(player);
			return;
		}
		
		BlockPos randomBed = null;
		for(int i = 0; i < worldData.getHospitalData().getHospitalCount(); i++)
		{
			randomBed = worldData.getHospitalData().getRandomHospitalBed(world);
			IBlockState state = world.getBlockState(randomBed);
			
			if(state.getBlock() != Blocks.BED)
			{
				worldData.getHospitalData().removeHospitalBed(world, randomBed);
				randomBed = null;
			}
			else
			{
				if(state.getValue(BlockBed.OCCUPIED))
				{
					randomBed = null;
					continue;
				}
				else
				{
					break;
				}
			}
		}
		
		
		if(randomBed == null)
		{
			MinecraftUtils.sendMessage(player, "§cL'hôpital est surchargé et n'a pas eu les moyens de vous hospitaliser, vous êtes donc décédé.");
			subitDeath(player);
		}
		else
		{
			player.setPositionAndUpdate(randomBed.getX(), randomBed.getY(), randomBed.getZ());
			
			final BlockPos loc = randomBed;
			
			TicksHandler.scheduleCallback(1000, new CallBackObject()
			{
				@Override
				public void call()
				{
					if(((EntityPlayerMP)player).hasDisconnected())
					{
						try 
						{
							NBTTagCompound compound = DataUtils.getDataOfflinePlayer(player.getPersistentID());
							IPlayer playerData = DataUtils.parsePlayerTag(compound);
							playerData.getHealthData().setShouldBeReanimate(true);
							DataUtils.writeDataOfflinePlayer(player.getPersistentID(), compound, playerData);
						} catch (IOException e) {
							e.printStackTrace();
						}
						return;
					}
					playerData.onReanimationEnter();
					
					ModControllers.playerController.forcePlayerSleep(player, loc);
					ServerUtils.broadcastMessageJob("Medecin", world, "§5[§dRéanimation§5] Un patient doit être soigner d'urgence dans §d" + worldData.getHospitalData().getRoom(loc));

				}
			});
		}
				
		
	}
	
	public void subitDeath(EntityPlayer player)
	{
		IPlayer extendedPlayer = MinecraftUtils.getPlayerCapability(player);
		
		if(extendedPlayer.getHealthData().reanimatingPlayername != null)
		{
			MinecraftUtils.sendHudMessage(player, "§cVous êtes entrain de vous faire réanimer vous ne pouvez pas faire de mort subite.");
			return;
		}
		
		if(player.isPlayerSleeping()) player.wakeUpPlayer(true, true, false);
		
		extendedPlayer.onRespawn();
	
		player.setPositionAndUpdate(-489, 47, -1146);
		
		MinecraftUtils.sendHudMessage(player, "§cMort subite!");
	}
	
	@SubscribeEvent
	public static void onPlayerInteractBed(PlayerInteractEvent.RightClickBlock event)
	{
		if(!event.getWorld().isRemote)
		{
			if(event.getHand() == EnumHand.OFF_HAND) return;
			
			EntityPlayer player = event.getEntityPlayer();
			if(player.capabilities.isCreativeMode)
			{
				if(!player.getHeldItemMainhand().isEmpty() &&  player.getHeldItemMainhand().getItem() == Items.syringe)
				{
					WorldDataManager worldData = WorldDataManager.get(event.getWorld());
					if(player.isSneaking())
					{
						if(worldData.getHospitalData().removeHospitalBed(event.getWorld(), event.getPos()))
        				{
        					MinecraftUtils.sendMessage(event.getEntityPlayer(), "§aLit d'hôpital supprimé avec succès!");
        				}
        				else
        				{
        					MinecraftUtils.sendMessage(event.getEntityPlayer(), "§cCe lit d'hôpital n'existe pas");
        				}
					}
					else
					{
						if(worldData.getHospitalData().addHospitalBed(event.getWorld(), event.getPos(), "Chambre " + (worldData.getHospitalData().getHospitalCount()+1)))
        				{
        					MinecraftUtils.sendMessage(event.getEntityPlayer(), "§aLit d'hôpital ajouté avec succès!");
        				}
        				else
        				{
        					MinecraftUtils.sendMessage(event.getEntityPlayer(), "§cCe lit d'hôpital existe déjà ou il n'y a pas de tête de lit dans votre clique");
        				}
					}
					event.setCanceled(true);
				}
			}
			else
			{
				World world = event.getWorld();
				if(!world.isRemote)
				{
					EntityPlayer doctorPlayer = event.getEntityPlayer();
					BlockPos pos = event.getPos();

					
					IBlockState state = world.getBlockState(event.getPos());
					
					if(state.getBlock() != Blocks.BED) 
					{
						return;
					}

					if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.HEAD)
				    {
		                pos = pos.offset((EnumFacing)state.getValue(BlockBed.FACING));
		                state = world.getBlockState(pos);

		                if (state.getBlock() != Blocks.BED)
		                {
		                    return;
		                }
				    }
					
					
					EntityPlayer interactPlayer = null;
					
					List<EntityPlayer> playerList = new ArrayList<>(world.playerEntities);

					for (EntityPlayer entityplayer : playerList)
				    {
						if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos))
				        {
							interactPlayer = entityplayer;
							break;
				        }
				    }
						
						
					if(interactPlayer != null)
					{		
						event.setCanceled(true);
						IPlayer doctorData = MinecraftUtils.getPlayerCapability(doctorPlayer);
						doctorData.reanimate(interactPlayer);
					}
				}
			}
		}
	}
	
	public String getLeftTimeDisplay(int leftTime)
	{
		
		if(leftTime < 0)
		{
			leftTime = 0;
		}
		
		int hours = (int) (leftTime  / 60 / 60); 
		int minutes = (int) (leftTime / 60) % 60;
		int seconds = (int)(leftTime % 60);


		String hoursStr = hours + "";
		if(hours <= 9)
		{
			hoursStr = "0" + hours;
		}
		
		String minutesStr = minutes + "";

		if(minutes <= 9)
		{
			minutesStr = "0" + minutes;
		}

		String secondsStr = seconds + "";

		if(seconds <= 9)
		{
			secondsStr = "0" + seconds;
		}
		
		return hoursStr + ":" + minutesStr + ":" + secondsStr;

	}
	
	public void clearInventoryInReanimation(EntityPlayer player)
	{
		for(int i = 0; i < player.inventory.mainInventory.size(); i++)
		{
			ItemStack is = player.inventory.mainInventory.get(i);
			
			if(is.isEmpty()) continue;
			
			if(itemsToForceRemoveOnReanimation.contains(Item.getIdFromItem(is.getItem())))
			{
				player.inventory.mainInventory.set(i, ItemStack.EMPTY);
			}
		}
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		for(int i = 0; i < playerData.getInventory().getSlots(); i++)
		{
			playerData.getInventory().setStackInSlot(i, ItemStack.EMPTY);
		}
	}
	
	public void clearInventory(EntityPlayer player)
	{
		for(int i = 0; i < player.inventory.mainInventory.size(); i++)
		{
			ItemStack is = player.inventory.mainInventory.get(i);
			
			if(is.isEmpty()) continue;
			
			if(itemsToNotRemove.contains(Item.getIdFromItem(is.getItem())))
			{
				continue;
			}
			
			player.inventory.mainInventory.set(i, ItemStack.EMPTY);
		}
		
		for(int i = 0; i < player.inventory.armorInventory.size(); i++)
		{
			ItemStack is = player.inventory.armorInventory.get(i);
			
			if(is.isEmpty()) continue;
			
			player.inventory.armorInventory.set(i, ItemStack.EMPTY);
		}
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		for(int i = 0; i < playerData.getInventory().getSlots(); i++)
		{
			playerData.getInventory().setStackInSlot(i, ItemStack.EMPTY);
		}
	}
	
	@SubscribeEvent
	public static void onSleep(PlayerSleepInBedEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		World world = player.world;
		BlockPos bedLocation = event.getPos();
		
		IBlockState state = world.isBlockLoaded(event.getPos()) ? world.getBlockState(bedLocation) : null;
        
		if(state == null) return;
		
		final boolean isBed = state != null && state.getBlock().isBed(state, world, bedLocation, player);
        final EnumFacing enumfacing = isBed && state.getBlock() instanceof BlockHorizontal ? (EnumFacing)state.getValue(BlockHorizontal.FACING) : null;

        if (!world.isRemote)
        {
            if (player.isPlayerSleeping() || !player.isEntityAlive())
            {
                event.setResult(SleepResult.OTHER_PROBLEM);
                return;
            }
            
            
            if (((Boolean)state.getValue(BlockBed.OCCUPIED)).booleanValue())
            {
				List<EntityPlayer> playerList = new ArrayList<>(world.playerEntities);

            	EntityPlayer playerInBed = null;
            	for (EntityPlayer entityplayer : playerList)
                {
                    if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(bedLocation))
                    {
                    	playerInBed = entityplayer;
                    	break;
                    }
                }

                if (playerInBed != null)
                {
                	MinecraftUtils.sendMessage(player, "§cCe lit est déjà utilisé.");
                    event.setResult(SleepResult.NOT_POSSIBLE_NOW);
                	return;
                }

                state = state.withProperty(BlockBed.OCCUPIED, Boolean.valueOf(false));
                world.setBlockState(bedLocation, state, 4);
            }
            else
            {
                state = state.withProperty(BlockBed.OCCUPIED, Boolean.valueOf(true));
                world.setBlockState(bedLocation, state, 4);
            }
            

            if(!playerData.getHealthData().getShouldBeReanimate())
            {
                if (!ModControllers.playerController.bedInRange(player,bedLocation, enumfacing))
                {
                    event.setResult(SleepResult.TOO_FAR_AWAY);
                    return;
                }
            }

        }

        if (player.isRiding())
        {
        	player.dismountRidingEntity();
        }

        if(!player.world.isRemote) ModCore.getAnimationManager().stopAnimation(player);
        
        MinecraftUtils.setPlayerSize(player, 0.2F, 0.2F);

        if (enumfacing != null) {
            float f1 = 0.5F + (float)enumfacing.getFrontOffsetX() * 0.4F;
            float f = 0.5F + (float)enumfacing.getFrontOffsetZ() * 0.4F;
            
            player.renderOffsetX = -1.8F * (float)enumfacing.getFrontOffsetX();
            player.renderOffsetZ = -1.8F * (float)enumfacing.getFrontOffsetZ();
            
            player.setPosition((double)((float)bedLocation.getX() + f1), (double)((float)bedLocation.getY() + 0.6875F), (double)((float)bedLocation.getZ() + f));
        }
        else
        {
        	player.setPosition((double)((float)bedLocation.getX() + 0.5F), (double)((float)bedLocation.getY() + 0.6875F), (double)((float)bedLocation.getZ() + 0.5F));
        }

        ReflectionHelper.setPrivateValue(EntityPlayer.class, player, true, 26);
        ReflectionHelper.setPrivateValue(EntityPlayer.class, player, 0, 28);

        player.bedLocation = bedLocation;
        player.motionX = 0.0D;
        player.motionY = 0.0D;
        player.motionZ = 0.0D;
        
        player.prevRotationPitch = 0F;
        player.rotationPitch = 0F;

        if (!player.world.isRemote)
        {
        	player.world.updateAllPlayersSleepingFlag();
        }

        event.setResult(SleepResult.OK);
	}
	
	@SubscribeEvent
	public static void onSleepTimer(SleepingTimeCheckEvent event)
	{
        ReflectionHelper.setPrivateValue(EntityPlayer.class, event.getEntityPlayer(), 0, 28);
        
		event.setResult(Result.ALLOW);
	}
	
	public static void onPlayerWakeUP(PlayerWakeUpEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		
		if(playerData.getHealthData().getShouldBeReanimate())
		{
			ModControllers.reanimationController.subitDeath(player);
		}
	}
	


	
	
	
	
	
}
