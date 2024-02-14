package fr.innog.network.packets.decrapted;

import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.network.packets.PacketBase;
import fr.innog.phone.Call;
import fr.innog.phone.NetworkCallTransmitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketFinishCall extends PacketBase {
	
	String number;
	
	boolean noData = false;
	
	public PacketFinishCall(String number)
	{
		this.number = number;
	}
	
	public PacketFinishCall()
	{
		noData = true;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		if(number != null)
			ByteBufUtils.writeUTF8String(data, this.number);
		data.writeBoolean(noData);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		if(number != null)
			this.number = ByteBufUtils.readUTF8String(data);
		this.noData = data.readBoolean();
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		NetworkCallTransmitter nt = null;

		nt = NetworkCallTransmitter.getByUsername(playerEntity.getName());
		
		if(nt != null)
		{
			nt.finishCall(playerEntity);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) {
		if(PhoneUI.getPhone().currentApp != null && PhoneUI.getPhone().currentApp instanceof Call)
		{
			((Call) PhoneUI.getPhone().currentApp).finishCall();
		}
	}

}