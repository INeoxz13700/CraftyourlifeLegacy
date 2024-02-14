package fr.innog.network.packets;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketPlayerSleep extends PacketBase {

	/*
	 *  0: unsleep
	 *  1: death subit
	 */
	private byte action;
	
			
	public PacketPlayerSleep() 
	{ 
		
	}
	
	public PacketPlayerSleep(byte action) 
	{ 
		this.action = action;
	}
 
	
	public static PacketPlayerSleep unSleep()
	{
		PacketPlayerSleep packet = new PacketPlayerSleep((byte)0);
		return packet;
	}
	
	public static PacketPlayerSleep subitDeath()
	{
		PacketPlayerSleep packet = new PacketPlayerSleep((byte)1);
		return packet;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(action);	
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		action = data.readByte();
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		if(action == 0)
		{
			ModControllers.playerController.forcePlayerWakeup(playerEntity);
		}
		else
		{
			ModControllers.reanimationController.subitDeath(playerEntity);
		}		
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) {

		
	}

}
