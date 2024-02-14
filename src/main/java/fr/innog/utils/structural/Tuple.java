package fr.innog.utils.structural;

public class Tuple<X,Y> {
	
	private X x; 
	
	private Y y; 
	
	public Tuple(X x, Y y) 
	{ 
		this.x = x; 
	    this.y = y; 
	} 
	
	public X getItem1()
	{
		return x;
	}
	
	public Y getItem2()
	{
		return y;
	}
	
	public void set(X x, Y y)
	{
		setItem1(x);
		setItem2(y);
	}
	
	public void setItem1(X x)
	{
		this.x = x;
	}
	
	public void setItem2(Y y)
	{
		this.y = y;
	}
}
