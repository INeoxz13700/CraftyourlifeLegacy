package fr.innog.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;

public class NumberData {
	
	public static String generateNumber(String playerName)
	{
		Random random = new Random();
		int rand = random.nextInt((8 - 6)) + 6;
		String number = "0" + rand;
		while(true)
		{
			for(int i = 0; i < 8; i++)
			{
				number += random.nextInt(10);
			}
			
			if(!numberExist(number))
				return number;
		}
	}
	
	public static boolean numberExist(String number)
	{
		File data = new File("NumberData/" + number + ".txt");
	
		if(data.exists())
			return true;
		return false;
	}
	
	public static String getUsernameByNumber(String number) throws IOException
	{
		File data = new File("NumberData/" + number + ".txt");
		if(!data.exists())
			return null;
		
		FileReader r = new FileReader(data);
		BufferedReader bf = new BufferedReader(r);
		
		String username = "";
		
		while((username = bf.readLine()) != null)
		{
			bf.close();
			return username;
		}
		
		bf.close();
		return null;
	}
	
	public static void saveNumber(String number, EntityPlayer p) throws IOException
	{
		File repertory = new File("NumberData");
		
		if(!repertory.exists()) repertory.mkdir();
		
		File data = new File("NumberData/" + number + ".txt");
		if(!data.exists())
		{
			data.createNewFile();
		}

		
		FileWriter w = new FileWriter(data);
		w.write(p.getName());
		w.close();
	}

}