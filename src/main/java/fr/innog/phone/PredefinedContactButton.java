package fr.innog.phone;

import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.data.ContactData;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PredefinedContactButton extends ContactButton {
	
	private String commandExecuted;	
	
	public PredefinedContactButton(String attribuatedCommand, ContactData attribuatedData, Type type , ResourceLocation texture, ResourceLocation hoverTexture)
	{
		super(attribuatedData, type, texture, hoverTexture);
		
		PredefinedContactButton btn = this;
		commandExecuted = attribuatedCommand;
		
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
	
	public void executeCommand()
	{
    	Minecraft.getMinecraft().player.sendChatMessage(commandExecuted);
	}
}
