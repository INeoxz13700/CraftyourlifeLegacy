package fr.innog.network.packets.decrapted;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import fr.innog.network.INetworkElement;
import fr.innog.network.packets.PacketBase;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketListTransmission<E extends INetworkElement> extends PacketBase {

	
	/*
	 * 0 : ask element to Server
	 * 1 : send elements to Client
	 */
	protected byte action;
	
	protected int askedElementCount;
	
	public List<E> elements = new ArrayList<E>();
	
	public int lastIndex = 0;
	
	public PacketListTransmission()
	{
		
	}
	
	public PacketListTransmission(List<E> elementsToTransmit)
	{
		elements = elementsToTransmit;
		action = 1;
	}
	
	public PacketListTransmission(final int askedElementCount)
	{
		this.askedElementCount = askedElementCount;
		action = 0;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(action);
		if(action == 0)
		{
			data.writeInt(askedElementCount);
			data.writeInt(lastIndex);

		}
		else
		{
			data.writeInt(elements.size());
			for(E e : elements)
			{
				e.encodeInto(data);
			}
			data.writeInt(lastIndex);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		action = data.readByte();
		if(action == 0)
		{
			askedElementCount = data.readInt();
			lastIndex = data.readInt();
		}
		else
		{
			int elementsSize = data.readInt();
			
			Type type = getClass().getGenericSuperclass();
		    ParameterizedType paramType = (ParameterizedType) type;
		    Class<E> genericClass = (Class<E>) paramType.getActualTypeArguments()[0];
		   
		    E e = null;
			for(int i = 0; i < elementsSize; i++)
			{
				try {
					e = genericClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
				e.decodeInto(data);
				elements.add(e);
			}
			lastIndex = data.readInt();

		}
		
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) { }

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) { }

}