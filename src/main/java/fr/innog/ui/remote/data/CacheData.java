package fr.innog.ui.remote.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.utils.structural.Tuple;

public class CacheData {

	private HashMap<String, Tuple<SyncStruct<?>, Boolean>> datas = new HashMap<>();
	
	public void setCached(String key, SyncStruct<?> value)
	{
		if(datas.containsKey(key))
		{
			datas.put(key, new Tuple<SyncStruct<?>,Boolean>(value, datas.get(key).getItem2()));
		}
		else
		{
			datas.put(key, new Tuple<SyncStruct<?>,Boolean>(value, true));
		}
	}
	
	public boolean dataExist(String key)
	{
		if(!datas.containsKey(key))
		{
			return false;
		}
		
		return true;
	}
	
	public void removeFromCache(String key)
	{
		if(dataExist(key)) datas.remove(key);
	}
	
	public boolean isDirty(String key)
	{

		if(dataExist(key)) return datas.get(key).getItem2();
		
		
		return false;
		
	}
	
	public void setDirty(String key, boolean dirty)
	{
		if(dataExist(key))
		{
			Tuple<SyncStruct<?>, Boolean> data = datas.get(key);
				
			data.setItem2(dirty);
		}
	}
	
	public SyncStruct<?> getData(String key)
	{
		if(dataExist(key))
		{
			return datas.get(key).getItem1();
		}
		
		return null;
	}
	
	public ArrayList<String> getKeys()
	{
		return new ArrayList<>(datas.keySet());
	}
	
}
