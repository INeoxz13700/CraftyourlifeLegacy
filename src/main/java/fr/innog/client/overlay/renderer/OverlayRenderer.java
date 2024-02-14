package fr.innog.client.overlay.renderer;

import org.lwjgl.opengl.GL11;

import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIProgressBar;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.advancedui.utils.MathsUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.inventory.CustomInventoryPlayer;
import fr.innog.common.items.Items;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.common.shield.ShieldStats;
import fr.innog.common.tiles.IStealingTileEntity;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = ModCore.MODID, value = Side.CLIENT)
public class OverlayRenderer {
	
	private static UIProgressBar stealingProgress = new UIProgressBar(new UIRect(new UIColor(0,0,0,150)), new UIRect(new UIColor(39, 186, 83)));

    private static Minecraft mc = Minecraft.getMinecraft();
    
	private static int elapsedTimeInSeconds = 0;
	
    private static float previousValue = 0, currentValue = 0;

  
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderPre(RenderGameOverlayEvent.Pre event)
    {
    	Minecraft mc = Minecraft.getMinecraft();
		
    	int width = event.getResolution().getScaledWidth() , height = event.getResolution().getScaledHeight();
    	IPlayer ep = MinecraftUtils.getPlayerCapability(mc.player);
	
        GlStateManager.enableAlpha();

    	if(event.getType() == RenderGameOverlayEvent.ElementType.DEBUG)
    	{
    		
    		event.setCanceled(true);  
    		
    		
    	    UIRect serverNameRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect fpsChunkRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect coordinatesRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect directionRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect biomeRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect javaVersionRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect memoryRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect allocatedRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect componentsRect = new UIRect(new UIColor(0,0,0,0));
    	    UIRect blockRect = new UIRect(new UIColor(0,0,0,0));
    	    
    	    serverNameRect.setPosition(2, 5, GuiUtils.getStringWidth(mc, "§lCraftyourliferp - Legacy " + ModCore.VERSION, 0.8f), 10);
    	    GuiUtils.renderText("§lCraftyourliferp - Legacy " + ModCore.VERSION, serverNameRect.getX() + 2, (serverNameRect.getY() + serverNameRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,7410316,0.8f);
    	    
    	    String[] debug = mc.debug.split(", ")[0].split(" ");
    	    fpsChunkRect.setPosition(2, 15, GuiUtils.getStringWidth(mc, debug[0] + " " + debug[1], 0.8f), 10);
    	    GuiUtils.renderText(debug[0] + " " + debug[1], fpsChunkRect.getX() + 2, (fpsChunkRect.getY() + fpsChunkRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,7211397,0.8f);
    	
    	    String coordinates = "XYZ : " + (int)mc.player.posX + " / " + (int)mc.player.posY + " / " + (int)mc.player.posZ;
    	    coordinatesRect.setPosition(2, 30, GuiUtils.getStringWidth(mc, coordinates, 0.8f), 10);
    	    GuiUtils.renderText(coordinates, coordinatesRect.getX() + 2, (coordinatesRect.getY() + coordinatesRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,7211397,0.8f);
    	    
    	    String direction = "Direction : " + mc.player.getHorizontalFacing().toString();
    	    directionRect.setPosition(2, 40, GuiUtils.getStringWidth(mc, direction, 0.8f), 10);
    	    GuiUtils.renderText(direction, directionRect.getX() + 2, (directionRect.getY() + directionRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,7211397,0.8f);
    	
    	    String biome = "Biome : " + mc.world.getBiome(new BlockPos((int)mc.player.posX ,mc.player.posY, (int)mc.player.posZ)).getBiomeName();
    	    biomeRect.setPosition(2, 55, GuiUtils.getStringWidth(mc, biome, 0.8f), 10);
    	    GuiUtils.renderText(biome, biomeRect.getX() + 2, (biomeRect.getY() + biomeRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,7211397,0.8f);
    	
    	    String java = "Java: " + System.getProperty("java.version");
    	    javaVersionRect.setPosition(event.getResolution().getScaledWidth() - GuiUtils.getStringWidth(mc, java, 0.8f)-2, 5, GuiUtils.getStringWidth(mc, java, 0.8f), 10);
    	    GuiUtils.renderText(java, javaVersionRect.getX() + 2, (javaVersionRect.getY() + javaVersionRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,9381549,0.8f);
    	
            long i5 = Runtime.getRuntime().maxMemory();
            long j5 = Runtime.getRuntime().totalMemory();
            long k5 = Runtime.getRuntime().freeMemory();
            long l5 = j5 - k5;
            String mem = "Mem: " + l5 * 100L / i5 + "% (" + l5 / 1024L / 1024L + "/" + i5 / 1024L / 1024L + ") MB";
            
            memoryRect.setPosition(event.getResolution().getScaledWidth() - GuiUtils.getStringWidth(mc, mem, 0.8f)-2, 15, GuiUtils.getStringWidth(mc, mem, 0.8f), 10);
            GuiUtils.renderText(mem, memoryRect.getX() + 2, (memoryRect.getY() + memoryRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,9381549,0.8f);
            
            String allocated = "Allocated : " + j5 * 100L / i5 + "% (" + j5 / 1024L / 1024L + "MB)";
            allocatedRect.setPosition(event.getResolution().getScaledWidth() - GuiUtils.getStringWidth(mc, allocated, 0.8f)-2, 25, GuiUtils.getStringWidth(mc, allocated, 0.8f), 10);
            GuiUtils.renderText(allocated, allocatedRect.getX() + 2, (allocatedRect.getY() + allocatedRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,9381549,0.8f);
            
    	    
            String components = GL11.glGetString(GL11.GL_RENDERER);
            componentsRect.setPosition(event.getResolution().getScaledWidth() - GuiUtils.getStringWidth(mc, components, 0.8f)-2, 35, GuiUtils.getStringWidth(mc, components, 0.8f), 10);
            GuiUtils.renderText(components, componentsRect.getX() + 2, (componentsRect.getY() + componentsRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,9381549,0.8f);
            
    	    RayTraceResult mop = mc.player.rayTrace(5, event.getPartialTicks());
    	    if(mop != null)
    	    {
    	    	IBlockState blockState = mc.world.getBlockState(mop.getBlockPos());
    	    	if(blockState != null)
    	    	{
	    	    	String block = "Block : " + blockState.getBlock().getRegistryName().toString() + " [" + mop.getBlockPos().getX() + "/" + mop.getBlockPos().getY() + "/" + mop.getBlockPos().getZ() + "]";
	                blockRect.setPosition(event.getResolution().getScaledWidth() - GuiUtils.getStringWidth(mc, block, 0.8f)-2, 60, GuiUtils.getStringWidth(mc, block, 0.8f), 10);
	                GuiUtils.renderText(block, blockRect.getX() + 2, (blockRect.getY() + blockRect.getY2() - mc.fontRenderer.FONT_HEIGHT/2) / 2,9381549,0.8f);
    	    	}
    	    }

    	}
    	else if(event.getType() == RenderGameOverlayEvent.ElementType.HEALTH)
    	{
    		event.setCanceled(true);
    	   	float thirst = ep.getThirstStats().getThirstNormalized();
	        
    	   	if (thirst <= 0.1F || ep.getHealthData().getAlcolInBlood() >= 4)
    	   	{
    	         GlStateManager.disableAlpha();

    	   	     float alphaLevel = (MathHelper.sin(Minecraft.getMinecraft().player.ticksExisted * 0.05F));

    	   	     GuiUtils.drawRect(0, 0, width, height, 0, 0, alphaLevel);
    	         GlStateManager.enableAlpha();

    	   	} 
    		OverlayRenderer.renderHealth(width, height);
    		OverlayRenderer.renderShield(width, height);
    		OverlayRenderer.renderThirst(width, height);

    		
    	}
    	else if(event.getType() == RenderGameOverlayEvent.ElementType.FOOD)
    	{
    		event.setCanceled(true);
    		OverlayRenderer.renderFood(width, height);
    		
    		
    	}
    	else if(event.getType() == RenderGameOverlayEvent.ElementType.ARMOR)
    	{
    		event.setCanceled(true);
    	}
    	else if(event.getType() == RenderGameOverlayEvent.ElementType.AIR)
    	{
    		event.setCanceled(true);
    		OverlayRenderer.renderAir(width, height);
    	}
	   	         

	   	
    }
    
	@SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderPreChat(RenderGameOverlayEvent.Text event)
    {

	     ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
	     
	     if(mc.player.isRiding()) return;
	     
	     IPlayer playerData = MinecraftUtils.getPlayerCapability(mc.player);

	     
	     if(ModControllers.thirstController.playerLookWater(mc.player, mc.world))
	     {
	    	 renderAction("Pour boire");
	     }
	     else
	     {
	    	 if(mc.player.getHeldItemMainhand().getItem() == Items.identityCard)
	    	 {
	    	    	renderAction("Utiliser la carte d'identité"); 
	    	 } 
	    	 else if(mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != null)
	 	     {
		    	    	 
		    	    if(mc.objectMouseOver.typeOfHit == Type.BLOCK)
		    	    {
				    	  if(mc.player.ticksExisted % 7 == 0)
				    	  {

						       mc.world.getBlockState(mc.objectMouseOver.getBlockPos()).getBlock();
				    	  }
				    	    	
										 
						 if(!playerData.isStealing())
						 {
							 TileEntity tile = mc.player.world.getTileEntity(mc.objectMouseOver.getBlockPos());
				
							        	    
						      if(tile != null && tile instanceof IStealingTileEntity) 
						      {
						    	  renderAction(((IStealingTileEntity)tile).getDisplayMessageInLook());
						      }
						  }
						  else
						  {
								  elapsedTimeInSeconds = (int) (System.currentTimeMillis() - playerData.getStealingTile().getStealingStartedTime()) / 1000;
							      previousValue = stealingProgress.getValue();
							      currentValue = (float)elapsedTimeInSeconds / playerData.getStealingTile().getStealingTime();
						
								          
						
								  stealingProgress.setValue(MathsUtils.Lerp(previousValue,currentValue , event.getPartialTicks() * 0.025f));
						
								            		
								  int x = (res.getScaledWidth() - 80) / 2;
								  int y = (res.getScaledHeight() / 2) +43;
								            	    
								            	    
								  stealingProgress.setPosition(x, y, 80, 7);
								  stealingProgress.draw(0, 0);
								            		
								  GuiUtils.renderCenteredText("Vol en cours...", event.getResolution().getScaledWidth() / 2, y-15);
							 }
						}
		    	   		else if(mc.objectMouseOver.typeOfHit == Type.ENTITY)
		    	   		{
		    	   			if(Minecraft.getMinecraft().objectMouseOver.entityHit instanceof EntityPlayer)
		    	   			{
		    	   				EntityPlayer target = (EntityPlayer) Minecraft.getMinecraft().objectMouseOver.entityHit;
		    	   				if(target.getHeldItemMainhand().getItem() == Items.identityCard) renderAction("Voir la carte d'identité de " + target.getName());
		    	   			}
		    	   		}
	 	     }
	     }
	     
	    
	
    }
	
	
    public static void renderAction(String message) 
    {

      ScaledResolution res = new ScaledResolution(mc);
      GL11.glPushMatrix();
      String text = "[Appuyez " + GameSettings.getKeyDisplayString(ClientProxy.keyBindings[1].getKeyCode()) + "] " + message;
      int x = ((res.getScaledWidth() - mc.fontRenderer.getStringWidth(text)) / 2)+2;
      int y = (res.getScaledHeight() / 2)+43;
      int color = 16777215;
      
      mc.fontRenderer.drawStringWithShadow(text, x, y, color);
      GL11.glPopMatrix();
    }
    
    private static void renderShield(int width, int height)
    {    	
    	GlStateManager.color(1f, 1f, 1f, 1f);
    	GuiUtils.drawImageWithTransparency(width-80, height-25, new ResourceLocation("craftyourliferp","ui/overlay/bar.png"), 82, 18,0);
    	GuiUtils.drawImage(width-15, height-23, new ResourceLocation("craftyourliferp","ui/overlay/shield.png"), 14, 14,0);
    		
    	IPlayer playerData = MinecraftUtils.getPlayerCapability(mc.player);
    	int value = (int)playerData.getShieldStats().getShield();
    	
    	GuiUtils.renderTextWithShadow(value + " %", width-50, height-19,getColorForPercentage(value),1f);
    }
    
    private static void renderFood(int width, int height)
    {
    	GlStateManager.color(1f, 1f, 1f, 1f);

    	GuiUtils.drawImageWithTransparency(width-80, height-65, new ResourceLocation("craftyourliferp","ui/overlay/bar.png"), 82, 18, 0);
    	GuiUtils.drawImage(width-15, height-63, new ResourceLocation("craftyourliferp","ui/overlay/hungry.png"), 14, 14, 0);
    		
    	int value = (int) ((mc.player.getFoodStats().getFoodLevel() / 20F) * 100);
    		
    	GuiUtils.renderTextWithShadow(value + " %", width-50, height-59,getColorForPercentage(value),1f);
    }
    
    private static void renderThirst(int width, int height)
    {
    	GlStateManager.color(1f, 1f, 1f, 1f);

    	GuiUtils.drawImageWithTransparency(width-80, height-45, new ResourceLocation("craftyourliferp","ui/overlay/bar.png"), 82, 18,0);
    	GuiUtils.drawImage(width-15, height-43, new ResourceLocation("craftyourliferp","ui/overlay/water.png"), 14, 14,0);
    	
    	IPlayer playerData = MinecraftUtils.getPlayerCapability(mc.player);
    	
    	int value = (int) (playerData.getThirstStats().getThirstNormalized() * 100);
    	
    	GuiUtils.renderTextWithShadow(value + " %", width-50, height-39,getColorForPercentage(value),1f);
    }
    
    private static void renderAir(int width, int height)
    {
    	GlStateManager.color(1f, 1f, 1f, 1f);
    	
    	if(mc.player.getAir() < 300)
    	{
    		float value = mc.player.getAir() / 300F;
        	
        	new UIRect(new UIColor(4, 212, 201)).setPosition((width-180)/2, height-32, (int)(180*value), 2).draw(0, 0);
    	}
    }

    
    
    private static void renderHealth(int width, int height)
    {
    	GlStateManager.color(1f, 1f, 1f, 1f);


    	GuiUtils.drawImageWithTransparency(width-80, height-85, new ResourceLocation("craftyourliferp","ui/overlay/bar.png"), 82, 18,0);
    	GuiUtils.drawImage(width-15, height-83, new ResourceLocation("craftyourliferp","ui/overlay/heart.png"), 14, 14,0);
    		
    	int value = (int) ((mc.player.getHealth() / mc.player.getMaxHealth()) * 100);
    		
    	GuiUtils.renderTextWithShadow(value + " %", width-50, height-79,getColorForPercentage(value),1f);
    }
    
    private static int getColorForPercentage(int percentage)
    {
    	if(percentage > 75 && percentage <= 100)
    	{
    		return 3319890;
    	}
    	else if(percentage > 45 && percentage <= 75)
    	{
    		return 13598988;
    	}
    	else
    	{
    		return 15073280;
    	}
    }

    

}
