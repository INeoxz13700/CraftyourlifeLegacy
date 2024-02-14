package fr.innog.ui.remote.data;

public class RemoteMethodResult {

	
	private RemoteMethodCallback callback;
	
	public RemoteMethodResult(RemoteMethodCallback callback)
	{
		this.callback = callback;
	}
	
	public RemoteMethodCallback getCallback()
	{
		return callback;
	}
	
}
