package fr.innog.data;

import java.util.ArrayList;
import java.util.List;

public class ShopData {
	
	public List<MarketItem> shopCart = new ArrayList<>();

	public ShopData()
	{
		
	}
	
	public void addItemToCart(MarketItem item)
	{
		if(shopCart.contains(item)) return;
		
		shopCart.add(item);
	}
	
	public void removeItemFromCart(MarketItem item)
	{
		if(!shopCart.contains(item)) return;
		
		shopCart.remove(item);
	}

	
	
	
}