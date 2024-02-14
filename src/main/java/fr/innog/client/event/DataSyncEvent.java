package fr.innog.client.event;

import fr.innog.ui.remote.data.SyncStruct;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DataSyncEvent extends Event {

	private SyncStruct<?> syncData;
	
	private EntityPlayer player;
	
	private String key;
	
	public DataSyncEvent(String key, SyncStruct<?> syncData, EntityPlayer player)
	{
		this.key = key;
		this.syncData = syncData;
		this.player = player;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public EntityPlayer getPlayer()
	{
		return player;
	}
	
	public SyncStruct<?> getData()
	{
		return syncData;
	}
	
}
