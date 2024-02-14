package fr.innog.common.proxy;

import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import fr.innog.client.AntiCheatHelper;
import fr.innog.client.creativetab.CYLCreativeTab;
import fr.innog.client.data.ModClient;
import fr.innog.client.model.ModelBipedArmor;
import fr.innog.client.model.cosmetics.Model3DGlasses;
import fr.innog.client.model.cosmetics.ModelAlienMask;
import fr.innog.client.model.cosmetics.ModelAntenna;
import fr.innog.client.model.cosmetics.ModelAureole;
import fr.innog.client.model.cosmetics.ModelBird;
import fr.innog.client.model.cosmetics.ModelClownHead;
import fr.innog.client.model.cosmetics.ModelColorfulMask;
import fr.innog.client.model.cosmetics.ModelCowboyHat;
import fr.innog.client.model.cosmetics.ModelCylrpGlasses;
import fr.innog.client.model.cosmetics.ModelDemonWings;
import fr.innog.client.model.cosmetics.ModelDevilHorn;
import fr.innog.client.model.cosmetics.ModelFairyWings;
import fr.innog.client.model.cosmetics.ModelHardHat;
import fr.innog.client.model.cosmetics.ModelJesterHat;
import fr.innog.client.model.cosmetics.ModelKingsCrown;
import fr.innog.client.model.cosmetics.ModelMarioHat;
import fr.innog.client.model.cosmetics.ModelMincinno;
import fr.innog.client.model.cosmetics.ModelNoelBonnet;
import fr.innog.client.model.cosmetics.ModelPropellerHat;
import fr.innog.client.model.cosmetics.ModelRabbitEars;
import fr.innog.client.model.cosmetics.ModelSmithyHat;
import fr.innog.client.model.cosmetics.ModelSnowman;
import fr.innog.client.model.cosmetics.ModelSwordOnHead;
import fr.innog.client.model.cosmetics.ModelUnicornHat;
import fr.innog.client.model.cosmetics.ModelVoxel;
import fr.innog.client.model.cosmetics.ModelWitchHat;
import fr.innog.client.model.cosmetics.ModelWitchHat2;
import fr.innog.client.model.entity.ModelPlayerOverride;
import fr.innog.client.renderer.entity.RenderLootableEntityBody;
import fr.innog.client.renderer.entity.layers.LayerArmorAdditionnal;
import fr.innog.client.ui.ingame.AnimationUI;
import fr.innog.client.ui.ingame.AtmUI;
import fr.innog.client.ui.ingame.CharacterUI;
import fr.innog.client.ui.ingame.ConcessionnaireUI;
import fr.innog.client.ui.ingame.CosmeticUI;
import fr.innog.client.ui.ingame.GarageUI;
import fr.innog.client.ui.ingame.PenaltyUI;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.common.cosmetics.ICosmeticSetup;
import fr.innog.common.entity.EntityLootableBody;
import fr.innog.common.registries.UIRemoteRegistry;
import fr.innog.handler.InputHandler;
import fr.innog.handler.UIHandler;
import fr.innog.ui.remote.RemoteAnimationUI;
import fr.innog.ui.remote.RemoteAtmUI;
import fr.innog.ui.remote.RemoteCharacterUI;
import fr.innog.ui.remote.RemoteConcessionnaireUI;
import fr.innog.ui.remote.RemoteCosmeticUI;
import fr.innog.ui.remote.RemoteGarageUI;
import fr.innog.ui.remote.RemotePenaltyUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerCustomHead;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientProxy implements IProxy {

	public static KeyBinding[] keyBindings; 
	
	public static ModClient modClient;
	
	public static CYLCreativeTab creativeTabs = null;
	
	public static boolean forceExit = false;
	
	@Override
	public void preInit(FMLPreInitializationEvent event) 
	{
        OBJLoader.INSTANCE.addDomain(ModCore.MODID);
		registerKeysBindings();
		registerHandlers();
		
		creativeTabs = new CYLCreativeTab("crp_creative_tabs");
		
        registerEntityRender();
        
        modClient = new ModClient();

	}

	@Override
	public void init(FMLInitializationEvent event) 
	{
    	Display.setTitle("CraftYourLifeRP - Legacy " + ModCore.VERSION);

        Render<AbstractClientPlayer> render = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(AbstractClientPlayer.class);
        Map<String, RenderPlayer> skinMap = render.getRenderManager().getSkinMap();
        this.overridePlayerRender(skinMap.get("default"), false);
        this.overridePlayerRender(skinMap.get("slim"), true);
                
		AntiCheatHelper.startAntiCheat();
		
	} 
	

	@Override
	public void postInit(FMLPostInitializationEvent event) 
	{

	}

	@Override
	public void serverStarting(FMLServerStartingEvent event) {
		
	}
	
	@SuppressWarnings("deprecation")
	private void overridePlayerRender(RenderPlayer player, boolean smallArms)
    {
        ModelBiped model = new ModelPlayerOverride(0.0F, smallArms);
        
        List<LayerRenderer<EntityLivingBase>> layers = ObfuscationReflectionHelper.getPrivateValue(RenderLivingBase.class, player, 4);

        
        if(layers != null)
        {
            layers.removeIf(layerRenderer -> layerRenderer instanceof LayerHeldItem || layerRenderer instanceof LayerCustomHead);
            layers.add(new LayerHeldItem(player));
            layers.add(new LayerCustomHead(model.bipedHead));
            layers.forEach(layerRenderer ->
            {
                if(layerRenderer instanceof LayerBipedArmor)
                {
                    this.overrideArmor((LayerBipedArmor) layerRenderer, model);
                }
            });
            layers.add(new LayerArmorAdditionnal<ModelBiped>(player));
        }

        ObfuscationReflectionHelper.setPrivateValue(RenderLivingBase.class, player, model, 2);
    }
	
    @SuppressWarnings("deprecation")
	private void overrideArmor(LayerBipedArmor layerBipedArmor, ModelBiped source)
    {
    	ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, layerBipedArmor, new ModelBipedArmor(source, 1.0F), 2);
        ObfuscationReflectionHelper.setPrivateValue(LayerArmorBase.class, layerBipedArmor, new ModelBipedArmor(source, 0.5F), 1);
    }
	
    public void registerEntityRender()
    {
        RenderingRegistry.registerEntityRenderingHandler(EntityLootableBody.class, RenderLootableEntityBody::new);
    }
    
	
    public void registerHandlers()
    {
    	MinecraftForge.EVENT_BUS.register(new UIHandler());
    	MinecraftForge.EVENT_BUS.register(new InputHandler());
    }
    
	private void registerKeysBindings()
	{
		keyBindings = new KeyBinding[3]; 
		  
		keyBindings[0] = new KeyBinding("key.garage", Keyboard.KEY_G, "key.craftyourliferp");
		keyBindings[1] = new KeyBinding("key.utiliser", Keyboard.KEY_U, "key.craftyourliferp");
		keyBindings[2] = new KeyBinding("key.animation", Keyboard.KEY_M, "key.craftyourliferp");

		for (int i = 0; i < keyBindings.length; ++i) 
		{
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}

	@Override
	public EntityPlayer getPlayerEntityFromContext(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : ModCore.proxy.getPlayerEntityFromContext(ctx));
	}

	@Override
	public void registerUIRemote() {
		try {
			UIRemoteRegistry.registerUI(RemoteConcessionnaireUI.class, ConcessionnaireUI.class);
			UIRemoteRegistry.registerUI(RemoteGarageUI.class, GarageUI.class);
			UIRemoteRegistry.registerUI(RemoteCosmeticUI.class, CosmeticUI.class);
			UIRemoteRegistry.registerUI(RemoteAnimationUI.class, AnimationUI.class);
			UIRemoteRegistry.registerUI(RemoteCharacterUI.class, CharacterUI.class);
			UIRemoteRegistry.registerUI(RemoteAtmUI.class, AtmUI.class);
			UIRemoteRegistry.registerUI(RemotePenaltyUI.class, PenaltyUI.class);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void registerCosmetics() {
		
		CosmeticObject bird = ModCore.getCosmeticsManager().registerCosmetic("Bird", false, (byte)2,0);
		CosmeticObject minccino = ModCore.getCosmeticsManager().registerCosmetic("PokemonMinccino", false, (byte)2,1);
		CosmeticObject smithyHat = ModCore.getCosmeticsManager().registerCosmetic("Smithy Hat", false, (byte)0,2);
		CosmeticObject cylGlasses = ModCore.getCosmeticsManager().registerCosmetic("Lunette d'anniversaire de CYLRP §62020", false, (byte)1,3);
		CosmeticObject witchHat = ModCore.getCosmeticsManager().registerCosmetic("Chapeau de sorcière", false, (byte)0,4);
		CosmeticObject voxelPet = ModCore.getCosmeticsManager().registerCosmetic("voxelPet", false, (byte)2,5);
		CosmeticObject snowman = ModCore.getCosmeticsManager().registerCosmetic("Bonhomme de neige", false, (byte)2,6);
		CosmeticObject noelBonnet = ModCore.getCosmeticsManager().registerCosmetic("Bonnet de noel", false, (byte)0,7);
		CosmeticObject rabbitEars = ModCore.getCosmeticsManager().registerCosmetic("Oreilles de lapin", false, (byte)0,8);
		CosmeticObject propellerHat = ModCore.getCosmeticsManager().registerCosmetic("Casquette de baseball", false, (byte)0,9);
		CosmeticObject threedGlasses = ModCore.getCosmeticsManager().registerCosmetic("Lunettes 3d", false, (byte)1,10);
		CosmeticObject aureole = ModCore.getCosmeticsManager().registerCosmetic("Auréole", false, (byte)0,11);
		CosmeticObject marioHat = ModCore.getCosmeticsManager().registerCosmetic("Chapeau Mario", false, (byte)0,12);
		CosmeticObject jesterHat = ModCore.getCosmeticsManager().registerCosmetic("Chapeau Fou du roi", false, (byte)0,13);
		CosmeticObject swordOnHead = ModCore.getCosmeticsManager().registerCosmetic("Epee sur la tête", false, (byte)0,14);
		CosmeticObject alienMask = ModCore.getCosmeticsManager().registerCosmetic("Masque d'alien", false, (byte)1,15);
		CosmeticObject clownHead = ModCore.getCosmeticsManager().registerCosmetic("Tête de clown", false, (byte)0,16);
		CosmeticObject witchHat2 = ModCore.getCosmeticsManager().registerCosmetic("Chapeau de sorcière 2", false, (byte)0,17);		
		CosmeticObject devilHorn = ModCore.getCosmeticsManager().registerCosmetic("Cornes de diable", false, (byte)0,18);		
	    CosmeticObject unicornHat = ModCore.getCosmeticsManager().registerCosmetic("Chapeau de licorne", false, (byte)0, 19);
	    unicornHat.setSpecialMessage("§6Cosmétique légendaire");
	    CosmeticObject kingsCrown = ModCore.getCosmeticsManager().registerCosmetic("Couronne des rois", false, (byte)0,20);
	    kingsCrown.setSpecialMessage("§6Cosmétique légendaire §f° §eLongue vie au roi!");
	    CosmeticObject cowboyHat = ModCore.getCosmeticsManager().registerCosmetic("Chapeau de cowboy",false, (byte)0,21);
	    CosmeticObject hardHat = ModCore.getCosmeticsManager().registerCosmetic("Casque d'ouvrier", true, (byte)0,22);
	    CosmeticObject antenna = ModCore.getCosmeticsManager().registerCosmetic("Joystick",false, (byte)0,23);
	    antenna.setSpecialMessage("§5Cosmétique épique §f° §dC'est partie pour une game!");
	  
	    CosmeticObject demonWings = ModCore.getCosmeticsManager().registerCosmetic("Aile de démon", false, (byte)2,24);
		CosmeticObject fairyWings = ModCore.getCosmeticsManager().registerCosmetic("Aile d'ange", false, (byte)2,25);
		CosmeticObject colorfulMaskBlue = ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 26);
		CosmeticObject colorfulMaskOrange = ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 27);
		CosmeticObject colorfulMaskBlack = ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 28);
		CosmeticObject colorfulMaskRed = ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 29);
		CosmeticObject colorfulMaskPink = ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 30);
		CosmeticObject colorfulMaskYellow = ModCore.getCosmeticsManager().registerCosmetic("Masque coloré", false, (byte)1, 31);		
		
		bird.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glScalef(50, 50, 50);
			}
	
		}, new ModelBird());
		
		minccino.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glScalef(1, 1, 1);
				GL11.glRotatef(180, 0, 1, 0);
				GL11.glTranslated(-16, 5, 0);
			}
			
		
		}, new ModelMincinno());
		
		smithyHat.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslated(18, 5, 0);
				GL11.glRotatef(90, 0, 1, 0);
			}
			
			
			
		}, new ModelSmithyHat());
		
		cylGlasses.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(25f, -6f, 0);

				GL11.glScalef(40, 40, 40);				
			}
			
			
			
		}, new ModelCylrpGlasses());
		
	
		witchHat.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslated(20f, 3f, 0);
				GL11.glScalef(25f, 25f, 25f);
				GL11.glRotatef(180, 0, 1, 0);
			}


			
		}, new ModelWitchHat());
		
		voxelPet.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(16f, -6f, 0);
				GL11.glScalef(0.8f, 0.8f, 0.8f);
				GL11.glRotatef(190, 0, 1, 0);
			}

		}, new ModelVoxel());
		
		
		snowman.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslated(20, 12, 0);

				GL11.glScalef(35, 35, 35);
			}

		}, new ModelSnowman());
		
		noelBonnet.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslated(18, 20, 0);
				GL11.glScalef(40, 40, 40);


			}

		}, new ModelNoelBonnet());
		
		rabbitEars.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslated(21, -11, 0);
				GL11.glScalef(30, 30, 30);
			}

		}, new ModelRabbitEars());
		
		propellerHat.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslated(18, -5, 0);
				GL11.glScalef(20, 20, 20);

			}

		}, new ModelPropellerHat());
		
		threedGlasses.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(20f, 32, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				
				GL11.glScalef(150,150,150);
			}

		}, new Model3DGlasses());
		
		aureole.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(18f, -12, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(120, 0, 1, 0);

				GL11.glScalef(150,150,150);

			}

		}, new ModelAureole());
		
		marioHat.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(18f, 25, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(10, 0, 1, 0);

				GL11.glScalef(125,125,125);

			}

		}, new ModelMarioHat());
		
		jesterHat.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(18f, 25, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(120, 0, 1, 0);

				GL11.glScalef(120,120,120);
			}

		}, new ModelJesterHat());
		
		swordOnHead.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(22f, -5, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(120, 0, 1, 0);

				GL11.glScalef(100,100,100);

			}

		}, new ModelSwordOnHead());
		
		alienMask.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(19f, -2, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(120, 0, 1, 0);

				GL11.glScalef(100,100,100);
			}

		}, new ModelAlienMask());
		
		clownHead.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(19f, 21, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(0, 0, 1, 0);

				GL11.glScalef(100,100,100);
			}

		}, new ModelClownHead());
		
		witchHat2.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(19f, -8, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-90, 0, 1, 0);

				GL11.glScalef(100,100,100);
			}

		}, new ModelWitchHat2());
		
		devilHorn.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(19f, -8, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-0, 0, 1, 0);

				GL11.glScalef(110,110,110);
			}

		}, new ModelDevilHorn());
		
		demonWings.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(17, -2, 0);
				GL11.glScalef(12f, 12f, 12f);
				GL11.glRotatef(100, 1, 0, 0);
				GL11.glRotatef(150, 0, 0, 1);

				GL11.glRotatef(180, 0, 1, 0);
			}
			
		
		}, new ModelDemonWings());
		
		fairyWings.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(17, -2, 0);
				GL11.glScalef(12f, 12f, 12f);
				GL11.glRotatef(100, 1, 0, 0);
				GL11.glRotatef(150, 0, 0, 1);

				GL11.glRotatef(180, 0, 1, 0);
			}
			
			
			
		}, new ModelFairyWings());
		
		
		
		colorfulMaskBlue.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(20f, 0, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-0, 0, 1, 0);

				GL11.glScalef(150,150,150);

			}

		}, new ModelColorfulMask(new ResourceLocation("craftyourliferp","textures/cosmetics/colorful_mask_blue.png")));
		
		colorfulMaskOrange.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(20f, 0, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-0, 0, 1, 0);

				GL11.glScalef(150,150,150);
			}

		}, new ModelColorfulMask(new ResourceLocation("craftyourliferp","textures/cosmetics/colorful_mask_orange.png")));
		
		colorfulMaskBlack.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(20f, 0, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-0, 0, 1, 0);

				GL11.glScalef(150,150,150);
			}

		}, new ModelColorfulMask(new ResourceLocation("craftyourliferp","textures/cosmetics/colorful_mask_black.png")));
		
		colorfulMaskRed.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(20f, 0, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-0, 0, 1, 0);

				GL11.glScalef(150,150,150);
			}

		}, new ModelColorfulMask(new ResourceLocation("craftyourliferp","textures/cosmetics/colorful_mask_red.png")));
		
		colorfulMaskPink.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(20f, 0, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-0, 0, 1, 0);

				GL11.glScalef(150,150,150);
			}

		}, new ModelColorfulMask(new ResourceLocation("craftyourliferp","textures/cosmetics/colorful_mask_pink.png")));
		
		colorfulMaskYellow.setupRender(new ICosmeticSetup()
		{
			@Override
			public void setupCosmeticGuiDisplay() 
			{
				GL11.glTranslatef(20f, 0, 0);

				GL11.glRotatef(-180, 1, 0, 0);
				GL11.glRotatef(-0, 0, 1, 0);

				GL11.glScalef(150,150,150);
			}

		}, new ModelColorfulMask(new ResourceLocation("craftyourliferp","textures/cosmetics/colorful_mask_yellow.png")));
		
		unicornHat.setupRender(new ICosmeticSetup()
		{

			@Override
			public void setupCosmeticGuiDisplay() {
				GL11.glTranslated(17, 8, 0);
				GL11.glScalef(20F, 20F, 20F);
				GL11.glRotatef(180, 0, 1, 0);
			}
			
		}, new ModelUnicornHat());
		
		kingsCrown.setupRender(new ICosmeticSetup()
		{

			@Override
			public void setupCosmeticGuiDisplay() {
				GL11.glTranslated(18, 10, 0);
				GL11.glScalef(20F, 20F, 20F);
				GL11.glRotatef(180, 0, 1, 0);				
			}
			
		}, new ModelKingsCrown());
		
		
		cowboyHat.setupRender(new ICosmeticSetup()
		{

			@Override
			public void setupCosmeticGuiDisplay() {
				GL11.glTranslated(18, 10, 0);
				GL11.glScalef(15F, 15F, 15F);
				GL11.glRotatef(180, 0, 1, 0);	
			}
			
		}, new ModelCowboyHat());
		
		hardHat.setupRender(new ICosmeticSetup()
		{

			@Override
			public void setupCosmeticGuiDisplay() {
				GL11.glTranslated(18, 5, 0);
				GL11.glScalef(15F, 15F, 15F);
				GL11.glRotatef(180, 0, 1, 0);	
			}
			
		}, new ModelHardHat());
		
		antenna.setupRender(new ICosmeticSetup()
		{

			@Override
			public void setupCosmeticGuiDisplay() {
				GL11.glTranslated(18, 0, 0);
				GL11.glScalef(18F, 18F, 18F);
				GL11.glRotatef(180, 0, 1, 0);	
			}
			
		}, new ModelAntenna());

   }
	
   public static void exit()
   {
	   Runtime.getRuntime().exit(0);
   }
	
}
