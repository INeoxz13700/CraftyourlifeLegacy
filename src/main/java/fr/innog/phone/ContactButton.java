package fr.innog.phone;

import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.data.ContactData;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ContactButton extends UIButton {

	
	public ContactData attribuatedData;
	
	protected UIImage profileImg;
		
	public ContactButton(ContactData attribuatedData, Type type , ResourceLocation texture, ResourceLocation hoverTexture)
	{
		super(type,"",texture,hoverTexture,false,null);
		this.attribuatedData = attribuatedData;
		this.profileImg = new UIImage(new ResourceLocation("craftyourliferp","ui/phone/profil.png"));
		
		ContactButton btn = this;
		
		callback = new CallBackObject()
		{
			@Override
			public void call()
			{
				PhoneUI phone = PhoneUI.getPhone();
				phone.displayContact(btn);
			}
		};
	}
	
	@Override
	public void draw(int x, int y)
	{
		super.draw(x, y);
		profileImg.draw(x, y);
		GuiUtils.renderText(attribuatedData.name, posX+30, (posY+(height-mc.fontRenderer.FONT_HEIGHT)/2)+1);
	}
	
	
	@Override
	public GraphicObject setPosition(int x, int y, int width, int height)
	{
		super.setPosition(x, y, width, height);
		profileImg.setPosition(x+5, y+(height-15)/2, 15, 15);
		return this;
	}
	
}
