package fr.innog.phone.web;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIDropdown;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.advancedui.guicomponents.UITextField;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketBitcoinPage;
import fr.innog.phone.Tor.WebPage;
import fr.innog.phone.web.page.BitcoinConverterPage;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class BitcoinConverterPageUI extends WebPageUI {

	private String dropdownPreviousElement;
	
	public BitcoinConverterPageUI() {
		super(new BitcoinConverterPage(Minecraft.getMinecraft().player), "https://www.bitcoinconvert.onion", "Bitcoin Converter", null);
	}
	
	@Override
	public void initializeComponent()
	{
		this.addComponent(new UIRect(new UIColor(84, 151, 214))).setZIndex(999).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), 20);
		
		

		
		super.initializeComponent();
	}
	
	@Override
	public void back()
	{
		super.back();

		WebPage page = (WebPage)parent;
		page.openPage(null);
	}
	
	public void updateScrollviewContents()
	{
		contentRect.childs.clear();
		super.updateScrollviewContents();
		
		this.addToContainer(new UIText("Convertir : ",new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0));
		spacing = 10;
		this.addToContainer(new UIDropdown(10,Arrays.asList(new String[] { "Euro -> Bitcoin", "Bitcoin -> Euro" }),new UIColor(84, 151, 214)).setPosition(0, 0, 100, 15));
		this.addToContainer(new UIText("Entrez une valeur : ",new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0));
		UITextField convertField = (UITextField)this.addToContainer(new UITextField(new UIRect(new UIColor(255,255,255)), 0.8f, UITextField.Type.TEXT).setTextColor(new UIColor(80,80,80)).setPosition(0, 0, 100, 15));
		spacing = 10;
		this.addToContainer(new UIText(" = ",new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0));
		UITextField resultField = (UITextField)this.addToContainer(new UITextField(new UIRect(new UIColor(255,255,255)), 0.8f, UITextField.Type.NUMBER).setTextColor(new UIColor(80,80,80)).setEnabled(false).setPosition(0, 0, 100, 15));
		spacing = 20;
		this.addToContainer(new UIButton(Type.SQUARE,"Convertir", new UIRect(new UIColor(84, 151, 214)), new UIRect(new UIColor(84, 151, 240)), new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				convert();
			}
			
		}).setPosition(0,0,100,15));
		
		spacing = 5;
		this.addToContainer(new UIText("§lCours du bitcoin : ",new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0));

		this.addToContainer(new BitcoinDetailsContainer().setPosition(0, 0, 100, 30));
		
		BitcoinConverterPage page = (BitcoinConverterPage) getWebPageData();
		
		spacing = 20;
		addToContainer(new UIText("§lVotre solde :",new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0));
		spacing = 5;
		addToContainer(new UIText("Euro : " + MinecraftUtils.getMoneyDisplay("%.2f",page.playerData.getPhoneData().userMoney) ,new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0));
		addToContainer(new UIText("BTC : " + MinecraftUtils.getMoneyDisplay("%f",page.playerData.getPhoneData().bitcoin),new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0));

		
		convertField.setContourColor(new UIColor(80,80,80));
		resultField.setContourColor(new UIColor(80,80,80));
	
		 contentRect.setHeight(contentRect.getHeight() + 30);
	}
	
	public void convert()
	{
		 UIDropdown convertDropdown =  (UIDropdown) contentRect.childs.get(1);
		 UITextField textField = (UITextField) contentRect.childs.get(3);

		float value;
		try
		{
			 value = convertValueToFloat();

		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		 
		if(convertDropdown.getSelectedElement().equalsIgnoreCase("Euro -> Bitcoin"))
		{
			ModCore.getPackethandler().sendToServer(PacketBitcoinPage.convertMoneyToBitcoin((byte)0, value));
		}
		else
		{
			ModCore.getPackethandler().sendToServer(PacketBitcoinPage.convertMoneyToBitcoin((byte)1, value));
		}
	}

	
	private float convertValueToFloat() throws Exception
	{
		 UITextField textField =  (UITextField) contentRect.childs.get(3);
		 return Float.parseFloat(textField.getText().replace(" ", "").replace(",", ".").replace("Euro", "").replace("BTC", ""));
	}
	
	 public GraphicObject addToContainer(GraphicObject object)
	 {
		 if(object == null)  return null;
		
		 
		 contentRect.addChild(object);
		 		 		 
		 object.localPosX = (contentRect.getWidth() - object.getWidth()) / 2;
		 object.localPosY = contentRect.getHeight() + 30;
		 
		 if(object.localPosY + object.getHeight() > contentRect.getHeight())
		 {
			 contentRect.setHeight(contentRect.getHeight() + object.getHeight() + spacing);
		 }
		 
		 return object;
	 }
	
	 
	 @Override
	 public void updateScreen()
	 {
		 super.updateScreen();

		 if(getGuiTicks() % 20 == 0)
		 {
			 UITextField textField = (UITextField) contentRect.childs.get(3);
			 
			 
			 UIDropdown convertDropdown =  (UIDropdown) contentRect.childs.get(1);
			 
			 if(!convertDropdown.getSelectedElement().equalsIgnoreCase(dropdownPreviousElement))
			 {
				 dropdownPreviousElement = convertDropdown.getSelectedElement();
				 textField.setText("");
				 textField = (UITextField) contentRect.childs.get(5);
				 textField.setText("");
			 }
	
			 if(!textField.isFocused() && !textField.getText().isEmpty())
			 {
				 if(convertDropdown.getSelectedElement().equalsIgnoreCase("Euro -> Bitcoin"))
				 {
					if(!textField.getText().contains("Euro"))
					{
						textField.setText(textField.getText() + " Euro");
					}
				 }
				 else
				 {
					if(!textField.getText().contains("BTC"))
					{
						textField.setText(textField.getText() + " BTC");
					}
				 }
			 }
			 
			 if(!textField.getText().isEmpty())
			 {
				 
				 float value;
				 try
				 {
					 value = Float.parseFloat(textField.getText().replace(" Euro", "").replace(" BTC", "").replace(",", "."));
				 }
				 catch(Exception e)
				 {
					 textField = (UITextField) contentRect.childs.get(5);
					 textField.setText("nombre à virgule demandé");
					 return;
				 }
				 
	
				 textField = (UITextField) contentRect.childs.get(5);
				 if(convertDropdown.getSelectedElement().equalsIgnoreCase("Euro -> Bitcoin"))
				 {
					 float bitcoinPrice = ((BitcoinConverterPage)getWebPageData()).getBitcoinInEuro();

					 bitcoinPrice = (float) Math.round(bitcoinPrice*100) / 100;
					 
					 float convertValue = value / bitcoinPrice;
					 
					 textField.setText(MinecraftUtils.getMoneyDisplay("%f",convertValue).replace(".", ",") + " BTC");

				 }
				 else
				 {
					 float bitcoinPrice = ((BitcoinConverterPage)getWebPageData()).getBitcoinInEuro();

					 bitcoinPrice = (float) Math.round(bitcoinPrice*100) / 100;
					 
					 float convertValue = value * bitcoinPrice;
					 textField.setText(MinecraftUtils.getMoneyDisplay("%f",convertValue).replace(".", ",")  + " Euro");
				 }

			 }
			 else
			 {
				 textField.setText("");
				 textField = (UITextField) contentRect.childs.get(5);
				 textField.setText("");
			 }
			 
			 UIText euroTxt = (UIText) contentRect.childs.get(10);
			 UIText bictoinTxt = (UIText) contentRect.childs.get(11);
			 
			 BitcoinConverterPage page = (BitcoinConverterPage)getWebPageData();
			 
			 euroTxt.setText("Euro : " + MinecraftUtils.getMoneyDisplay("%.2f",(float)page.playerData.getPhoneData().userMoney));
			 bictoinTxt.setText("BTC : " + MinecraftUtils.getMoneyDisplay("%f",(float)page.playerData.getPhoneData().bitcoin));
		 }
	 }
	 
	
	@Override
	public void drawScreen(int x, int y, float pt)
	{		
		super.drawScreen(x, y, pt);
		
		if(scrollBarVertical.isVisible())
		{
			getComponent(0).setWidth(getWindowWidth()-5);
		}
		else
		{
			getComponent(0).setWidth(getWindowWidth());
		}

		GraphicObject header = getComponent(0);
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 0, 999);
		
		GuiUtils.renderText("Convertisseur bitcoin", header.getX() + 5, header.getY() + 7,GuiUtils.gameColor,0.8f);
		
		GL11.glPopMatrix();
	}
	
	@Override
	public void onPageOpen()
	{
		PacketBitcoinPage page = new PacketBitcoinPage();
		page.action = 0;
		page.pageId = getWebPageData().pageId();
		ModCore.getPackethandler().sendToServer(page);
	}
	
	public void updatePageData()
	{
		
	}
	
	
	public class BitcoinDetailsContainer extends GraphicObject
	{
		
		
		public UIImage bitcoinIco;
		
		public BitcoinDetailsContainer()
		{
			bitcoinIco = new UIImage(new ResourceLocation("craftyourliferp","ui/phone/bitcoin.png"));
		}
		
		public GraphicObject setPosition(int x, int y, int width, int height)
		{
			super.setPosition(x, y, width, height);
			bitcoinIco.setPosition((x + (width - 15) / 2)-25, y + (height - 15) / 2, 15, 15);
			return this;
		}
		
		
		public void draw(int x, int y)
		{
			super.draw(x, y);
			bitcoinIco.draw(x, y);
			
			String displayText = " = " + ((BitcoinConverterPage)getWebPageData()).getBitcoinInEuro() + " €";
			GuiUtils.renderText(displayText, posX + 32, posY + 11, 5263440);
		}
	}
	

}