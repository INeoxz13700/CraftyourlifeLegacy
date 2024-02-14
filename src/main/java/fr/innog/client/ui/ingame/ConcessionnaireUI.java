package fr.innog.client.ui.ingame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.jme3.math.Vector3f;

import fr.dynamx.addons.basics.common.infos.FuelTankInfos;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.items.DynamXItem;
import fr.dynamx.common.items.vehicle.ItemCar;
import fr.dynamx.utils.client.DynamXRenderUtils;
import fr.innog.advancedui.gui.IGuiClickableElement;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.CallBackObject;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIDialogBox;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.data.ConcessionnaireItem;
import fr.innog.ui.remote.RemoteConcessionnaireUI;
import fr.innog.utils.TextureLocation;
import fr.innog.utils.VehicleUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ConcessionnaireUI extends GarageUI {

	
	protected VehicleData selectedVehicle;
	
	
	@Override
	public void initializeComponent() 
	{ 
		super.initializeComponent();
		this.selectedVehicle = null;
		
		RemoteConcessionnaireUI remoteUI = (RemoteConcessionnaireUI) this.remoteUI;
		
		getComponent(3).setVisible(false);
		getComponent(5).setVisible(false);
		getComponent(6).setVisible(false);
		getComponent(7).setVisible(false);
		
		if(mc.gameSettings.guiScale == 1)
		{
			this.setScrollViewPosition(getWindowPosX() + 5*2, getWindowPosY() + 20*2, 100*2, 125*2);


			UIText concessionnaireText = this.getComponent(1);
			
			concessionnaireText.setText("Concessionnaire " + remoteUI.getConcessionnaireName());
			
			UIButton buyButton = this.getComponent(2);
			
			CallBackObject buyCallback = new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					if(selectedVehicle != null)
					{
						UIText text = new UIText("Êtes-vous sûr d'acheter " + selectedVehicle.getVehicleName() + " pour " + selectedVehicle.currentModel.concessionnaireItem.getPrice() + "$ ?");
						text.setSize(1f);
						text.setTextCentered(true);
						
						UIButton yesBtn = new UIButton(Type.SQUARE, "Oui", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);
						
						UIButton noBtn = new UIButton(Type.SQUARE, "Non", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);

						
						UIDialogBox dialogBox = ConcessionnaireUI.this.displayDialogBox( 
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
						
						yesBtn.callback = new UIButton.CallBackObject()
						{
							@Override
							public void call()
							{
								ConcessionnaireUI.this.destroyDialogBox(dialogBox);
								remoteUI.executeMethod("buyVehicle", new Object[] { selectedVehicle.currentModel.concessionnaireItem.getId() });
							}
						};
						
						noBtn.callback = new UIButton.CallBackObject()
						{
							@Override
							public void call()
							{
								ConcessionnaireUI.this.destroyDialogBox(dialogBox);
							}
						};
					}
				}
			};
			
			buyButton.callback = buyCallback;

			buyButton.setDisplayText("Acheter");
			
			UIRect vehicleDisplayContainer = getComponent(4);
			
			UIText caractText = (UIText) this.addComponent(new UIText("Caractéristiques :", new UIColor(Color.gray), 1f).setTextCentered(true)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75*2) / 2, vehicleDisplayContainer.getY2()+5*2, 75*2,20*2);
			UIText seatsCountText = (UIText)this.addComponent(new UIText("Nombre de sièges :", new UIColor(Color.gray), 1f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75*2) / 2, caractText.getY2()+5*2, 75*2,20*2);
			UIText speedMaxTxt = (UIText) this.addComponent(new UIText("Vitesse max :", new UIColor(Color.gray), 1f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75*2) / 2, seatsCountText.getY2()+5*2, 77*2,20*2);
			UIText priceTxt = (UIText) this.addComponent(new UIText("Prix :", new UIColor(Color.gray), 1f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75*2) / 2, speedMaxTxt.getY2()+5*2, 75*2,20*2);
			UIText tankSizeTxt = (UIText) this.addComponent(new UIText("Taille du réservoir :", new UIColor(Color.gray), 1f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75*2) / 2, priceTxt.getY2()+5*2, 75*2,20*2);
			UIText fuelConsumptionTxt = (UIText) this.addComponent(new UIText("Consommation :", new UIColor(Color.gray), 1f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75*2) / 2, tankSizeTxt.getY2()+5*2, 75*2,20*2);

			
			caractText.setVisible(false);
			seatsCountText.setVisible(false);
			speedMaxTxt.setVisible(false);
			tankSizeTxt.setVisible(false);
			fuelConsumptionTxt.setVisible(false);
			priceTxt.setVisible(false);
			
			UIButton nextModelBtn = (UIButton) this.addComponent(new UIButton(Type.ROUNDED, "", TextureLocation.getTextureLocation("ui/right.png"), null, false, new CallBackObject()
			{
				@Override
				public void call()
				{
					VehicleStack stack = ConcessionnaireUI.this.selectedVehicle.currentModel.vehicleStack;
					DynamXItem itemVehicle = (DynamXItem)stack.getItem();
					
					stack.setMeta((byte)(stack.getMeta()+1));
					
					if(stack.getMeta() > itemVehicle.getMaxMeta()-1) stack.setMeta((byte)(itemVehicle.getMaxMeta()-1));
					
					priceTxt.setText("Prix : §9" + ConcessionnaireUI.this.selectedVehicle.currentModel.concessionnaireItem.getPrice() + " $");
				}
			}).setPosition(vehicleDisplayContainer.getX2()-10*2, vehicleDisplayContainer.getY() + (vehicleDisplayContainer.getHeight() - 10*2) / 2, 10*2, 10*2));
			nextModelBtn.setZIndex(200);
			nextModelBtn.setVisible(false);


			UIButton previousModelBtn = (UIButton) this.addComponent(new UIButton(Type.ROUNDED, "", TextureLocation.getTextureLocation("ui/left.png"), null, false, new CallBackObject()
			{
				@Override
				public void call()
				{
					VehicleStack stack = ConcessionnaireUI.this.selectedVehicle.currentModel.vehicleStack;
					stack.setMeta((byte)(stack.getMeta()-1));
				}
			}).setPosition(vehicleDisplayContainer.getX(), vehicleDisplayContainer.getY() + (vehicleDisplayContainer.getHeight() - 10*2) / 2, 10*2, 10*2));
			previousModelBtn.setZIndex(200);
			previousModelBtn.setVisible(false);
		}
		else
		{
			this.setScrollViewPosition(getWindowPosX() + 5, getWindowPosY() + 20, 100, 125);


			UIText concessionnaireText = this.getComponent(1);
			
			
			concessionnaireText.setText("Concessionnaire " + remoteUI.getConcessionnaireName());
			
			UIButton buyButton = this.getComponent(2);
			CallBackObject buyCallback = new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					if(selectedVehicle != null)
					{
						UIText text = new UIText("Êtes-vous sûr d'acheter " + selectedVehicle.getVehicleName() + " pour " + selectedVehicle.currentModel.concessionnaireItem.getPrice() + "$ ?");
						text.setSize(0.8f);
						text.setTextCentered(true);
						
						UIButton yesBtn = new UIButton(Type.SQUARE, "Oui", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);
						
						UIButton noBtn = new UIButton(Type.SQUARE, "Non", new UIRect(buttonColor), new UIRect(buttonColor_hover), null);

						
						UIDialogBox dialogBox = ConcessionnaireUI.this.displayDialogBox( 
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
						
						yesBtn.callback = new UIButton.CallBackObject()
						{
							@Override
							public void call()
							{
								ConcessionnaireUI.this.destroyDialogBox(dialogBox);
								remoteUI.executeMethod("buyVehicle", new Object[] { selectedVehicle.currentModel.concessionnaireItem.getId() });
							}
						};
						
						noBtn.callback = new UIButton.CallBackObject()
						{
							@Override
							public void call()
							{
								ConcessionnaireUI.this.destroyDialogBox(dialogBox);
							}
						};
					}
				}
			};
			buyButton.callback = buyCallback;

			buyButton.setDisplayText("Acheter");
			
			UIRect vehicleDisplayContainer = getComponent(4);
			
			UIText caractText = (UIText) this.addComponent(new UIText("Caractéristiques :", new UIColor(Color.gray), 0.6f).setTextCentered(true)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75) / 2, vehicleDisplayContainer.getY2()+5, 75,20);
			UIText seatsCountText = (UIText)this.addComponent(new UIText("Nombre de sièges :", new UIColor(Color.gray), 0.6f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75) / 2, caractText.getY2()+5, 75,20);
			UIText speedMaxTxt = (UIText) this.addComponent(new UIText("Vitesse max :", new UIColor(Color.gray), 0.6f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75) / 2, seatsCountText.getY2()+5, 77,20);
			UIText priceTxt = (UIText) this.addComponent(new UIText("Prix :", new UIColor(Color.gray), 0.6f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75) / 2, speedMaxTxt.getY2()+5, 75,20);
			UIText tankSizeTxt = (UIText) this.addComponent(new UIText("Taille du réservoir :", new UIColor(Color.gray), 0.6f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75) / 2, priceTxt.getY2()+5, 75,20);
			UIText fuelConsumptionTxt = (UIText) this.addComponent(new UIText("Consommation :", new UIColor(Color.gray), 0.6f)).setPosition(vehicleDisplayContainer.getX() + (vehicleDisplayContainer.getWidth() - 75) / 2, tankSizeTxt.getY2()+5, 75,20);

			
			caractText.setVisible(false);
			seatsCountText.setVisible(false);
			speedMaxTxt.setVisible(false);
			tankSizeTxt.setVisible(false);
			fuelConsumptionTxt.setVisible(false);
			priceTxt.setVisible(false);
			
			UIButton nextModelBtn = (UIButton) this.addComponent(new UIButton(Type.ROUNDED, "", TextureLocation.getTextureLocation("ui/right.png"), null, false, new CallBackObject()
			{
				@Override
				public void call()
				{
					ConcessionnaireUI.this.selectedVehicle.nextModel();
					
					priceTxt.setText("Prix : §9" + selectedVehicle.currentModel.concessionnaireItem.getPrice() + " $");
				}
			}).setPosition(vehicleDisplayContainer.getX2()-10, vehicleDisplayContainer.getY() + (vehicleDisplayContainer.getHeight() - 10) / 2, 10, 10));
			nextModelBtn.setZIndex(200);
			nextModelBtn.setVisible(false);


			UIButton previousModelBtn = (UIButton) this.addComponent(new UIButton(Type.ROUNDED, "", TextureLocation.getTextureLocation("ui/left.png"), null, false, new CallBackObject()
			{
				@Override
				public void call()
				{
					ConcessionnaireUI.this.selectedVehicle.previousModel();
					
					priceTxt.setText("Prix : §9" + selectedVehicle.currentModel.concessionnaireItem.getPrice() + " $");
				}
			}).setPosition(vehicleDisplayContainer.getX(), vehicleDisplayContainer.getY() + (vehicleDisplayContainer.getHeight() - 10) / 2, 10, 10));
			previousModelBtn.setZIndex(200);
			previousModelBtn.setVisible(false);
		}
		
		updateScrollviewContents(mc.gameSettings.guiScale);
		
	}
	

	
	
	@Override
	public void updateContentElements(int guiScale) {
		
		this.contentRect.childs.clear();
			
		RemoteConcessionnaireUI concessionnaireRemoteUI = (RemoteConcessionnaireUI) this.remoteUI;
	
		HashMap<Integer, ConcessionnaireItem> concessionnaireVehicles = (HashMap<Integer, ConcessionnaireItem>) concessionnaireRemoteUI.getCacheData().getData("ConcessionnaireItems").getData();
		
		List<VehicleData> diff = new ArrayList<>();
		
		for(ConcessionnaireItem item : concessionnaireVehicles.values())
		{		
			VehicleModel model = new VehicleModel(item, concessionnaireRemoteUI.getVehicleStackById(item.getVehicleItemId()));
			
			boolean isSame = false;
			for(VehicleData vehicle : diff)
			{
				if(vehicle.isSameVehicle(item))
				{
					vehicle.linkedVehicleData.add(model);
					isSame = true;
					break;
				}
			}
			
			if(isSame) continue;
			
			
			VehicleData data = new VehicleData(item, concessionnaireRemoteUI.getVehicleStackById(item.getVehicleItemId()));

			diff.add(data);
		
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
					
			this.contentRect.setHeight(this.contentRect.getHeight() + spacing);
		}		
	}
	
	@Override
	public GraphicObject addToContainer(GraphicObject object)
	{
		 if(!(object instanceof VehicleData)) return object;

		 super.addToContainer(object);
		 
		 
		 VehicleData vehicleData = (VehicleData)object;
		 
		 vehicleData.button.callback = new CallBackObject()
		 {
			 @Override
			 public void call()
			 {
				 ConcessionnaireUI.this.selectedVehicle = vehicleData;
				 
				 UIText caracText = getComponent(9);
				 UIText seatsCountText = getComponent(10);
				 UIText maxSpeedText = getComponent(11);
				 UIText priceText = getComponent(12);
				 UIText tankSizeText = getComponent(13);
				 UIText fuelConsumptionText = getComponent(14);

				 DynamXItem itemVehicle = (DynamXItem)selectedVehicle.currentModel.vehicleStack.getItem();

				 ModularVehicleInfo vehicleInfo = (ModularVehicleInfo) itemVehicle.getInfo();
				 
				 seatsCountText.setText("Nombre de sièges : §9" + VehicleUtils.getSeatsCount(vehicleInfo));
				 maxSpeedText.setText("Vitesse max : §9" + (vehicleInfo.getVehicleMaxSpeed() == Integer.MAX_VALUE ? "Infinie" : vehicleInfo.getVehicleMaxSpeed()) + " km/h");
				 priceText.setText("Prix : §9" + selectedVehicle.currentModel.concessionnaireItem.getPrice() + " $");
				 
				 caracText.setVisible(true);
				 seatsCountText.setVisible(true);
				 maxSpeedText.setVisible(true);
				 priceText.setVisible(true);
				 
				 FuelTankInfos fuelTankInfo = (FuelTankInfos)vehicleInfo.getSubPropertyByType(FuelTankInfos.class);
				 if(fuelTankInfo != null)
				 {
					 tankSizeText.setText("Taille du réservoir : §9" + fuelTankInfo.getTankSize() + " L");
					 tankSizeText.setVisible(true);
					 
					 fuelConsumptionText.setText("Consommation : §9" + fuelTankInfo.getFuelConsumption() + " L/km");
					 fuelConsumptionText.setVisible(true);
				 }
				 
				 getComponent(2).setEnabled(true);
				 
				 if(itemVehicle.getMaxMeta() > 0)
				 {
					 getComponent(15).setVisible(true);
					 getComponent(16).setVisible(true);
				 }

			 }
		 };
		 
		 return object;
	}
	
	@Override
	public void drawScreen(int x, int y, float partialTicks)
	{
		super.drawScreen(x, y, partialTicks);
	
		

		if(this.selectedVehicle != null)
		{
			Item item = selectedVehicle.currentModel.vehicleStack.getItem();
			if(item instanceof DynamXItem)
			{
				if(mc.gameSettings.guiScale == 1)
				{
					UIRect vehicleDisplayContainer = getComponent(4);
			        
					GlStateManager.pushMatrix();
					DynamXRenderUtils.glTranslate(new Vector3f(3.1f*10*2,2*10*2,100));
					GlStateManager.pushMatrix();

					DynamXRenderUtils.glTranslate(new Vector3f(vehicleDisplayContainer.getX() + 12*2 ,vehicleDisplayContainer.getY() + 15*2,0));
					GlStateManager.rotate(-getGuiTicks(), 0, 1, 0);
					GlStateManager.scale(-10*2, -10*2, -10*2);
					DynamXItem itemVehicle = (DynamXItem) item;
					DynamXRenderUtils.renderCar((ModularVehicleInfo)itemVehicle.getInfo(),(byte) selectedVehicle.currentModel.vehicleStack.getMeta());
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
					DynamXItem itemVehicle = (DynamXItem) item;
					DynamXRenderUtils.renderCar((ModularVehicleInfo)itemVehicle.getInfo(),(byte) selectedVehicle.currentModel.vehicleStack.getMeta());
					GlStateManager.popMatrix();

					GlStateManager.popMatrix();
				}
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
			
			UIDialogBox dialogBox = ConcessionnaireUI.this.displayDialogBox(okBtn,null,text,new UIRect(new UIColor(61, 157, 212)));		

			dialogBox.setPosition(getWindowPosX() + (getWindowWidth() - 150*2) / 2, getWindowPosY() + (getWindowHeight()-80*2)/2, 150*2, 80*2);
		
			okBtn.callback = new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					ConcessionnaireUI.this.destroyDialogBox(dialogBox);
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
			
			UIDialogBox dialogBox = ConcessionnaireUI.this.displayDialogBox(okBtn,null,text,new UIRect(new UIColor(61, 157, 212)));		

			dialogBox.setPosition(getWindowPosX() + (getWindowWidth() - 150) / 2, getWindowPosY() + (getWindowHeight()-80)/2, 150, 80);
		
			okBtn.callback = new UIButton.CallBackObject()
			{
				@Override
				public void call()
				{
					ConcessionnaireUI.this.destroyDialogBox(dialogBox);
				} 
			};
			
			okBtn.setTextScale(0.8f);
			okBtn.setY(dialogBox.getY()+dialogBox.getHeight()-15);
			okBtn.setHeight(15);
		}
		
	}
	
	class VehicleModel
	{
		public ConcessionnaireItem concessionnaireItem;
		
		public VehicleStack vehicleStack;
		
		public VehicleModel(ConcessionnaireItem item, VehicleStack stack)
		{
			this.concessionnaireItem = item;
			this.vehicleStack = stack;
		}
	}
	
	class VehicleData extends GraphicObject implements IGuiClickableElement
	{
						
		public UIButton button;
		

		public List<VehicleModel> linkedVehicleData = new ArrayList<VehicleModel>();
		
		public VehicleModel currentModel = null;
		
		private int currentModelIndex = 0;
		
		public VehicleData(ConcessionnaireItem item, VehicleStack stack)
		{
	
			VehicleModel vehicleModel = new VehicleModel(item, stack);

			currentModel = vehicleModel;
				
			button = new UIButton(Type.SQUARE, stack.getDisplayName(), new UIRect(buttonColor), new UIRect(buttonColor_hover), new CallBackObject()
			{
				@Override
				public void call()
				{
					ConcessionnaireUI.this.selectedVehicle = VehicleData.this;
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
		
			
			
			linkedVehicleData.add(vehicleModel);

		}
		
		public void nextModel()
		{
			currentModelIndex++;
			if(currentModelIndex > linkedVehicleData.size()-1)
			{
				currentModelIndex = linkedVehicleData.size()-1;
			}
			currentModel = linkedVehicleData.get(currentModelIndex);
		}
		
		public void previousModel()
		{
			currentModelIndex--;
			if(currentModelIndex < 0)
			{
				currentModelIndex = 0;
			}
			currentModel = linkedVehicleData.get(currentModelIndex);
		}
		
		public boolean isSameVehicle(ConcessionnaireItem item)
		{
			String vehicleId = item.getVehicleItemId();
			if(vehicleId.contains(":"))
			{
				vehicleId = vehicleId.split(":")[0];
			}
			
			String thisVehicleId = currentModel.concessionnaireItem.getVehicleItemId();
			if(thisVehicleId.contains(":"))
			{
				thisVehicleId = thisVehicleId.split(":")[0];
			}
			
			if(vehicleId.equals(thisVehicleId))
			{
				return true;
			}
			return false;
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
	
}
