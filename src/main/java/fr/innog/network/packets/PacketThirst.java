package fr.innog.network.packets;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.common.thirst.ThirstStats;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketThirst extends PacketBase {

	public static PacketThirst drinkWater()
	{
		PacketThirst packet = new PacketThirst();
		return packet;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) { }

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) { }

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		IPlayer ep = MinecraftUtils.getPlayerCapability(playerEntity);
		if(ModControllers.thirstController.playerLookWater(playerEntity, playerEntity.world))
		{
			ep.getThirstStats().setThirst(ThirstStats.maxThirst);
		}		
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) { }

}
