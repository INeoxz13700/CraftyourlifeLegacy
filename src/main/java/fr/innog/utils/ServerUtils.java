package fr.innog.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class ServerUtils {

	public static boolean isPompier(EntityPlayer player)
	{
		try {
			if(ModCore.getCylrp().getJobName(player).toLowerCase() == "pompier")
			{
				return true;
			}
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException | NoSuchMethodException | InstantiationException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean isIlegalJob(String jobName)
	{
		if(jobName == null) return false;
		
		return jobName.equalsIgnoreCase("voleur") || jobName.equalsIgnoreCase("rebelle") || jobName.equalsIgnoreCase("psychopathe") || jobName.equalsIgnoreCase("terroriste") || jobName.equalsIgnoreCase("hacker");
	}
	
	public static boolean isForceOrder(String jobName)
	{
		if(jobName == null) return false;
		
		return jobName.equalsIgnoreCase("gendarme") || jobName.equalsIgnoreCase("policier") || jobName.equalsIgnoreCase("bac") || jobName.equalsIgnoreCase("gign") || jobName.equalsIgnoreCase("douanier") || jobName.equalsIgnoreCase("militaire") || jobName.equalsIgnoreCase("pompier") ||  jobName.equalsIgnoreCase("medecin");
	}
	
	public static boolean canPutPenalty(EntityPlayer player)
	{
		String job = null;

		int gradeOrder = -1;
		
		try 
		{
			job =  ModCore.getCylrp().getJobName(player);
			gradeOrder = ModCore.getCylrp().getGradeOrder(player);
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException | NoSuchFieldException | InstantiationException e) {
			e.printStackTrace();
		}
		
		if(job.equalsIgnoreCase("bac"))
		{
			return true;
		}
		
		if(job.equalsIgnoreCase("douanier") && gradeOrder >= 3)
		{
			return true;
		}
		
		if(gradeOrder >= 2 && (job.equalsIgnoreCase("policier") || job.equalsIgnoreCase("gendarme") || job.equalsIgnoreCase("gign") || job.equalsIgnoreCase("militaire")))
		{
			return true;
		}
		
		return false;
	}
	
	public static int getPlayerCountForJob(World world, String jobName)
	{
			if(ModCore.getCylrp() == null) return -1;
			
	    	int count = 0;
	    	
	        List<EntityPlayer> playerList = new ArrayList<>(world.playerEntities); // Créez une copie de la liste

	    	
	    	for(EntityPlayer player : playerList)
	    	{
	        	try {
	        		String playerJob = ModCore.getCylrp().getJobName(player);
	        		if(playerJob != null)
	        		{
	        			if(playerJob.equalsIgnoreCase(jobName)) count++;
	        		}
				} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
						| IllegalAccessException | NoSuchMethodException | InstantiationException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	return count;
	 }
	
	 public static void broadcastMessage(World world, String msg)
	 {
	    List<EntityPlayer> playerList = new ArrayList<>(world.playerEntities);

		for(EntityPlayer player : playerList)
		{
			MinecraftUtils.sendMessage(player, msg);
		}
	 }
	 
	 public static void broadcastMessageJob(String job, World world, String msg)
	 {
		List<EntityPlayer> playerList = new ArrayList<>(world.playerEntities);

		for(EntityPlayer player : playerList)
		{
			try {
				if(ModCore.getCylrp().getJobName(player).equalsIgnoreCase(job))
				{
					MinecraftUtils.sendMessage(player, msg);
				}
			} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException
					| IllegalAccessException | NoSuchMethodException | InstantiationException
					| InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	 }
	
}
