package fr.innog.data;

import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;

public class SmsData {
	
	public long date; 
	
	public String senderNumber;
	
	public String receiverNumber;
	
	public String message;
	
	public boolean readed = false;
	
	
	public SmsData(long date, String senderNumber, String receiverNumber, String message, boolean readed)
	{
		this.date = date;
		this.message = message;
		this.senderNumber = senderNumber; 
		this.receiverNumber = receiverNumber;
		this.readed = readed;
	}
	
	public static SmsData getSmsData(long date, String senderNumber, String message, EntityPlayer p)
	{
		for(SmsData sms : MinecraftUtils.getPlayerCapability(p).getPhoneData().sms)
		{
			if(sms.date  == date && sms.senderNumber.equalsIgnoreCase(senderNumber) && sms.message.equalsIgnoreCase(message))
			{
				return sms;
			}
		}
		return null;
	}

}