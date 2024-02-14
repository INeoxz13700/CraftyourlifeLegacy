package fr.innog.common.controllers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.common.ModCore;
import fr.innog.common.registries.UIRemoteRegistry;
import fr.innog.network.PacketCollection;
import fr.innog.ui.remote.IRemoteUI;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.RemoteUICache;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class UIRemoteController {


	
	public void displayUI(EntityPlayer player, int ID, HashMap<String, SyncStruct<?>> syncDatas, Object... args)
	{
		IPlayer exPlayer = MinecraftUtils.getPlayerCapability(player);

		try
		{
			RemoteUIProcessor remoteUIProcessor = instantiateRemoteUI(player, ID, args);
						
			try {
				CacheData remoteUICache = null;
				
				if(!exPlayer.getCachedData().identifierHaveCache(remoteUIProcessor.getIdentifier()))
				{
					remoteUICache = exPlayer.getCachedData().initCacheByIdentifier(remoteUIProcessor.getIdentifier());
				}
				else
				{
					remoteUICache = exPlayer.getCachedData().getCachesByIdentifier(remoteUIProcessor.getIdentifier());
				}
								
				if(!player.world.isRemote)
				{	
					ModCore.debug("Initialisation du remote ui - ServerSide");
					remoteUIProcessor.initRemoteUI(remoteUICache);
				}
				else
				{	
					if(syncDatas != null)
					{
						for(Map.Entry<String, SyncStruct<?>> datas : syncDatas.entrySet())
						{
							remoteUICache.setCached(datas.getKey(), datas.getValue());
						}
					}
					remoteUIProcessor.setCacheData(remoteUICache);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!player.world.isRemote)
			{
				ModCore.debug("Envoie du packet pour ouvrir le gui au client - Server Side");
				PacketCollection.openUIToPlayer(ID, (EntityPlayerMP)player, remoteUIProcessor.getDatasToSync(), args);
			}
			else
			{
				Class<? extends GuiScreen> guiScreenClass = UIRemoteRegistry.getGuiScreenClassFromRegistry(ID);
				GuiScreen guiScreen = guiScreenClass.newInstance();
				IRemoteUI remoteUI = (IRemoteUI) guiScreen;
				remoteUI.setRemoteUI(remoteUIProcessor);
				Minecraft.getMinecraft().displayGuiScreen(guiScreen);
				ModCore.debug("Ouverture du GuiScreen - Client Side");
			}
			
		}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void displayUI(EntityPlayer player, int ID, Object... args)
	{
		this.displayUI(player, ID, null, args);
	}
	
	public void displayUI(EntityPlayer player, int ID)
	{
		this.displayUI(player, ID, (Object[]) null);
	}
	
	public RemoteUIProcessor instantiateRemoteUI(EntityPlayer player, int ID, Object... args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{		
		if(player.world.isRemote)
		{
			ModCore.debug("Instantiation du remote UI - ClientSide");
		}
		else
		{
			ModCore.debug("Instantiation du remote UI - ServerSide");
		}
		
		Class<? extends RemoteUIProcessor> theClass =  UIRemoteRegistry.getRemoteUIClassFromRegistry(ID);
		
		RemoteUIProcessor remoteUIProcessor = null;
		
		if(args != null)
		{
			Constructor<?>[] constructors = theClass.getConstructors();
			
			Constructor<?> validConstructor = null;
			
			Object[] exactArgs = new Object[args.length+1];
			exactArgs[0] = player;
			for(int i = 0; i < args.length; i++)
			{
				exactArgs[i+1] = args[i];
			}
			
			outerloop:
			for(Constructor<?> constructor : constructors)
			{
				int i = 0;
				validConstructor = constructor;

				for(Parameter param : constructor.getParameters())
				{
					if(!param.getType().isAssignableFrom(exactArgs[i].getClass()))
					{
						validConstructor = null;
						continue outerloop;
					}
					i++;
				}
			}
			

			
			remoteUIProcessor = (RemoteUIProcessor) validConstructor.newInstance(exactArgs);
		}
		else
		{
			remoteUIProcessor = theClass.getConstructor(EntityPlayer.class).newInstance(new Object[] { player });
		}
	
		
		IPlayer exPlayer = MinecraftUtils.getPlayerCapability(player);
		
		exPlayer.setRemoteUI(remoteUIProcessor);
		
		return remoteUIProcessor;
	}

	
	
		
}
