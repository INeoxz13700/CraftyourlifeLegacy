package fr.innog.network.packets;

import fr.innog.client.ui.ingame.IdentityCardUI;
import fr.innog.common.items.Items;
import fr.innog.data.IdentityData;
import fr.innog.network.PacketCollection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCardIdentity extends PacketBase {

	private int targetId;
	
	private boolean sendFromClient;
	
	private IdentityData identity;
	
	public static PacketCardIdentity openCardIdentityOf(EntityPlayer target)
	{
		PacketCardIdentity packet = new PacketCardIdentity();
		
		packet.targetId = target.getEntityId();
		packet.sendFromClient = true;
		
		return packet;
	}
	
	public static PacketCardIdentity openCardIdentityOf(IdentityData identityData)
	{
		PacketCardIdentity packet = new PacketCardIdentity();
			
		packet.identity = identityData;
		packet.sendFromClient = false;
		
		return packet;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeBoolean(sendFromClient);
		if(!sendFromClient)
		{
			identity.encodeInto(data);
		}
		else
		{
			data.writeInt(targetId);
		}		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		this.sendFromClient = data.readBoolean();
		if(!sendFromClient)
		{
			identity = new IdentityData();
			identity.decodeInto(data);	
		}
		else
		{
			this.targetId = data.readInt();
		}		
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		Entity targetEntity = playerEntity.getEntityWorld().getEntityByID(targetId);
		if(targetEntity instanceof EntityPlayer)
		{
			EntityPlayer target = (EntityPlayer) targetEntity;
			if(target.getHeldItemMainhand() != ItemStack.EMPTY && target.getHeldItemMainhand().getItem() == Items.identityCard)
			{
				if(target != null) 
				{
					PacketCollection.openCardIdentityOf(playerEntity, target);
				}
			}	
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleClientSide(EntityPlayer clientPlayer) 
	{
		Minecraft.getMinecraft().displayGuiScreen(new IdentityCardUI(identity));
	}

}
