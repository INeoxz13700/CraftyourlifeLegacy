package fr.innog.ui.remote;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IRemoteUI {

	void setRemoteUI (RemoteUIProcessor remoteUI);
	
}
