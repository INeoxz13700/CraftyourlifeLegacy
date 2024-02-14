package fr.innog.phone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import org.lwjgl.opengl.GL11;

import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UICheckBox;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIDropdown;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Settings extends Apps {
	
	
	private Recorder recorder;
	
	private int developperModClickNeed;
			
	
	public Settings(String name, ResourceLocation ico, PhoneUI phone) {
		super(name, ico, phone);
		
		recorder = new Recorder(true);		
	}
	
	@Override
	public void initGui()
	{
		if(!phone.settings.developperModeActive)
		{
			developperModClickNeed = 10;
			phone.settings.developperModeActive = false;
		}

		super.initGui();
	}
	
	@Override	
	public void initWindows()
	{
		PhoneUI.GuiApps guiApps = (PhoneUI.GuiApps) phone.getChild(0);
		this.setWindowSize(guiApps.getWindowWidth(), guiApps.getWindowHeight());
		this.setWindowPosition(guiApps.getWindowPosX(), guiApps.getWindowPosY());

	}
	
	@Override
	public void initializeComponent()
	{		
		List<String> mic_elements = new ArrayList<String>();
		List<String> source_elements = new ArrayList<String>();
		
		
		UIDropdown micsDropDown = (UIDropdown)addComponent(new UIDropdown(10, mic_elements, new UIColor("#4876FF")).setPosition(getWindowPosX() + (getWindowWidth() - 105) / 2, getWindowPosY() + 42, 105, 12));
		UIDropdown srcDropDown = (UIDropdown)addComponent(new UIDropdown(10, source_elements, new UIColor("#4876FF")).setPosition(getWindowPosX() + (getWindowWidth() - 105) / 2, getWindowPosY() + 75, 105, 12));
		
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (Mixer.Info info: mixerInfos){
			Mixer m = AudioSystem.getMixer(info);
			Line.Info[] targetlineInfos = m.getTargetLineInfo();
			Line.Info[] sourceLineInfos = m.getSourceLineInfo();
			if(targetlineInfos.length>=1 && targetlineInfos[0].getLineClass().equals(TargetDataLine.class)){
				mic_elements.add(info.getName());
			}
			else if(sourceLineInfos.length >= 1 && sourceLineInfos[0].getLineClass().equals(SourceDataLine.class))
			{
				source_elements.add(info.getName());
			}
		}
		

		
		srcDropDown.setElementByName(phone.settings.selectedSourceAudio);	
		micsDropDown.setElementByName(phone.settings.selectedMicroPhone);
		
		addComponent(new UICheckBox(Type.SQUARE, phone.settings.notDisturb).setPosition(getWindowPosX() + (getWindowWidth() - 10) / 2, getWindowPosY() + 107, 10, 10));
		UIButton activateMicBtn = (UIButton)addComponent(new UIButton(Type.SQUARE,recorder.isRunning() ? "Désactiver" : "Activer le test",new UIRect(new UIColor("#4f9e5c")),new UIRect(new UIColor("#428a4e")),null).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 137, 80, 12));
		
		activateMicBtn.callback = new UIButton.CallBackObject()
		{
			public void call()
			{
				recorder.setRunninng(!recorder.isRunning());
				
				if(recorder.isRunning())
				{
					try {	
						recorder = new Recorder(true);
						recorder.start();
					}
					catch (Exception e) {
						System.err.println(e);
					}
				}
				else
				{
					recorder.setRunninng(false);
				}
				
				activateMicBtn.setDisplayText(recorder.isRunning() ? "Désactiver" : "Activer le test");
				
			}
		};
		
		super.initializeComponent();

	}

	
	@Override
	public void drawScreen(int x, int y, float partialTicks) 
	{		
		GuiUtils.drawImage(getWindowPosX()-2, getWindowPosY()-1, this.phone.background_apps, getWindowWidth()+3, getWindowHeight()+2, 0);
		
		GuiUtils.drawImage(getWindowPosX()-2, getWindowPosY() , new ResourceLocation("craftyourliferp", "ui/phone/contact_container.png"), getWindowWidth()+3, 25,0);
		GuiUtils.renderCenteredText("Numéro de telephone ", getWindowPosX() + getWindowWidth() / 2, getWindowPosY() + 3, 0.9f);
		GuiUtils.renderCenteredText(phone.playerData.getPhoneData().getNumber(), getWindowPosX() + getWindowWidth() / 2, getWindowPosY() + 16, 0.9f,0);
		
		GL11.glColor4f(1f, 1f, 1f, 1f);
		GuiUtils.drawImage(getWindowPosX()-2, getWindowPosY() + 28, new ResourceLocation("craftyourliferp", "ui/phone/contact_container.png"), getWindowWidth()+3, 30,0);
		GuiUtils.renderCenteredText("Micro selectionné ", getWindowPosX() + getWindowWidth() / 2, getWindowPosY() + 31, 0.9f);
		
		GuiUtils.drawImage(getWindowPosX()-2, getWindowPosY() + 61, new ResourceLocation("craftyourliferp", "ui/phone/contact_container.png"), getWindowWidth()+3, 30,0);
		GuiUtils.renderCenteredText("Sortie Audio ", getWindowPosX() + getWindowWidth() / 2, getWindowPosY() + 65, 0.9f);
		
		GuiUtils.drawImage(getWindowPosX()-2, getWindowPosY() + 94, new ResourceLocation("craftyourliferp", "ui/phone/contact_container.png"), getWindowWidth()+3, 24,0);
		GuiUtils.renderCenteredText("Mode ne pas déranger ", getWindowPosX() + getWindowWidth() / 2, getWindowPosY() + 97, 0.9f);
		
		GuiUtils.drawImage(getWindowPosX()-2, getWindowPosY() + 121, new ResourceLocation("craftyourliferp", "ui/phone/contact_container.png"), getWindowWidth()+3, 30,0);
		GuiUtils.renderCenteredText("Test microphone ", getWindowPosX() + getWindowWidth() / 2, getWindowPosY() + 124, 0.9f);
		
		UIDropdown dropdown = (UIDropdown) getComponent(0);
		

		super.drawScreen(x, y, partialTicks);
	
		
		if(dropdown.wasClicked)
		{
			if(dropdown.getElementHoverMouse(y) != null) drawHoveringText(Arrays.asList(dropdown.getElementHoverMouse(y)),x,y,phone.mc.fontRenderer);
		}
		
		dropdown = (UIDropdown) getComponent(1);
		
		if(dropdown.wasClicked)
		{
			if(dropdown.getElementHoverMouse(y) != null) drawHoveringText(Arrays.asList(dropdown.getElementHoverMouse(y)),x,y,phone.mc.fontRenderer);
		}
		
		
	}

	@Override
	public void mouseClickMove(int x, int y, int buttonId, long timeSinceLastClick) 
	{
		
	}
	
	
	public static TargetDataLine getTargetDataLine(String microphone){
		try {
			
			Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
			
			for (Mixer.Info info: mixerInfos)
			{
				Mixer m = AudioSystem.getMixer(info);
								
				if(info.getName().equalsIgnoreCase(microphone))
				{
				      if(m.getSourceLineInfo().length > 0)
				      {
				          return (TargetDataLine)AudioSystem.getLine(m.getSourceLineInfo()[0]);
				      }
				}
			}
			
			return null;
		} catch (LineUnavailableException ex) {//If the line is unavailable, it returns
			ex.printStackTrace();
			return null;
		}
	}
	
	public static SourceDataLine getSourceDataLine(String source){
		try 
		{
			Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
			
			for (Mixer.Info info: mixerInfos)
			{
				Mixer m = AudioSystem.getMixer(info);
								
				if(info.getName().equalsIgnoreCase(source))
				{
				      if(m.getSourceLineInfo().length > 0)
				      {
				          return (SourceDataLine)AudioSystem.getLine(m.getSourceLineInfo()[0]);
				      }
				}
			}
			
			return null;
		} catch (LineUnavailableException ex) {//If the line is unavailable, it returns
			ex.printStackTrace();
			return null;
		}
	}

	@Override
	public void updateScreen() {
		UIDropdown micsDropdown = (UIDropdown) getComponent(0);
		UIDropdown srcDropdown = (UIDropdown) getComponent(1);

		UICheckBox checkBox = (UICheckBox) getComponent(2);
		phone.settings.notDisturb = checkBox.checked;
		phone.settings.selectedMicroPhone = micsDropdown.getSelectedElement();
		phone.settings.selectedSourceAudio = srcDropdown.getSelectedElement();
		
		super.updateScreen();
	}

	@Override
	public void keyTyped(char par1, int par2) {
		
	}

	@Override
	public void back() {
		phone.currentApp = null;
		try {
			phone.settings.saveData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateGuiState() {
		
	}

	@Override
	public void openApps() {
		
		if(phone.settings.developperModeActive) return;
		
		if(developperModClickNeed-- <= 1)
		{
			phone.settings.setDevelopperMode(true);
			phone.displayToast("Mode développeur activé", 4);
			phone.playerData.getPhoneData().setDevelopperModeActive();
			phone.registerApp(new Tor("Tor", new ResourceLocation("craftyourliferp", "ui/phone/tor_ico.png"), phone));
			GuiScrollableView scrollview = (GuiScrollableView)phone.getChild(0);
			scrollview.updateScrollviewContents();
			return;
		}
		phone.displayToast("Appuyez " + developperModClickNeed + "x pour le mod développeur", 4);
	}
	



	
	

	

}
