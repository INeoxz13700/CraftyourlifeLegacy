package fr.innog.common;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.Logger;

import fr.dynamx.api.contentpack.DynamXAddon;
import fr.innog.api.informations.ApiInformations;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerFactory;
import fr.innog.capability.playercapability.PlayerStorage;
import fr.innog.commands.CommandAnimation;
import fr.innog.commands.CommandApi;
import fr.innog.commands.CommandBlackMarket;
import fr.innog.commands.CommandBlacklist;
import fr.innog.commands.CommandConcessionnaire;
import fr.innog.commands.CommandCosmetics;
import fr.innog.commands.CommandEthylotest;
import fr.innog.commands.CommandIdentity;
import fr.innog.commands.CommandPenalty;
import fr.innog.commands.CommandStressTest;
import fr.innog.commands.CommandVehicle;
import fr.innog.common.animations.AnimationManager;
import fr.innog.common.animations.CoucouAnimation;
import fr.innog.common.animations.FlossDanceAnimation;
import fr.innog.common.animations.HandCuffedAnimation;
import fr.innog.common.animations.HandUpAnimation;
import fr.innog.common.cosmetics.CosmeticManager;
import fr.innog.common.entity.EntityLootableBody;
import fr.innog.common.items.ItemExtinguisher;
import fr.innog.common.proxy.IProxy;
import fr.innog.handler.CapabilityHandler;
import fr.innog.handler.TicksHandler;
import fr.innog.network.packets.PacketHandler;
import fr.innog.server.adapter.Adapter;
import fr.innog.server.adapter.CYLRPCoreAdapter;
import fr.innog.server.adapter.EssentialsAdapter;
import fr.innog.server.adapter.WorldGuardAdapter;
import fr.innog.ui.remote.RemoteCharacterUI;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

@Mod(useMetadata = true, modid = ModCore.MODID, dependencies="after:advancedui")
@DynamXAddon(modid = ModCore.MODID, name = "CraftYourLifeRP Mod", version = "5.0.2")
public class ModCore
{
    public static final String MODID = "craftyourliferp";
        
    public static String NAME;
    public static String VERSION;
    
    public static final boolean debugMode = false;
        
    private static Configuration configFile;
    
    
    private static final CosmeticManager cosmeticsManager = new CosmeticManager();

    private static final PacketHandler packetHandler = new PacketHandler();
    
    private static final AnimationManager animationManager = new AnimationManager();
    
    private static final HashMap<Class<? extends Adapter>, Adapter> adapters = new HashMap<>();
           
	@SidedProxy(clientSide = "fr.innog.common.proxy.ClientProxy", serverSide = "fr.innog.common.proxy.ServerProxy")
   	public static IProxy proxy;
	
