package fr.innog.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.network.INetworkCustomizedDeserialization;
import fr.innog.network.INetworkElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class NetworkUtils {

	public static void serialize(Object obj, ByteBuf buffer)
	{	
		 if (obj instanceof Integer) {
			buffer.writeByte(0);
	        buffer.writeInt((Integer) obj);
	     } else if (obj instanceof Double) {
	    	buffer.writeByte(1);
	        buffer.writeDouble((Double) obj);
	     } else if (obj instanceof Boolean) {
		    buffer.writeByte(2);
	        buffer.writeBoolean((Boolean) obj);
	     } else if (obj instanceof Float) {
		    buffer.writeByte(3);
	        buffer.writeFloat((Float) obj);
	     } else if (obj instanceof Short) {
		    buffer.writeByte(4);
	        buffer.writeShort((Short) obj);
	     } else if (obj instanceof Byte) {
		    buffer.writeByte(5);
	        buffer.writeByte((Byte) obj);
	     } else if (obj instanceof Long) {
		    buffer.writeByte(6);
	        buffer.writeLong((Long) obj);
	     } else if (obj instanceof Character) {
		    buffer.writeByte(7);
	        buffer.writeChar((Character) obj);
	     } else if (obj instanceof String) {
		    buffer.writeByte(8);
	    	ByteBufUtils.writeUTF8String(buffer, (String)obj);
	     }
	     else if(obj instanceof INetworkElement) {
		    buffer.writeByte(9);
		    ByteBufUtils.writeUTF8String(buffer, obj.getClass().getName());
	    	INetworkElement networkElement = (INetworkElement) obj;
	    	networkElement.encodeInto(buffer);
	     }
	     else if (obj instanceof Object[]) {
	         Object[] array = (Object[]) obj;
	         buffer.writeByte(10);
	         buffer.writeInt(array.length);
	         for (Object element : array) {
	             serialize(element, buffer);
	         }
	     }
	     else if (obj instanceof List) {
	         List<?> list = (List<?>) obj;
	         buffer.writeByte(11);
	         buffer.writeInt(list.size());
	         for (Object element : list) {
	             serialize(element, buffer);
	         }
	     }
	     else if (obj instanceof HashMap) {
	         HashMap<?, ?> map = (HashMap<?, ?>) obj;
	         buffer.writeByte(12);
	         buffer.writeInt(map.size());
	         for (Map.Entry<?, ?> entry : map.entrySet()) {
	             serialize(entry.getKey(), buffer);
	             serialize(entry.getValue(), buffer);
	         }
	     }
	     else if(obj instanceof ItemStack) {
	         buffer.writeByte(13);
	    	 ByteBufUtils.writeItemStack(buffer, (ItemStack)obj);
	     }
	}
	
	public static Object deserialize(ByteBuf buffer)
	{	
		byte type = buffer.readByte();
		switch(type)
		{
			case 0:
			{
				return buffer.readInt();
			}
			case 1:
			{
				return buffer.readDouble();
			}
			case 2:
			{
				return buffer.readBoolean();
			}
			case 3:
			{
				return buffer.readFloat();
			}
			case 4:
			{
				return buffer.readShort();
			}
			case 5:
			{
				return buffer.readByte();
			}
			case 6:
			{
				return buffer.readLong();
			}
			case 7:
			{
				return buffer.readChar();
			}
			case 8:
			{
				return ByteBufUtils.readUTF8String(buffer);
			}
			case 9:
			{
			
				INetworkElement networkElement;
				try {
					networkElement = (INetworkElement)Class.forName(ByteBufUtils.readUTF8String(buffer)).newInstance();					
					networkElement.decodeInto(buffer);
					if(networkElement instanceof INetworkCustomizedDeserialization)
					{
						return ((INetworkCustomizedDeserialization<?>)networkElement).getDeserializationInstance();
					}
					else
					{
						return networkElement;
					}
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					e.printStackTrace();
					return null;
				}
			}
			case 10: // Tableaux
	        {
	            int length = buffer.readInt();
	            Object[] array = new Object[length];
	            for (int i = 0; i < length; i++) {
	                array[i] = deserialize(buffer);
	            }
	            return array;
	        }
	        case 11: // ArrayLists
	        {
	            int size = buffer.readInt();
	            List<Object> list = new ArrayList<>();
	            for (int i = 0; i < size; i++) {
	                list.add(deserialize(buffer));
	            }
	            return list;
	        }
	        case 12: // HashMaps
	        {
	            int size = buffer.readInt();
	            HashMap<Object, Object> map = new HashMap<>(size);
	            for (int i = 0; i < size; i++) {
	                Object key = deserialize(buffer);
	                Object value = deserialize(buffer);
	                map.put(key, value);
	            }
	            return map;
	        }
	        case 13:
	        {
	        	ItemStack is = ByteBufUtils.readItemStack(buffer);
	        	return is;
	        }
		}
		return null;
	}
	
}
