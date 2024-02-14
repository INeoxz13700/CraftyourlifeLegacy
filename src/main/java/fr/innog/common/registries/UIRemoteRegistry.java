package fr.innog.common.registries;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.ui.remote.RemoteUIProcessor;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UIRemoteRegistry {
		
	private static List<Class<? extends RemoteUIProcessor>> registeredRemoteUI = new ArrayList<Class<? extends RemoteUIProcessor>>();

	private static List<Class<? extends GuiScreen>> linkedUIToRemoteUI = new ArrayList<Class<? extends GuiScreen>>();

	
	public static int registerUI(Class<? extends RemoteUIProcessor> registryClass)
	{
		registeredRemoteUI.add(registryClass);
		
		return registeredRemoteUI.indexOf(registryClass)+1;
	}
	
	@SideOnly(Side.CLIENT)
	public static int registerUI(Class<? extends RemoteUIProcessor> registryClass, Class<? extends GuiScreen> linkedUI) throws IllegalAccessException
	{
		boolean k1 = false;
		
		
		for(Class<?> inter : linkedUI.getSuperclass().getInterfaces())
		{
			if(inter.getSimpleName().equals("IRemoteUI"))
			{
				k1 = true;
				break;
			}
		}
		
		if(!k1)
		{
			for(Class<?> inter : linkedUI.getInterfaces())
			{
				if(inter.getSimpleName().equals("IRemoteUI"))
				{
					k1 = true;
					break;
				}
			}
		}

		if(!k1)
			throw new IllegalAccessException ("Votre UI doit implémenter l'interface IRemoteUI");
	    
		
		int id = registerUI(registryClass);
		
		linkedUIToRemoteUI.add(linkedUI);
			
		return id;
	}
	
	public static Class<? extends RemoteUIProcessor> getRemoteUIClassFromRegistry(int id)
	{
		return registeredRemoteUI.get(id-1);
	}
	
	@SideOnly(Side.CLIENT)
	public static Class<? extends GuiScreen> getGuiScreenClassFromRegistry(int id)
	{
		return linkedUIToRemoteUI.get(id-1);
	}
	
	public static int getIdFromClass(Class<? extends RemoteUIProcessor> theClass)
	{		
		int i = 1;
		for(Class<? extends RemoteUIProcessor> remoteUIClass : registeredRemoteUI)
		{
			if(theClass == remoteUIClass)
			{
				return i;
			}
			
			i++;
		}
		
		return -1;
	}
	
	
	
}
