package fr.innog.network.packets;

import fr.innog.common.controllers.PlayerController;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class PacketCrochetDoor extends PacketBase {

	private BlockPos doorPos;
	
	public PacketCrochetDoor() {}
	
	public static PacketCrochetDoor crochetDoor(BlockPos doorPos)
	{
		PacketCrochetDoor packet = new PacketCrochetDoor();
		packet.doorPos = doorPos;
		return packet;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeInt(doorPos.getX());
		data.writeInt(doorPos.getY());
		data.writeInt(doorPos.getZ());
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		doorPos = new BlockPos(data.readInt(), data.readInt(), data.readInt());
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		if(playerEntity.getDistance(doorPos.getX(), doorPos.getY(), doorPos.getZ()) > 4)
		{
			MinecraftUtils.sendHudMessage(playerEntity, "§cRapprochez-vous de la porte.");
			return;
		}
		
		PlayerController.crochetDoor(playerEntity, doorPos);
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) 
	{
		
	}

}
