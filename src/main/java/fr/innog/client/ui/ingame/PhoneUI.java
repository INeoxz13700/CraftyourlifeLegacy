package fr.innog.client.ui.ingame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.lwjgl.opengl.GL11;

import fr.innog.advancedui.gui.GuiBase;
import fr.innog.advancedui.gui.GuiGridView;
import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.data.PhoneSettingsData;
import fr.innog.phone.Apps;
import fr.innog.phone.AppsButton;
import fr.innog.phone.Bank;
import fr.innog.phone.Call;
import fr.innog.phone.CallHandler;
import fr.innog.phone.ContactButton;
import fr.innog.phone.Contacts;
import fr.innog.phone.Paypal;
import fr.innog.phone.Settings;
import fr.innog.phone.Sms;
import fr.innog.phone.SmsButton;
import fr.innog.phone.Tor;
import fr.innog.utils.MathsUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class PhoneUI extends GuiBase 
{
	
	private List<Apps> registeredApps = new ArrayList<>();

	private Calendar date = Calendar.getInstance();
	
	private static PhoneUI instance;
	
	public IPlayer playerData;
	
	private CallHandler callHandler;
	
	public Apps currentApp = null;
		
	public PhoneSettingsData settings = new PhoneSettingsData();
	
	public ResourceLocation background_apps = new ResourceLocation("craftyourliferp","ui/phone/apps_background.png");

	private boolean animationPlayed = false;
	
	private int animationPhonePosY;
	
	private ToastMessage displayedToast = null;


	@Override
	public void initGui() 
	{		
		instance = this;
		
		super.initGui();


		animationPlayed = false;
		animationPhonePosY = height+200;
				
		if(currentApp != null)
		{
			currentApp.initGui();
		}
	}
	
	@Override	
	public void initWindows()
	{
		this.setWindowSize(130, 200);
		this.setWindowPosition(width-135, height-205);
	}
	
	@Override
	public void registerChilds()
	{  
		playerData = MinecraftUtils.getPlayerCapability(mc.player);
		playerData.getPhoneData().loadGeneralData();
		
		try 
		{
			settings.loadData();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		
		registerApp(new Call("Appel", new ResourceLocation("craftyourliferp", "ui/phone/call-ico.png"), this));
		registerApp(new Contacts("Contact", new ResourceLocation("craftyourliferp", "ui/phone/contact-ico.png"), this));
		registerApp(new Sms("SMS", new ResourceLocation("craftyourliferp", "ui/phone/message-ico.png"), this));
		registerApp(new Settings("Params", new ResourceLocation("craftyourliferp", "ui/phone/settings_ico.png"), this));
		registerApp(new Bank("CBank", new ResourceLocation("craftyourliferp", "ui/phone/bank.png"), this));
		registerApp(new Paypal("Paypal", new ResourceLocation("craftyourliferp", "ui/phone/paypal-ico.png"), this));

		
		if(settings.developperModeActive) 
		{
			registerApp(new Tor("Tor", new ResourceLocation("craftyourliferp", "ui/phone/tor_ico.png"), this));
		}

		
		addChild(new GuiApps());
		
		super.registerChilds();
	}
	
	public boolean appRegistered(Apps appCheck)
	{
		for(Apps app : registeredApps)
		{
			if(app.name.equalsIgnoreCase(appCheck.name))
			{
				return true;
			}
		}
		return false;
	}
		
	public void registerApp(Apps app)
	{		
		
		if(appRegistered(app)) return;
				
		registeredApps.add(app);
	}
	
	public void openApp(int index)
	{
		currentApp = registeredApps.get(index);
				
		if(currentApp != null) currentApp.initGui();
	}
	
	public void openApp(Apps app)
	{
		currentApp = app;
		
		if(currentApp != null) currentApp.initGui();
	}
	
	public Apps getApp(int index)
	{
		return registeredApps.get(index);
	}
	
	
	public void home()
	{
		if(currentApp != null) currentApp.onGuiClosed();
		currentApp = null;
	}
	
	public void back()
	{
		if(currentApp != null)
		{
			currentApp.back();
		}
	}
	
	@Override
	public void onGuiClosed()
	{
		if(currentApp != null)
		{
			currentApp.onGuiClosed();
		}
		super.onGuiClosed();
	}

	
	public void displayContact(ContactButton button)
	{
		if(!(currentApp instanceof Contacts))
		{
			openApp(1);
		}
		
		Contacts contacts = (Contacts) currentApp;
		contacts.selectedContact = button;
	}
	
	public void displayConversation(SmsButton button)
	{
		if(!(currentApp instanceof Sms))
		{
			openApp(3);
		}
		
		Sms sms = (Sms) currentApp;
		sms.selectedContact = button;
		sms.selectedContact.setReaded();
		((GuiScrollableView)sms.getChild(1)).updateScrollviewContents();
	}
	
	@Override
    public void onScroll(int i)
    {
		if(!animationPlayed) return;
		
    	if(currentApp != null)
    	{
    		currentApp.onScroll(i);
    	}
    }
	
	
	@Override
	public void initializeComponent() 
	{ 
		this.addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/smartphone_home.png")).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), getWindowHeight()));
		this.addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/time_container.png")).setPosition(getWindowPosX()+5, getWindowPosY()+14, 121, 15));

		
		this.addComponent(new UIButton(Type.SQUARE,"",new UIRect(new UIColor(0,0,0,0)),null, new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				back();
			}
			
		}).setPosition(getWindowPosX() + 24, getWindowPosY() + getWindowHeight() - 13, 13, 10));
		
		this.addComponent(new UIButton(Type.SQUARE,"",new UIRect(new UIColor(0,0,0,0)),null, new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				home();
			}
			
		}).setPosition(getWindowPosX() + 58, getWindowPosY() + getWindowHeight() - 13, 16, 10));
		
		this.addComponent(new UIButton(Type.SQUARE,"",new UIRect(new UIColor(0,0,0,0)),null,  new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				openApps();
			}
			
		}).setPosition(getWindowPosX() + 93, getWindowPosY() + getWindowHeight() - 13, 16, 10));

		
		super.initializeComponent();
	}
	

	 @Override
	 protected void mouseClickMove(int x, int y, int state, long timeSinceLastClick)
	 {
		 if(!animationPlayed) return;
		 
		 if(currentApp != null)
		 {
			 currentApp.mouseClickMove(x, y, state, timeSinceLastClick);
		 }
		 super.mouseClickMove(x, y, state, timeSinceLastClick);
	 }
	
	 @Override
	 public void mouseReleased(int mouseX, int mouseY, int state)
	 {
		 if(currentApp != null)
		 {
			 currentApp.mouseReleased(mouseX, mouseY, state);
		 }
		 super.mouseReleased(mouseX, mouseY, state);
	 }
	
	 @Override
	 protected void keyTyped(char character, int keycode)
	 {
		 
		  if(!animationPlayed) return;
		 
		  if(currentApp != null) currentApp.keyTyped(character, keycode);
		 
		  try {
			super.keyTyped(character, keycode);
		} catch (IOException e) {
			e.printStackTrace();
		}
		  
	      if (keycode == 1)
	      {
	         this.mc.displayGuiScreen((GuiScreen)null);
	         this.mc.setIngameFocus();
	      }
	 }
	 
	@Override
	public void drawScreen(int x, int y, float partialTicks)
	{				
		if(!animationPlayed)
		{
			if(MathsUtils.Abs(getWindowPosY() - animationPhonePosY) <= 0.2)
			{
				animationPlayed = true;
			}
			animationPhonePosY = (int) MathsUtils.Lerp(animationPhonePosY, getWindowPosY(), partialTicks * 0.2f);
			GuiUtils.drawImage(width-135, animationPhonePosY, new ResourceLocation("craftyourliferp","ui/phone/smartphone_home.png"), 130, 200, 0);
			return;
		}
		
		super.drawScreen(x, y, partialTicks);
		
		UIImage time_container = (UIImage) getComponent(1);
		String dateStr = getDateDisplay();
		GuiUtils.renderText(getDateDisplay(), time_container.getX2() - mc.fontRenderer.getStringWidth(dateStr)-5, time_container.getY()+mc.fontRenderer.FONT_HEIGHT / 2);
				
		if(currentApp != null) currentApp.drawScreen(x, y, partialTicks);
		
		if(displayedToast != null)
		{
			displayedToast.drawToast(x, y);
			if((System.currentTimeMillis() - displayedToast.displayedTimestamp) / 1000 > displayedToast.timeInSeconds)
			{
				displayedToast = null;
			}
		}
	}
	
	public void displayToast(String message, int displayTime)
	{
		displayedToast = ToastMessage.displayToast(this, message, displayTime);
	}
	
	public String getDateDisplay()
	{
		date.setTime(new Date());  
		int hours = date.get(Calendar.HOUR_OF_DAY);
		int minutes = date.get(Calendar.MINUTE);
		String display;
		
		if(hours < 10)
			display =  "0" + hours + ":" + minutes;
		else
			display =  hours + ":" + minutes;

		
		if(minutes < 10)
			display = hours + ":0" + minutes;
		
		return display;
	}
	
    public static PhoneUI getPhone()
    {
    	return instance;
    }
    
    public static void setPhone(PhoneUI phone)
    {
    	instance = phone;
    }
    
    public void setCallHandler(CallHandler ch)
    {
    	callHandler = ch;
    }
    
    public CallHandler getCallHandler()
    {
    	return callHandler;
    }
    
    @Override
    protected void mouseClicked(int x, int y, int mouseBtn)
    {
    	
    	if(!animationPlayed) return;
    	
    	if(currentApp != null)
    	{
    		currentApp.mouseClicked(x, y, mouseBtn);
    		
    		UIButton backBtn =  (UIButton) getComponent(2);
    		
    		if(backBtn.isClicked(x, y))
    		{
    			backBtn.callback.call();
    		}
    		
    		UIButton homeBtn =  (UIButton) getComponent(3);
    		
    		if(homeBtn.isClicked(x, y))
    		{
    			homeBtn.callback.call();
    		}
    		
    		UIButton appsBtn =  (UIButton) getComponent(4);
    		
    		if(appsBtn.isClicked(x, y))
    		{
    			appsBtn.callback.call();
    		}


    		
    		return;
    	}
    	
    	try {
			super.mouseClicked(x, y, mouseBtn);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void openApps()
    {
    	if(currentApp != null)
    	{
    		currentApp.openApps();
    	}
    }
    
    @Override
    public void updateScreen()
    {
    	if(currentApp != null)
    	{
    		currentApp.updateScreen();
    	}
    	super.updateScreen();
    }
    

	
	public class GuiApps extends GuiGridView 
	{


		@Override
		public void initGui()
		{
			this.elementSize = 20;
			this.spacing = 8;
			super.initGui();
		}
		
		@Override	
		public void initWindows()
		{
			this.setWindowSize(118, 153);
			this.setWindowPosition(parent.getWindowPosX()+7, parent.getWindowPosY()+30);
		}
		
		public void initializeComponent()
		{
			this.contentRect = new UIRect(new UIColor(0,0,0,0));
			this.viewport = new UIRect(new UIColor(0,0,0,0));

			
			this.scrollBarVertical = new UIScrollbarVertical(new UIColor(0,0,0,255),new UIColor(255,255,255,255));
			this.scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor(0,0,0,255),new UIColor(255,255,255,255));
			
			this.selectedScrollBar = scrollBarVertical;
			
			setScrollViewPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), getWindowHeight());
			
			updateScrollviewContents();
		}
		
		@Override
		public void registerChilds()
		{
			super.registerChilds();
		}
		
		
		@Override
		public void updateScrollviewContents()
		{
			contentRect.childs.clear();
			
			resetContainerLayout();
			
			PhoneUI phone = PhoneUI.getPhone();

			for(Apps app : phone.registeredApps)
			{
				addToContainer(new AppsButton(app, Type.ROUNDED, "",app.getIcoTexture(),null,true).setPosition(0, 0, this.elementSize, this.elementSize));
			}

		}
		
		
		@Override
		public GraphicObject addToContainer(GraphicObject object)
		{
			 if(object == null)  return null;
			 
			 if(contentRect.getChilds().size() == 0) 
			 {
				 lastElementposX = object.localPosX += lastElementposX + spacing - 2;
				 lastElementposY = object.localPosY += spacing;
				 contentRect.setHeight(contentRect.getHeight() + elementSize + spacing);
			 }
			 else
			 {
				 lastElementposX = object.localPosX += lastElementposX + spacing + elementSize;
				 object.localPosY = lastElementposY;
			 }
			 
			 if(lastElementposX + object.getWidth() > viewport.getWidth())
			 {
				 lastElementposY = object.localPosY += spacing + elementSize;
				 object.localPosX = lastElementposX = spacing - 2;
				 contentRect.setHeight(contentRect.getHeight() + elementSize + spacing);
			 }
			 		 
			 contentRect.addChild(object);
			 
			 return object;
		 }
		
	}
	
	public static class ToastMessage
	{
		
		private String message;
		
		private int timeInSeconds;
		
		private long displayedTimestamp;
		
		public UIRect rect = new UIRect(new UIColor("#424242"));;
		
		private ToastMessage()
		{
			
		}
		
		public static ToastMessage displayToast(GuiBase gui, String message, int timeInSeconds)
		{
			ToastMessage toast = new ToastMessage();
			toast.message = message;
			toast.timeInSeconds = timeInSeconds;
			toast.displayedTimestamp = System.currentTimeMillis();
			
			int width = (int)(Minecraft.getMinecraft().fontRenderer.getStringWidth(message) * 0.6f) + 2;
			
			toast.rect.setPosition(gui.getWindowPosX() + (gui.getWindowWidth() - width) / 2, gui.getWindowPosY() + gui.getWindowHeight() - 45, width, 15);
			
			return toast;
		}
		
		
		public void drawToast(int x, int y)
		{
			rect.draw(x, y);
			GuiUtils.renderCenteredText(message, rect.getX() + rect.getWidth() / 2, rect.getY() + 5, 0.6f);
		}
	}
	

}