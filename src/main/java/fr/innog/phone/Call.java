package fr.innog.phone;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketConnectingCall;
import fr.innog.network.packets.decrapted.PacketFinishCall;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Call extends Apps {
	
	private PhoneUI parent;
	
	public UIButton[] keys = new UIButton[12];
	
	public UIButton remove;
	
	public UIButton call;
	
	public UIButton hangup;
	
	public UIButton answer;
	
	private String display = "";

	private String callState = "";
	
	private int animState = 0;
		
	public ResourceLocation phonekey = new ResourceLocation("craftyourliferp:phonekey");
	
	private int i;
	
	private int ticks;

	public Call(String name, ResourceLocation ico, PhoneUI phone)
	{
		super(name, ico, phone);	
		parent = phone;
	}
	
	
	@Override
	public void initializeComponent()
	{
		super.initializeComponent();
		
		remove = (UIButton) addComponent(new UIButton(UIButton.Type.SQUARE, "remove_button", new ResourceLocation("craftyourliferp", "ui/phone/button_remove.png"),null, false, new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
	    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(phonekey), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
				if(display.length() > 0)
					display = display.subSequence(0, display.length() - 1).toString();
			}
		}).setPosition(getWindowPosX() + getWindowWidth() - 20, getWindowPosY()+28,15,10));
		
		call = (UIButton)addComponent(new UIButton(UIButton.Type.SQUARE, "call_button", new ResourceLocation("craftyourliferp", "ui/phone/call-ico.png"),null, false,  new UIButton.CallBackObject()
		{
			
			@Override
			public void call()
			{
				
				Call.this.call(display);
				updateGuiState();
			}
			
		}));
		
		hangup = (UIButton)addComponent(new UIButton(UIButton.Type.SQUARE, "hangup_button", new ResourceLocation("craftyourliferp", "ui/phone/call_hangup.png"),null, false, new UIButton.CallBackObject()
		{
		
			public void call()
			{
				if(!phone.getCallHandler().receiveCall || phone.getCallHandler().receiveCall && phone.getCallHandler().callState == 1)
				{
					finishCall();
					ModCore.getPackethandler().sendToServer(new PacketFinishCall());
				}
				else
				{
					ModCore.getPackethandler().sendToServer(new PacketConnectingCall(0));
					finishCall();
				}
			}
			
		}));
		
		answer = (UIButton)addComponent(new UIButton(UIButton.Type.SQUARE, "answer_button", new ResourceLocation("craftyourliferp", "ui/phone/call_answer.png"),null, false, new UIButton.CallBackObject()
		{
			
			public void call()
			{
				if(!(!phone.getCallHandler().receiveCall || phone.getCallHandler().receiveCall && phone.getCallHandler().callState == 1))
				{
					ModCore.getPackethandler().sendToServer(new PacketConnectingCall(1));
					
				}
			}
			
		}));

		int baseX = getWindowPosX() + 1;
		int baseY = getWindowPosY() + 30;

		int j = 0;
		int k = 0;
		for(i = 0; i < 12; i++)
		{
			if(i > 0 && i < 10)
			{
				keys[i] = (UIButton)addComponent(new UIButton(UIButton.Type.SQUARE, i + "_button", new ResourceLocation("craftyourliferp", "ui/phone/" + i + ".png"), null, false, new UIButton.CallBackObject()
				{
					
					private int attribuatedNumber = i;
					
					@Override
					public void call()
					{
						
						if(display.length() >= 15) return;

						display += "" + attribuatedNumber;
			    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(phonekey), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
					}
					
				}).setPosition(baseX + 25 * (j+1), baseY + 25 * (k+1),15,15));
				if(j++ == 2)
				{
					j=0;
					k++;
				}
			}
			else if(i == 10)
			{
				keys[i] = (UIButton)addComponent(new UIButton(UIButton.Type.SQUARE, i + "_button", new ResourceLocation("craftyourliferp", "ui/phone/0.png"), null, false, new UIButton.CallBackObject()
				{			
					@Override
					public void call()
					{
						if(display.length() >= 15) return;

						
						display += "" + 0;
			    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(phonekey), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
					}
					
				}).setPosition(baseX + 18, getWindowPosY()+getWindowHeight()-25,15,15));
			}
			else if(i == 11)
			{
				keys[i] = (UIButton)addComponent(new UIButton(UIButton.Type.SQUARE, i + "_button", new ResourceLocation("craftyourliferp", "ui/phone/#.png"), null, false, new UIButton.CallBackObject()
				{			
					@Override
					public void call()
					{
					
						if(display.length() >= 15) return;
						
						display += "#";
						
			    		Minecraft.getMinecraft().getSoundHandler().playSound(new PositionedSoundRecord(new SoundEvent(phonekey), SoundCategory.PLAYERS, 1.0F, 1.0F, Minecraft.getMinecraft().player.getPosition()));
					}
					
				}).setPosition(getWindowPosX() + getWindowWidth() -35, getWindowPosY() + getWindowHeight()-25,15,15));	
			}
		}
		call.setPosition(getWindowPosX() + (getWindowWidth() - 18) / 2, getWindowPosY() + getWindowHeight() - 25,18,18);
		
		updateGuiState();
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
	}
	
	@Override	
	public void initWindows()
	{
		PhoneUI.GuiApps guiApps = (PhoneUI.GuiApps) phone.getChild(0);

		this.setWindowSize(guiApps.getWindowWidth(), guiApps.getWindowHeight());
		this.setWindowPosition(guiApps.getWindowPosX(), guiApps.getWindowPosY());
	}
	
	
	public void call(String number)
	{
		this.phone.setCallHandler(new CallHandler(number, false));
	}
	
	public void finishCall()
	{
		if(this.phone.getCallHandler() != null)
		{
			phone.getCallHandler().stopSound();
			
			phone.getCallHandler().recorder.setRunninng(false);
			
			phone.setCallHandler(null);
		}
		updateGuiState();
	}
	
	@Override
	public void drawScreen(int x, int y, float partialTicks) 
	{
		
		
		
		PhoneUI.GuiApps guiApps = (PhoneUI.GuiApps) phone.getChild(0);
		

		if(this.phone.getCallHandler() != null)
		{	
			GuiUtils.drawImage(guiApps.getWindowPosX()-2, guiApps.getWindowPosY()-1, new ResourceLocation("craftyourliferp", "ui/phone/call_background.png"), guiApps.getWindowWidth()+3, guiApps.getWindowHeight()+2,0);
			GuiUtils.drawImage(guiApps.getWindowPosX() + (guiApps.getWindowWidth() - 30) / 2, guiApps.getWindowPosY()+30, new ResourceLocation("craftyourliferp", "ui/phone/profil.png"), 30, 30,0);
			
			if(!this.phone.getCallHandler().receiveCall || (this.phone.getCallHandler().receiveCall && this.phone.getCallHandler().callState == 1))
			{
				
				hangup.setPosition(guiApps.getWindowPosX() + (guiApps.getWindowWidth() - 20) / 2, guiApps.getWindowPosY() + guiApps.getWindowHeight() - 30, 20, 20);

				GuiUtils.renderText(this.callState, guiApps.getWindowPosX() + 4, guiApps.getWindowPosY()+4, GuiUtils.gameColor, 0.8f);
				
				GuiUtils.renderCenteredText(this.phone.getCallHandler().getContactData() == null ? this.phone.getCallHandler().getNumber() : this.phone.getCallHandler().getContactData().name, guiApps.getWindowPosX() + guiApps.getWindowWidth() / 2, guiApps.getWindowPosY()+90);
				
				if(this.phone.getCallHandler().callState == 1) GuiUtils.renderCenteredText("" + this.TimeFormat(this.phone.getCallHandler().getCallElapsedTime()), guiApps.getWindowPosX() + guiApps.getWindowWidth() / 2, guiApps.getWindowPosY()+70,0.8f);
			}
			else
			{
				hangup.setPosition((guiApps.getWindowPosX() + (guiApps.getWindowWidth() - 20) / 2) - 25, guiApps.getWindowPosY()+120, 20, 20);
				answer.setPosition((guiApps.getWindowPosX() + (guiApps.getWindowWidth() - 20) / 2) + 25, guiApps.getWindowPosY()+120, 20, 20);
				
				GuiUtils.renderText(this.callState,  guiApps.getWindowPosX() + 4,  guiApps.getWindowPosY()+4, GuiUtils.gameColor, 0.8f);
				GuiUtils.renderCenteredText(this.phone.getCallHandler().getContactData() == null ? this.phone.getCallHandler().getNumber() : this.phone.getCallHandler().getContactData().name, ( guiApps.getWindowPosX() + guiApps.getWindowWidth() / 2), guiApps.getWindowPosY()+90);
				
				if(this.phone.getCallHandler().callState == 1) GuiUtils.renderCenteredText("" + this.TimeFormat(this.phone.getCallHandler().getCallElapsedTime()), guiApps.getWindowPosX() + guiApps.getWindowWidth() / 2,  guiApps.getWindowPosY()+70,0.8f);
			}
		}
		else
		{
			GuiUtils.drawImage(guiApps.getWindowPosX()-2, guiApps.getWindowPosY()-1, this.phone.background_apps, guiApps.getWindowWidth()+3, guiApps.getWindowHeight()+2,0);
			GuiUtils.drawImage(guiApps.getWindowPosX(), guiApps.getWindowPosY()+20, new ResourceLocation("craftyourliferp", "ui/phone/numero_container.png"), guiApps.getWindowWidth(), 25,0);
			GuiUtils.drawImage(guiApps.getWindowPosX(), guiApps.getWindowPosY()+46, new ResourceLocation("craftyourliferp", "ui/phone/key_container.png"), guiApps.getWindowWidth(), 105,0);
			
			GuiUtils.renderText(display, guiApps.getWindowPosX() + 5, guiApps.getWindowPosY()+30, 0);
		}
		super.drawScreen(x, y, partialTicks);
	}
	

	
	@Override
	public void mouseClickMove(int x, int y, int buttonId, long timeSinceLastClick) {
	}
	
	
	
	public String TimeFormat(int seconds)
	{
		int fmin = seconds / 60;
		int fsec = seconds % 60;
		
		return (fmin < 10 ? "0" + fmin : "" + fmin) + ":" + (fsec < 10 ? "0" + fsec : "" + fsec);
	}


	@Override
	public void onGuiClosed() {
		

	}
	

	@Override
	public void updateScreen() {
		

		
		if(this.phone.getCallHandler() != null)
		{
			this.phone.getCallHandler().update();
			if(this.phone.getCallHandler() != null && this.phone.getCallHandler().callState == -1)
			{
				if(this.phone.getGuiTicks() % 14 == 0)
				{
					if(animState == 0)
					{
						if(this.phone.getCallHandler() != null && this.phone.getCallHandler().receiveCall)
							this.callState = "Appel entrant";
						else
							this.callState = "Numérotation";
					}
					else if(animState == 1)
					{
						if(this.phone.getCallHandler() != null && this.phone.getCallHandler().receiveCall)
							this.callState = "Appel entrant.";
						else
							this.callState = "Numérotation.";
					}
					else if(animState == 2)
					{
						if(this.phone.getCallHandler() != null && this.phone.getCallHandler().receiveCall)
							this.callState = "Appel entrant..";
						else
							this.callState = "Numérotation..";
					}
					else if(animState == 3)
					{
						if(this.phone.getCallHandler() != null && this.phone.getCallHandler().receiveCall)
							this.callState = "Appel entrant...";
						else
							this.callState = "Numérotation...";
					}
					else
					{
						animState = 0;
						return;
					}
					animState++;					
				}

			}
			else
			{
				callState = "Appel en cours";
			}
		}
	}

	@Override
	public void keyTyped(char par1, int par2) {
		
	}

	
	public void updateGuiState()
	{

		if(this.phone.getCallHandler() != null)
		{	
			if(!this.phone.getCallHandler().receiveCall || (this.phone.getCallHandler().receiveCall && this.phone.getCallHandler().callState == 1))
			{

				getComponent(0).setVisible(false);
				getComponent(1).setVisible(false);
				
				getComponent(2).setVisible(true);
				
				getComponent(3).setVisible(false);

				
				for(int i = 4; i < 15; i++)
				{
					getComponent(i).setVisible(false);
				}
			}
			else
			{
				getComponent(0).setVisible(false);
				getComponent(1).setVisible(false);
				
				getComponent(2).setVisible(true);
				
				getComponent(3).setVisible(true);

				
				for(int i = 4; i < 15; i++)
				{
					getComponent(i).setVisible(false);
				}
			}
		}
		else
		{
			getComponent(0).setVisible(true);
			getComponent(1).setVisible(true);
			
			getComponent(2).setVisible(false);
			getComponent(3).setVisible(false);


			for(int i = 4; i < 15; i++)
			{
				getComponent(i).setVisible(true);
			}
		}
	
	}


	@Override
	public void back() {
		phone.currentApp = null;
	}


	@Override
	public void openApps() {
		// TODO Auto-generated method stub
		
	}
	
	

	

}
