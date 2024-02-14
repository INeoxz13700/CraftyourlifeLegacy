package fr.innog.ui.remote;

import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;

public class RemoteAnimationUI extends RemoteUIProcessor {

	public RemoteAnimationUI(EntityPlayer player) {
		super(player);
	}
	
	public void initRemoteUI(CacheData cachedData)
	{
		super.initRemoteUI(cachedData);
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		cachedData.setCached("UnlockedAnimations", new SyncStruct<List<Integer>>(playerData.getUnlockedAnimations()));
	}

}
