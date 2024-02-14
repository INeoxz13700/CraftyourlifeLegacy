package fr.innog.data;

import fr.innog.network.INetworkElement;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ConcessionnaireItem implements INetworkElement {
		
	private String vehicleItemId;
	
	private int id;
	
	private double price;
	
	public ConcessionnaireItem()
	{
		
	}
	
	public ConcessionnaireItem(int id, String itemId,  double price)
	{
		this.id = id;
		this.vehicleItemId = itemId;
		this.price = price;
	}
	
	public String getVehicleItemId()
	{
		return vehicleItemId;
	}
	
	public double getPrice()
	{
		return this.price;
	}
	
	public int getId()
	{
		return id;
	}
	
	public boolean isSameVehicleType(ConcessionnaireItem item)
	{
		return vehicleItemId.split(":")[0].equals(item.vehicleItemId.split(":")[0]);
	}

	@Override
	public void encodeInto(ByteBuf data) {
		ByteBufUtils.writeUTF8String(data, vehicleItemId);
		data.writeDouble(price);
		data.writeInt(id);
	}

	@Override
	public void decodeInto(ByteBuf data) {
		this.vehicleItemId = ByteBufUtils.readUTF8String(data);
		this.price = data.readDouble();
		this.id = data.readInt();
	}
    
}
