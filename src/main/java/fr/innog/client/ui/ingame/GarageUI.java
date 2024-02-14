package fr.innog.client.ui.ingame;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.jme3.math.Vector3f;

import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.items.DynamXItem;
import fr.dynamx.common.items.ItemModularEntity;
import fr.dynamx.common.items.vehicle.ItemBoat;
import fr.dynamx.common.items.vehicle.ItemCar;
import fr.dynamx.common.items.vehicle.ItemHelicopter;
import fr.dynamx.utils.client.DynamXRenderUtils;
import fr.innog.advancedui.gui.Anchor;
import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.gui.IGuiClickableElement;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.CallBackObject;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIDialogBox;
import fr.innog.advancedui.guicomponents.UIDropdown;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.data.VehicleDatas;
import fr.innog.ui.remote.IRemoteUI;
import fr.innog.ui.remote.RemoteGarageUI;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.ui.remote.data.CacheData;
import fr.innog.ui.remote.data.RemoteMethodCallback;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.TextureLocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GarageUI extends GuiScrollableView implements IRemoteUI {

	protected RemoteUIProcessor remoteUI;
	
	protected VehicleData selectedVehicle;
	
	private UIDropdown selectedVehicleTypeDropdown;
	
	private UIButton spawnVehicleBtn;
	private UIButton transferVehicleBtn;

	
	private UIText vcoinsDisplay;
	
	
	protected UIColor buttonColor = new UIColor(0,135,212,255);
	protected UIColor buttonColor_hover = new UIColor(26, 146, 214,255);
	
	protected UIColor buttonColor2 = new UIColor(212, 0, 0,255);
	protected UIColor buttonColor2_hover = new UIColor(214, 26, 26,255);

	public GarageUI()
	{
		
	}
	
	@Override
	public void initGui()
	{
		super.initGui();
	}
	
	@Override	
	public void initWindows()
	{
		if(mc.gameSettings.guiScale == 1)
		{
			this.setWindowSize(200*2, 150*2);
			this.setWindowPosition((width - getWindowWidth()) / 2, (height - getWindowHeight()) / 2);
		}
		else
		{
			this.setWindowSize(200, 150);
			this.setWindowPosition((width - getWindowWidth()) / 2, (height - getWindowHeight()) / 2);
		}
		
	}
	
	@Override
	public void initializeComponent() 
	{ 
		this.selectedVehicle = null;

		spacing = 1;
		
		this.contentRect = new UIRect(new UIColor(255,255,255,0));
		this.viewport = new UIRect(new UIColor(232, 232, 232,255));
		this.viewport.contourColor = new UIColor(0,0,0,0);
		this.scrollBarVertical = new UIScrollbarVertical(new UIColor(232, 232, 232),buttonColor);
		this.scrollBarVertical.setButtonColor(new UIColor(80,80,80));
		this.scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor(0,0,0,255),new UIColor(255,255,255,255));
		this.scrollBarHorizontal.setButtonColor(new UIColor(80,80,80));
		this.scrollBarVertical.setEnabled(true);
		this.scrollBarHorizontal.setEnabled(false);

		selectedScrollBar = scrollBarVertical;
		
		
		String vcoinsDisplay = "";
		if(this.remoteUI.getCacheData().dataExist("VCoins"))
		{
			vcoinsDisplay = this.getVcoinsDisplay((Integer)this.remoteUI.getCacheData().getData("VCoins").getData());
		}
		
		
		if(mc.gameSettings.guiScale == 1)
		{
			this.addComponent(new UIRect(new UIColor(Color.white)).setAnchoredPosition(this, 0, 0, this.getWindowWidth(), this.getWindowHeight(), Anchor.TOP_LEFT));
			this.addComponent(new UIText("Votre garage", new UIColor(Color.gray), 1.5f).setAnchoredPosition(this, 5, 8, this.getWindowWidth(), this.getWindowHeight(), Anchor.TOP_MIDDLE));
			spawnVehicleBtn = (UIButton) this.addComponent(new UIButton(Type.SQUARE, "Sortir/Rentrer le véhicule", new UIRect(buttonColor), new UIRect(buttonColor_hover), null).setAnchoredPosition(this, -2 * 2, -5*2, 90*2, 12*2, Anchor.BOTTOM_RIGHT));
			
			transferVehicleBtn = (UIButton) this.addComponent(new UIButton(Type.SQUARE, "Transférer dans l'inventaire", new UIRect(buttonColor2), new UIRect(buttonColor2_hover), null).setPosition(spawnVehicleBtn.getX(), spawnVehicleBtn.getY() - 14*2, 90*2,12*2));
			
			this.addComponent(new UIRect(new UIColor(232, 232, 232,255)).setAnchoredPosition(this, -2*2, 20*2, 85*2, 50*2, Anchor.TOP_RIGHT));


			
			spawnVehicleBtn.setTextScale(1.2f);
			spawnVehicleBtn.setEnabled(false);
			
			transferVehicleBtn.setTextScale(1.2f);
			transferVehicleBtn.setEnabled(false);

			this.setScrollViewPosition(getWindowPosX() + 5*2, getWindowPosY() + 20*2, 100*2, 115*2);

			selectedVehicleTypeDropdown = (UIDropdown) this.addComponent(new UIDropdown(10*2, Arrays.asList(new String[] {"Voiture", "Bateau", "Helicoptère"}), buttonColor)).setPosition(viewport.getX(), viewport.getY2() + 2*2, viewport.getWidth(), 10*2);
			selectedVehicleTypeDropdown.setTextSize(1f);
			
			int textWidth = GuiUtils.getStringWidth(mc, vcoinsDisplay, 1f) + 10*2 + 8*2;
			UIRect vcoinsContainer = (UIRect) this.addComponent(new UIRect(buttonColor)).setAnchoredPosition(this, -2, 5, textWidth, 10*2, Anchor.TOP_RIGHT);
			UIImage vcoinsImg = (UIImage) this.addComponent(new UIImage(TextureLocation.getTextureLocation("ui/garage/vcoins.png"))).setPosition(vcoinsContainer.getX() + 2*2, vcoinsContainer.getY()+ (vcoinsContainer.getHeight() - 8*2) / 2, 8*2, 8*2);
			this.vcoinsDisplay = (UIText) this.addComponent(new UIText(vcoinsDisplay, new UIColor(Color.white), 1f).setPosition(vcoinsImg.getX2() + 4*2, vcoinsContainer.getY() + (vcoinsContainer.getHeight() - 4*2) / 2, 200*2, 8*2));

			
			
			updateScrollviewContents(mc.gameSettings.guiScale);
		}
		else
		{
			this.addComponent(new UIRect(new UIColor(Color.white)).setAnchoredPosition(this, 0, 0, this.getWindowWidth(), this.getWindowHeight(), Anchor.TOP_LEFT));
			this.addComponent(new UIText("Votre garage", new UIColor(Color.gray), 0.8f).setAnchoredPosition(this, 5, 8, this.getWindowWidth(), this.getWindowHeight(), Anchor.TOP_MIDDLE));
			spawnVehicleBtn = (UIButton) this.addComponent(new UIButton(Type.SQUARE, "Sortir/rentrer le véhicule", new UIRect(buttonColor), new UIRect(buttonColor_hover), null).setAnchoredPosition(this, -2, -5, 90, 12, Anchor.BOTTOM_RIGHT));
			
			transferVehicleBtn = (UIButton) this.addComponent(new UIButton(Type.SQUARE, "Transférer dans l'inventaire", new UIRect(buttonColor2), new UIRect(buttonColor2_hover), null).setPosition(spawnVehicleBtn.getX(), spawnVehicleBtn.getY() - 14, 90,12));
			
			this.addComponent(new UIRect(new UIColor(232, 232, 232,255)).setAnchoredPosition(this, -2, 20, 85, 50, Anchor.TOP_RIGHT));

			transferVehicleBtn.setTextScale(0.6f);
			transferVehicleBtn.setEnabled(false);
			
			spawnVehicleBtn.setTextScale(0.6f);
			spawnVehicleBtn.setEnabled(false);

			//this.setScrollViewPosition(getWindowPosX() + 5, getWindowPosY() + 20, 100, 125);
			this.setScrollViewPosition(getWindowPosX() + 5, getWindowPosY() + 20, 100, 115);

			selectedVehicleTypeDropdown = (UIDropdown) this.addComponent(new UIDropdown(10, Arrays.asList(new String[] {"Voiture", "Bateau", "Helicoptère"}), buttonColor)).setPosition(viewport.getX(), viewport.getY2() + 2, viewport.getWidth(), 10);
			selectedVehicleTypeDropdown.setTextSize(0.6f);
			
	
			
			int textWidth = GuiUtils.getStringWidth(mc, vcoinsDisplay, 0.6f) + 10 + 8;
			UIRect vcoinsContainer = (UIRect) this.addComponent(new UIRect(buttonColor)).setAnchoredPosition(this, -2, 5, textWidth, 10, Anchor.TOP_RIGHT);
			UIImage vcoinsImg = (UIImage) this.addComponent(new UIImage(TextureLocation.getTextureLocation("ui/garage/vcoins.png"))).setPosition(vcoinsContainer.getX() + 2, vcoinsContainer.getY()+ (vcoinsContainer.getHeight() - 8) / 2, 8, 8);
			this.vcoinsDisplay = (UIText)this.addComponent(new UIText(vcoinsDisplay, new UIColor(Color.white), 0.6f).setPosition(vcoinsImg.getX2() + 4, vcoinsContainer.getY( )+ (vcoinsContainer.getHeight() - 4) / 2, 200, 8));

			
			
			
			updateScrollviewContents(mc.gameSettings.guiScale);
		}
		
		spawnVehicleBtn.callback = new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				if(GarageUI.this.selectedVehicle != null)
				{
					remoteUI.executeMethod("spawnVehicle", new Object[] {getVehicleTypeClass(GarageUI.this.selectedVehicleTypeDropdown.getSelectedElement()).getName(), GarageUI.this.selectedVehicle.vehicleIndex});
				}
			}
		};
		
		transferVehicleBtn.callback = new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				remoteUI.executeMethodWithResult("transferToInventory", new RemoteMethodCallback()
				{

					@Override
					public void call(ActionResult result) {
						if(result == ActionResult.SUCCESS)
						{
							int index = GarageUI.this.selectedVehicle.vehicleIndex;
							
							HashMap<String, List<ItemStack>> vehicles = (HashMap<String, List<ItemStack>>) remoteUI.getCacheData().getData("Vehicles").getData();
							
							List<ItemStack> vehiclesList = vehicles.get(getVehicleType(GarageUI.this.selectedVehicleTypeDropdown.getSelectedElement()));
							
							vehiclesList.remove(index);
							
							updateScrollviewContents(mc.gameSettings.guiScale);
							
							resetSelectedVehicle();
						}
					}
					
				},
				new Object[] {getVehicleTypeClass(GarageUI.this.selectedVehicleTypeDropdown.getSelectedElement()).getName(), GarageUI.this.selectedVehicle.vehicleIndex});
			}
		};
		
		selectedVehicleTypeDropdown.onValueChange = new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				updateContentElements(mc.gameSettings.guiScale);
			}
		};
				
		
		
		
	}
	
	private void resetSelectedVehicle()
	{
		this.selectedVehicle = null;
		
		this.transferVehicleBtn.setEnabled(false);
		this.spawnVehicleBtn.setEnabled(false);
	}
	
	@Override
	protected void setScrollViewPosition(int x, int y, int width, int height)
	{
		this.viewport.setPosition(x, y, width, height);
		this.contentRect.setPosition(x, y, width, 0);
				
		this.scrollBarVertical.setPosition(viewport.getX2()+1, y-1, 4, height+2);
		this.scrollBarHorizontal.setPosition(viewport.getX()-1, viewport.getY2()+1, width+2, 6);
	}
	
	public void updateScrollviewContents(int guiScale) {
		updateContentElements(guiScale);
	}
	
	public String getVehicleType(String element)
	{
		switch(element)
		{
			case "Voiture":
				return ItemCar.class.getSimpleName();
			case "Bateau":
				return ItemBoat.class.getSimpleName();
			case "Helicoptère":
				return ItemHelicopter.class.getSimpleName();
		}
		
		return ItemCar.class.getSimpleName();
	}
	
	public Class<? extends ItemModularEntity> getVehicleTypeClass(String element)
	{
		switch(element)
		{
			case "Voiture":
				return ItemCar.class;
			case "Bateau":
				return ItemBoat.class;
			case "Helicoptère":
				return ItemHelicopter.class;
		}
		
		return ItemCar.class;
	}
	
	
	public void updateContentElements(int guiScale) {
		this.contentRect.setHeight(0);
		this.contentRect.childs.clear();
	
		HashMap<String, List<VehicleStack>> vehicles = (HashMap<String, List<VehicleStack>>) this.remoteUI.getCacheData().getData("Vehicles").getData();
		
		String vehicleTypeStr = getVehicleType(selectedVehicleTypeDropdown.getSelectedElement());
		List<VehicleStack> vehiclesIs = null;
		
		if(vehicles.containsKey(vehicleTypeStr))
		{
			vehiclesIs = vehicles.get(getVehicleType(selectedVehicleTypeDropdown.getSelectedElement()));
		}
		else
		{
			vehiclesIs = new ArrayList<>();
		}
			
		int i = 0; 
		for(VehicleStack vehicleIs : vehiclesIs)
		{
			VehicleData data = new VehicleData(vehicleIs, i);
			
			data.localPosX = 0;
			data.localPosY = 0;
			
			if(guiScale == 1)
			{
				addToContainer(data.setPosition(0, 0, 100*2, 18*2));
			}
			else
			{
				addToContainer(data.setPosition(0, 0, 100, 18));
			}
			i++; 
		}
		
		IPlayer playerData = ClientProxy.modClient.getLocalPlayerCapability();
		
		if(playerData.getVehicleDatas().getUnlockedSlot() < VehicleDatas.maxSlot)
		{
			UIButton extendedGarageBtn = new UIButton(Type.SQUARE, "+", new UIRect(buttonColor), new UIRect(buttonColor_hover), new CallBackObject()
			{
				@Override
				public void call()
				{
					UIButton yesBtn = null;
					UIButton noBtn = null;
					UIDialogBox dialogBox = null;
					if(mc.gameSettings.guiScale == 1)
					{
						UIText text = new UIText("Souhaitez-vous agrandir votre garage ? Tarif : " + ((RemoteGarageUI)remoteUI).getSlotPrice() + " VCoins.");
						text.setSize(1f);
						text.setTextCentered(true);
						
						yesBtn = new UIButton(Type.SQUARE, "Oui", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);
						
						noBtn = new UIButton(Type.SQUARE, "Non", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);

						
						dialogBox = GarageUI.this.displayDialogBox( 
								yesBtn, 
								noBtn, 
								text,
								new UIRect(new UIColor(61, 157, 212)));		
						
						dialogBox.setPosition(getWindowPosX() + (getWindowWidth() - 150*2) / 2, getWindowPosY() + (getWindowHeight()-80*2)/2, 150*2, 80*2);
					
						
						yesBtn.setTextScale(1f);
						yesBtn.setY(dialogBox.getY()+dialogBox.getHeight()-15*2);
						yesBtn.setHeight(15*2);
						noBtn.setTextScale(1f);
						noBtn.setHeight(15*2);
						noBtn.setY(dialogBox.getY()+dialogBox.getHeight()-15*2);
					}
					else
					{
						UIText text = new UIText("Souhaitez-vous agrandir votre garage ? Tarif : " + ((RemoteGarageUI)remoteUI).getSlotPrice() + " VCoins.");
						text.setSize(0.8f);
						text.setTextCentered(true);
						
						yesBtn = new UIButton(Type.SQUARE, "Oui", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);
						
						noBtn = new UIButton(Type.SQUARE, "Non", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);

						
						dialogBox = GarageUI.this.displayDialogBox( 
								yesBtn, 
								noBtn, 
								text,
								new UIRect(new UIColor(61, 157, 212)));		
						
						dialogBox.setPosition(getWindowPosX() + (getWindowWidth() - 150) / 2, getWindowPosY() + (getWindowHeight()-80)/2, 150, 80);
					
						
						yesBtn.setTextScale(0.8f);
						yesBtn.setY(dialogBox.getY()+dialogBox.getHeight()-15);
						yesBtn.setHeight(15);
						noBtn.setTextScale(0.8f);
						noBtn.setHeight(15);
						noBtn.setY(dialogBox.getY()+dialogBox.getHeight()-15);
					}
					
					final UIDialogBox theDialogBox = dialogBox;
					
					yesBtn.callback = new UIButton.CallBackObject()
					{
						@Override
						public void call()
						{
							GarageUI.this.destroyDialogBox(theDialogBox);
							remoteUI.executeMethodWithResult("extendGarage", new RemoteMethodCallback()
							{
								@Override
								public void call(ActionResult result) {
									if(result == ActionResult.SUCCESS)
									{
										RemoteGarageUI remoteUI = (RemoteGarageUI) GarageUI.this.remoteUI;
										CacheData cacheData = remoteUI.getCacheData();
																				
										int currentVcoins = (Integer) cacheData.getData("VCoins").getData();
										
										int unlockedSlot = (Integer) cacheData.getData("UnlockedSlot").getData();

										currentVcoins -= remoteUI.getSlotPrice();
										unlockedSlot += 1;
										
										cacheData.setCached("VCoins", new SyncStruct<Integer>(currentVcoins));
										cacheData.setCached("UnlockedSlot", new SyncStruct<Integer>(unlockedSlot));
										
										GarageUI.this.vcoinsDisplay.setText(GarageUI.this.getVcoinsDisplay(currentVcoins));
									}							
								}
								
							});
							
							
						}
					};
					
					noBtn.callback = new UIButton.CallBackObject()
					{
						@Override
						public void call()
						{
							GarageUI.this.destroyDialogBox(theDialogBox);
						}
					};
				}
			});
			
			if(guiScale == 1)
			{
				addToContainer(extendedGarageBtn.setPosition(0, 0, 100*2, 18*2));
			}
			else
			{
				addToContainer(extendedGarageBtn.setPosition(0, 0, 100, 18));
			}
		}
	}
	

	
	public void displayMessage(String message)
	{
		if(mc.gameSettings.guiScale == 1)
		{
			UIText text = new UIText(message);
			text.setSize(1f);
			text.setTextCentered(true);
			
			
			UIButton okBtn = new UIButton(Type.SQUARE, "Ok", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);
			
			UIDialogBox dialogBox = GarageUI.this.displayDialogBox(okBtn,null,text,new UIRect(new UIColor(61, 157, 212)));		

			dialogBox.setPosition(getWindowPosX() + (getWindowWidth() - 150*2) / 2, getWindowPosY() + (getWindowHeight()-80*2)/2, 150*2, 80*2);
		
			okBtn.callback = new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					GarageUI.this.destroyDialogBox(dialogBox);
				} 
			};
			
			okBtn.setTextScale(1f);
			okBtn.setY(dialogBox.getY()+dialogBox.getHeight()-15*2);
			okBtn.setHeight(15*2);
		}
		else
		{
			UIText text = new UIText(message);
			text.setSize(0.8f);
			text.setTextCentered(true);
			
			
			UIButton okBtn = new UIButton(Type.SQUARE, "Ok", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);
			
			UIDialogBox dialogBox = GarageUI.this.displayDialogBox(okBtn,null,text,new UIRect(new UIColor(61, 157, 212)));		

			dialogBox.setPosition(getWindowPosX() + (getWindowWidth() - 150) / 2, getWindowPosY() + (getWindowHeight()-80)/2, 150, 80);
		
			okBtn.callback = new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					GarageUI.this.destroyDialogBox(dialogBox);
				} 
			};
			
			okBtn.setTextScale(0.8f);
			okBtn.setY(dialogBox.getY()+dialogBox.getHeight()-15);
			okBtn.setHeight(15);
		}
		
	}
	
	@Override
	public GraphicObject addToContainer(GraphicObject object)
	{
		 if(object == null)  return null;
		
		 contentRect.addChild(object);
		 
		 super.positionElement(object);
		 
		 return object;
	}
	
	@Override
	public void drawScreen(int x, int y, float partialTicks)
	{
		super.drawScreen(x, y, partialTicks);
		

		if(this.selectedVehicle != null)
		{
			Item item = selectedVehicle.vehicleStack.getItem();
			DynamXItem<?> dynamxItem = null;
			if(item instanceof DynamXItem)
			{
				dynamxItem = (DynamXItem<?>) item;
			}
			
			if(dynamxItem == null) return;
			
			if(mc.gameSettings.guiScale == 1)
			{
				UIRect vehicleDisplayContainer = getComponent(4);
			        
				GlStateManager.pushMatrix();
				DynamXRenderUtils.glTranslate(new Vector3f(3.1f*10*2,2*10*2,100));
				GlStateManager.pushMatrix();

				DynamXRenderUtils.glTranslate(new Vector3f(vehicleDisplayContainer.getX() + 12*2 ,vehicleDisplayContainer.getY() + 15*2,0));
				GlStateManager.rotate(-getGuiTicks(), 0, 1, 0);
				GlStateManager.scale(-10*2, -10*2, -10*2);
				//ItemCar itemCar = (ItemCar) item;
				DynamXRenderUtils.renderCar((ModularVehicleInfo)dynamxItem.getInfo(),(byte) selectedVehicle.vehicleStack.getMeta());
				GlStateManager.popMatrix();

				GlStateManager.popMatrix();
				}
				else
				{
					UIRect vehicleDisplayContainer = getComponent(4);
			        
					GlStateManager.pushMatrix();
					DynamXRenderUtils.glTranslate(new Vector3f(3.1f*10,2*10,100));
					GlStateManager.pushMatrix();

					DynamXRenderUtils.glTranslate(new Vector3f(vehicleDisplayContainer.getX() + 12 ,vehicleDisplayContainer.getY() + 15,0));
					GlStateManager.rotate(-getGuiTicks(), 0, 1, 0);
					GlStateManager.scale(-10, -10, -10);
					//ItemCar itemCar = (ItemCar) item;
					DynamXRenderUtils.renderCar((ModularVehicleInfo)dynamxItem.getInfo(),(byte) selectedVehicle.vehicleStack.getMeta());
					GlStateManager.popMatrix();

					GlStateManager.popMatrix();
				}
			
		}
	}
	
	public String getVcoinsDisplay(int vcoinsCount)
	{
	    DecimalFormat df = new DecimalFormat("###,###,###");
	    return df.format(vcoinsCount);
	}
	
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		if(mc.gameSettings.guiScale == 1)
		{
			if(scrollBarVertical.isVisible())
			{
				spawnVehicleBtn.setAnchoredPosition(this, 2, -5*2, 90*2, 12*2, Anchor.BOTTOM_RIGHT);
				spawnVehicleBtn.setWidth(85*2);
				transferVehicleBtn.setPosition(spawnVehicleBtn.getX(), spawnVehicleBtn.getY() - 14*2, 85*2,12*2);
				transferVehicleBtn.setTextScale((float) (0.55*2F));
			}
			else
			{
				spawnVehicleBtn.setAnchoredPosition(this, -2*2, -5*2, 90*2, 12*2, Anchor.BOTTOM_RIGHT);
				transferVehicleBtn.setPosition(spawnVehicleBtn.getX(), spawnVehicleBtn.getY() - 14*2, 90*2,12*2);
			}
		}
		else
		{
			if(scrollBarVertical.isVisible())
			{
				spawnVehicleBtn.setAnchoredPosition(this, 2, -5, 90, 12, Anchor.BOTTOM_RIGHT);
				spawnVehicleBtn.setWidth(85);
				transferVehicleBtn.setPosition(spawnVehicleBtn.getX(), spawnVehicleBtn.getY() - 14, 85,12);
				transferVehicleBtn.setTextScale(0.55F);
			}
			else
			{
				spawnVehicleBtn.setAnchoredPosition(this, -2, -5, 90, 12, Anchor.BOTTOM_RIGHT);
				transferVehicleBtn.setPosition(spawnVehicleBtn.getX(), spawnVehicleBtn.getY() - 14, 90,12);
			}
		}
	}
	
	class VehicleData extends GraphicObject implements IGuiClickableElement
	{
						
		public UIButton button;
		
		public VehicleStack vehicleStack;
		
		public int vehicleIndex;
		
		public VehicleData(VehicleStack stack, int vehicleIndex)
		{
			this.vehicleStack = stack;
			this.vehicleIndex = vehicleIndex;
			
			button = new UIButton(Type.SQUARE, stack.getDisplayName(), new UIRect(buttonColor), new UIRect(buttonColor_hover), new CallBackObject()
			{
				@Override
				public void call()
				{
					GarageUI.this.selectedVehicle = VehicleData.this;
					spawnVehicleBtn.setEnabled(true);
					transferVehicleBtn.setEnabled(true);
				}
			});
			
			if(mc.gameSettings.guiScale == 1)
			{
				button.setTextScale(1f);
			}
			else
			{
				button.setTextScale(0.7f);
			}
		}
		
		public String getVehicleName()
		{
			return button.getText();
		}
		
		@Override
		public void draw(int x, int y)
		{
			super.draw(x, y);
			button.draw(x, y);
		}
		
		@Override
		public GraphicObject setPosition(int x, int y, int width, int height)
		{
			super.setPosition(x, y, width, height);
			button.setPosition(x, y, width, height);
			return this;
		}

		@Override
		public boolean onLeftClick(int arg0, int arg1) 
		{
			return button.onLeftClick(arg0, arg1);
		}

		@Override
		public boolean onRightClick(int arg0, int arg1) 
		{
			return false;
		}

		@Override
		public boolean onWheelClick(int arg0, int arg1)
		{
			return false;
		}
		
	}

	@Override
	public void setRemoteUI(RemoteUIProcessor remoteUI) 
	{
		this.remoteUI = remoteUI;		
	}


	
	
}
