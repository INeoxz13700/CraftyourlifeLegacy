package fr.innog.phone;

import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketSendVoice;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Recorder extends Thread {
		
	private boolean running = false;
	
	private boolean isLocal = true;
		
	public AudioFormat audioFormat;
	
	private DataLine.Info targetInfo;
	private DataLine.Info sourceInfo;
	
	private TargetDataLine targetLine;
	private SourceDataLine sourceLine;	
	
	private byte[] buffer;
	
	private ArrayList<byte[]> storedSonoreData = new ArrayList<byte[]>();
	
	public Recorder(boolean isLocal)
	{
		this.isLocal = isLocal;
	
	}

	@Override
	public void run() {
		audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000.0F, 16, 1, 2, 16000.0F, false);

		targetInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
		sourceInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
		
		
		TargetDataLine configTargetDataLine = (TargetDataLine) Settings.getTargetDataLine(PhoneUI.getPhone().settings.selectedMicroPhone);
		SourceDataLine configSourceDataLine = (SourceDataLine) Settings.getSourceDataLine(PhoneUI.getPhone().settings.selectedSourceAudio);
		

				
		if(configTargetDataLine != null)
		{
			targetLine = (TargetDataLine)configTargetDataLine;
		}
		else
		{
			try {
				targetLine = (TargetDataLine) AudioSystem.getLine(targetInfo);
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
		
	
		if(configSourceDataLine != null)
		{
			sourceLine = (SourceDataLine) configSourceDataLine;
		}
		else
		{
			try 
			{
				sourceLine = (SourceDataLine) AudioSystem.getLine(sourceInfo);
			} 
			catch (LineUnavailableException e) 
			{
				e.printStackTrace();
			}
		}
		
		if(targetLine != null)
		{
			if(!targetLine.isOpen() && !targetLine.isActive())
			{
				try 
				{
					targetLine.open(audioFormat);
					targetLine.start();
				} catch (LineUnavailableException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		if(sourceLine != null)
		{
			if(!sourceLine.isOpen() && !sourceLine.isActive())
			{			
				try {
					sourceLine.open(audioFormat);
					sourceLine.start();
				} catch (LineUnavailableException e) {
					e.printStackTrace();
				}
			}
		}
		
		int numBytesRead;

		byte[] targetData = new byte[targetLine.getBufferSize() / 5];
		
		if(!isLocal)
		{
			new Thread(new Runnable() 
			{

				@Override
				public void run() 
				{
					while(running)
					{

						if(sourceLine != null && !storedSonoreData.isEmpty())
						{
							byte[] b = storedSonoreData.remove(0);
							sourceLine.write(b, 0, b.length);
						}
						
						if(storedSonoreData.size() >= 20)
						{
							storedSonoreData.clear();
						}
						
						try 
						{
							Thread.sleep(10);
						} 
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}
				
			}).start();
		}

		
		
		
		while(running)
		{
			
			numBytesRead = targetLine.read(targetData, 0, targetData.length);

			if (numBytesRead != -1)
			{
				if(isLocal)
				{
					sourceLine.write(targetData, 0, numBytesRead);				
				}
				else
				{
					if(Minecraft.getMinecraft().world != null) ModCore.getPackethandler().sendToServer(new PacketSendVoice(targetData, PhoneUI.getPhone().mc.player.getName()));
				}
			}
		
			
			
		}	
		
		this.closeDevices();
	}
	
	public void setRunninng(boolean state)
	{
		this.running = state;
	}
	
	public boolean isRunning()
	{
		return this.running;
	}
	
	public void closeDevices()
	{
		if(this.targetLine != null)
		{
			this.targetLine.close();
		}
		
		if(this.sourceLine != null)
		{
			this.sourceLine.close();
		}
		
	}
	
	@Override
	public void start()
	{
		this.setRunninng(true);
		super.start();
	}
	
	public void ProcessDataFromServer(byte[] data) {
		this.storedSonoreData.add(data);
	}
	
	
}
