package fr.innog.ui.remote.data;

public interface RemoteMethodCallback {

	public enum ActionResult
	{
		SUCCESS,
		FAILURE
	};
	
	
	public void call(ActionResult result);
	
}
