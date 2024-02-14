package fr.innog.network;

public interface INetworkCustomizedDeserialization<T> extends INetworkElement {

	public T getDeserializationInstance();

}
