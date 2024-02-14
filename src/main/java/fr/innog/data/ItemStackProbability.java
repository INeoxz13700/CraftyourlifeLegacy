package fr.innog.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemStackProbability {

	private HashMap<MarketItem, Integer> items = new HashMap<>();
		
	
	public ItemStackProbability()
	{
		
	}
	
		
	private static Random rand = new Random();
	
    public MarketItem getRandomDropItem()
    {
    	
    	if(items.size() <= 0) return null;
    	
		final int chance = rand.nextInt(100 - 1 + 1) + 1;
		final int intervalMiddle = 50;	
		
		ArrayList<MarketItem> itemsInInterval = new ArrayList<MarketItem>();
		

		for(Entry<MarketItem, Integer> entry : items.entrySet()) {
				
			MarketItem key = entry.getKey();
		   int value = entry.getValue();
	
			if(chance >= intervalMiddle-value/2 && chance <= intervalMiddle+value/2)
		    {
			   itemsInInterval.add(key);
			}
			
			
		}
		
		if(itemsInInterval.size() == 0) return null;
			
		return itemsInInterval.get(rand.nextInt(itemsInInterval.size()-1 - 0 + 1) + 0);
    }
    

    
    
    public boolean addItem(String id, int quantity, float priceInEuro, int dropChance)
    {
    	return addItem(id,quantity,priceInEuro, dropChance, null);
    }
    
    public boolean addItem(String id, int quantity, double priceInEuro, int dropChance, String displayName)
    {
    	String[] idData = id.split(":");
    	
    	int itemId = Integer.parseInt(idData[0]);
    	int metaData = 0;
    	
    	if(containsItem(id, dropChance))
    	{
    		return false;
    	}
    	
    	ItemStack is = new ItemStack(Item.getItemById(itemId), quantity, metaData);
    	
    	if(displayName != null)
    	{
    		
    		displayName = displayName.replace("&", "§");
    		
    		is.setStackDisplayName(displayName);
    	}
    	
    	if(is.getItem() == null) 
    	{
    		return false;
    	}
    	
    	items.put(new MarketItem(is,priceInEuro),dropChance);
    	return true;
    }
    
    public boolean containsItem(String id, int probability)
    {
    	String[] idData = id.split(":");
    	
    	int itemId = Integer.parseInt(idData[0]);
    	int metaData = 0;
    	
    	for(MarketItem marketItem : items.keySet())
    	{
    		if(Item.getIdFromItem(marketItem.getItem().getItem()) == itemId && metaData == marketItem.getItem().getItemDamage() &&  items.get(marketItem) == probability)
    		{
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean remvoveItem(String id, int probability)
    {
    	String[] idData = id.split(":");
    	
    	int itemId = Integer.parseInt(idData[0]);
    	int metaData = 0;
    	
    	
    	if(idData.length == 2) metaData = Integer.parseInt(idData[1]);
    	
    	
    	MarketItem isToRemove = null;
    	for(MarketItem marketItem : items.keySet())
    	{
    		if(Item.getIdFromItem(marketItem.getItem().getItem()) == itemId && marketItem.getItem().getItemDamage() == metaData)
    		{
    			if(items.get(marketItem) == probability)
	    		{
    				isToRemove = marketItem;
	    			break;
	    		}
    		}
    	}
    	
    	if(isToRemove != null)
    	{
    		items.remove(isToRemove);
    		return true;
    	}
    	
    	return false;
    }
    
    public HashMap<MarketItem,Integer> getRegisteredItems()
    {
    	return items;
    }
    
    public void writeToNBT(String key, NBTTagCompound compound)
	{
		NBTTagList ItemStackProbabilityList = new NBTTagList();
		
		for(MarketItem marketItem : items.keySet())
		{
			NBTTagCompound tag = new NBTTagCompound();
						
			marketItem.getItem().writeToNBT(tag);
			
			tag.setInteger("Probability", items.get(marketItem));
			
			tag.setDouble("Price", marketItem.getPriceInEuro());
			
			
			ItemStackProbabilityList.appendTag(tag);
			
		}
		
		compound.setTag(key, ItemStackProbabilityList);
	}
	
	public void readFromNBT(String key, NBTTagCompound compound)
	{
		NBTTagList ItemStackProbabilityList = (NBTTagList) compound.getTag(key);
		
		
		for(int i = 0; i < ItemStackProbabilityList.tagCount(); i++)
		{
			NBTTagCompound tag = ItemStackProbabilityList.getCompoundTagAt(i);
			
			ItemStack is = new ItemStack(tag);
			
			if(is.isEmpty()) continue;
			
			MarketItem marketItem = new MarketItem(new ItemStack(tag),tag.getDouble("Price"));
				
			items.put(marketItem, tag.getInteger("Probability"));
		}
		
	}
	
	public int getRegisteredItemCount()
	{
		return items.size();
	}



}
