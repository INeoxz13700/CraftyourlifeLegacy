package fr.innog.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class LoginInformation implements ISaveHandler {

	private boolean firstConnection;

	public boolean passwordReceived;
	
    public int authentificationAttempt = 0;
    
    public long authentificationAttemptResetTime;
    
	public Vec3d connectionPos;

	
	public LoginInformation()
	{
		passwordReceived = false;
		firstConnection = true;
	}
	
	public boolean isFirstConnection() {
		return this.firstConnection;
	}

	public void setFirstConnection(boolean bool) {
		this.firstConnection = bool;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("FirstConnection", firstConnection);
		compound.setInteger("AuthentificationAttempt", authentificationAttempt);
		compound.setLong("AuthentificationAttemptResetTime", authentificationAttemptResetTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		setFirstConnection(compound.getBoolean("FirstConnection"));
		authentificationAttempt = compound.getInteger("AuthentificationAttempt");
		authentificationAttemptResetTime = compound.getLong("AuthentificationAttemptResetTime");
	}
	
}
