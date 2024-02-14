package fr.innog.common.controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;

import fr.dynamx.api.events.VehicleEntityEvent;
import fr.dynamx.api.events.VehicleEntityEvent.PlayerInteract.InteractionType;
import fr.dynamx.common.contentpack.parts.PartSeat;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.items.DynamXItem;
import fr.dynamx.common.items.DynamXItemArmor;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerData;
import fr.innog.client.event.DataSyncEvent;
import fr.innog.client.model.ModModelBody;
import fr.innog.client.model.ModModelHead;
import fr.innog.client.ui.ingame.SleepingUI;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticCachedData;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.common.entity.EntityLootableBody;
import fr.innog.common.inventory.CustomInventoryPlayer;
import fr.innog.common.inventory.container.CustomInventoryContainer;
import fr.innog.common.thirst.ThirstStats;
import fr.innog.common.tiles.IStealingTileEntity;
import fr.innog.data.AvatarData;
import fr.innog.data.EthylotestRequest;
import fr.innog.network.PacketCollection;
import fr.innog.phone.NetworkCallTransmitter;
import fr.innog.server.adapter.datas.ProtectedRegion;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDoor.EnumDoorHalf;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class PlayerController {

	public void heal(EntityPlayer player)
	{
		player.setHealth(20);
		player.getFoodStats().addStats(20, 5F);
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		playerData.getHealthData().setAlcolInBlood(0F);

		playerData.getThirstStats().setThirst(ThirstStats.initialThirst);

		player.extinguish();
		player.clearActivePotions();
	}
	
	@SubscribeEvent
	public static void onDataSync(DataSyncEvent event)
	{
		EntityPlayer player = event.getPlayer();
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player); 
		if(event.getKey().equals("ShouldBeReanimate"))
		{
			playerData.getHealthData().setShouldBeReanimate((Boolean)event.getData().getData());
			if(Minecraft.getMinecraft().currentScreen instanceof SleepingUI)
			{
				SleepingUI gui = (SleepingUI) Minecraft.getMinecraft().currentScreen;
				gui.updateGui();
			}
		}
		else if(event.getKey().equals("Alcol"))
		{
			playerData.getHealthData().setAlcolInBlood((Float)event.getData().getData());
		}
	}

	
	@SubscribeEvent
    public static void onDamage(LivingAttackEvent event)
    {
    	if(event.getSource() != null && event.getSource().getTrueSource() != null && !event.getSource().getTrueSource().world.isRemote)
    	{
        	if(event.getSource().getTrueSource() instanceof EntityPlayer)
        	{
        		EntityPlayer attacker = (EntityPlayer)event.getSource().getTrueSource();

        		IPlayer attackerData = MinecraftUtils.getPlayerCapability(attacker);
         		
        	
    	        if(attackerData.getCurrentPlayingAnimation() != null)
    	        {
    	        	event.setCanceled(true);
    	        }
    	        

        	}
    	}

    	if(event.getEntityLiving() instanceof EntityPlayer)
		{
    		
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			
			if(player.isPlayerSleeping())
			{
				IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
				if(playerData.getHealthData().getShouldBeReanimate())
				{

					event.setCanceled(true);
				}
				else if(playerData.getHealthData().shouldBeInEthylicComa())
				{
					
	    			player.setHealth(player.getHealth( )- event.getAmount());
	    			
	    			if(player.world.isRemote)
	    			{
	    				player.performHurtAnimation();
	    			}
	    			else
	    			{
	    				EntityPlayerMP playerMP = (EntityPlayerMP) player; // Convertissez le joueur en joueur multi-joueurs
	    				SPacketAnimation animationPacket = new SPacketAnimation(playerMP, 1); // Le deuxième argument '1' représente le type d'animation de blessure

	    				// Envoyez le packet à tous les joueurs connectés
	    				playerMP.getServerWorld().getEntityTracker().sendToTracking(player, animationPacket);
	    			}
					event.setCanceled(true);
				}
			}
		}
    }
	
	@SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent e)
    {
		if(e.getEntityLiving() instanceof EntityPlayer) 
    	{
    		EntityPlayer victim = (EntityPlayer) e.getEntityLiving();

    		IPlayer playerData = MinecraftUtils.getPlayerCapability(victim);

	    	if(!e.getEntityLiving().world.isRemote)
	    	{	
		    	if(playerData.getIdentityData().waitingDataFromClient)
				{
					e.setCanceled(true);
			        e.setAmount(0f);
				}
		    	

		    }	
	    	
	    	for(int i = 0; i < playerData.getInventory().getSlots(); i++)
	    	{
	    		ItemStack is = playerData.getInventory().getStackInSlot(i);
	    		
	    		if(i == 1)
	    		{
	    			if(!is.isEmpty())
	    			{
	    				if(e.getSource().getTrueSource() instanceof Entity)
	    				{
	    		    		is.damageItem((int) e.getAmount(), victim);
	    					e.setAmount(e.getAmount()/2.5F);
	    				}
	    			}

	    		}
	    	}
	    	
	    	
	    	
	    	
    	}
    }
	
	
	@SubscribeEvent
	public static void onPlayerUseEthylotest(VehicleEntityEvent.PlayerInteract event)
	{
		if(event.getPlayer().world.isRemote) return;
		
		if(event.getPlayer().getHeldItemMainhand().getItem() != fr.innog.common.items.Items.ethylotest) return;
		
		if(event.getInteractionType() == InteractionType.PART)
		{
			if(event.getPart() == null) return;
			
			if(event.getPart() instanceof PartSeat)
			{
				PartSeat seat = (PartSeat)event.getPart();
				if(seat.isDriver())
				{
					BaseVehicleEntity<?> vehicle = event.getEntity();
					
					Entity targetEntity = vehicle.getControllingPassenger();
					
					if(targetEntity instanceof EntityPlayer)
					{
						EntityPlayer player = event.getPlayer();
						EntityPlayer target = (EntityPlayer) targetEntity;

						
						IPlayer targetData = MinecraftUtils.getPlayerCapability(target);
					
						
						MinecraftUtils.sendMessage(player, "§6Vous avez demandez à §e" + target.getName() + " §6de souffler sur l'éthylotest en attente de sa réponse. (gardez l'éthylotest en main)");

						
						targetData.setEthylotestRequest(new EthylotestRequest(player.getName()));
						
				        TextComponentString message = new TextComponentString("Un FDL veut prélever votre taux d'alcoolémie, voulez-vous souffler sur l'éthylotest?");
				        
				        TextComponentString oui = new TextComponentString(" [OUI]");
				        oui.getStyle().setColor(TextFormatting.GREEN);
				        oui.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ethylotest OUI"));
				        
				        TextComponentString non = new TextComponentString(" [NON]");
				        non.getStyle().setColor(TextFormatting.RED);
				        non.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ethylotest NON"));
				        
				        // Ajoutez les parties du message au message principal
				        message.appendSibling(oui);
				        message.appendSibling(new TextComponentString(" "));
				        message.appendSibling(non);
				        target.sendMessage(message);
					}
					
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void onPlayerInteractParrot(PlayerInteractEvent.EntityInteract event)
	{
		if(event.getWorld().isRemote) return;

		if(event.getTarget() instanceof EntityParrot)
		{
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerUseEthylotest(PlayerInteractEvent.EntityInteract event)
	{
		if(event.getWorld().isRemote) return;
		
		if(event.getHand() == EnumHand.OFF_HAND) return;
			
		if(event.getEntityPlayer().getHeldItemMainhand().getItem() != fr.innog.common.items.Items.ethylotest) return;
		
		EntityPlayer player = (EntityPlayer) event.getEntityLiving();
		
		if(event.getTarget() instanceof EntityPlayer)
		{
			EntityPlayer target = (EntityPlayer) event.getTarget();

			
			IPlayer targetData = MinecraftUtils.getPlayerCapability(target);
		
			
			MinecraftUtils.sendMessage(player, "§6Vous avez demandez à §e" + target.getName() + " §6de souffler sur l'éthylotest en attente de sa réponse. (gardez l'éthylotest en main)");

			
			targetData.setEthylotestRequest(new EthylotestRequest(player.getName()));
			
	        TextComponentString message = new TextComponentString("Un FDL veut prélever votre taux d'alcoolémie, voulez-vous souffler sur l'éthylotest?");
	        
	        TextComponentString oui = new TextComponentString(" [OUI]");
	        oui.getStyle().setColor(TextFormatting.GREEN);
	        oui.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ethylotest OUI"));
	        
	        TextComponentString non = new TextComponentString(" [NON]");
	        non.getStyle().setColor(TextFormatting.RED);
	        non.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ethylotest NON"));
	        
	        // Ajoutez les parties du message au message principal
	        message.appendSibling(oui);
	        message.appendSibling(new TextComponentString(" "));
	        message.appendSibling(non);
	        target.sendMessage(message);
		}
	}
	
	
    public boolean bedInRange(EntityPlayer player, BlockPos bedLocation, EnumFacing bedDirection)
    {
        if (Math.abs(player.posX - (double)bedLocation.getX()) <= 3.0D && Math.abs(player.posY - (double)bedLocation.getY()) <= 2.0D && Math.abs(player.posZ - (double)bedLocation.getZ()) <= 3.0D)
        {
            return true;
        }
        else if (bedDirection == null) return false;
        else
        {
            BlockPos blockpos = bedLocation.offset(bedDirection.getOpposite());
            return Math.abs(player.posX - (double)blockpos.getX()) <= 3.0D && Math.abs(player.posY - (double)blockpos.getY()) <= 2.0D && Math.abs(player.posZ - (double)blockpos.getZ()) <= 3.0D;
        }
    }
	
	public void forcePlayerSleep(EntityPlayer player, BlockPos bedLocation)
	{
		player.trySleep(bedLocation);
	}
	
	public void forcePlayerWakeup(EntityPlayer player)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		if(!playerData.getHealthData().getShouldBeReanimate())
		{
			player.wakeUpPlayer(true, true, false);
		}
		else
		{
			MinecraftUtils.sendHudMessage(player, "§cVous êtes actuellement en attente de réanimation vous ne pouvez pas vous lever");
		}
	}
	
	@SubscribeEvent
	public static void onPlayerDeath(LivingDeathEvent event)
	{
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
									
			NetworkCallTransmitter activeCall = NetworkCallTransmitter.getByUsername(player.getName());
    		if(activeCall != null)
    		{
    			activeCall.finishCall();
    		}
    					
            Vector3f pos = new Vector3f((float)player.posX, (float)player.posY+1, (float)player.posZ);
            
			EntityLootableBody entity = new EntityLootableBody(player.world, player.rotationYaw % 360.0F, pos, player.getName());
			
			player.world.spawnEntity(entity);
			
			ServerUtils.broadcastMessageJob("Medecin", entity.world, "§6[Cadavre] §eUn cadavre est apparu en x=" + pos.x +  " y=" + pos.y +  " z=" + pos.z + " Il faut le placer dans un congélateur mortuaire");
			
			ModCore.getAnimationManager().stopAnimation(player);
			
		}
	}
	
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event)
	{
		if(event.getWorld().isRemote)
		{
			if(event.getEntity() instanceof AbstractClientPlayer)
			{	
				if(event.getEntity().isEntityAlive())
				{
					AbstractClientPlayer abstractPlayer = (AbstractClientPlayer) event.getEntity();

					new AvatarData(abstractPlayer).updateAvatar();
				}
			}
			else if(event.getEntity() instanceof EntityLootableBody) 
			{
				EntityLootableBody lootableBody = (EntityLootableBody) event.getEntity();
						
				new AvatarData(lootableBody.getSkin()).updateAvatar();
			}
			
		}
		
		if(event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
            if(!(player.openContainer instanceof CustomInventoryContainer)) {
    			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
    			player.openContainer = new CustomInventoryContainer(player, playerData.getInventory());
    			player.inventoryContainer = player.openContainer;
            }
		}
		
	}
	
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRenderLiving(RenderLivingEvent.Specials.Pre<EntityPlayer> event) {
		
		if (event.getEntity() instanceof EntityPlayer) {
			if (event.isCancelable()) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRender(RenderPlayerEvent.Pre event)
	{
		List<CosmeticObject> data = new ArrayList<CosmeticObject>();
		
		if(event.getEntityPlayer() == Minecraft.getMinecraft().player)
		{
			IPlayer capability = MinecraftUtils.getPlayerCapability(event.getEntityPlayer());
			
			if(capability == null) return;
			
			data = capability.getCosmeticDatas().cosmeticsData;
		}
		else
		{			
			CosmeticCachedData cachedData = CosmeticCachedData.getData(event.getEntityPlayer().getEntityId());
			
			
			if(cachedData.cosmeticsData != null) data = cachedData.cosmeticsData;
		}
		
		
		for(int i = 0; i < data.size(); i++)
		{
			CosmeticObject obj = data.get(i);
						
			if(!obj.getIsEquipped()) continue;
			
			if(obj.getModel() instanceof ModModelBody)
			{
				ModModelBody model = (ModModelBody) obj.getModel();
				model.render(event.getEntityPlayer(), event.getEntityPlayer().limbSwing, event.getEntityPlayer().limbSwingAmount, event.getRenderer().getMainModel().bipedHead.rotateAngleY,  event.getRenderer().getMainModel().bipedHead.rotateAngleY,  event.getRenderer().getMainModel().bipedHead.rotateAngleX, event.getPartialRenderTick());
			}
			else
			{
				ModModelHead model = (ModModelHead) obj.getModel();
				
				model.render(event.getEntityPlayer(), event.getEntityPlayer().limbSwing, event.getEntityPlayer().limbSwingAmount, event.getRenderer().getMainModel().bipedHead.rotateAngleY,  event.getRenderer().getMainModel().bipedHead.rotateAngleY,  event.getRenderer().getMainModel().bipedHead.rotateAngleX, event.getPartialRenderTick());
			}
		} 
	}
	
	@SubscribeEvent
	public static void onPlaceBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getWorld().isRemote) return;
		
		if(event.getHand() == EnumHand.OFF_HAND) return;
		
		Block block = Block.getBlockFromItem(event.getEntityPlayer().getHeldItemMainhand().getItem());
		
		if(block instanceof fr.innog.common.blocks.BlockPainting)
		{
			if(!MinecraftUtils.isPlayerOp((EntityPlayerMP)event.getEntityPlayer())) event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event)
	{
		EntityPlayer player = event.player;

		IPlayer d = MinecraftUtils.getPlayerCapability(event.player);
		CustomInventoryPlayer customInventory = d.getInventory();
    	ItemStack is = customInventory.getStackInSlot(1);
    	
    	if(is.isEmpty()) is = player.inventory.armorInventory.get(2);
    	
    	if(!is.isEmpty())
    	{
    		int left = is.getMaxDamage() - is.getItemDamage();
    		d.getShieldStats().setShield((float)left / is.getMaxDamage() * 100,false);
    	}
    	else
    	{
    		d.getShieldStats().setShield(0f, false);
    	}
		
		if(player.world.isRemote)
		{
			if(Minecraft.getMinecraft().player == player)
			{
				if(player.getHealth() < player.getMaxHealth() * 0.1)
				{
					player.setSprinting(false);
					if(player.ticksExisted % 100 == 0)
					{
						MinecraftUtils.sendMessage(player, "Vous êtes dans une mauvaise posture vous devez appeler les urgences.");
					}
					
					if(!player.isPlayerSleeping())
					{
						player.motionX *= 0.01;
						if(player.motionY > 0)
						{
							player.motionY *= -10;
						}

						player.motionZ *= 0.01;
					}
				}
				else if(player.getHealth() < player.getMaxHealth() / 2f)
				{
					player.motionX *= 0.65;
					player.motionY *= 0.999985;
					player.motionZ *= 0.65;
				}
			}
			
			return;
		}
		else
		{
			try {
				if(player.ticksExisted % 20 == 0)
				{
					List<ProtectedRegion> regions = ModCore.getWorldGuard().getApplicableRegions(player.getPositionVector());
					for(ProtectedRegion rg : regions)
					{
						if(rg.getId().contains("clandestin") || rg.getId().contains("medecin"))
						{
							if(!d.getHealthData().shouldBeInEthylicComa() && !d.getHealthData().getShouldBeReanimate() && player.isPlayerSleeping())
							{
								
								player.setHealth(player.getHealth()+ 0.05F);
									
								MinecraftUtils.sendMessage(player, "§cPV : " + String.format("%.2f", player.getHealth()) +  "/20");
								
								
								if(player.ticksExisted % 100 == 0)
								{
									MinecraftUtils.sendMessage(player, "§cVotre repos vous fait gagner des points de vie. ");
								}
							}
							break;
						}
					}
				}
				
			} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
		
			
			if(!customInventory.getStackInSlot(1).isEmpty() && !player.inventory.armorInventory.get(2).isEmpty())
			{

				ItemStack itemstackReplace = player.inventory.armorInventory.get(2).copy();
				Item item = player.inventory.armorInventory.get(2).getItem();

				if(item instanceof DynamXItemArmor)
				{
					DynamXItemArmor<?> dynamXItem = (DynamXItemArmor<?>) item;
					if(dynamXItem.getInfo().getDescription().toLowerCase().contains("gilet pare-balles"))
					{
						player.inventory.armorInventory.set(2, ItemStack.EMPTY);
						if(!player.inventory.addItemStackToInventory(itemstackReplace))
						{
							player.dropItem(dynamXItem, 1);
						}
						MinecraftUtils.sendMessage(player, "Vous ne pouvez pas équiper 2 gilets pare-balles en même temps.");
					}
				}
			}
		}
		
		if(event.phase == Phase.END) return;
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		if(playerData != null) playerData.getPlayerRenderSynchronizer().update();
		
		
		
		if(!player.world.isRemote)
		{	
			if(player.isPlayerSleeping())
			{
					
				++playerData.getHealthData().sleepingTime;
			
					
				if(playerData.getHealthData().sleepingTime > 100)
				{
					if(playerData.getHealthData().getAlcolInBlood() < 4.0F)
					{
						playerData.getHealthData().setAlcolInBlood(playerData.getHealthData().getAlcolInBlood()-0.00040F);
					}
				}
			}
			
			float playergAlcolInBlood = playerData.getHealthData().getAlcolInBlood();
	
			
			if(playerData.getHealthData().getAlcolInBlood() > 0)
			{
				if(!playerData.getHealthData().shouldBeInEthylicComa() && playerData.getHealthData().tickAlcol++ >= 20*60*10)
				{
					playerData.getHealthData().tickAlcol = 0;
					playergAlcolInBlood -= 0.25F;
					playerData.getHealthData().setAlcolInBlood(playergAlcolInBlood);
				}
					
				int time = (int) ((playerData.getHealthData().getAlcolInBlood()*4)*50/4);
				int effectPower = (int) (4-(4-playerData.getHealthData().getAlcolInBlood()));
					
	
				if(effectPower > 0)
				{
					player.addPotionEffect(new PotionEffect(Potion.getPotionById(2),time,effectPower-1));
					player.addPotionEffect(new PotionEffect(Potion.getPotionById(9),time,effectPower-1));
				}
					
			}
				
			if(playerData.getHealthData().getAlcolInBlood() >= 4)
			{
				if(MathHelper.getInt(player.getRNG(), 0, 20*15 / (player.world.getDifficulty().ordinal()+1)) == 0)
				{
					player.attackEntityFrom(new DamageSource("alcol"), 0.5F);
				}
			}
				
			
			if(playerData.getStealingTile() == null) return;
			
			if(player.ticksExisted % 5 == 0)
			{
				if(playerData.isStealing())
				{
					String jobName = null;
					try {
						jobName = ModCore.getCylrp().getJobName(player);
					} catch (ClassNotFoundException | NoSuchFieldException | SecurityException
							| IllegalArgumentException | IllegalAccessException | NoSuchMethodException
							| InstantiationException | InvocationTargetException e) {
						e.printStackTrace();
					}
					
					
					if(jobName == null || !ServerUtils.isIlegalJob(jobName))
					{
						MinecraftUtils.sendMessage(player, "§cVous ne pouvez pas braquer, vous êtes en hors service ou n'avez pas le métier nécessaire");
						
						playerData.stopStealing();
						return;
					}
					
					RayTraceResult result = MinecraftUtils.rayTraceServer(player,4.0F, 1.0F);
					
					TileEntity tileEntity = (TileEntity) player.world.getTileEntity(result.getBlockPos());
					
					if(tileEntity == null)
					{
						tileEntity = (TileEntity) player.world.getTileEntity(result.getBlockPos());
					}
				
					TileEntity dataTile = (TileEntity) playerData.getStealingTile();
					
					if(player.getHeldItemMainhand().getItem() != Items.AIR)
					{
						playerData.stopStealing();
						MinecraftUtils.sendMessage(player, "§cVol annulé vous ne devez rien avoir en main");
					}
					
					if(tileEntity == null || !(tileEntity instanceof IStealingTileEntity) || dataTile != tileEntity)
					{
						playerData.stopStealing();
						MinecraftUtils.sendMessage(player,"§cVol annulé rapprochez-vous ou visez bien le block");
					}
				}
	
	
			}
			
			if(player.ticksExisted % 10 == 0)
			{
				if(playerData.isStealing())
				{
					
					if((System.currentTimeMillis() - playerData.getStealingTile().getStealingStartedTime()) / 1000 >= playerData.getStealingTile().getStealingTime())
					{
						playerData.getStealingTile().finalizeStealing();	
					}
				}
			}
		}
		
		
	}
	
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerRespawnEvent event)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(event.player);
		
		EntityPlayer victim = event.player;
		
		if(victim.world != FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld()) return;
		
		if(playerData.getHealthData().shouldBeInEthylicComa())
		{
			ModControllers.reanimationController.subitDeath(event.player);
			return;
		}
		
		if(!playerData.getHealthData().getShouldBeReanimate())
		{	
			ModControllers.reanimationController.placePlayerInReanimation(victim);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerInteractDoor(PlayerInteractEvent.RightClickBlock event)
	{
		World world = event.getWorld();
		
		if(!world.isRemote) return;
		
		if(event.getHand() == EnumHand.OFF_HAND) return;
		
		
		ItemStack mainHandItem = event.getEntityPlayer().getHeldItemMainhand();
		
		if(mainHandItem != ItemStack.EMPTY && Item.getIdFromItem(mainHandItem.getItem()) == PlayerData.crowbarId)
		{
			BlockPos pos = event.getPos();
			
			if(world.isRemote) 
			{
				PacketCollection.crochetDoor(pos);
				return;
			}
		}
	}
	
	public static void crochetDoor(EntityPlayer player, BlockPos pos)
	{
		World world = player.world;
		IBlockState state = world.getBlockState(pos);
		
		if(state.getBlock() instanceof BlockDoor)
		{
			if(state.getValue(BlockDoor.HALF) == EnumDoorHalf.UPPER)
			{
				pos = pos.down();
				state = world.getBlockState(pos);
			}
			
			if(state.getValue(BlockDoor.OPEN))
			{
				return;
			}
		}
		else if(state.getBlock() instanceof BlockTrapDoor)
		{
			if(state.getValue(BlockTrapDoor.OPEN))
			{
				return;
			}
		}
		else
		{
			return;
		}
			
			
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			
		if(playerData.getCrowbarState() == null)
		{
			playerData.crowbarDoor(pos);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerBreakingDoor(PlayerTickEvent event)
	{
		World world = event.player.world;
		
		if(world.isRemote) return;
		
		if(event.phase == TickEvent.Phase.END) return;
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(event.player);
		
		if(playerData.getCrowbarState() == null) return;
		
		if(event.player.ticksExisted % 10 == 0)
		{
			if(playerData.getCrowbarState().playerMoved(event.player.getPosition()))
			{
				MinecraftUtils.sendHudMessage(playerData.getPlayer(), "§cVous avez bougé. Action annulée.");
				playerData.clearCrowbarState();
				return;
			}
			
			if(playerData.getCrowbarState().getState() < 100)
			{
				playerData.getCrowbarState().incrementState();
				MinecraftUtils.sendHudMessage(playerData.getPlayer(), "§aCrochetage de la porte : §b" + playerData.getCrowbarState().getState() + " %");
			}
			else
			{
				playerData.getCrowbarState().onDoorBreaked(world, playerData);
				playerData.clearCrowbarState();
			}
		}
	}
	
	


}
