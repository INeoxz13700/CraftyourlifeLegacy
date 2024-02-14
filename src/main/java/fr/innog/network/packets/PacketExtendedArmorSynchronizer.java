package fr.innog.network.packets;

import java.util.ArrayList;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketExtendedArmorSynchronizer extends PacketBase {

	private int entityId;
	
	private List<ItemStack> slotContent = new ArrayList<ItemStack>();
	
	public PacketExtendedArmorSynchronizer() { }
	
	public static PacketExtendedArmorSynchronizer syncExtendedArmor(EntityPlayer player)
	{
		PacketExtendedArmorSynchronizer packet = new PacketExtendedArmorSynchronizer();
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		packet.entityId = player.getEntityId();
		for(int i = 0; i < playerData.getInventory().getSlots(); i++)
		{
			packet.slotContent.add(playerData.getInventory().getStackInSlot(i));
		}
		return packet;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeInt(entityId);
		data.writeInt(slotContent.size());
		for(int i = 0; i < slotContent.size(); i++)
		{
			ByteBufUtils.writeItemStack(data, slotContent.get(i));
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		entityId = data.readInt();
		int size = data.readInt();

		for(int i = 0; i < size; i++)
		{
			ItemStack is = ByteBufUtils.readItemStack(data);
			slotContent.add(is);
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) {
		Entity entity = clientPlayer.world.getEntityByID(entityId);
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer target = (EntityPlayer)entity;
			IPlayer data = MinecraftUtils.getPlayerCapability(target);
			for(int i = 0; i < slotContent.size(); i++)
			{
				data.getInventory().setStackInSlot(i, slotContent.get(i));
			}
		}
	}

}
