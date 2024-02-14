package fr.innog.data;

import fr.innog.network.INetworkElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class IdentityData implements ISaveHandler, INetworkElement {

	public boolean waitingDataFromClient;

	public String lastname;
	
	public String name;
	
	public String birthday;
	
	public String gender;
	
	public long lastIdentityResetTime;
	
	public IdentityData() {
		lastname = "";
		name = "";
		birthday = "";
		gender = "";
	}
	
	public void setData(String lastname, String name, String birthday, String gender)
	{
		this.lastname = lastname;
		this.name = name;
		this.birthday = birthday;
		this.gender = gender;
		this.waitingDataFromClient = false;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setString("LastName", lastname);
		compound.setString("Name", name);
		compound.setString("Birthday", birthday);
		compound.setString("Gender", gender);
		compound.setLong("LastIdentityResetTime", lastIdentityResetTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		lastname = compound.getString("LastName");
		name = compound.getString("Name");
		birthday = compound.getString("Birthday");
		gender = compound.getString("Gender");
		lastIdentityResetTime = compound.getLong("LastIdentityResetTime");
	}

	@Override
	public void encodeInto(ByteBuf data) {
		ByteBufUtils.writeUTF8String(data, this.lastname);
		ByteBufUtils.writeUTF8String(data, this.name);
		ByteBufUtils.writeUTF8String(data, this.gender);
		ByteBufUtils.writeUTF8String(data, this.birthday);
	}

	@Override
	public void decodeInto(ByteBuf data) {
		this.lastname = ByteBufUtils.readUTF8String(data);
		this.name = ByteBufUtils.readUTF8String(data);
		this.gender = ByteBufUtils.readUTF8String(data);
		this.birthday = ByteBufUtils.readUTF8String(data);
	}
	
	
}
