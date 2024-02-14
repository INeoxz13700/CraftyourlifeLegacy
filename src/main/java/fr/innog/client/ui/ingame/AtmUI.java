package fr.innog.client.ui.ingame;

import java.io.IOException;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.ui.remote.IRemoteUI;
import fr.innog.ui.remote.RemoteAtmUI;
import fr.innog.ui.remote.RemoteUIProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class AtmUI extends GuiScreen implements IRemoteUI {
	
	ResourceLocation background = new ResourceLocation("craftyourliferp","ui/atm/Background.png");
	ResourceLocation logo = new ResourceLocation("craftyourliferp","ui/atm/ATM.png");
	ResourceLocation card_insertion = new ResourceLocation("craftyourliferp","ui/atm/Carte.png");
	ResourceLocation container = new ResourceLocation("craftyourliferp","ui/atm/ChiffreBox.png");
	ResourceLocation dispose = new ResourceLocation("craftyourliferp","ui/atm/Deposer.png");
	ResourceLocation take = new ResourceLocation("craftyourliferp","ui/atm/Retirer.png");
	ResourceLocation display = new ResourceLocation("craftyourliferp","ui/atm/EcranBox.png");
	ResourceLocation screen = new ResourceLocation("craftyourliferp","ui/atm/Montant.png");
	
	ResourceLocation atm_sound = new ResourceLocation("craftyourliferp:atm_press");

	private UIButton[] keysBtn = new UIButton[11];
	
	private UIButton takeBtn;
	private UIButton disposeBtn;
	
	private RemoteAtmUI remoteUI;

	int guiPosX1;
	int guiPosX2;
	int guiPosY1;
	int guiPosY2;
	
	private String value = "";
	
	public AtmUI() {


	}
	
	public void initGui() {
		guiPosX1 = this.width/4;
		guiPosX2 = this.width - (this.width/4);
		guiPosY1 = 0;
		guiPosY2 = this.height;
		
		for(int i = 0; i < keysBtn.length; i++)
		{
			
			if(i != 10)
			{
				final int li = i;
				keysBtn[i] = new UIButton(UIButton.Type.SQUARE, i + "", new ResourceLocation("craftyourliferp","ui/atm/" + i + ".png"), null,false, new UIButton.CallBackObject()
				{
					@Override
					public void call()
					{
			    		value += li + "";

			    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(atm_sound), SoundCategory.PLAYERS, 0.5F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
					}
				});
			}
			else
			{
				keysBtn[i] = new UIButton(UIButton.Type.SQUARE, i + "", new ResourceLocation("craftyourliferp","ui/atm/Effacer.png"), null,false, new UIButton.CallBackObject()
				{
					@Override
					public void call()
					{
			    		value = value.subSequence(0, Math.max(0,value.length() - 1)).toString();
			    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(atm_sound), SoundCategory.PLAYERS, 0.5F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
					}
				});
			}
		}
		
		this.buttonList.clear();
		
		int i = 0;
		int keystartx = guiPosX1 + 15;
		int keystarty = ((this.height/2) - (guiPosY1 + 5)) + 10;
		for(i = 0; i < keysBtn.length; i++)
		{
			if(i > 0 && i <= 3)
				keysBtn[i].setPosition(keystartx,keystarty + (24*(i)),25,20);
			else if(i > 3 && i <= 6)
				keysBtn[i].setPosition(keystartx + 30,keystarty + (24*(i-3)),25,20);
			else if(i > 6 && i <= 9)
				keysBtn[i].setPosition(keystartx + 60,keystarty + (24*(i-6)),25,20);
			else if(i == 0)
				keysBtn[i].setPosition(keystartx + 90,keystarty + (24*(i+3)),25,20);
			else if(i == 10)
				keysBtn[i].setPosition(keystartx + 90,keystarty + (24 * (i-8)),25,20);
			
		}
		
		//button take
		takeBtn = new UIButton(UIButton.Type.SQUARE,"take",take,null,false, new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				if(value.isEmpty())
				{
					return;
				}
				
				Long valueParsed = Long.parseLong(value);
				
				remoteUI.executeMethod("withdraw", new Object[] { valueParsed });
			}
		});
		takeBtn.setPosition(guiPosX2-55,guiPosY1 + 35,40,15);
		//button dispose
		disposeBtn = new UIButton(UIButton.Type.SQUARE, "dispose", dispose, null, false, new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				if(value.isEmpty())
				{
					return;
				}
				
				Long valueParsed = Long.parseLong(value);
				
				remoteUI.executeMethod("deposit", new Object[] { valueParsed });
			}		
		});
		disposeBtn.setPosition(guiPosX2-55,guiPosY1 + 55,40,15);
	}
	
   
	
    public void drawScreen(int x, int y, float fl)
    {  	
	   //background
       GuiUtils.drawImage(guiPosX1, guiPosY1, background, guiPosX2 - guiPosX1, this.height, 0);
   
       //logo
       GuiUtils.drawImage(guiPosX2 - 60, guiPosY1 + 10, logo, 50, 20, 0);
   	   
   	   //card_insertion
   	   GuiUtils.drawImage(guiPosX2-62, guiPosY2 - 60, card_insertion, 55,20, 0);
   	   
   	   //display
   	   
   	   int displayX1 = guiPosX1 + 5;
   	   int displayX2 = (guiPosX1 + 5) + (guiPosX2 - 75) - (guiPosX1 + 5);
   	   int displayY1 = guiPosY1 + 5;
   	   int displayY2 = (guiPosY1 + 5) + (this.height/2) - (guiPosY1 + 5); 
   	   
   	   GuiUtils.drawImage(displayX1, displayY1, display, (guiPosX2 - 75) - (guiPosX1 + 5), (this.height/2) - (guiPosY1 + 5), 0);
   	   
   	   GuiUtils.drawImage(((displayX1 + displayX2) / 2)-50, ((displayY1 + displayY2) / 2)-12, screen, 100, 25, 0);
   	   
   	   //keys
   	   GuiUtils.drawImage(guiPosX1 + 5, ((this.height/2) - (guiPosY1 + 5)) + 15, container, (guiPosX2 - 75) - (guiPosX1 + 5), (guiPosY2-10) - (((this.height/2) - (guiPosY1 + 5)) + 15), 0);
   	   for(UIButton obj : keysBtn)
   	   {
   		   obj.draw(x, y);
   	   }
   	   
   	   takeBtn.draw(x, y);
   	   disposeBtn.draw(x, y);
   	   
   	   GuiUtils.setClippingRegion(((displayX1 + displayX2) / 2)-50, ((displayY1 + displayY2) / 2)-12, 100, 25);
   	   GuiUtils.renderText(value, ((displayX1 + displayX2) / 2)-45, ((displayY1 + displayY2) / 2)-4,0, 1);
   	   GuiUtils.disableScissorBox();

    }
    
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	if(mouseButton == 0)
    	{
    		for(UIButton button : keysBtn)
    		{
    			if(button.onLeftClick(mouseX, mouseY)) return;
    		}
    	
    		if(takeBtn.onLeftClick(mouseX, mouseY)) return;
    		
    		if(disposeBtn.onLeftClick(mouseX, mouseY)) return;
    	}
    	
    	
    }
    

    public boolean doesGuiPauseGame()
    {
        return false;
    }

	@Override
	public void setRemoteUI(RemoteUIProcessor remoteUI) {
		this.remoteUI = (RemoteAtmUI) remoteUI;
	}
	
}