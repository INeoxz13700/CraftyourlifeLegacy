package fr.innog.data;

public class EthylotestRequest {

	private String requestOwner;
		
	public EthylotestRequest(String requestOwner)
	{
		this.requestOwner = requestOwner;
	}
	
	public String getRequestOwnerName()
	{
		return requestOwner;
	}
	
}
