package fr.innog.ui.remote.data;

import fr.innog.network.INetworkElement;
import fr.innog.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;

public class SyncStruct<T> implements INetworkElement  {
	
	private T data;
	
	public SyncStruct()
	{
		
	}
	
	public SyncStruct(T data)
	{
		this.data = data;
	}
	
	public T getData()
	{
		return data;
	}
	
	public void setData(T data)
	{
		this.data = data;
	}

	@Override
	public void encodeInto(ByteBuf buffer) {
		NetworkUtils.serialize(data, buffer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void decodeInto(ByteBuf buffer) {
		data = (T) NetworkUtils.deserialize(buffer);
	}

}
