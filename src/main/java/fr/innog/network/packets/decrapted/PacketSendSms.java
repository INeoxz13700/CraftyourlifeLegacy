package fr.innog.network.packets.decrapted;

import java.io.IOException;

import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.NumberData;
import fr.innog.data.SmsData;
import fr.innog.network.packets.PacketBase;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSendSms extends PacketBase {
	
	/*
	 *  0 : sms cannot be send message
	 *  1 : send sms
	 */
	public byte packetType;
	
	public String receiverNumber;
	
	public String senderNumber;
	
	public String message;	
	
	public PacketSendSms()
	{
		
	}
	
	public PacketSendSms(String message , String receiverNumber, String senderNumber)
	{
		this.packetType = 1;
		this.message = message;
		this.receiverNumber = receiverNumber;
		this.senderNumber = senderNumber;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(packetType);
		
		if(packetType == 1)
		{
			ByteBufUtils.writeUTF8String(data, senderNumber);
			ByteBufUtils.writeUTF8String(data, receiverNumber);
			ByteBufUtils.writeUTF8String(data, message);
		}

	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		this.packetType = data.readByte();
		
		if(packetType == 1)
		{
			this.senderNumber = ByteBufUtils.readUTF8String(data);
			this.receiverNumber = ByteBufUtils.readUTF8String(data);
			this.message = ByteBufUtils.readUTF8String(data);
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		
		EntityPlayer receiverEntity = null;
		
		try {
			String username = NumberData.getUsernameByNumber(receiverNumber);

			if(username == null)
			{
				packetType = 0;
				ModCore.getPackethandler().sendTo(this, (EntityPlayerMP) playerEntity);
				return;
			}
			
			receiverEntity = playerEntity.world.getPlayerEntityByName(username);
			
			if(receiverEntity == null)
			{
				WorldDataManager.get(playerEntity.world).getPhoneAppData().addSmsToSend(playerEntity.world,new SmsData(0,senderNumber,receiverNumber,message,false));
				return;
			}
			

			if(receiverEntity.inventory.hasItemStack(new ItemStack(fr.innog.common.items.Items.kamsung,1))) receiverEntity.getEntityWorld().playSound(receiverEntity,receiverEntity.getPosition(), new SoundEvent(new ResourceLocation("craftyourliferp:sms_received")),SoundCategory.PLAYERS, 1.0f, 1.0f);

			
			ModCore.getPackethandler().sendTo(new PacketSendSms(message, receiverNumber, senderNumber),  (EntityPlayerMP) receiverEntity);

			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) {

		PhoneUI phone = PhoneUI.getPhone();
		if(phone == null)
		{
			PhoneUI.setPhone(new PhoneUI());
		}
		
		if(packetType == 1)
		{
			MinecraftUtils.getPlayerCapability(clientPlayer).getPhoneData().addSms(message, senderNumber, receiverNumber);
		}
		else
		{
			if(Minecraft.getMinecraft().currentScreen instanceof PhoneUI)
			{
				phone.displayToast("Cette Sms n'a pas pu être envoyer !", 4);
			}
		}
		
	}
	

	
}