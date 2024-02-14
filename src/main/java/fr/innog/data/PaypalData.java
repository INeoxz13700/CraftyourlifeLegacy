package fr.innog.data;

import java.util.Random;

import fr.innog.common.ModCore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;

public class PaypalData implements ISaveHandler 
{
		
	public String password = "";
	
	public float accountSold;
	
	public String smsCode;
	
	public boolean isAuthentified;
	
	
	private String generateSmsCode()
	{
		String smsCode = "";
		Random rand = new Random();
		for(int i = 0; i < 6; i++)
		{
			char randomChar = (char) MathHelper.getInt(rand, 48, 57);
			smsCode += randomChar;
		}
		return smsCode;
	}
	
	public void updateSmsCode()
	{
		smsCode = generateSmsCode();
	}
	
	public boolean accountIsRegistered()
	{
		return !password.isEmpty();
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		if(accountIsRegistered())
		{
			compound.setString("Password", password);
			compound.setBoolean("Authentified", isAuthentified);
		}		
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if(compound.hasKey("Password"))
		{
			password = compound.getString("Password");
			isAuthentified = compound.getBoolean("Authentified");
		}		
	}
	
}