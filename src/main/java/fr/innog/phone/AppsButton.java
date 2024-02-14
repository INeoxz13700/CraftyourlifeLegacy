package fr.innog.phone;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.client.ui.ingame.PhoneUI;
import net.minecraft.util.ResourceLocation;

public class AppsButton extends UIButton 
{
		
	
	public Apps attribuatedApp;

	public AppsButton(Apps attribuatedApp, Type type , String text, ResourceLocation texture, ResourceLocation hoverTexture, boolean displayText) {
		super(type, text, texture, hoverTexture, displayText,new CallBackObject() 
		{
			@Override
			public void call()
			{
				PhoneUI.getPhone().openApp(attribuatedApp);
			}
		});
		
		this.attribuatedApp = attribuatedApp;
	}
	
	@Override
	public void draw(int x, int y)
	{
		super.draw(x, y);
		GuiUtils.renderCenteredText(attribuatedApp.name, posX+1+width/2 , posY+22,0.7f);
	}
	
	

}