    @DynamXAddon.AddonEventSubscriber
    public static void init()
    {
        System.out.println("Dynamx Addon initialized!");
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
            
        ModMetadata metadata = event.getModMetadata();
        NAME = metadata.name;
        VERSION = metadata.version;
        
        registerHandlers();
        
        registerAnimations();

        proxy.preInit(event);
        
    	configFile = new Configuration(event.getSuggestedConfigurationFile());
    	syncConfig(event.getSide());
      
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
		packetHandler.initialise();	
		
		registerPermissions();
				
		CapabilityManager.INSTANCE.register(IPlayer.class, new PlayerStorage(), new PlayerFactory());

		registerEntity();
		
		proxy.init(event);
		
		proxy.registerUIRemote();
		
		proxy.registerCosmetics();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        log(NAME + " (" + VERSION + ") loaded successfully");
        
        proxy.postInit(event);
        
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
    	event.registerServerCommand(new CommandConcessionnaire("concessionnaire"));
    	event.registerServerCommand(new CommandVehicle("vehicle"));
    	event.registerServerCommand(new CommandCosmetics("cosmetics"));
    	event.registerServerCommand(new CommandAnimation("animations"));
    	event.registerServerCommand(new CommandIdentity("identity"));
    	event.registerServerCommand(new CommandApi("api"));
    	event.registerServerCommand(new CommandEthylotest("ethylotest"));
    	event.registerServerCommand(new CommandBlacklist("blacklist"));
    	event.registerServerCommand(new CommandBlackMarket("blackmarket"));
    	event.registerServerCommand(new CommandPenalty("penalty"));
    	
        ApiInformations.getMysql().initConnection();
    	
    	
    	ModControllers.concessionnaireController.loadConcessionnaires();
    	
    	if(event.getServer().isDedicatedServer())
    	{
    		adapters.put(EssentialsAdapter.class, new EssentialsAdapter());
    		adapters.put(WorldGuardAdapter.class, new WorldGuardAdapter());
    		adapters.put(CYLRPCoreAdapter.class, new CYLRPCoreAdapter());

    		new Thread()
        	{
        		@Override
        		public void run()
        		{
        			while(true)
        			{
        				
        					HashMap<Class<? extends Adapter>, Adapter> toRemove = new HashMap<>();
	        				for(Entry<Class<? extends Adapter>, Adapter> entry : adapters.entrySet())
	            			{
	        					try
	            				{
	        						Adapter adapter = entry.getValue();
		            				if(!adapter.initialized())
		            				{
		            					adapter.init();
		            				}
	            				}
	            				catch(NullPointerException e)
	            				{
	            					toRemove.put(entry.getKey(), entry.getValue());
	            					continue;
	            				}
	            				
	            			}
	        				adapters.keySet().removeAll(toRemove.keySet());
        			
        				
						try {
							Thread.sleep(100L);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        			}
        		}
        			
        			
        	}.start();
    	}
        	
        
    }
    
    private void registerEntity()
    {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "lootableBody"), EntityLootableBody.class, "lootableBody", 108, this, 200, 4, true);
    }
    
    private void registerAnimations()
    {
    	animationManager.registerAnimation("Coucou", CoucouAnimation.class);
    	animationManager.registerAnimation("Lever les mains", HandUpAnimation.class);
    	animationManager.registerAnimation("Menotter", HandCuffedAnimation.class);
    	animationManager.registerAnimation("Floss dance", FlossDanceAnimation.class);
    }
    
    private void registerPermissions()
    {
	    PermissionAPI.registerNode(ModCore.MODID + ".command.concessionnaire", DefaultPermissionLevel.OP, "Allows players to use the concessionnaire commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.vehicle", DefaultPermissionLevel.OP, "Allows players to use the vehicle commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.cosmetics", DefaultPermissionLevel.OP, "Allows players to use the cosmetics commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.animations", DefaultPermissionLevel.OP, "Allows players to use the animations commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.identity", DefaultPermissionLevel.OP, "Allows players to use the identity commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.api", DefaultPermissionLevel.OP, "Allows players to use the api commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.blacklist", DefaultPermissionLevel.OP, "Allows players to use the blacklist commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.blackmarket", DefaultPermissionLevel.OP, "Allows players to use the blackmarket commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.penalty", DefaultPermissionLevel.OP, "Allows players to use the penalty commands");
	    PermissionAPI.registerNode(ModCore.MODID + ".command.ethylotest", DefaultPermissionLevel.ALL, "Allows players to use the ethylotest commands");

    }
    
	private void registerHandlers()
    {
    	MinecraftForge.EVENT_BUS.register(new TicksHandler());
    	
    	MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
    }
	
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
	    if(eventArgs.getModID().equals(MODID))
	    {
	       syncConfig();
	    }
	}
	    
	public static void syncConfig() {
	    if(configFile != null && configFile.hasChanged())
	    {
	    	configFile.save();
	    }
	}
	    
	public static void syncConfig(Side side)
	{		   	 	
	    if(side.isServer())
	    {
		    RemoteCharacterUI.startItem = configFile.getStringList("Item de depart", Configuration.CATEGORY_GENERAL, new String[]{"1-1"}, "ItemId-Quantite (Item de depart)");
		    ItemExtinguisher.minMoneyEarn = configFile.getInt("Feu éteint Argent gagné minimum",Configuration.CATEGORY_GENERAL, 1, 1, Integer.MAX_VALUE, "Argent gagné minimum");
		    ItemExtinguisher.maxMoneyEarn = configFile.getInt("Feu éteint Argent gagné maximum",Configuration.CATEGORY_GENERAL, 5, 1, Integer.MAX_VALUE, "Argent gagné maximum");
	    }
	    
	    if(configFile != null && configFile.hasChanged())configFile.save();
	}
   
    
    public static void log(Object msg)
    {
    	System.out.println(msg);
    }
    
    public static void debug(Object msg)
    {
    	if(debugMode) System.out.println(msg);

    }

	public static PacketHandler getPackethandler() {
		return packetHandler;
	}
	
	public static EssentialsAdapter getEssentials() {
		return (EssentialsAdapter) adapters.get(EssentialsAdapter.class);
	}
	
	public static WorldGuardAdapter getWorldGuard() {
		return (WorldGuardAdapter) adapters.get(WorldGuardAdapter.class);
	}
	
	public static CYLRPCoreAdapter getCylrp() {
		return (CYLRPCoreAdapter) adapters.get(CYLRPCoreAdapter.class);
	}
	
	public static CosmeticManager getCosmeticsManager() {
		return cosmeticsManager;
	}
	
	public static AnimationManager getAnimationManager() {
		return animationManager;
	}

}