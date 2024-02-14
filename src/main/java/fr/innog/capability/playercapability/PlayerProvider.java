package fr.innog.capability.playercapability;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerProvider implements ICapabilityProvider,INBTSerializable<NBTTagCompound> {

	@CapabilityInject(IPlayer.class)
	public static final Capability<IPlayer> PLAYER_CAP = null;

	private IPlayer instance = PLAYER_CAP.getDefaultInstance();
	
	public PlayerProvider(EntityPlayer player)
	{
		instance.initData(player);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing)
	{
		return PLAYER_CAP != null && capability == PLAYER_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing)
	{
		return PLAYER_CAP != null && capability == PLAYER_CAP ? PLAYER_CAP.<T> cast(instance) : null;
	}

	@Override
	public NBTTagCompound serializeNBT() 
	{
		return (NBTTagCompound) PLAYER_CAP.getStorage().writeNBT(PLAYER_CAP, instance, null);

	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) 
	{
		PLAYER_CAP.getStorage().readNBT(PLAYER_CAP, instance, null, nbt);
	}
	
}
