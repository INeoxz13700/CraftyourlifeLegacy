package fr.innog.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.api.informations.ApiInformations;
import fr.innog.common.ModCore;
import fr.innog.handler.TicksHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class AvatarData {

	public enum AvatarType
	{
		SKIN,
		CAPE;
	}
	
	private final static String skinUrl = ApiInformations.apiUrl + "/skins";
	private final static String capeUrl = ApiInformations.apiUrl + "/capes";

	public AbstractClientPlayer player;
	
	private String username;
	
	
	public AvatarData(AbstractClientPlayer p)
	{
		player = p;
		username = p.getName();
	}
	
	public AvatarData(String username)
	{
		this.username = username;
	}
	
    public Object[] getDownloadImageAvatar(AvatarType type)
    {

       TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        
        ResourceLocation texture = getLocationFromType(type);
                
        Object object = texturemanager.getTexture(texture);


        if(type == AvatarType.SKIN)
        {
    	    object = new ThreadDownloadImageData(null, skinUrl + "/" + username + ".png" ,DefaultPlayerSkin.getDefaultSkin(EntityPlayer.getOfflineUUID(username)), new ImageBufferDownload());
        }
        else
        {	
        	String url = capeUrl + "/" + username + ".png";
        	
        	if(!exists(url))
        	{
        		Object[] obj = new Object[2];

        		obj[0] = (ThreadDownloadImageData)object;
        		obj[1] = null;
        		
        		return obj;
        	}
        	
        	object = new ThreadDownloadImageData(null,url , null, new ImageBufferDownload());
        }


	    texturemanager.loadTexture(texture, (ITextureObject)object);

		Object[] obj = new Object[2];

		obj[0] = (ThreadDownloadImageData)object;
		obj[1] = texture;
		
		return obj;   
	}
    
    public ResourceLocation getLocationFromType(AvatarType type)
    {
    	if(type == AvatarType.SKIN)
    	{
    		return new ResourceLocation("craftyourliferp:" + username + "_skin");
    	}
    	else
    	{
    		return new ResourceLocation("craftyourliferp:" + username + "_cape");
    	}
    }
	
	public void updateAvatar()
	{		
		
	     TicksHandler.scheduleCallback(1000, new UIButton.CallBackObject()
	     {
	    	 
	    	 @Override
	    	 public void call()
	    	 {
					
					ResourceLocation texture = (ResourceLocation) getDownloadImageAvatar(AvatarType.SKIN)[1];
					if(player != null)
					{
						AbstractClientPlayer absPlayer = (AbstractClientPlayer)player;

						
						Method method = ReflectionHelper.findMethod(AbstractClientPlayer.class, "getPlayerInfo", "func_175155_b", new Class[] {});
						method.setAccessible(true);
								
						NetworkPlayerInfo networkInfo = null;
						try {
							networkInfo = (NetworkPlayerInfo) method.invoke(absPlayer, new Object[] {});
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
						
						if(networkInfo == null) return;
										
						Map<Type, ResourceLocation> playerTextures = ReflectionHelper.getPrivateValue(NetworkPlayerInfo.class, networkInfo, 1);
						
				        playerTextures.put(Type.SKIN, texture);
				        
						texture = (ResourceLocation) getDownloadImageAvatar(AvatarType.CAPE)[1];
				      
						playerTextures.put(Type.CAPE, texture);
					}
	    		 
	    	 }
	     });
	}
	
	
	public boolean exists(String url){
		
		HttpURLConnection con = null;
		
	    try {
	      HttpURLConnection.setFollowRedirects(false);

	      con = (HttpURLConnection) new URL(url).openConnection();
	      
	      con.setRequestMethod("HEAD");
	      
	      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	    }
	    catch (Exception e) {
	       e.printStackTrace();
	       return false;
	    }
	    finally
	    {
	    	if(con != null)
	    	{
	    		con.disconnect();
	    	}
	    }
	    
	}
	

	
}
