package fr.innog.server.adapter;

public abstract class Adapter {

	protected Object adapterInstance;
	
	public final boolean initialized()
	{
		return adapterInstance != null;
	}
	
	public abstract void init();

	
}
