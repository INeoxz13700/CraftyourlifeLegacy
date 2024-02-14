package fr.innog.server.adapter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fr.innog.common.ModCore;
import fr.innog.server.adapter.datas.ProtectedRegion;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WorldGuardAdapter extends Adapter {
	

	
	public void init()
    {
		if(adapterInstance == null)
		{
			try 
	        {
	        	Class<?> BukkitClass = Class.forName("org.bukkit.Bukkit");
	            Method getPluginManagerMethod = BukkitClass.getMethod("getPluginManager");
	            Object pluginManager = getPluginManagerMethod.invoke(null);
	            Method getPluginMethod = pluginManager.getClass().getMethod("getPlugin", String.class);
	            adapterInstance = getPluginMethod.invoke(pluginManager, "WorldGuard");

	            ModCore.log("Worldguard instance loaded " + adapterInstance.getClass().getName());
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
	
	public Object getWorld() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
    	Class<?> BukkitClass = Class.forName("org.bukkit.Bukkit");
    	Method method = BukkitClass.getMethod("getWorld", String.class);
    	return method.invoke(null, new Object[] { "cyl" });
	}
	
	public Object getRegionManager() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Object world = getWorld();
		Method method = adapterInstance.getClass().getMethod("getRegionManager", Class.forName("org.bukkit.World"));
		return method.invoke(adapterInstance, new Object[] { world});
	}
	
	public Object getRegions() throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Object regionManager = getRegionManager();
		
		Method method = regionManager.getClass().getMethod("getRegions");
		
		return method.invoke(regionManager);
	}
	
	public List<ProtectedRegion> getApplicableRegions(Vec3d location) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		Object regionManager = getRegionManager();
		
		if(regionManager == null) return new ArrayList<ProtectedRegion>();

		Object locationBukkit = getLocation(location);
		
		if(locationBukkit == null) return new ArrayList<ProtectedRegion>();
		
		Method method = regionManager.getClass().getMethod("getApplicableRegions", locationBukkit.getClass());
		
		Object applicableRegionSet = method.invoke(regionManager, locationBukkit);
		
		method = applicableRegionSet.getClass().getMethod("getRegions");
		
		Object protectedRegionSet = method.invoke(applicableRegionSet);
		
        Method iteratorMethod = Set.class.getMethod("iterator");
        Iterator<?> iterator = (Iterator<?>) iteratorMethod.invoke(protectedRegionSet);
        
        Method hasNextMethod = Iterator.class.getMethod("hasNext");
        Method nextMethod = Iterator.class.getMethod("next");
		
        List<ProtectedRegion> regions = new ArrayList<>();
        
        while ((boolean) hasNextMethod.invoke(iterator)) {
           Object protectedRgBukkit = nextMethod.invoke(iterator);
           Method getId = protectedRgBukkit.getClass().getMethod("getId");
           String id = (String) getId.invoke(protectedRgBukkit);
          
           Method getMinimumPoint = protectedRgBukkit.getClass().getMethod("getMinimumPoint");
           Method getMaximumPoint = protectedRgBukkit.getClass().getMethod("getMaximumPoint");

           BlockPos minPoint = toBlockPos(getMinimumPoint.invoke(protectedRgBukkit));
           BlockPos maxPoint = toBlockPos(getMaximumPoint.invoke(protectedRgBukkit));

   
           ProtectedRegion region = new ProtectedRegion(id, minPoint, maxPoint);
           
           regions.add(region);
        }
        
        return regions;
	}
	
	public Object getLocation(Vec3d location) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException
	{
		Object world = getWorld();
    	Class<?> locationClass = Class.forName("org.bukkit.Location");
    	Constructor<?> constructor = locationClass.getConstructor(new Class[] { Class.forName("org.bukkit.World"), double.class, double.class, double.class });
    	return constructor.newInstance(new Object[] { world, location.x, location.y, location.z});
	}
	
	public BlockPos toBlockPos(Object blockVector) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		double x = (double)blockVector.getClass().getMethod("getX").invoke(blockVector);
		double y = (double)blockVector.getClass().getMethod("getY").invoke(blockVector);
		double z = (double)blockVector.getClass().getMethod("getZ").invoke(blockVector);

		return new BlockPos(x,y,z);
	}
	
	
	
}
