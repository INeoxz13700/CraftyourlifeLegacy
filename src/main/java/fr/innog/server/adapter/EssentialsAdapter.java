package fr.innog.server.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;

public class EssentialsAdapter extends Adapter {

	
	
	
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
	            adapterInstance = getPluginMethod.invoke(pluginManager, "Essentials");

	            ModCore.log("Essentials instance loaded " + adapterInstance.getClass().getName());
	        } 
	        catch (ClassNotFoundException e) 
	        {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
    }
	
	public double getUserMoney(EntityPlayer player)
	{		
		ModCore.log("Obtention du solde de : " + player.getName());
		Object user;
		try {
			user = adapterInstance.getClass().getMethod("getUser", String.class).invoke(adapterInstance, player.getName());
			BigDecimal userBalance = (BigDecimal)user.getClass().getMethod("getMoney").invoke(user);
			ModCore.log("Balance : " + userBalance.doubleValue());
			return userBalance.doubleValue();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return 0.0D;
		}
	}
	
	public void takeMoney(EntityPlayer player, double money)
	{
		Object user;
		try {
			user = adapterInstance.getClass().getMethod("getUser", String.class).invoke(adapterInstance, player.getName());
			user.getClass().getMethod("takeMoney", BigDecimal.class).invoke(user, BigDecimal.valueOf(money));
			MinecraftUtils.sendMessage(player, "§b" + money + "$ §aont été retirés de votre compte bancaire.");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void giveMoney(EntityPlayer player, double money)
	{
		Object user;
		try {
			user = adapterInstance.getClass().getMethod("getUser", String.class).invoke(adapterInstance, player.getName());
			user.getClass().getMethod("giveMoney", BigDecimal.class).invoke(user, BigDecimal.valueOf(money));
			MinecraftUtils.sendMessage(player, "§b" + money + "$ §aont été ajoutés à votre compte bancaire.");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public void setMoney(EntityPlayer player, double money)
	{
		Object user;
		try {
			user = adapterInstance.getClass().getMethod("getUser", String.class).invoke(adapterInstance, player.getName());
			user.getClass().getMethod("setMoney", BigDecimal.class).invoke(user, BigDecimal.valueOf(money));
			MinecraftUtils.sendMessage(player, "§bVotre compte bancaire a été fixé à §a" + money + "$");
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
}
