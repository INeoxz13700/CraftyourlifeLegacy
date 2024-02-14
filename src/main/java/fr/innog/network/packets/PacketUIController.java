package fr.innog.network.packets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.network.PacketCollection;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.RemoteMethod;
import fr.innog.ui.remote.data.RemoteMethodCallback.ActionResult;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketUIController extends PacketBase {

	/*
	 * 0 : open UI
	 * 1 : sync data to client
	 * 2 : execute remote function
	 * 3 : execute remote function with result
	 * 4 : sendResult
	 */
	private byte packetType;
	
	private int id;
	
	private Object[] remoteArgs;
	
	private String methodName;
	
	private ActionResult result;
	
	private HashMap<String, SyncStruct<?>> datasToSync;
	
	public PacketUIController()
	{
		
	}
	
	public PacketUIController(int type)
	{
		this.packetType = (byte) type;
	}
	
	public static PacketUIController openUI(int uiId)
	{
		return openUI(uiId,null, null);
	}
	
	public static PacketUIController openUI(int uiId, HashMap<String, SyncStruct<?>> initDatas, Object[] remoteArgs)
	{
		PacketUIController packet = new PacketUIController(0);
		
		packet.id = uiId;
		
		packet.datasToSync = initDatas;
		
		packet.remoteArgs = remoteArgs;
		
		return packet;
	}
	
	public static PacketUIController executeMethod(String methodName, Object... args)
	{
		PacketUIController packet = new PacketUIController(2);
		
		packet.methodName = methodName;
		packet.remoteArgs = args;
		
		return packet;
	}
	
	public static PacketUIController executeMethodWithResult(String methodName, int id, Object... args)
	{
		PacketUIController packet = new PacketUIController(3);
		
		packet.methodName = methodName;
		packet.remoteArgs = args;
		packet.id = id;
		
		return packet;
	}
	
	public static PacketUIController sendRemoteMethodResult(int id, ActionResult result)
	{
		PacketUIController packet = new PacketUIController(4);
		
		packet.result = result;
		packet.id = id;
		
		return packet;
	}
	
	public static PacketUIController syncDataToClient(HashMap<String, SyncStruct<?>> data)
	{
		PacketUIController packet = new PacketUIController(1);
		
		packet.datasToSync = data;
		
		return packet;
	}
	
	public void putData(String key, SyncStruct<?> data)
	{
		datasToSync.put(key, data);
	}
	
	public byte encodeState() {
	    byte state = 0;
	    if (datasToSync == null) {
	        state |= 0x01;
	    }
	    if (remoteArgs == null) {
	        state |= 0x02;
	    }
	    return state;
	}
	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(packetType);
		
		if(packetType == 3 || packetType == 4)
		{
			data.writeInt(id);
		}
		
		if(packetType == 0)
		{
			data.writeInt(id);
			
			byte state = encodeState();
			
			data.writeByte(state);

			if(remoteArgs != null)
			{			
				NetworkUtils.serialize(remoteArgs, data);	
			}
			
			if(datasToSync != null)
			{
				NetworkUtils.serialize(datasToSync, data);
			}
			
			
		}
		else if(packetType == 1)
		{
			NetworkUtils.serialize(datasToSync, data);	
		}
		else if(packetType == 2 || packetType == 3)
		{
			ByteBufUtils.writeUTF8String(data, methodName);
			if(remoteArgs != null)
			{			
				NetworkUtils.serialize(remoteArgs, data);	
			}
		}
		else if(packetType == 4)
		{
			data.writeInt(result.ordinal());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		packetType = data.readByte();
		
		if(packetType == 3 || packetType == 4)
		{
			id = data.readInt();
		}
		
		if(packetType == 0)
		{
			id = data.readInt();
			
			byte state = data.readByte();
		    boolean datasToSyncIsNull = (state & 0x01) != 0;
		    boolean remoteArgsIsNull = (state & 0x02) != 0;

		    if (!remoteArgsIsNull) 
		    {
				remoteArgs = (Object[]) NetworkUtils.deserialize(data);
		    }
		    
		    if (!datasToSyncIsNull) 
		    {
				datasToSync = (HashMap<String, SyncStruct<?>>) NetworkUtils.deserialize(data);
		    } 
		}
		else if(packetType == 1)
		{
			datasToSync = (HashMap<String, SyncStruct<?>>) NetworkUtils.deserialize(data);
		}
		else if(packetType == 2 || packetType == 3)
		{
			methodName = ByteBufUtils.readUTF8String(data);
			remoteArgs = (Object[]) NetworkUtils.deserialize(data);
		}
		else if(packetType == 4)
		{
			this.result = ActionResult.values()[data.readInt()];
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{ 
		if(packetType == 0) 
		{
			ModControllers.uiController.displayUI(playerEntity, id, null, remoteArgs);
		}
		else if(packetType == 2 || packetType == 3)
		{
			IPlayer playerData = playerEntity.getCapability(PlayerProvider.PLAYER_CAP, null);
						
			Class<?>[] parameters = new Class<?>[remoteArgs.length];
            for (int i = 0; i < remoteArgs.length; i++) {
            	parameters[i] = remoteArgs[i].getClass();
            }
            
            try 
            {
            	if(playerData.getCurrentRemoteUI() == null) return;
            	
				Method methode = playerData.getCurrentRemoteUI().getClass().getMethod(methodName, parameters);
				if(methode.isAnnotationPresent(RemoteMethod.class))
				{

					try 
					{
						if(packetType == 3)
						{
							if(methode.getReturnType() != ActionResult.class)
							{
								throw new IllegalArgumentException("Une méthode remote qui attend un résultat doit avoir un type de retour " + ActionResult.class.getSimpleName());
							}
							
							ActionResult result = (ActionResult) methode.invoke(playerData.getCurrentRemoteUI(), remoteArgs);
							PacketCollection.sendResult(id, result, playerEntity);
						}
						else
						{
							methode.invoke(playerData.getCurrentRemoteUI(), remoteArgs);
						}
					
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
            } catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) 
	{
		if(packetType == 0)
		{
			ModControllers.uiController.displayUI(clientPlayer, id, datasToSync, remoteArgs);
		}		
		else if(packetType == 1)
		{		
			IPlayer player = MinecraftUtils.getPlayerCapability(clientPlayer);
			RemoteUIProcessor processor = (RemoteUIProcessor) player.getCurrentRemoteUI();
		
			
			CacheData cacheData = processor.getCacheData();
		
			for(Map.Entry<String, SyncStruct<?>> datas : this.datasToSync.entrySet())
			{
				cacheData.setCached(datas.getKey(), datas.getValue());
			}
		}
		else if(packetType == 2)
		{
			IPlayer playerData = ClientProxy.modClient.getLocalPlayerCapability();
			
			Class<?>[] parameters = new Class<?>[remoteArgs.length];
            for (int i = 0; i < remoteArgs.length; i++) {
            	parameters[i] = remoteArgs[i].getClass();
            }
            
            try 
            {
				Method methode = playerData.getCurrentRemoteUI().getClass().getMethod(methodName, parameters);
				try 
				{
					methode.invoke(playerData.getCurrentRemoteUI(), remoteArgs);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
            } catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		else if(packetType == 4)
		{
			IPlayer player = MinecraftUtils.getPlayerCapability(clientPlayer);
			player.getCurrentRemoteUI().handleResult(id, result);
		}
	}

}
