package fr.innog.phone;

import java.util.List;

import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.utils.FontUtils;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.data.SmsData;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SmsButton extends UIButton {

	
	public String name;
	
	public String number;
	
	public List<SmsData> attribuatedData;
	
	public int notReadedSmsCount = 0;
	
	private UIImage profileImg;
	
	private UIImage notificationImg;

		
	public SmsButton(List<SmsData> datas,String name, String number, Type type , ResourceLocation texture, ResourceLocation hoverTexture)
	{
		super(type,"",texture,hoverTexture,false,null);
		
		profileImg = new UIImage(new ResourceLocation("craftyourliferp","ui/phone/profil.png"));

		notificationImg = new UIImage(new ResourceLocation("craftyourliferp","ui/phone/notification_circle.png"));
		notificationImg.setColor(new UIColor(255,255,255,190));
				
		attribuatedData = datas;
		
		PhoneUI phone = PhoneUI.getPhone();
		
		this.name = name;
		this.number = number;
		
		SmsButton instance = this;
		
		callback = new CallBackObject()
		{
			@Override
			public void call()
			{
				phone.displayConversation(instance);
			}
		};
	}
	
	@Override
	public void draw(int x, int y)
	{
		super.draw(x, y);
		int charCount = FontUtils.getNumberCharacterInWidth(width-40, 1f);

		String displayedName = "";
		if(name.length() == 0)
		{
			displayedName = number.length() > charCount ? number.substring(0, charCount) + "..." : number;
		}
		else
		{
			displayedName = name.length() > charCount ? name.substring(0, charCount) + "..." : name;
		}
		
		GuiUtils.renderText(displayedName, posX + 25, (posY + (height - mc.fontRenderer.FONT_HEIGHT) / 2) - 2);
		profileImg.draw(x, y);
		if(notReadedSmsCount > 0)
		{
			notificationImg.draw(x, y);
			GuiUtils.renderCenteredText(notReadedSmsCount + "", notificationImg.getX()+8, notificationImg.getY()+5,0.7f);
		}
		
		SmsData data = attribuatedData.get(attribuatedData.size()-1);
		
		charCount = FontUtils.getNumberCharacterInWidth(width-30, 0.8f);
		
		String message = data.message.length() > charCount ? data.message.substring(0, charCount) + "..." : data.message;


		GuiUtils.renderText(message, posX + 25, posY + 12, GuiUtils.gameColor,0.8f);
	}
	
	@Override
	public GraphicObject setPosition(int x, int y, int width, int height)
	{
		super.setPosition(x, y, width, height);
		profileImg.setPosition(x + 2, y + (height - 15) / 2, 15, 15);
		notificationImg.setPosition(x + width - 20, y + (height - 15) / 2, 15, 15);
		return this;
	}
	
	public SmsData getData(String number)
	{
		for(SmsData sData : attribuatedData)
		{
			if(sData.senderNumber.equalsIgnoreCase(number))
				return sData;
		}
		return null;
	}
	
	public String getLastMessage()
	{
		if(attribuatedData.size() > 0)
			return attribuatedData.get(attribuatedData.size()-1).message;
		return null;
	}
	
	public static boolean Contains(SmsButton sb, List<GraphicObject> l) {
		for(Object obj : l)
		{
			SmsButton sbt = (SmsButton) obj;
			if(sbt.number.equalsIgnoreCase(sb.number)) return true;
		}
		return false;
	}
	
	public void updateCountSmsNotReaded(List<SmsData> sms)
	{
		int count = 0;
		for(SmsData s : sms)
		{
			if(!s.readed && !s.senderNumber.equalsIgnoreCase(MinecraftUtils.getPlayerCapability(Minecraft.getMinecraft().player).getPhoneData().getNumber()))
			{
				count++;
			}
		}
		this.notReadedSmsCount = count;
	}
	
	public void setReaded()
	{
		PhoneUI phone = PhoneUI.getPhone();
		for(SmsData sms : this.attribuatedData)
		{
			if(!sms.readed)
			{
				sms.readed = true;
			}
		}
		phone.playerData.getPhoneData().setSmsReaded(number);
		updateCountSmsNotReaded(attribuatedData);
	}
	
}
