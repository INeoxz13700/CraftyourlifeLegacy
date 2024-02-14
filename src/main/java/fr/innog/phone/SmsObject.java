package fr.innog.phone;

import java.sql.Timestamp;
import java.util.List;

import org.lwjgl.opengl.GL11;

import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.data.SmsData;
import net.minecraft.util.ResourceLocation;

public class SmsObject extends UIImage {

	public SmsData attribuatedData;
	
	private boolean senderIsClient = false;
	
	private boolean displayDate = false;
	
	public SmsObject(SmsData data, boolean senderIsClient)
	{
		this.senderIsClient = senderIsClient;
		if(senderIsClient)
		{
			texture = new ResourceLocation("craftyourliferp","ui/phone/bubble_orange.png");
		}
		else
		{
			texture = new ResourceLocation("craftyourliferp","ui/phone/bubble_blue.png");
		}
		this.attribuatedData = data;
	}
	
	public void draw(int x, int y)
	{
		super.draw(x, y);
		List<String> array = mc.fontRenderer.listFormattedStringToWidth(attribuatedData.message, (int) (Sms.bubble_max_width * 1/Sms.font_scale));
		GL11.glColor3f(1f, 1f, 1f);
		
		int tempy = posY;
		for(String word : array)
		{
			GL11.glPushMatrix();
			GL11.glColor3f(1, 1, 1);
			GuiUtils.renderText(word, posX + 3, tempy + 2,GuiUtils.gameColor,Sms.font_scale);
			
			tempy+= mc.fontRenderer.FONT_HEIGHT * (Sms.font_scale + 0.1f);
									
			GL11.glPopMatrix();
		}
		
		Timestamp today = new Timestamp(System.currentTimeMillis());
		Timestamp date = new Timestamp(attribuatedData.date);


		if(today.getDay() == date.getDay() && today.getMonth() == date.getMonth() && today.getYear() == date.getYear())
		{
			String displayDate = "";
			if(date.getMinutes() >= 0 && date.getMinutes() <= 9)
				displayDate = date.getHours() + ":"  + "0" + date.getMinutes();
			else
				displayDate = date.getHours() + ":"  + date.getMinutes();
			
			GL11.glPushMatrix();
			GL11.glColor3f(1, 1, 1);
			GuiUtils.renderText(displayDate, posX+width+5, tempy-6,0,0.6f);
			GL11.glPopMatrix();
			
			this.displayDate = true;
		}
		
		if(!senderIsClient)
		{
			GL11.glColor3f(1, 1, 1);
			GuiUtils.drawImage(posX-18, posY, new ResourceLocation("craftyourliferp","ui/phone/profil.png"), 15, 15, 0);
		}
	}
	
	@Override
	public GraphicObject setPosition(int x, int y, int width, int height)
	{
		super.setPosition(x, y, width, height);
		return this;
	}
	
	
}
