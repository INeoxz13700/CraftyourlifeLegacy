package fr.innog.common.proxy;

import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.common.registries.UIRemoteRegistry;
import fr.innog.ui.remote.RemoteAnimationUI;
import fr.innog.ui.remote.RemoteAtmUI;
import fr.innog.ui.remote.RemoteCharacterUI;
import fr.innog.ui.remote.RemoteConcessionnaireUI;
import fr.innog.ui.remote.RemoteCosmeticUI;
import fr.innog.ui.remote.RemoteGarageUI;
import fr.innog.ui.remote.RemotePenaltyUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerProxy implements IProxy {

	@Override
	public void preInit(FMLPreInitializationEvent event) 
	{

	}

	@Override
	public void init(FMLInitializationEvent event) 
	{ 
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) 
	{

	}

	@Override
	public void serverStarting(FMLServerStartingEvent event) {
		
	}

	@Override
	public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
        return ctx.getServerHandler().player;
	}

	@Override
	public void registerUIRemote() {
		UIRemoteRegistry.registerUI(RemoteConcessionnaireUI.class);	
		UIRemoteRegistry.registerUI(RemoteGarageUI.class);
		UIRemoteRegistry.registerUI(RemoteCosmeticUI.class);
		UIRemoteRegistry.registerUI(RemoteAnimationUI.class);
		UIRemoteRegistry.registerUI(RemoteCharacterUI.class);
		UIRemoteRegistry.registerUI(RemoteAtmUI.class);
		UIRemoteRegistry.registerUI(RemotePenaltyUI.class);
	}

	@Override
	public void registerCosmetics() {
		ModCore.getCosmeticsManager().registerCosmetic("Bird", false, (byte)2,0);
		ModCore.getCosmeticsManager().registerCosmetic("PokemonMinccino", false, (byte)2,1);
		ModCore.getCosmeticsManager().registerCosmetic("Smithy Hat", false, (byte)0,2);
		ModCore.getCosmeticsManager().registerCosmetic("Lunette d'anniversaire de CYLRP §62020", false, (byte)1,3);
		ModCore.getCosmeticsManager().registerCosmetic("Chapeau de sorcière", false, (byte)0,4);
		ModCore.getCosmeticsManager().registerCosmetic("voxelPet", false, (byte)2,5);
		ModCore.getCosmeticsManager().registerCosmetic("Bonhomme de neige", false, (byte)2,6);
		ModCore.getCosmeticsManager().registerCosmetic("Bonnet de noel", false, (byte)0,7);
		ModCore.getCosmeticsManager().registerCosmetic("Oreilles de lapin", false, (byte)0,8);
		ModCore.getCosmeticsManager().registerCosmetic("Casquette de baseball", false, (byte)0,9);
		ModCore.getCosmeticsManager().registerCosmetic("Lunettes 3d", false, (byte)1,10);
		ModCore.getCosmeticsManager().registerCosmetic("Auréole", false, (byte)0,11);
		ModCore.getCosmeticsManager().registerCosmetic("Chapeau Mario", false, (byte)0,12);
		ModCore.getCosmeticsManager().registerCosmetic("Chapeau Fou du roi", false, (byte)0,13);
		ModCore.getCosmeticsManager().registerCosmetic("Epee sur la tête", false, (byte)0,14);
		ModCore.getCosmeticsManager().registerCosmetic("Masque d'alien", false, (byte)1,15);
		ModCore.getCosmeticsManager().registerCosmetic("Tête de clown", false, (byte)0,16);
		ModCore.getCosmeticsManager().registerCosmetic("Chapeau de sorcière 2", false, (byte)0,17);		
	    ModCore.getCosmeticsManager().registerCosmetic("Cornes de diable", false, (byte)0,18);		
	    CosmeticObject unicornHat = ModCore.getCosmeticsManager().registerCosmetic("Chapeau de licorne", false, (byte)0, 19);
	    unicornHat.setSpecialMessage("§6Cosmétique légendaire");
	    CosmeticObject kingsCrown = ModCore.getCosmeticsManager().registerCosmetic("Couronne des rois", false, (byte)0,20);
	    kingsCrown.setSpecialMessage("§6Cosmétique légendaire §f° §eLongue vie au roi!");
	    ModCore.getCosmeticsManager().registerCosmetic("Chapeau de cowboy",false, (byte)0,21);
	    ModCore.getCosmeticsManager().registerCosmetic("Casque d'ouvrier", true, (byte)0,22);
	    CosmeticObject antenna = ModCore.getCosmeticsManager().registerCosmetic("Joystick",false, (byte)0,23);
	    antenna.setSpecialMessage("§5Cosmétique épique §f° §dC'est partie pour une game!");
	    ModCore.getCosmeticsManager().registerCosmetic("Aile de démon", false, (byte)2,24);
		ModCore.getCosmeticsManager().registerCosmetic("Aile d'ange", false, (byte)2,25);
		ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 26);
		ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 27);
		ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 28);
		ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 29);
		ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 30);
		ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 31);				
	}
	
}
