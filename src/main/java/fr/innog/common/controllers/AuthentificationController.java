package fr.innog.common.controllers;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.innog.advancedui.guicomponents.UIButton.CallBackObject;
import fr.innog.api.informations.ApiInformations;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.data.LoginInformation;
import fr.innog.data.NumberData;
import fr.innog.data.UserSession;
import fr.innog.handler.TicksHandler;
import fr.innog.network.PacketCollection;
import fr.innog.network.packets.decrapted.PacketOpenTelephone;
import fr.innog.utils.DataUtils;
import fr.innog.utils.MigrateUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class AuthentificationController {

    public final static int authentificationAttemptEveryMinutes = 5;
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event)
	{
		EntityPlayer player = event.player;
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		LoginInformation login = playerData.getLoginInformation();

		if(player.ticksExisted % 20 == 0)
		{	

			if(login.connectionPos != null)
			{
				if(player.posX != login.connectionPos.x || player.posY != login.connectionPos.y || player.posZ != login.connectionPos.z)
				{		
					player.setPositionAndUpdate(login.connectionPos.x, login.connectionPos.y, login.connectionPos.z);
				}
		    }
			
			if(!player.world.isRemote)
			{
				
				if(!player.getLeftShoulderEntity().hasNoTags())
				{		
		            try {
		            	Method setLeftShoulderEntity = EntityPlayer.class.getDeclaredMethod("func_192029_h", NBTTagCompound.class);
						setLeftShoulderEntity.setAccessible(true); // Permet d'accéder à une méthode protégée ou privée
						setLeftShoulderEntity.invoke(player, new NBTTagCompound());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
				
				if(!player.getRightShoulderEntity().hasNoTags())
				{
		            try {
		            	Method setRightShoulderEntity = EntityPlayer.class.getDeclaredMethod("func_192031_i", NBTTagCompound.class);
		            	setRightShoulderEntity.setAccessible(true); 
		            	setRightShoulderEntity.invoke(player, new NBTTagCompound());
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
	@SubscribeEvent
	public static void itemEvent(ItemTossEvent event)
	{
		if(!event.getEntity().world.isRemote)
		{
			EntityPlayer player = (EntityPlayer) event.getPlayer();

			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

			LoginInformation login = playerData.getLoginInformation();

	        
			if(!login.passwordReceived)
			{
				player.inventory.addItemStackToInventory(event.getEntityItem().getItem());
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent event)
	{
		EntityPlayer player = event.getEntityPlayer();
		if(!player.world.isRemote)
	    {
		    IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
			LoginInformation login = playerData.getLoginInformation();

			if(!login.passwordReceived)
			{
				event.setCanceled(true);
			}  
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
         		
        		if(!attackerData.getLoginInformation().passwordReceived)
        		{
        			event.setCanceled(true);
        		}
        	}
    	}
    }
 	
 	@SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent e)
    {
 		if(!e.getEntityLiving().world.isRemote)
    	{
	    	if(e.getEntityLiving() instanceof EntityPlayer) 
	    	{
	    		
	    		EntityPlayer victim = (EntityPlayer) e.getEntityLiving();

	    		IPlayer playerData = MinecraftUtils.getPlayerCapability(victim);
	    		
	    		if(!playerData.getLoginInformation().passwordReceived)
	    		{
					e.setCanceled(true);
		        	e.setAmount(0f);
	    		}
	    	}
    	}
    }
    
	@SubscribeEvent
	public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
	{
		
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		LoginInformation login = playerData.getLoginInformation();

		if(player.getServer().isDedicatedServer())
		{	
			if(ModCore.debugMode)
			{
				login.passwordReceived = true;
		  		MinecraftUtils.dispatchConsoleCommand("apidata password " + player.getName());
				ModControllers.authController.onPlayerAuthentificated(player);

			}
			else
			{
		    	player.setInvisible(true);
		    	login.connectionPos = player.getPositionVector();
				PacketCollection.askClientLoginData(player);
			}
		}
		else
		{
			login.passwordReceived = true;
			ModControllers.authController.onPlayerAuthentificated(player);
		}

	}
	
	public void onPlayerAuthentificated(EntityPlayerMP player)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

		LoginInformation login = playerData.getLoginInformation();

		
		if(login.isFirstConnection())
		{
			try {
				DataUtils.writeUUIDToDisk(player);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(playerData.getPhoneData().getNumber().isEmpty())
			{
				String number = NumberData.generateNumber(player.getName());
				try {
					NumberData.saveNumber(number, player);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				playerData.getPhoneData().setNumber(number);
			}
			
    		if(player.getServer().isDedicatedServer())
    		{
    			MigrateUtils.migrateOldCosmetics(player);
	    		playerData.getIdentityData().waitingDataFromClient = true;
				ModControllers.uiController.displayUI(player, 5);
    		}
		}
		else if(playerData.getIdentityData().lastname.isEmpty())
		{
			if(player.getServer().isDedicatedServer())
			{
	    		playerData.getIdentityData().waitingDataFromClient = true;
				ModControllers.uiController.displayUI(player, 5);
			}
		}
		
		PacketCollection.updatePlayerCosmeticRenderData(player, player, playerData.getCosmeticDatas().cosmeticsData);

		playerData.syncLoginData();

		ModCore.getPackethandler().sendTo(new PacketOpenTelephone(true), (EntityPlayerMP) player); 

		
		playerData.getPenaltyManager().updatePenalties();
		
	}
	
	public void handleAuthentification(EntityPlayerMP player, String cryptedPassword, String connectionData)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
				
		LoginInformation login = playerData.getLoginInformation();
		
		
		if(login.authentificationAttempt >= authentificationAttemptEveryMinutes)
		{
			if((System.currentTimeMillis() - login.authentificationAttemptResetTime) / 1000 >= 60)
			{
				login.authentificationAttempt = 0;
				login.authentificationAttemptResetTime = System.currentTimeMillis();
			}
			else
			{
				return;
			}
		}	
		
		login.authentificationAttempt++;

		
		Thread connectionThread = new Thread(new Runnable() 
		{
		      @Override
		      public void run() 
		      {
		    	    Object[] cData = UserSession.connectUser(player.getName(), cryptedPassword, ApiInformations.blacklist_systemkey + "," + connectionData);
		  		
			  		if(!(boolean)cData[3])
			  		{
			  			TicksHandler.scheduleCallback(1000, new CallBackObject()
			  			{
			  				
			  				public void call()
			  				{
					  			player.connection.disconnect(new TextComponentString((String)cData[2]));
			  				}
			  				
			  			});
			  			return;
			  		}
			  		else
			  		{
			  			MinecraftUtils.sendMessage(player, (String)cData[2]);
			  		}
			  		
			  		player.setInvisible(false);
			  		
			  		login.passwordReceived = true;
			  		MinecraftUtils.dispatchConsoleCommand("apidata password " + player.getName());
			  		onPlayerAuthentificated(player);
			  		login.connectionPos = null;
		      }
		 }, "AuthentificationThread");

		connectionThread.setDaemon(true);
		connectionThread.start();
	}
	
	
}
