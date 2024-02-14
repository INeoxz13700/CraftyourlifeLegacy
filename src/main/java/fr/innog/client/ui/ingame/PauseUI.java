package fr.innog.client.ui.ingame;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.guicomponents.UIProgressBar;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.client.ui.MainMenuUI;
import fr.innog.common.ModCore;
import fr.innog.network.PacketCollection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;

public class PauseUI extends GuiScreen {

	
	private UIImage logo = new UIImage(new ResourceLocation(ModCore.MODID,"ui/logo.png"));
	
	private UIProgressBar bar;
	
	private UIButton[] buttons = new UIButton[7];
	
	private UIRect rect = new UIRect(new UIColor(0,0,0,125));
	private UIRect containerRect = new UIRect(new UIColor(255,255,255));
	
	private boolean isDisconnecting = false;
	
	private long disconnectPressedTime;
	
	private final int timeBeforeDisconnection = 5;
	
	private int ticks;
	private int state;
	
	protected UIColor buttonColor = new UIColor(0,135,212,255);
	protected UIColor buttonColor_hover = new UIColor(26, 146, 214,255);
	
	
	@Override
	public void initGui()
	{	
		
		if(mc.gameSettings.guiScale == 1)
		{
			containerRect.setPosition((width - 300*2) / 2, (height - 200*2) / 2, 300*2, 200*2);
			
			buttons[0] = new UIButton(Type.SQUARE, "Reprendre la partie", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
				}
				
			
			});
			
			buttons[1] = new UIButton(Type.SQUARE, "Cosmétiques", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{

				@Override
				public void call()
				{
					PacketCollection.askToServerOpenUI(3);
				}
				
			});
			
