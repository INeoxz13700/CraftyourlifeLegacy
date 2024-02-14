package fr.innog.network.packets.decrapted;


import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.network.packets.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketOpenTelephone extends PacketBase {
	
	boolean initialize = false;
	
	
	public PacketOpenTelephone() {
		
	}
	
	public PacketOpenTelephone(boolean isInitialization) {
		this.initialize = isInitialization;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeBoolean(initialize);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		initialize = data.readBoolean();
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) {
		if(initialize)
		{
			new PhoneUI();
			return;
		}
		Minecraft.getMinecraft().displayGuiScreen(PhoneUI.getPhone() == null ? new PhoneUI() : PhoneUI.getPhone());
	}
	
	
	
}