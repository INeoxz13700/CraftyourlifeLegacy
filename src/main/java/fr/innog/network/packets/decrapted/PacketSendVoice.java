package fr.innog.network.packets.decrapted;

import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.network.packets.PacketBase;
import fr.innog.phone.CallHandler;
import fr.innog.phone.NetworkCallTransmitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSendVoice extends PacketBase {
	
	public byte[] targetData;
		
	public PacketSendVoice() {

	}
	
	public PacketSendVoice(byte[] targetData, String senderUsername) {
		this.targetData = targetData;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeBytes(targetData);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		targetData = new byte[data.readableBytes()];
		data.readBytes(targetData);
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		
		NetworkCallTransmitter nt = NetworkCallTransmitter.getByUsername(playerEntity.getName());

		
		if(nt != null)
			nt.TransmitVoiceData(playerEntity, this.targetData);
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) {
		CallHandler CH = PhoneUI.getPhone().getCallHandler();

		if(CH != null)
		{
			CH.recorder.ProcessDataFromServer(this.targetData);
		}

	}

}