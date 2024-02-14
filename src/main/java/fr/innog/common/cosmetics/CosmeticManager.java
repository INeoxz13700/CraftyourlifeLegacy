package fr.innog.common.cosmetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class CosmeticManager {

	private static List<CosmeticObject> registeredCosmetics = new ArrayList<CosmeticObject>();
			
	public CosmeticManager()
	{
    
	}

	public CosmeticObject registerCosmetic(String name, boolean unlockedDefault,byte type, int id)
	{
		CosmeticObject cosmeticObj = new CosmeticObject(name, unlockedDefault, type, id);
		registeredCosmetics.add(cosmeticObj);
		return cosmeticObj;
	}
	
	
	public CosmeticObject getCopy(int id)
	{
		Optional<CosmeticObject> optional = registeredCosmetics.stream().filter(x -> x.getId() == id).findFirst();
		CosmeticObject obj = null;
		
		if(optional.isPresent())
		{
			obj = optional.get();
		}
		else
		{
			return null;
		}
		
		return getCopy(obj);
	}
	
	public CosmeticObject getCopy(CosmeticObject obj)
	{
		CosmeticObject copy = null;
		
		copy = new CosmeticObject(obj.getName(),!obj.getIsLocked(),obj.getType(), obj.getId());
		copy.setSpecialMessage(obj.getSpecialMessage());

		if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
		{
			copy.setupRender(obj.getCosmeticRenderSetup(), obj.getModel());
		}
		return copy;
	}

	
	public List<CosmeticObject> getCosmetics()
	{
		return registeredCosmetics;
	}
	

	
}
