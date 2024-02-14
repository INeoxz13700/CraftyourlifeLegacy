package fr.innog.ui.remote;

import java.util.HashMap;

import fr.innog.common.ModCore;
import fr.innog.network.PacketCollection;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.RemoteMethodCallback;
import fr.innog.ui.remote.data.RemoteMethodCallback.ActionResult;
import fr.innog.ui.remote.data.RemoteMethodResult;
import fr.innog.ui.remote.data.SyncStruct;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class RemoteUIProcessor {
	
	protected EntityPlayer player;
	
	protected CacheData cachedData;
	
	private HashMap<Integer,RemoteMethodResult> methodResult = new HashMap<>();
			
	
	public RemoteUIProcessor(EntityPlayer player)
	{
		this.player = player;
	}
		
	public String getIdentifier()
	{
		return this.getClass().getSimpleName();
	}
	
	//called only server side when UIRemote is instantiated register all variables that should be synchronized with client
	public void initRemoteUI(CacheData cachedData)
	{
		if(player.world.isRemote) return;

		setCacheData(cachedData);
	}
	
	public void setCacheData(CacheData cachedData)
	{
		this.cachedData = cachedData;
	}
	
	public CacheData getCacheData()
	{
		return cachedData;
	}
	
	public final HashMap<String, SyncStruct<?>> getDatasToSync()
	{
		HashMap<String, SyncStruct<?>> datas = new HashMap<>();
	
		for(String key : this.cachedData.getKeys())
		{	
			if(cachedData.isDirty(key)) 
			{
				datas.put(key, cachedData.getData(key));
				cachedData.setDirty(key, false);
			}
		}
		
		if(datas.size() == 0) return null;
		
		return datas;
	}
	
	//Client only
	public final void handleResult(int id, ActionResult result) 
	{
		if(methodResult.containsKey(id))
		{
			RemoteMethodResult remoteResult = methodResult.get(id);
			remoteResult.getCallback().call(result);
		}
	}
	
	//Client only
	public final void executeMethodWithResult(String methodName, RemoteMethodCallback callback, Object... args)
	{
		if(player.world.isRemote)
		{
			int id = methodResult.size();
			RemoteMethodResult remoteMethodResult = new RemoteMethodResult(callback);
			methodResult.put(id, remoteMethodResult);
			PacketCollection.executeRemoteMethodWithResult(player,methodName, id, args);
		}
	}
	
	public final void executeMethod(String methodName, Object... args)
	{
		PacketCollection.executeRemoteMethod(player,methodName, args);
	}

	
	
	//(Server side seulement) Liste des clés des données en cache qui seront synchronisé au client à l'appelle de cette fonction
	public final void syncData()
	{
		if(player.world.isRemote) return;
		
		
		ModCore.debug("Sync des données au client - Server Side");
		PacketCollection.syncDataRemoteUI(getDatasToSync(), (EntityPlayerMP)player);
	}
}
