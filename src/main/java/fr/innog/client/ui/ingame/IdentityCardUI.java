package fr.innog.client.ui.ingame;

import java.util.concurrent.ThreadLocalRandom;

import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.data.IdentityData;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class IdentityCardUI extends GuiScreen {

	private String id;
	
	private IPlayer ep;
	
	private IdentityData identityData;
	
	public IdentityCardUI(EntityPlayer p) {
		id = "IDCRAFTYOURL<<<<<<<<<<<<<<<<<<<<";
		for(int i = 0; i < 18; i++)
		{
			id += ThreadLocalRandom.current().nextInt(1, 11);
		}
		id += "CYL<<<<<<<<<<<<";
		for(int i = 0; i < 11; i++)
		{
			id += ThreadLocalRandom.current().nextInt(0, 11);
		}
		this.ep = MinecraftUtils.getPlayerCapability(p);
	}
	
	public IdentityCardUI(IdentityData identity) {
		id = "IDCRAFTYOURL<<<<<<<<<<<<<<<<<<<<";
		for(int i = 0; i < 18; i++)
		{
			id += ThreadLocalRandom.current().nextInt(1, 11);
		}
		id += "CYL<<<<<<<<<<<<";
		for(int i = 0; i < 11; i++)
		{
			id += ThreadLocalRandom.current().nextInt(0, 11);
		}
		this.identityData = identity;
	}
	
	public void initGui() {
		
	}
	
    public void drawScreen(int x, int y, float fl)
    {
    	GuiUtils.drawImage(this.width/4, this.height/4, new ResourceLocation("craftyourliferp", "ui/cardidentity/background.png"), (this.width - this.width/4) - (this.width / 4), (this.height - this.height/4) - (this.height / 4),0);
    	GuiUtils.drawImage(this.width/4, this.height/6, new ResourceLocation("craftyourliferp", "ui/cardidentity/logo.png"), (this.width - this.width/4) - (this.width / 4), ((this.height / 4) - this.height / 6) + 10,0);
    	
    	GuiUtils.drawImage((this.width/4) + 5, (this.height/4) + 12, new ResourceLocation("craftyourliferp", "ui/cardidentity/container.png"), 170, (this.height - this.height/3) - (this.height / 3),0);
    	
    	if(this.identityData != null)
    	{
    		GuiUtils.renderTextWithShadow("Prenom : " + identityData.name, (this.width/4) + 8, (this.height/4) + 30);
    		GuiUtils.renderTextWithShadow("Nom : " + identityData.lastname , (this.width/4) + 8, (this.height/4) + 40);
    		GuiUtils.renderTextWithShadow("Genre : " + (identityData.gender.equalsIgnoreCase("Masculin") ? "M" : "F"), (this.width/4) + 8, (this.height/4) + 50);
    		GuiUtils.renderTextWithShadow("Date de naissance : " + identityData.birthday , (this.width/4) + 8, (this.height/4) + 60);
    	}
    	
    	mc.fontRenderer.drawSplitString(this.id, this.width/4 + 4, this.height/4 + (this.height - this.height/4) - (this.height / 4) - 27, ((this.width - this.width/4) - (this.width / 4)) - 5, 0);
    }
    
    protected void mouseClicked(int x, int y, int btn) 
    {
    	
    }
    
    public void updateScreen()
    {

    }
    
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
	
}
