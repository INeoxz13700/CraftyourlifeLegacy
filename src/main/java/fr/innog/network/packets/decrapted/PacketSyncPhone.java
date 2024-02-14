package fr.innog.network.packets.decrapted;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.network.packets.PacketBase;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSyncPhone extends PacketBase {
	
	
	
	public String clientNumber;
	
	
	public PacketSyncPhone() 
	{
		
	}
	
	public PacketSyncPhone(String clientNumber) 
	{
		this.clientNumber = clientNumber;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) 
	{
		ByteBufUtils.writeUTF8String(data, this.clientNumber);
	}
		


	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {	
		this.clientNumber = ByteBufUtils.readUTF8String(data);
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		 MinecraftUtils.getPlayerCapability(playerEntity).getPhoneData().syncPhone();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) {
		
		IPlayer pData = MinecraftUtils.getPlayerCapability(clientPlayer);	
		
		pData.getPhoneData().setNumber(clientNumber);
		
		pData.getPhoneData().initDatabase();
		pData.getPhoneData().loadContacts();
		pData.getPhoneData().loadSms();
		
	}

}