			buttons[2] =  new UIButton(Type.SQUARE,"Discord", new UIRect(buttonColor),new UIRect(buttonColor_hover), new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
		    		try 
		    		{
						java.awt.Desktop.getDesktop().browse(new URI("https://discord.com/invite/bDBhTEApJh"));
					} 
		    		catch (IOException | URISyntaxException e) 
		    		{
						e.printStackTrace();
					}
				}
			});
			
			buttons[3] = new UIButton(Type.SQUARE,"Paramètres", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					Minecraft mc = Minecraft.getMinecraft();
		    		mc.displayGuiScreen(new GuiOptions(PauseUI.this, mc.gameSettings));
				}
				
			});
			
			buttons[4] = new UIButton(Type.SQUARE,"Site web", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
		    		try 
		    		{
						java.awt.Desktop.getDesktop().browse(new URI("http://www.craftyourliferp.fr"));
					} 
		    		catch (IOException | URISyntaxException e) 
		    		{
						e.printStackTrace();
					}
				}
			});
			
			buttons[5] = new UIButton(Type.SQUARE,"Déconnexion", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					isDisconnecting = !isDisconnecting;
		            buttons[5].setDisplayText("Déconnexion");
					if(mc.isSingleplayer())
					{
		                mc.world.sendQuittingDisconnectingPacket();
		                mc.loadWorld((WorldClient)null);
		                mc.displayGuiScreen(new MainMenuUI());
					}
					else
					{
				    	disconnectPressedTime = System.currentTimeMillis() + (timeBeforeDisconnection * 1000);	
					}
				}
			});
			
			buttons[6] = new UIButton(Type.SQUARE,"Gestion Avatar", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					mc.displayGuiScreen(new SkinUI());
				}
			});
			
			bar = new UIProgressBar(new UIRect(new UIColor(0,0,0)), new UIRect(new UIColor(0,255,0)));

			int logoPosX = containerRect.getX() + (containerRect.getWidth() - 60*2) / 2;
			int logoPosY = containerRect.getY();
			
			logo.setPosition(logoPosX, logoPosY, 60*2, 60*2);
			
			buttons[0].setPosition((width-145*2) / 2, logo.getY2() + 10*2, 145*2, 18*2);
			buttons[0].setTextScale(1f);
			buttons[1].setPosition((width-145*2) / 2, logo.getY2() + 32*2, 70*2, 18*2);
			buttons[1].setTextScale(1f);
			buttons[2].setPosition(buttons[0].getX2() - 70*2, logo.getY2() + 32*2, 70*2, 18*2);
			buttons[2].setTextScale(1f);
			buttons[3].setPosition((width-145*2) / 2, logo.getY2() + 54*2, 145*2, 18*2);
			buttons[3].setTextScale(1f);
			buttons[4].setPosition((width-145*2) / 2, logo.getY2() + 76*2, 70*2, 18*2);
			buttons[4].setTextScale(1f);
			buttons[5].setPosition(buttons[0].getX2() - 70*2, logo.getY2() + 76*2, 70*2, 18*2);
			buttons[5].setTextScale(1f);
			buttons[6].setPosition((width-145*2) / 2, logo.getY2() + 98*2, 145*2, 18*2);
			buttons[6].setTextScale(1f);
			
			
			
			rect.setPosition(10*2, height - 25*2, 140*2, 15*2);
		}
		else
		{
			containerRect.setPosition((width - 300) / 2, (height - 200) / 2, 300, 200);
			
			buttons[0] = new UIButton(Type.SQUARE, "Reprendre la partie", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
				}
				
			});
			buttons[1] = new UIButton(Type.SQUARE, "Cosmétiques", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{

				@Override
				public void call()
				{
					mc.displayGuiScreen(null);
					mc.setIngameFocus();
					PacketCollection.askToServerOpenUI(3);
				}
				
			});
			
			buttons[2] =  new UIButton(Type.SQUARE,"Discord", new UIRect(buttonColor),new UIRect(buttonColor_hover), new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
		    		try 
		    		{
						java.awt.Desktop.getDesktop().browse(new URI("https://discord.com/invite/bDBhTEApJh"));
					} 
		    		catch (IOException | URISyntaxException e) 
		    		{
						e.printStackTrace();
					}
				}
			});
			
			buttons[3] = new UIButton(Type.SQUARE,"Paramètres", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
		    		mc.displayGuiScreen(new GuiOptions(PauseUI.this, mc.gameSettings));
				}
				
			});
			
			buttons[4] = new UIButton(Type.SQUARE,"Site web", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
		    		try 
		    		{
						java.awt.Desktop.getDesktop().browse(new URI("http://www.craftyourliferp.fr"));
					} 
		    		catch (IOException | URISyntaxException e) 
		    		{
						e.printStackTrace();
					}
				}
			});
			
			buttons[5] = new UIButton(Type.SQUARE,"Déconnexion", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					isDisconnecting = !isDisconnecting;
		            buttons[5].setDisplayText("Déconnexion");
					if(mc.isSingleplayer())
					{
		                mc.world.sendQuittingDisconnectingPacket();
		                mc.loadWorld((WorldClient)null);
		                mc.displayGuiScreen(new MainMenuUI());
					}
					else
					{
				    	disconnectPressedTime = System.currentTimeMillis() + (timeBeforeDisconnection * 1000);	
					}
				}
			});
			
			buttons[6] = new UIButton(Type.SQUARE,"Gestion Avatar", new UIRect(buttonColor),new UIRect(buttonColor_hover),new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					mc.displayGuiScreen(new SkinUI());
				}
			});
			
			bar = new UIProgressBar(new UIRect(new UIColor(0,0,0)), new UIRect(new UIColor(0,255,0)));

			int logoPosX = containerRect.getX() + (containerRect.getWidth() - 60) / 2;
			int logoPosY = containerRect.getY();
			
			logo.setPosition(logoPosX, logoPosY, 60, 60);
			
			buttons[0].setPosition((width-145) / 2, logo.getY2() + 10, 145, 18);
			buttons[1].setPosition((width-145) / 2, logo.getY2() + 32, 70, 18);
			buttons[2].setPosition(buttons[0].getX2() - 70, logo.getY2() + 32, 70, 18);
			buttons[3].setPosition((width-145) / 2, logo.getY2() + 54, 145, 18);
			buttons[4].setPosition((width-145) / 2, logo.getY2() + 76, 70, 18);
			buttons[5].setPosition(buttons[0].getX2() - 70, logo.getY2() + 76, 70, 18);
			buttons[6].setPosition((width-145) / 2, logo.getY2() + 98, 145, 18);

			rect.setPosition(10, height - 25, 140, 15);
		}
		
	}
	
    public void drawScreen(int x, int y, float partialTicks)
    {
    	new UIRect(new UIColor(0,0,0,50)).setPosition(0, 0, width, height).draw(x, y);
    	drawBackground(x,y, partialTicks);
    	
    
    	    	
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].draw(x, y);
		}
		
		rect.draw(x, y);
		
		if(mc.gameSettings.guiScale == 1)
			GuiUtils.renderCenteredText("§6CraftYourLifeRP Legacy §8- §cV" + ModCore.VERSION, rect.getX() + rect.getWidth() / 2,(rect.getY() + rect.getHeight() / 2) - 2 , 1.2f);
		else
			GuiUtils.renderCenteredText("§6CraftYourLifeRP Legacy §8- §cV" + ModCore.VERSION, rect.getX() + rect.getWidth() / 2,(rect.getY() + rect.getHeight() / 2) - 2 , 0.8f);
  
    	if(this.isDisconnecting)
    	{
	    	long now = System.currentTimeMillis();
	    	float timeLeft = (this.disconnectPressedTime - now) / 1000;
	    	bar.setValue((float)(timeLeft / this.timeBeforeDisconnection));
	    	if(timeLeft <= 0)
	    	{
	    		this.isDisconnecting = false;
                this.mc.world.sendQuittingDisconnectingPacket();
                this.mc.loadWorld((WorldClient)null);
                this.mc.displayGuiScreen(new MainMenuUI());
	    	}
    	}
    	
    	if(this.isDisconnecting)
    	{
    		bar.setPosition(buttons[5].getX(), buttons[5].getY2() - 2, buttons[5].getWidth(), 2);
    		bar.draw(x, y);
    	}
    	else
    	{
    		buttons[5].setDisplayText("Quitter");
    	}
    }
    
    @Override
    protected void mouseClicked(int x, int y, int mouseBtn)
    {
    	if(mouseBtn == 0)
    	{
    		
    		for(int i = 0; i < buttons.length; i++)
    		{
    			if(buttons[i].isClicked(x, y))
    			{
    				buttons[i].callback.call();
    			}
    		}
    	}
    }
    
    public void drawBackground(int x, int y, float partialTicks)
    {
    	logo.draw(x, y);
    }
    
    @Override
    public void updateScreen()
    {
    	ticks++;
    	if(this.isDisconnecting)
    	{
    		if(ticks % 10 == 0)
    		{
    			if(state == 0)
    			{
    				buttons[5].setDisplayText("Déconnexion");
    			}
    			else if(state == 1)
    			{
    				buttons[5].setDisplayText("Déconnexion.");
    			}
    			else if(state == 2)
    			{
    				buttons[5].setDisplayText("Déconnexion..");
    			}
    			else if(state == 3)
    			{
    				buttons[5].setDisplayText("Déconnexion...");
    			}
    			if(state++ > 3) state = 0;
    		}
    	}
    }
    

    
}