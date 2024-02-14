package fr.innog.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;


import fr.innog.common.cosmetics.CosmeticObject;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class MigrateUtils {

	public static void migrateOldCosmetics(EntityPlayerMP player)
	{
		String world = player.getEntityWorld().getWorldInfo().getWorldName();
		
		File oldUUIDPlayerName = new File(world + "/OldUUID/" + player.getName() + ".txt");
		if(oldUUIDPlayerName.exists())
		{
			try {
				UUID oldUUID = UUID.fromString(Files.readAllLines(Paths.get(oldUUIDPlayerName.getPath())).get(0));
			
				File playerOldData = new File(world + "/OldPlayerData/" + oldUUID.toString() + ".dat");
				if(playerOldData.exists())
				{
					NBTTagList tagList = (NBTTagList) getOldData(playerOldData);
					
					if(tagList.tagCount() > 0)
					{
						MinecraftUtils.sendMessage(player, "Migration de vos cosmétiques sur la V5 en cours.");
						for(int id = 0; id < tagList.tagCount(); id++)
						{
							NBTTagCompound compoundCosmetic = tagList.getCompoundTagAt(id);
							boolean unlocked = !compoundCosmetic.getBoolean("isLocked");
							if(unlocked) CosmeticObject.setCosmetiqueUnlocked(player, id);
						}
						MinecraftUtils.sendMessage(player, "Migration de vos cosmétiques terminé.");
					}
				
					playerOldData.delete();
				}
				
				oldUUIDPlayerName.delete();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static NBTBase getOldData(File playerOldDataFile) throws FileNotFoundException, IOException
	{
	    NBTTagCompound oldNbt = CompressedStreamTools.readCompressed(new FileInputStream(playerOldDataFile));

	    
	    return oldNbt.getTag("CYLRPMAINDATA-Cosmetics");
	}
	
}
