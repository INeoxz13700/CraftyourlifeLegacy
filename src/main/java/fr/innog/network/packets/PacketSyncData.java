package fr.innog.network.packets;

import fr.innog.client.event.DataSyncEvent;
import fr.innog.common.ModCore;
import fr.innog.ui.remote.data.SyncStruct;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketSyncData extends PacketBase {

	private SyncStruct<?> dataToSync;
	
	private String dataKey;
	
	public static PacketSyncData syncData(String key, SyncStruct<?> element)
	{
		PacketSyncData packet = new PacketSyncData();
		packet.dataToSync = element;
		packet.dataKey = key;
		return packet;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		ByteBufUtils.writeUTF8String(data, dataKey);
		dataToSync.encodeInto(data);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		dataKey = ByteBufUtils.readUTF8String(data);
		dataToSync = new SyncStruct<>();
		dataToSync.decodeInto(data);
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) { }

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) {		
        MinecraftForge.EVENT_BUS.post(new DataSyncEvent(dataKey, dataToSync, clientPlayer));
	}

	
	
}
