package fr.innog.phone;

import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.data.ContactData;
import fr.innog.data.PhoneData;
import fr.innog.network.packets.decrapted.PacketFinishCall;
import fr.innog.network.packets.decrapted.PacketStartCall;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CallHandler {
	
	public ResourceLocation call = new ResourceLocation("craftyourliferp", "call");
	public ResourceLocation call_ringtone = new ResourceLocation("craftyourliferp", "call_ringtone");
	public ResourceLocation call_false = new ResourceLocation("craftyourliferp", "false_request");
	public ResourceLocation call_unkown_number = new ResourceLocation("craftyourliferp", "unkown_number");
	
	private ContactData cData;
	
	private String receiverPhoneNumber;
	
	private String receiverUsername;
	
	private String callerUsername;
	
	private String callerNumber;
	
	private ISound playedSound;
	
	private int elapsedSecondsCall;
	
	private int ticksSinceCall;
	
	private boolean terminate = false;
	
	public Recorder recorder;
	
	/*Call state
	 * 
	 * -1 = waiting
	 *  0 = false_request
	 *  1 = accepted_request
	 *  2 = receiver already in call
	 *  3 = receiver not connected
	 *  4 = unkown number
	 */
	public int callState = -1;
		
	
	boolean receiveCall = false;

	//public AudioFormat format;
	
	//public DataLine.Info targetInfo;
	//public DataLine.Info sourceInfo;
	//public TargetDataLine targetLine;
	//public SourceDataLine sourceLine;
	//public int numBytesRead;
	//public byte[] targetData;

	public CallHandler(String number, boolean fromServer)
	{
		if(!fromServer)
		{
			this.receiverPhoneNumber = number;	
			PhoneUI.getPhone().playerData.getPhoneData();
			cData = PhoneData.getContact(number);
			ModCore.getPackethandler().sendToServer(new PacketStartCall(this.receiverPhoneNumber, PhoneUI.getPhone().playerData.getPhoneData().getNumber(), Minecraft.getMinecraft().player.getName()));	
		}
		else
		{
			this.callerNumber = number;
			this.receiveCall = true;
			cData = PhoneData.getContact(number);
		}
		
		recorder = new Recorder(false);
	
		
	}
	
	public void stopSound()
	{
		if(playedSound == null) return;
		
		Minecraft.getMinecraft().getSoundHandler().stopSound(playedSound);
	}
	

	public void update()
	{

		this.ticksSinceCall++;
		
		if(terminate)
		{
    		if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this.playedSound))
    		{
    			if(!this.receiveCall)
    				ModCore.getPackethandler().sendToServer(new PacketFinishCall());
    			((Call) PhoneUI.getPhone().currentApp).finishCall();
    		}
		}
				
		if(this.callState == 1 && !recorder.isRunning())
		{
			recorder.start();			
		}
		
		if(this.ticksSinceCall % 20 == 0)
		{
			if(this.callState == 1)
			{
				this.elapsedSecondsCall++;
			}
			else if(this.callState == 0 || this.callState == 2 || this.callState == 3)
			{
				if(this.playedSound == null)
				{
					this.playedSound = new PositionedSoundRecord(new SoundEvent(call_false), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition());
					Minecraft.getMinecraft().getSoundHandler().playSound(this.playedSound);
				}
				else
				{
		    		if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this.playedSound))
		    		{
		    			((Call) PhoneUI.getPhone().currentApp).finishCall();
		    		}
				}
			}
			else if(this.callState == 4)
			{
				if(this.playedSound == null)
				{
					this.playedSound = new PositionedSoundRecord(new SoundEvent(call_unkown_number), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition());

					Minecraft.getMinecraft().getSoundHandler().playSound(this.playedSound);
				}
				else
				{
		    		if(!Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(this.playedSound))
		    		{
		    			((Call) PhoneUI.getPhone().currentApp).finishCall();
		    		}
				}
			}
			
		}
		if(this.ticksSinceCall % (20*5) == 0 && this.callState == -1)
		{
			if(!this.receiveCall && !this.terminate)
			{

	    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(call), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
			}
	    	else if(this.receiveCall)
	    	{
	    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(call_ringtone), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
	    	}
		}
		if(this.ticksSinceCall % ((20 * 5) * 5) == 0)
		{
			if(this.callState == -1 && !this.receiveCall)
			{
				if(this.playedSound == null)
				{
					this.playedSound = new PositionedSoundRecord(new SoundEvent(call_false), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition());
					Minecraft.getMinecraft().getSoundHandler().playSound(this.playedSound);
					terminate = true;
				}
			}
			else if(this.receiveCall && this.callState == -1)
			{
				this.terminate = true;
			}
		}
	}
	
	/*public void readCallDataFromServer(byte[] data)
	{
		if(sourceLine != null)
			sourceLine.write(data, 0, data.length);
	}*/
	
	public ContactData getContactData()
	{
		return this.cData;
	}
	
	public String getNumber()
	{
		if(!this.receiveCall) return this.receiverPhoneNumber;
		return this.callerNumber;
	}
	
	public int getCallElapsedTime()
	{
		return this.elapsedSecondsCall;
	}
	
	

}
