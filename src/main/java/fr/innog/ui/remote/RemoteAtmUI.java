package fr.innog.ui.remote;

import fr.innog.common.ModCore;
import fr.innog.ui.remote.data.RemoteMethod;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;

public class RemoteAtmUI extends RemoteUIProcessor {

	public RemoteAtmUI(EntityPlayer player) {
		super(player);
	}
	
	@RemoteMethod
	public void withdraw(Long value)
	{ 
		ModCore.log(value + " $ retiré du compte bancaire");
		MinecraftUtils.dispatchConsoleCommand("atm admin take " + player.getName() + " " + value);
	}
	
	@RemoteMethod
	public void deposit(Long value)
	{
		ModCore.log(value + " $ déposé sur le compte bancaire");
		MinecraftUtils.dispatchConsoleCommand("atm admin put " + player.getName() + " " + value);
	}

}
