package fr.innog.phone;

import java.io.IOException;

import fr.innog.advancedui.gui.GuiBase;
import fr.innog.client.ui.ingame.PhoneUI;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class Apps extends GuiBase  {
			
	public PhoneUI phone;
	
	public ResourceLocation ico;
	
	public String name;


	public Apps(String name, ResourceLocation ico, PhoneUI phone) {
		this.phone = phone;
		this.ico = ico;
		this.name = name;
		this.mc = Minecraft.getMinecraft();
        this.itemRender = mc.getRenderItem();
	}

	
	public Apps getApp() {
		return this;
	}
	
	public ResourceLocation getIcoTexture()
	{
		return ico;
	}
	
	public abstract void back();	
	
	public abstract void openApps();
	
	public abstract void updateGuiState();

	
	@Override
	public void mouseClickMove(int x, int y, int state, long timeSinceLastClick)
	{
		 super.mouseClickMove(x, y, state, timeSinceLastClick);
	}
	
	 @Override
	 public void keyTyped(char character, int keycode)
	 {
		 try {
			super.keyTyped(character, keycode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
	 @Override
	 public void mouseClicked(int x, int y, int mouseBtn)
	 {
		 try {
			super.mouseClicked(x, y, mouseBtn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 }
	 
	 @Override
	 public void mouseReleased(int mouseX, int mouseY, int state)
	 {
		 super.mouseReleased(mouseX, mouseY, state);
	 }

	

}
