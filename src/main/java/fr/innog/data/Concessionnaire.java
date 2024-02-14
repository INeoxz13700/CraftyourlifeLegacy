package fr.innog.data;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class Concessionnaire {

	private int id;

    private String name;
    
    private HashMap<Integer, ConcessionnaireItem> vehicles = new HashMap<>();
    
    public Concessionnaire(int id, String name)
    {
    	this.id = id;
    	this.name = name;
    }
    
    public String getName()
    {
    	return name;
    }

	public int getId() {
		return id;
	}
	
	public int getVehicleCount()
	{
		return vehicles.size();
	}
	
	public boolean containItemId(String itemId)
	{
		for(ConcessionnaireItem item : vehicles.values())
		{
			if(item.getVehicleItemId().equals(itemId)) return true;
		}
		
		return false;
	}
	
	public boolean containItemByKey(Integer key)
	{
		return vehicles.containsKey(key);
	}
	
	public void removeItem(int id)
	{
		if(containItemByKey(id))
		{
			vehicles.remove(id);
		}
	}
	
	public ConcessionnaireItem getItem(int id)
	{
		if(containItemByKey(id))
		{
			return vehicles.get(id);
		}
		
		return null;
	}
	
	public HashMap<Integer, ConcessionnaireItem> getVehicles()
	{
		return vehicles;
	}
	
	//id représente la clé primaire du véhicule dans la bdd elle est unique
	public void addVehicle(int id, String itemId, double price)
	{
		vehicles.put(id, new ConcessionnaireItem(id, itemId, price));
	}
	
	public void clearVehicles()
	{
		vehicles.clear();
	}
	
}
