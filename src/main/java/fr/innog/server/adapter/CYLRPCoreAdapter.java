package fr.innog.server.adapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import fr.innog.common.ModCore;
import net.minecraft.entity.player.EntityPlayer;

public class CYLRPCoreAdapter extends Adapter {

	@Override
	public void init() {
		if(adapterInstance == null)
		{
			try 
	        {
	        	Class<?> BukkitClass = Class.forName("org.bukkit.Bukkit");
	            Method getPluginManagerMethod = BukkitClass.getMethod("getPluginManager");
	            Object pluginManager = getPluginManagerMethod.invoke(null);
	            Method getPluginMethod = pluginManager.getClass().getMethod("getPlugin", String.class);
	            adapterInstance = getPluginMethod.invoke(pluginManager, "CYLRP-CORE");

	            ModCore.log("CYLRP-Core instance loaded " + adapterInstance.getClass().getName());
	        } 
	        catch (ClassNotFoundException e) 
	        {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Object getBukkitPlayer(EntityPlayer player) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
    	Class<?> BukkitClass = Class.forName("org.bukkit.Bukkit");
    	Method getPlayer = BukkitClass.getMethod("getPlayer", String.class);
    	Object playerBukkit = getPlayer.invoke(null, player.getName());
    	return playerBukkit;
	}
	
	public Object getPlayerData(EntityPlayer player) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(this.adapterInstance == null) return null;
		
		Object bukkitPlayer = getBukkitPlayer(player);
		Method getPlayerData = adapterInstance.getClass().getSuperclass().getMethod("getPlayerData", Class.forName("org.bukkit.entity.Player"));
		
		Object iplayerData = getPlayerData.invoke(adapterInstance, bukkitPlayer);
		
		
        return iplayerData;
	}
	
	public Object getJob(EntityPlayer player) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException
	{
		if(this.adapterInstance == null) return null;

		Object playerData = getPlayerData(player);
		if(playerData != null)
		{
			Method getSelectedJob = playerData.getClass().getMethod("getSelectedJob");
			Object ijob = getSelectedJob.invoke(playerData);
			return ijob;
		}
		return null;
	}
	
	public String getJobName(EntityPlayer player) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException
	{
		if(this.adapterInstance == null) return null;

		Object playerData = getPlayerData(player);
		if(playerData != null)
		{
			Object ijob = getJob(player);
			Method jobNameMethod = ijob.getClass().getMethod("jobName");
			jobNameMethod.setAccessible(true);
			
			String name = (String)jobNameMethod.invoke(ijob);
            return name;
		}
		return null;
	}
	
	public Object getGrades(Object ijob) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(this.adapterInstance == null) return null;

		if(ijob != null)
		{
			Method IGrades = ijob.getClass().getMethod("getGrades");
			IGrades.setAccessible(true);
				
			return IGrades.invoke(ijob);
		}
		return null;
	}
	
	public int getGradeOrder(EntityPlayer player) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(this.adapterInstance == null) return -1;

		Object IJob = getJob(player);
		if(IJob != null)
		{
			Object IGrades = getGrades(IJob);
			
			String gradeName = getPlayerJobGradeName(player);
			
			Method gradeOrder = IGrades.getClass().getMethod("gradeOrder", String.class);
			gradeOrder.setAccessible(true);
				
			return (int) gradeOrder.invoke(IGrades, gradeName);
		}
		return -1;
	}
	
	public Object getPlayerJobGrade(EntityPlayer player) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(this.adapterInstance == null) return null;

		Object ijob = getJob(player);
		if(ijob != null)
		{
			Method IGrade = ijob.getClass().getMethod("getPlayerGrade");
			IGrade.setAccessible(true);
				
			return IGrade.invoke(ijob);
		}
		return null;
	}
	
	public String getPlayerJobGradeName(EntityPlayer player) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		if(this.adapterInstance == null) return null;

		Object IGrade = getPlayerJobGrade(player);
		if(IGrade != null)
		{
			Method getName = IGrade.getClass().getMethod("getName");
			getName.setAccessible(true);
				
			return (String) getName.invoke(IGrade);
		}
		return null;
	}

}
