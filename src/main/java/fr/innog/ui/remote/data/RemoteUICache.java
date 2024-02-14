package fr.innog.ui.remote.data;

import java.util.HashMap;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;

public class RemoteUICache {

	private HashMap<String, CacheData> cachedData = new HashMap<>();
	
	public CacheData initCacheByIdentifier(String identifier) throws Exception
	{
		if(!identifierHaveCache(identifier))
		{
			CacheData data = new CacheData();
			cachedData.put(identifier, data);
			return data;
		}
		else
		{
			throw new Exception("L'identifier est déjà enregistré");
		}
	}
	
	public CacheData getCachesByIdentifier(String identifier)
	{
		if(!identifierHaveCache(identifier))
		{
			return null;
		}
		else
		{
			return cachedData.get(identifier);
		}
	}
	
	public boolean identifierHaveCache(String identifier)
	{
		return cachedData.containsKey(identifier);
	}
	
	public static void setDirtyForPlayers(String identifier, String key, MinecraftServer server)
	{
		try {
			for(EntityPlayerMP player : server.getPlayerList().getPlayers())
				setDirtyForPlayer(player, identifier, key);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setDirtyForPlayer(EntityPlayer player, String identifier, String key)
	{
		try {
			IPlayer IPlayer = player.getCapability(PlayerProvider.PLAYER_CAP, null);
			
			CacheData cacheData = IPlayer.getCachedData().getCachesByIdentifier(identifier);
			if(cacheData != null) cacheData.setDirty(key, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
