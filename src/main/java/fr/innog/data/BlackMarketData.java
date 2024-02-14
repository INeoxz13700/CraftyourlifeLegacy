package fr.innog.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class BlackMarketData implements ISaveHandler {

	private List<MarketItem> items;
	
	public BlackMarketData()
	{
		this.items = new ArrayList<>();
	}
	
	public void writeToNBT(NBTTagCompound compound)
	{
		NBTTagList blackMarketItemsList = new NBTTagList();
		
		for(MarketItem item : items)
		{
			NBTTagCompound tag = new NBTTagCompound();
			
			item.getItem().writeToNBT(tag);
			
			tag.setDouble("Price", item.getPriceInEuro());
			
			blackMarketItemsList.appendTag(tag);
		}
		
		compound.setTag("BlackMarketData", blackMarketItemsList);
	}
	
	public void readFromNBT(NBTTagCompound compound)
	{
		if(compound.hasKey("BlackMarketData"))
		{
			NBTTagList blackMarketItemsList = (NBTTagList) compound.getTag("BlackMarketData");
			
			for(int i = 0; i < blackMarketItemsList.tagCount(); i++)
			{
				NBTTagCompound tag = blackMarketItemsList.getCompoundTagAt(i);
				
				MarketItem item = new MarketItem(new ItemStack(tag),tag.getDouble("Price"));
				
				items.add(item);
			}
		}
	}
	
	public boolean addItem(MarketItem item)
	{
		if(containsItem(item)) return false;
		
		items.add(item);
		return true;
	}
	
	public boolean containsItem(MarketItem item)
	{
		for(int i = 0; i < items.size(); i++)
		{
			MarketItem marketItem = items.get(i);
			if(marketItem.getItem().getItem() == item.getItem().getItem() && marketItem.getItem().getItemDamage() == item.getItem().getItemDamage() && item.getPriceInEuro() == marketItem.getPriceInEuro())
			{
				return true;
			}
			
		}
		return false;
	}
	
	public List<MarketItem> getItems()
	{
		return items;
	}
	
	public void clear()
	{
		items.clear();
	}

	
	
}