package fr.innog.network;

import io.netty.buffer.ByteBuf;

public interface INetworkElement {

	public void encodeInto(ByteBuf data);

	public void decodeInto(ByteBuf data);
	
}
