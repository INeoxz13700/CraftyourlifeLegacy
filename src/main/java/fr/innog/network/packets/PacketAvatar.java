package fr.innog.network.packets;

import java.lang.reflect.InvocationTargetException;

import fr.innog.common.ModCore;
import fr.innog.data.AvatarData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketAvatar extends PacketBase {
	
	/*
	 * 2 update avatar
	 */
	public byte type;
	
	public int entityId;
	
	public static PacketAvatar updateAvatar(int entityId)
	{
		PacketAvatar packet = new PacketAvatar();
		packet.type = 2;
		packet.entityId = entityId;
		return packet;
	}
	
	@SideOnly(Side.CLIENT)
	public static PacketAvatar updateAvatar()
	{
		PacketAvatar packet = new PacketAvatar();
		packet.type = 2;
		return packet;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(type);
		if(type == 2)
		{
			data.writeInt(entityId);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		type = data.readByte();
		if(type == 2)
		{
			entityId = data.readInt();
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		if(type == 2)
		{
			entityId = playerEntity.getEntityId();
			ModCore.getPackethandler().sendToAllAround(this,playerEntity.posX,playerEntity.posY,playerEntity.posZ,64F,playerEntity.dimension);
		}
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) {
		if(type == 2)
		{
			EntityPlayer player = null;
			
			if(clientPlayer.getEntityId() == entityId)
			{
				player = clientPlayer;
			}
			else
			{
				player = (EntityPlayer) clientPlayer.world.getEntityByID(entityId);
			}
			
			if(player == null) return;

			new AvatarData((AbstractClientPlayer)player).updateAvatar();
		}
	}

}