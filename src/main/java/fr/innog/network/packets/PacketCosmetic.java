package fr.innog.network.packets;

import java.util.ArrayList;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticCachedData;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCosmetic extends PacketBase {
	
	/*
	 * type = 0 equipCosmetic
	 * type = 1 unequipCosmetic
	 * type = 2 unlock cosmetic
	 * type = 3 update renders
	 */
	public byte type;
		
	public int cosmeticId;
	
	public int entityId;
		
	
	public List<CosmeticObject> cosmeticsToSynchronise = new ArrayList<CosmeticObject>();
	
	
	public PacketCosmetic()
	{
				
	}		
	
			
	public PacketCosmetic(byte type, int id)
	{
		this.type = type;
		this.cosmeticId = id;
	}
	
	public PacketCosmetic(byte type)
	{
		this.type = type;
	}
	
	public static PacketCosmetic equipCosmetic(int cosmeticId)
	{
		PacketCosmetic packet = new PacketCosmetic((byte)0);
		packet.cosmeticId = cosmeticId;		
		return packet;
	}
	
	public static PacketCosmetic unequipCosmetic(int cosmeticId)
	{
		PacketCosmetic packet = new PacketCosmetic((byte)1);
		packet.cosmeticId = cosmeticId;
		
		return packet;
	}
	
	public static PacketCosmetic updateRender(int entityId, List<CosmeticObject> list)
	{
		PacketCosmetic packet = new PacketCosmetic((byte)3);
		
		packet.putList(list);
		packet.entityId = entityId;
		
		return packet;
	}
	
	
	public PacketCosmetic putList(List<CosmeticObject> list)
	{
		this.cosmeticsToSynchronise = list;
		return this;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(type);		
		if(type == 3)
		{
			data.writeInt(entityId);
			data.writeInt(cosmeticsToSynchronise.size());
			for(CosmeticObject obj : cosmeticsToSynchronise)
			{
				data.writeInt(obj.getId());
				data.writeBoolean(obj.getIsEquipped());
			}
		}
		else
		{
			data.writeInt(cosmeticId);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		type = data.readByte();
		if(type == 3)
		{
			entityId = data.readInt();
			int cosmeticCount = data.readInt();
			for(int i = 0; i < cosmeticCount; i++)
			{
				int id = data.readInt();
				boolean equipped = data.readBoolean();
				CosmeticObject obj = ModCore.getCosmeticsManager().getCopy(id);
				obj.setEquipped(equipped);
				cosmeticsToSynchronise.add(obj);
			}
		}
		else
		{
			cosmeticId = data.readInt();
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		if(type == 0) 
		{
			CosmeticObject.equipCosmetic(playerEntity, cosmeticId);
		}
		else if(type == 1)
		{
			CosmeticObject.unequipCosmetic(playerEntity, cosmeticId);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer clientPlayer) {

		if(type == 0)
		{
			CosmeticObject.equipCosmetic(clientPlayer, cosmeticId);
		}
		else if(type == 1)
		{
			CosmeticObject.unequipCosmetic(clientPlayer, cosmeticId);
		}
		else if(type == 2)
		{
			CosmeticObject.setCosmetiqueUnlocked(clientPlayer,cosmeticId);
		}
		else if(type == 3)
		{	
			if(clientPlayer.getEntityId() == entityId)
			{
				IPlayer playerData = MinecraftUtils.getPlayerCapability(clientPlayer);

				for(int i = 0; i < cosmeticsToSynchronise.size(); i++)
				{
					CosmeticObject obj = cosmeticsToSynchronise.get(i);
					CosmeticObject cosmetic = playerData.getCosmeticDatas().getCosmeticById(obj.getId());
					cosmetic.setEquipped(obj.getIsEquipped());
				}
				return;
			}
			
			CosmeticCachedData data = CosmeticCachedData.getData(entityId);
			data.cosmeticsData = cosmeticsToSynchronise;
		}
	}
	
}