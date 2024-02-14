package fr.innog.phone.web;

import java.text.DecimalFormat;
import java.time.LocalDate;

import org.lwjgl.opengl.GL11;

import fr.innog.advancedui.gui.IGuiClickableElement;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.data.MarketItem;
import fr.innog.network.packets.decrapted.PacketBMGPage;
import fr.innog.phone.Tor.WebPage;
import fr.innog.phone.web.page.BMGPage;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BMGPageUI extends WebPageUI
{

	public enum State
	{
		HOME,
		CART,
		TRANSACTION_MESSAGE
	};
	
	public State state = State.HOME;
		
	
	public BMGPageUI(String pageUrl, String pageTittle, String pageDescription) {
		super(new BMGPage(Minecraft.getMinecraft().player),pageUrl, pageTittle, pageDescription);
		spacing = 10;
	}


	@Override
	public void initializeComponent()
	{
		UIRect rect = (UIRect) this.addComponent(new UIRect(new UIColor(235, 198, 52))).setZIndex(999).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), 20);
		UIButton button = (UIButton) this.addComponent(new UIButton(Type.SQUARE,"shop",new ResourceLocation("craftyourliferp","ui/phone/shop_cart.png"),null,false, new UIButton.CallBackObject()
		{
			
			public void call()
			{
				state = State.CART;
				updateGuiState();
			}
			
		}).setZIndex(999).setPosition(getWindowPosX() + getWindowWidth() - 25, rect.getY() + (rect.getHeight() - 12) / 2, 12, 12));
		button.texture.setColor(new UIColor(0,0,0));

		
		super.initializeComponent();
	}
	
	@Override
	public void back()
	{
		super.back();
		
		if(state == State.HOME)
		{
			WebPage page = (WebPage)parent;
			page.openPage(null);
		}
		
		state = State.HOME;
		updateGuiState();
	}
	
	
	public void updateGuiState()
	{
		updateScrollviewContents();
	}
	
	@Override
	public void updateScreen()
	{
		if(getGuiTicks() % 20 == 0)
		{
			if(state == State.CART)
			{
				
				if(contentRect.getChilds().size() == 0)
				{
					super.updateScreen();
					return;
				}
				
				BMGPage page = ((BMGPage)this.getWebPageData());
				
				if(page.persistentData.getPhoneData().getShopData().shopCart.size() == 0) return;


				int size = 2 + page.persistentData.getPhoneData().getShopData().shopCart.size();

				UIText totalTxt = (UIText)contentRect.getChilds().get(size+1);
				UIText bitcoinTxt = (UIText)contentRect.getChilds().get(size+2);
				UIText moneyAfterTxt = (UIText)contentRect.getChilds().get(size+3);
				
				
				float price = page.getTotalPrice();
				
		        DecimalFormat df = new DecimalFormat("#.######");

				
				totalTxt.setText("Total (" + page.persistentData.getPhoneData().getShopData().shopCart.size() + " Articles) : §l" + df.format(price) + " BTC");

				bitcoinTxt.setText("Votre solde : §l" + df.format(page.persistentData.getPhoneData().bitcoin) + " BTC");
				moneyAfterTxt.setText("Après transaction : §l" + df.format(page.persistentData.getPhoneData().bitcoin - price) + " BTC");
			}
		}
		super.updateScreen();
	}
	
	public void updateScrollviewContents()
	{
		contentRect.childs.clear();
		super.updateScrollviewContents();
		spacing = 10;
		
		if(state == State.HOME)
		{
			BMGPage page = (BMGPage)getWebPageData();
			

			for(MarketItem item : page.getItemsAvaibleInMarket())
			{
				this.addToContainer(new ShopItem(item).setPosition(0, 0, 100, 70));
			}
		}
		else if(state == State.CART)
		{
			BMGPage page = (BMGPage)getWebPageData();

			addToContainer(new UIText("§lVotre panier",new UIColor(80,80,80),0.8f).setPosition(0, 0, 100, 0),5);
			
			IPlayer extendedPlayer = ((BMGPage)this.getWebPageData()).persistentData;
		
			if(extendedPlayer.getPhoneData().getShopData().shopCart.size() > 0)
			{
				spacing = 1;
				addToContainer(new UIText("Prix", new UIColor(150, 150, 150), 0.7f).setPosition(0, 0, 100, 0),getWindowWidth()-20);
				spacing = 5;
				for(MarketItem data : extendedPlayer.getPhoneData().getShopData().shopCart)
				{
					addToContainer(new CartShopItem(data,this).setPosition(0, 0, getWindowWidth(), 20), 1);
				}
				addToContainer(new UIRect(new UIColor("#dadfe8")).setPosition(0, 0, 114, 1),1);

		        DecimalFormat df = new DecimalFormat("#.######");

				float roundOff =page.getTotalPrice();

				addToContainer(new UIText("Total (" + extendedPlayer.getPhoneData().getShopData().shopCart.size() + " Articles) : §l" + df.format(roundOff) + " BTC", new UIColor(80,80,80), 0.6f).setPosition(0, 0, getWindowWidth(), 0), 2);
				
				
				addToContainer(new UIText("Votre solde : §l" + df.format(page.persistentData.getPhoneData().bitcoin) + " BTC", new UIColor(80,80,80), 0.6f).setPosition(0, 0, getWindowWidth(), 0), 2);
				addToContainer(new UIText("Après transaction : §l" + df.format((page.persistentData.getPhoneData().bitcoin - roundOff)) + " BTC", new UIColor(80,80,80), 0.6f).setPosition(0, 0, getWindowWidth(), 0), 2);


				contentRect.setHeight(contentRect.getHeight() + 10);
				addToContainer(new UIButton(Type.SQUARE,"Payer",new UIRect(new UIColor(235, 198, 52)), new UIRect(new UIColor(222, 186, 47)), new UIButton.CallBackObject()
				{
					
					public void call()
					{
						page.confirmTransaction();
					}
					
				}).setPosition(0, 0, 100,12));
			}
		}
		else if(state == State.TRANSACTION_MESSAGE)
		{
			BMGPage page = (BMGPage) getWebPageData();
			if(page.transactionSuccess)
			{

				addToContainer(new UIText("§2Transaction réussie", new UIColor(80,80,80), 1f).setTextCentered(true).setPosition(0, 0, getWindowWidth(), 0));
				spacing = 2;
				addToContainer(new UIText("Transaction ID : " + System.currentTimeMillis(), new UIColor(80,80,80), 0.7f).setTextCentered(true).setPosition(0, 0, getWindowWidth(), 0),0);
				LocalDate date = LocalDate.now();
				spacing = 10;
				addToContainer(new UIText("Date : " + date, new UIColor(80,80,80), 0.8f).setTextCentered(true).setPosition(0, 0, getWindowWidth(), 0),0);
				spacing = 2;
				addToContainer(new UIText("§cRendez-vous au marché noir", new UIColor(80,80,80), 0.8f).setTextCentered(true).setPosition(0, 0, getWindowWidth(), 0),0);
				addToContainer(new UIText("§cPour chercher vos colis", new UIColor(80,80,80), 0.8f).setTextCentered(true).setPosition(0, 0, getWindowWidth(), 0),0);

			}
			else
			{
				addToContainer(new UIText("§cTransaction Echouée", new UIColor(80,80,80), 1f).setTextCentered(true).setPosition(0, 0, getWindowWidth(), 0),0);
				spacing = 2;
				addToContainer(new UIText("§cVous n'avez pas l'argent nécessaire", new UIColor(80,80,80), 0.8f).setTextCentered(true).setPosition(0, 0, 100, 0));
			}
		}

	
		 contentRect.setHeight(contentRect.getHeight() + 30);
	}
	

	
	@Override
	public void onPageOpen()
	{
		PacketBMGPage page = new PacketBMGPage();
		page.action = 0;
		page.pageId = getWebPageData().pageId();
		ModCore.getPackethandler().sendToServer(page);
	}
	
	
	 public GraphicObject addToContainer(GraphicObject object, int localPosX)
	 {
		 if(object == null)  return null;
		
		 
		 contentRect.addChild(object);
		 		 		 
		 object.localPosX = localPosX;
		 object.localPosY = contentRect.getHeight() + 30;
		 
		 if(object.localPosY + object.getHeight() > contentRect.getHeight())
		 {
			 contentRect.setHeight(contentRect.getHeight() + object.getHeight() + spacing);
		 }
		 
		 return object;
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
	public void onScroll(int i)
	{
    	super.onScroll(i);
	}
	 
	@Override
	public void mouseClickMove(int x, int y, int state, long timeSinceLastClick)	 
	{
		super.mouseClickMove(x, y, state, timeSinceLastClick);
	}
	
	@Override
	public void mouseReleased(int mouseX, int mouseY, int state)
	{
		super.mouseReleased(mouseX, mouseY, state);
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
		
		GuiUtils.renderText("Empire market", header.getX() + 5, header.getY() + 7, 5263440,0.8f);

		GraphicObject shopCart = getComponent(1);
		
		IPlayer extendedPlayer = ((BMGPage)this.getWebPageData()).persistentData;

		
		GuiUtils.renderText(extendedPlayer.getPhoneData().getShopData().shopCart.size() + "", shopCart.getX()+8, shopCart.getY() + 8, 5263440, 0.9f);

		GL11.glPopMatrix();
	}
	
	
	public class ShopItem extends GraphicObject implements IGuiClickableElement
	{
		
		private MarketItem item;
						
		private UIButton addCartBtn;
		
		public ShopItem(MarketItem item)
		{
			this.item = item;
			
			addCartBtn = new UIButton(Type.SQUARE, "Ajouter au panier", new UIRect(new UIColor(235, 198, 52)), new UIRect(new UIColor(245, 210, 60)), new UIButton.CallBackObject()
			{
				public void call()
				{
					((BMGPage)getWebPageData()).addItemToCart(item);
					MinecraftUtils.getPlayerCapability(mc.player).getPhoneData().getShopData().addItemToCart(item);

				}
			});
			addCartBtn.setTextColor(new UIColor(80,80,80));
		}
		
		@Override
		public GraphicObject setPosition(int x, int y, int width, int height)
		{
			super.setPosition(x, y, width, height);
			addCartBtn.setPosition((x + (width - 80) / 2), y + height - 18, 80, 12);
			return this;
		}
		
		@Override
		public void draw(int x, int y)
		{
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 100);
			GuiUtils.renderItemStackIntoGUI(item.getItem(), posX + (width - 16) / 2 , posY);
			GL11.glPopMatrix();
			
			String bitcoinPrice = item.getPriceInBitcoin(((BMGPage)BMGPageUI.this.getWebPageData()).bitcoinPrice).toString();
			int posX = this.posX + width / 2;
			int pricePosX = posX - (GuiUtils.getStringWidth(mc, bitcoinPrice, 0.8f) / 2) + 5;
			
			GuiUtils.renderCenteredText(item.getItem().getDisplayName(), posX, posY + 20, 5263440);
			GL11.glColor3f(1f, 1f, 1f);
			GuiUtils.drawImage(pricePosX - 12 , posY + 32, new ResourceLocation("craftyourliferp","ui/phone/bitcoin.png"), 10, 10,0); 
			GuiUtils.renderText("" + item.getPriceInBitcoin(((BMGPage)BMGPageUI.this.getWebPageData()).bitcoinPrice), pricePosX, posY + 35, 5263440, 0.8f);
			addCartBtn.draw(x, y);
			super.draw(x, y);
		}
		


		@Override
		public boolean onRightClick(int x, int y) {
			return false;
		}

		@Override
		public boolean onLeftClick(int x, int y) {
			if(addCartBtn.isClicked(x, y))
			{
				addCartBtn.callback.call();
				return true;
			}
			return false;
		}

		@Override
		public boolean onWheelClick(int x, int y) {
			return false;
		}
	}
	
	public class CartShopItem extends GraphicObject implements IGuiClickableElement
	{
		
		private MarketItem cart;
								
		private UIButton deleteBtn;
		
		private UIButton increaseQuantityBtn;
		
		private UIButton decreaseQuantityBtn;
		
		public CartShopItem(MarketItem item, BMGPageUI page)
		{
			cart = item;
			increaseQuantityBtn = new UIButton("+", new UIColor(110,207,87), new UIColor(110,220,87),1f,new UIButton.CallBackObject()
			{
				
				public void call()
				{
					CartShopItem.this.cart.getItem().setCount(CartShopItem.this.cart.getItem().getCount()+1);
				}
				
			});
			
			decreaseQuantityBtn = new UIButton("-", new UIColor(214, 84, 92), new UIColor(230, 84, 92),1f,new UIButton.CallBackObject()
			{
				public void call()
				{
					if(CartShopItem.this.cart.getItem().getCount() == 1)
					{
						BMGPage page = (BMGPage) BMGPageUI.this.getWebPageData();
						page.removeItemFromCart(cart);
						BMGPageUI.this.updateGuiState();

						return;
					}
					CartShopItem.this.cart.getItem().setCount(CartShopItem.this.cart.getItem().getCount()-1);
				}
			});

		}
		
		public int getNumberCharacterInWidth(int width, float font_scale)
		{
			return Math.round((float)width / (float)(6 * font_scale));
		}
		
		@Override
		public GraphicObject setPosition(int x, int y, int width, int height)
		{
			super.setPosition(x, y, width, height);
			int textWidth = (int) (Minecraft.getMinecraft().fontRenderer.getStringWidth("Qté: " + (cart.getQuantity())) * 0.7);
			increaseQuantityBtn.setPosition(x + 20 + textWidth + 2, y + 14);
			decreaseQuantityBtn.setPosition(increaseQuantityBtn.getX() + 8, y + 14);
			return this;
		}
		
		@Override
		public void draw(int x, int y)
		{
			new UIRect(new UIColor(218, 223, 232)).setPosition(posX, posY, 114, 1).draw(x, y);
		    
			GL11.glPushMatrix();
			GL11.glTranslatef(0, 0, 100);
			GL11.glColor4f(1f, 1f, 1f, 1f);
			GuiUtils.renderItemStackIntoGUI(cart.getItem(), posX + 1 , posY + 5);
			GL11.glPopMatrix();
			
			int displayNamePosX = posX + 20;
			int displayNameWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(cart.getItem().getDisplayName());
			
	        DecimalFormat df = new DecimalFormat("#.######");

			
			float price = cart.getPriceQuantityInBitcoin(((BMGPage)BMGPageUI.this.getWebPageData()).bitcoinPrice).floatValue();
			
			String displayText = "" + df.format(price);
			
			
			int pricePosX = posX + width - (15 + (int) (Minecraft.getMinecraft().fontRenderer.getStringWidth(displayText) * 0.8f));
			
			if(displayNamePosX + displayNameWidth >= posX + width - 18)
			{
				int width = pricePosX - displayNamePosX;
				int wordCount = getNumberCharacterInWidth(width,0.9f);
				GuiUtils.renderText(cart.getItem().getDisplayName().substring(0, wordCount) + "...", posX + 20, posY + 6, 5343725,0.9f);
			}
			else
			{
				GuiUtils.renderText(cart.getItem().getDisplayName(), posX + 20, posY + 6, 5343725,0.9f);
			}
			
			

		
			GuiUtils.renderText(displayText, pricePosX, posY + 14, 1973790,0.7f); 
			GuiUtils.renderText("Qté: " + (cart.getQuantity()), posX + 20, posY + 15, 5263440,0.7f); 

			
			GL11.glColor4f(1f, 1f, 1f, 1f);
			
			GuiUtils.drawImage(posX + width - 17, posY + 11, new ResourceLocation("craftyourliferp","ui/phone/bitcoin.png"), 10, 10, 0);
			
			increaseQuantityBtn.draw(x, y);
			decreaseQuantityBtn.draw(x, y);
		
			super.draw(x, y);
		}

		@Override
		public boolean onRightClick(int x, int y) {
			return false;
		}

		@Override
		public boolean onLeftClick(int x, int y) {
			if(increaseQuantityBtn.isClicked(x, y))
			{
				increaseQuantityBtn.callback.call();
				return true;
			}
			else if(decreaseQuantityBtn.isClicked(x, y))
			{
				decreaseQuantityBtn.callback.call();
				return true;
			}
			return false;
		}

		@Override
		public boolean onWheelClick(int x, int y) {
			return false;
		}
	}



}