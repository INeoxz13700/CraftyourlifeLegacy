package fr.innog.client.ui.ingame;

import java.io.IOException;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.network.PacketCollection;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class SleepingUI extends GuiScreen {
	 
	 /**
    * Adds the buttons (and other controls) to the screen in question.
    */
   public void initGui()
   {
       super.initGui();
       
       IPlayer playerData = MinecraftUtils.getPlayerCapability(mc.player);
       
       if(playerData.getHealthData().getShouldBeReanimate())
       {
           this.buttonList.add(new GuiButton(1, (this.width / 2) - 200, this.height - 40, I18n.format("multiplayer.stopSleeping")));
           this.buttonList.add(new GuiButton(2, this.width / 2, this.height - 40, "§cMort subite"));
       }
       else
       {
           this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height - 40, I18n.format("multiplayer.stopSleeping")));
       }
       
   }

   @Override
   protected void keyTyped(char p_73869_1_, int p_73869_2_)
   {
       if (p_73869_2_ == 1)
       {
       	unSleep();
       }
       else if (p_73869_2_ != 28 && p_73869_2_ != 156)
       {
           try {
        	   super.keyTyped(p_73869_1_, p_73869_2_);
           } catch (IOException e) {
        	   e.printStackTrace();
           }
       }
   }

   protected void actionPerformed(GuiButton p_146284_1_)
   {
       if (p_146284_1_.id == 1)
       {
    	    unSleep();
       }
       else if(p_146284_1_.id == 2)
       {
       		subitDeath();
       }
       else
       {
           try {
        	   super.actionPerformed(p_146284_1_);
           } catch (IOException e) {
        	   e.printStackTrace();
           }
       }
   }
   
   private void unSleep()
   {
	   PacketCollection.setUnSleep();
   }
   
   private void subitDeath()
   {
	   PacketCollection.subitDeath();
   }
   
   public boolean doesGuiPauseGame()
   {
       return false;
   }
   
   public void updateGui()
   {
   	this.buttonList.clear();
   	this.initGui();
   }

	
}
