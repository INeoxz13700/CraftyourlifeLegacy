package fr.innog.network.packets.decrapted;

import java.io.IOException;

import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.common.items.Items;
import fr.innog.data.NumberData;
import fr.innog.network.packets.PacketBase;
import fr.innog.phone.CallHandler;
import fr.innog.phone.NetworkCallTransmitter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketStartCall extends PacketBase {
	
	private String receiverNumber;
	
	private String callerUsername;
	
	private String callerNumber;
	
	
	//when client start call
	public PacketStartCall(String receiverNumber, String callerNumber, String callerUsername)
	{		
		this.receiverNumber = receiverNumber;
		this.callerUsername = callerUsername;
		this.callerNumber = callerNumber;
	}
	
	//method from server
	public PacketStartCall(String callerNumber)
	{
		this.receiverNumber = "";
		this.callerUsername = "";
		this.callerNumber = callerNumber;
	}
	
	public PacketStartCall()
	{
		
	}
	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		ByteBufUtils.writeUTF8String(data, this.receiverNumber);
		ByteBufUtils.writeUTF8String(data, this.callerUsername);
		ByteBufUtils.writeUTF8String(data, this.callerNumber);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		this.receiverNumber = ByteBufUtils.readUTF8String(data);
		this.callerUsername = ByteBufUtils.readUTF8String(data);
		this.callerNumber = ByteBufUtils.readUTF8String(data);
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		
			String receiverUsername = null;
			
			try {
				receiverUsername = NumberData.getUsernameByNumber(this.receiverNumber);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(receiverUsername == null)
			{
				ModCore.getPackethandler().sendTo(new PacketConnectingCall(4), playerEntity);
				return;
			}
			
			
			EntityPlayer receiverPlayerEntity = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getEntityWorld().getPlayerEntityByName(receiverUsername);
			
			
			if(receiverPlayerEntity == null)
			{
				ModCore.getPackethandler().sendTo(new PacketConnectingCall(3), playerEntity);
				return;
			}
			else if(receiverPlayerEntity.getName().equalsIgnoreCase(playerEntity.getName()))
			{
				ModCore.getPackethandler().sendTo(new PacketConnectingCall(0), playerEntity);
				return;
			}
			
			boolean containPhone = false;
			for(Object obj : receiverPlayerEntity.inventoryContainer.inventoryItemStacks)
			{
				ItemStack it = (ItemStack) obj;
				if(it != null && it.getItem() != null && it.getItem() == Items.kamsung)
					containPhone = true;
				
			}
			
			if(!containPhone)
			{
				ModCore.getPackethandler().sendTo(new PacketConnectingCall(3), playerEntity);
				return;
			}
			
			
			NetworkCallTransmitter nt = null;
			
			try
			{
				nt = new NetworkCallTransmitter(playerEntity, receiverPlayerEntity,this.callerNumber, this.receiverNumber);
			} catch (Exception e) {
				
				ModCore.getPackethandler().sendTo(new PacketConnectingCall(2), playerEntity);
				
				e.printStackTrace();
				
				return;
			}
			
			nt.sendCallRequest();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) {
		
		
		if(PhoneUI.getPhone() == null)
		{
			PhoneUI.setPhone(new PhoneUI());
		}
		
		if(PhoneUI.getPhone().settings.notDisturb) return;
		
		Minecraft.getMinecraft().displayGuiScreen(PhoneUI.getPhone());
		
		PhoneUI.getPhone().setCallHandler(new CallHandler(this.callerNumber, true));
		PhoneUI.getPhone().openApp(0);
		PhoneUI.getPhone().currentApp.updateGuiState();
	}

}