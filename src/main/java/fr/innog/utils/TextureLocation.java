package fr.innog.utils;

import java.io.File;

import fr.innog.client.renderer.ThreadDownloadTexture;
import fr.innog.common.ModCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class TextureLocation {

	private TextureLocation() { }
	
	
	public static ResourceLocation getTextureLocation(String resourcesPath)
	{
		return new ResourceLocation(ModCore.MODID + ":" + resourcesPath);
	}
	
	public static ResourceLocation downloadTexture(String url)
	{
		String[] splittedUrl = url.split("/");
		ResourceLocation downloadTexture = new ResourceLocation("craftyourliferp", splittedUrl[splittedUrl.length-1]);
       
		TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

		ThreadDownloadTexture downloadThread = new ThreadDownloadTexture((File)new File("cache/" + splittedUrl[splittedUrl.length-1]), url, null, new ImageBufferDownload());
        
        textureManager.loadTexture(downloadTexture, downloadThread);
        
        return downloadTexture;
	}
	
}
