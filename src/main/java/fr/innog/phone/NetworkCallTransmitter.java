package fr.innog.phone;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketConnectingCall;
import fr.innog.network.packets.decrapted.PacketFinishCall;
import fr.innog.network.packets.decrapted.PacketSendVoice;
import fr.innog.network.packets.decrapted.PacketStartCall;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class NetworkCallTransmitter {
	
	public static List<NetworkCallTransmitter> calls = new ArrayList<NetworkCallTransmitter>();
	
	private EntityPlayer caller;
	
	private String callerNumber;
	
	private String callreceiverNumber;
	
	private EntityPlayer callreceiver;
	
	public boolean callConnected = false;
	
	public NetworkCallTransmitter(EntityPlayer caller, EntityPlayer callreceiver, String callerNumber, String receiverNumber) throws Exception
	{
		this.caller = caller;
		this.callreceiver = callreceiver;
		this.callerNumber = callerNumber;
		this.callreceiverNumber = receiverNumber;
				
		if(NetworkCallTransmitter.getByUsername(this.callreceiver.getName()) != null)
		{
			throw new Exception("Number has already in call");
		}
		
		calls.add(this);
	}
	
	public boolean continueCall()
	{		
		World world = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getEntityWorld();
		if(world.getPlayerEntityByName(caller.getName()) == null || world.getPlayerEntityByName(callreceiver.getName()) == null)
			return false;
		
		return true;
	}
	
	public void finishCall(EntityPlayer packetSender)
	{
		if(packetSender == this.caller)
			ModCore.getPackethandler().sendTo(new PacketFinishCall(), (EntityPlayerMP) this.callreceiver);

		else if(packetSender == this.callreceiver)
			ModCore.getPackethandler().sendTo(new PacketFinishCall(), (EntityPlayerMP) this.caller);

		calls.remove(this);
	}
	
	public void finishCall()
	{
		ModCore.getPackethandler().sendTo(new PacketFinishCall(), (EntityPlayerMP) this.caller);
		ModCore.getPackethandler().sendTo(new PacketFinishCall(), (EntityPlayerMP) this.callreceiver);
		calls.remove(this);
	}
	
	public void sendCallRequest()
	{
		ModCore.getPackethandler().sendTo(new PacketStartCall(this.callerNumber), (EntityPlayerMP) this.callreceiver);
	}
	
	/*
	 * Type == 0 client refused call
	 * Type == 1 client accepted call
	 * Type == 2 client isAlready in call
	 * Type == 3 client notConnected
	 * Type == 4 unknown number
	 */
	public void callRequestAnswer(int type)
	{
		if(type == 0)
		{
			this.callConnected = false;
			ModCore.getPackethandler().sendTo(new PacketConnectingCall(type), (EntityPlayerMP)caller);
		}
		else if(type == 1)
		{
			this.callConnected = true;
			ModCore.getPackethandler().sendTo(new PacketConnectingCall(type), (EntityPlayerMP)caller);
			ModCore.getPackethandler().sendTo(new PacketConnectingCall(type), (EntityPlayerMP)this.callreceiver);
		}
		else if(type == 2)
		{
			ModCore.getPackethandler().sendTo(new PacketConnectingCall(type), (EntityPlayerMP)caller);
			this.callConnected = false;
		}
	}
	
	public static NetworkCallTransmitter getByUsername(String username)
	{
		for(NetworkCallTransmitter nt : calls)
		{
			if(username.equalsIgnoreCase(nt.caller.getName()) || username.equalsIgnoreCase(nt.callreceiver.getName()))
			{
				return nt;
			}
		}
		return null;
	}
	
	public void TransmitVoiceData(EntityPlayer sender, byte[] data)
	{
		if(!this.continueCall())
		{
			this.finishCall();
			return;
		}
		
		if(sender == this.caller)
		{
			ModCore.getPackethandler().sendTo(new PacketSendVoice(data, sender.getName()),(EntityPlayerMP) this.callreceiver);
		}
		else
		{
			ModCore.getPackethandler().sendTo(new PacketSendVoice(data, sender.getName()),(EntityPlayerMP) this.caller);
		}
	}
	
	
	
}
