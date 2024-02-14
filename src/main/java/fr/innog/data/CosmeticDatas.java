package fr.innog.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticManager;
import fr.innog.common.cosmetics.CosmeticObject;

public class CosmeticDatas {

    public List<CosmeticObject> cosmeticsData = new ArrayList<CosmeticObject>();
        
    public CosmeticDatas(IPlayer playerData)
    {            	
		CosmeticManager manager = ModCore.getCosmeticsManager();
		
		for(CosmeticObject cosmeticObj : manager.getCosmetics())
		{
			CosmeticObject copy = manager.getCopy(cosmeticObj);
			cosmeticsData.add(copy);
		}
    }
    
	public void setCosmeticsData(List<CosmeticObject> cosmetics) 
	{
		cosmeticsData = cosmetics;
	}
    
    public List<CosmeticObject> getCosmeticsDatas()
	{
		return cosmeticsData;
	}

	public List<CosmeticObject> getEquippedCosmetics() 
	{
		return cosmeticsData.stream().filter(x -> x.getIsEquipped()).collect(Collectors.toList());
	}
	
	
	public CosmeticObject getCosmeticById(int id)
    {
    	for(CosmeticObject obj : cosmeticsData)
    	{
    		if(obj.getId() == id)
    		{
    			return obj;
    		}
    	}
    	return null;
    }



    
}
