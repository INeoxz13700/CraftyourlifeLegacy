package fr.innog.ui.remote;

import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;

public class RemoteCosmeticUI extends RemoteUIProcessor {

	public RemoteCosmeticUI(EntityPlayer player) {
		super(player);
	}
	
	@Override
	public void initRemoteUI(CacheData cachedData)
	{
		super.initRemoteUI(cachedData);
		
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
		cachedData.setCached("Cosmetics", new SyncStruct<List<CosmeticObject>>(playerData.getCosmeticDatas().cosmeticsData));
	}

}
