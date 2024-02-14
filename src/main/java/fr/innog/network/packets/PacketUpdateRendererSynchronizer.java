package fr.innog.network.packets;

import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketUpdateRendererSynchronizer extends PacketBase  {

	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {

	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		MinecraftUtils.getPlayerCapability(playerEntity).updateRenderer();
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) { }

}
