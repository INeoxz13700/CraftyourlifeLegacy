package fr.innog.network.packets;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.items.interact.IItemPress;
import fr.innog.data.ItemPressingData;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketItemInteract extends PacketBase {

	/*
	 *  0: custom right click
	 *  1: mouse released
	 *  2: sync using item
	 */
	private byte type;

	private int entityId;
	
	private boolean isUsing;
	
	public PacketItemInteract()
	{
		
	}
	
	public PacketItemInteract(byte type)
	{
		this.type = type;
	}
	
	public static PacketItemInteract syncMouseIsReleased()
	{
		PacketItemInteract packet = new PacketItemInteract((byte)1);
		return packet;
	}
	
	public static PacketItemInteract syncUsingItem(EntityPlayer player)
	{
		if(player.world.isRemote) return null;
		
		IPlayer data = MinecraftUtils.getPlayerCapability(player);
		
		PacketItemInteract packet = new PacketItemInteract((byte)2);
		
		packet.entityId = player.getEntityId();
		
		packet.isUsing = data.getItemPressing().isUsingItem();
		
		return packet;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(type);
		if(type == 2)
		{
			data.writeInt(entityId);
			data.writeBoolean(isUsing);
		}		
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		type = data.readByte();

		if(type == 2)
		{
			entityId = data.readInt();
			isUsing = data.readBoolean();
		}		
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		if(type == 1)
		{
			if(playerEntity.getHeldItemMainhand().getItem() instanceof IItemPress)
			{
				IPlayer playerData = MinecraftUtils.getPlayerCapability(playerEntity);
				
				if(playerData.getItemPressing().isUsingItem())
				{
					((IItemPress)playerEntity.getHeldItemMainhand().getItem()).onItemStopUsing(playerEntity, playerEntity.world, playerEntity.getHeldItemMainhand(), playerData.getItemPressing().itemPressTicks);
					
					playerData.getItemPressing().setItemReleased();
				}
			}
			
		}		
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) {
		EntityPlayer playerToSync = (EntityPlayer)clientPlayer.world.getEntityByID(entityId);
		
		if(playerToSync == null) return;
		
		if(type == 2)
		{
			IPlayer data = MinecraftUtils.getPlayerCapability(playerToSync);
			ItemPressingData pressing = data.getItemPressing();
			
			if(!isUsing && pressing.isUsingItem())
			{
				((IItemPress)pressing.usedItem.getItem()).onItemStopUsing(playerToSync, playerToSync.world, pressing.usedItem, pressing.itemPressTicks);
				pressing.setItemReleased();
			}
			else if(isUsing && !pressing.isUsingItem())
			{
				if(playerToSync.getHeldItemMainhand().getItem() instanceof IItemPress)
				{
					pressing.usedItem = playerToSync.getHeldItemMainhand();
					pressing.itemPressTicks = 0;
					((IItemPress)pressing.usedItem.getItem()).onItemRightClicked(playerToSync, playerToSync.world, pressing.usedItem);
				}
			}
		}		
	}

}
