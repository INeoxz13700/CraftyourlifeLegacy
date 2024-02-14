package fr.innog.client.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerData;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.common.ModCore;
import fr.innog.data.UserSession;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = ModCore.MODID, value = Side.CLIENT)
public class ModClient {

	private IPlayer playerData;
	
	private HashMap<EntityPlayer, IPlayer> otherClients = new HashMap<>();
	
	public String version = "undefined";
	
	private UserSession currentSession;
	
	public ModClient()
	{
		try {
			currentSession = UserSession.getSession();
			ModCore.log("Session obtenue mots de passe crypté : " + currentSession.getCryptedPassword());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public IPlayer getLocalPlayerCapability()
	{
		if(playerData == null)
		{
			playerData = Minecraft.getMinecraft().player.getCapability(PlayerProvider.PLAYER_CAP, null);
		}

		return playerData;
	}
	
	public IPlayer getPlayerCapability(EntityPlayer player)
	{
		IPlayer playerData = null;
		if(!otherClients.containsKey(player))
		{
			playerData = new PlayerData();
			
			playerData.initData(player);
			
			otherClients.put(player, playerData);
		}
		else
		{
			playerData = otherClients.get(player);
		}
	
		return playerData;
	}
	
	public UserSession getCurrentSession()
	{
		return currentSession;
	}
		
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
	public static void onClientTick(ClientTickEvent event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		

		if(!ModCore.debugMode)
		{
			mc.getRenderManager().setDebugBoundingBox(false);
		}

		if(mc.player != null && !(mc.player.getRidingEntity() instanceof BaseVehicleEntity))
		{
			if(!ModCore.debugMode)
			{
				if(!mc.player.capabilities.isCreativeMode)
				{
					if(mc.gameSettings.thirdPersonView > 1)
					{
						mc.gameSettings.thirdPersonView = 0;
					}
				}
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
    @SubscribeEvent
	public static void onCameraSetup(EntityViewRenderEvent.FOVModifier event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		
		if(!ModCore.debugMode)
		{
			mc.getRenderManager().setDebugBoundingBox(false);
		}

		if(mc.player != null && !(mc.player.getRidingEntity() instanceof BaseVehicleEntity))
		{
			if(mc.gameSettings.thirdPersonView == 1)
			{
				event.setFOV(30f);
			}
		}
	}
	
    public String getComputerUID() throws IOException, InterruptedException 
    {
    	String OS = System.getProperty("os.name").toLowerCase();
    	
    	if(OS.indexOf("win") >= 0)
    	{
    		String machineId = "";
    		try
    		{
	    		String command = "wmic csproduct get UUID";
	    	    StringBuffer output = new StringBuffer();
	
	    	    Process SerNumProcess = Runtime.getRuntime().exec(command);
	    	    BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));
	
	    	    String line = "";
	    	    while ((line = sNumReader.readLine()) != null) {
	    	        output.append(line + "\n");
	    	    }
	    	    
	    	    machineId = output.toString().substring(output.indexOf("\n"), output.length()).trim();
    		}
    		catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		
    	    return machineId;
    	}
    	else if(OS.indexOf("mac") >= 0)
    	{
    		String command = "system_profiler SPHardwareDataType | awk '/UUID/ { print $3; }'";

    	    StringBuffer output = new StringBuffer();


    	    Process SerNumProcess = Runtime.getRuntime().exec(command);

    	    BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));

    	    String line = "";

    	    while ((line = sNumReader.readLine()) != null) {
    	        output.append(line + "\n");
    	    }

    	    String machineId=output.toString().substring(output.indexOf("UUID: "), output.length()).replace("UUID: ", "");

    	    SerNumProcess.waitFor();

    	    sNumReader.close();

    	    return machineId;
    	}
    	else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0)
    	{
    		   StringBuffer output = new StringBuffer();
    	       Process process;
    	       process = Runtime.getRuntime().exec("cat /sys/class/dmi/id/product_uuid");
    	       BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    	       String line = "";
    	       while ((line = reader.readLine()) != null) 
    	       {
    	    	   output.append(line + "\n");
    	       }
    	       return output.toString();      
    	}
    	
    	return "undefined";
    }
    
    public String getMacAdress() throws UnknownHostException,SocketException
    {
		InetAddress ipAddress = InetAddress.getLocalHost();
		NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ipAddress);
		
		byte[] macAddressBytes = networkInterface.getHardwareAddress();
		StringBuilder macAddressBuilder = new StringBuilder();
		
		for (int macAddressByteIndex = 0; macAddressByteIndex < macAddressBytes.length; macAddressByteIndex++)
		{
		    String macAddressHexByte = String.format("%02X",
		            macAddressBytes[macAddressByteIndex]);
		    macAddressBuilder.append(macAddressHexByte);
		
		    if (macAddressByteIndex != macAddressBytes.length - 1)
		    {
		        macAddressBuilder.append(":");
		    }
		}
		
		return macAddressBuilder.toString();
    }
    
    private String getBiosVendor()
    {
    	return ManagementFactory.getOperatingSystemMXBean().getArch();
    }
    
    private String getBiosVendor2()
    {
    	return ManagementFactory.getOperatingSystemMXBean().getName();
    }
    
    private String getOs()
    {
    	return System.getProperty("os.name");
    }
    
    private String getVersion()
    {
        return System.getProperty("os.version");
    }
	
	
	
}
