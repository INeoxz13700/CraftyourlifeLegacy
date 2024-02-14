package fr.innog.client.ui.ingame;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class DeathUI extends GuiScreen  {

	 private int tick = 0; 
	 
	 @Override
	 public void initGui()
	 {
		 this.mc = Minecraft.getMinecraft();
		 super.initGui();
	 }
	 
	@Override
	 public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_)
	 {
	        this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
	        GL11.glPushMatrix();
	        GL11.glScalef(2.0F, 2.0F, 2.0F);
	        String s = "§cVous êtes à terre!";
	        this.drawCenteredString(mc.fontRenderer, s, this.width / 2 / 2, 30, 16777215);
	        GL11.glPopMatrix();
	        
	        GL11.glPushMatrix();
	        GL11.glScalef(1.5F, 1.5F, 1.5F);
	        
	        int leftTime = (20*5) - tick;
	        s = "§6Respawn dans §e" + (int)(leftTime / 20f) + "s";
	        
	        this.drawCenteredString(mc.fontRenderer, s, this.width / 3, 60, 16777215);
	        GL11.glPopMatrix();
	}
	 
	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}
	
	@Override
	public void updateScreen()
   {
       super.updateScreen();
       ++this.tick;

       if (this.tick == 20*5)
       {
           mc.player.respawnPlayer();
       }
       
       if(mc.player.isEntityAlive())
       {
           mc.displayGuiScreen(null);
       }
   }
	
   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException { }
	
}