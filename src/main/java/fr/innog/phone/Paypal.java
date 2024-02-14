package fr.innog.phone;

import java.io.IOException;
import java.util.HashMap;

import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.gui.IGuiClickableElement;
import fr.innog.advancedui.gui.IGuiKeytypeElement;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.guicomponents.UIProgressBar;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.advancedui.guicomponents.UITextField;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketPaypal;
import fr.innog.utils.DataUtils;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Paypal extends Apps 
{
	
	public HashMap<Integer,GraphicObject> errorComponents = new HashMap<>();
	public HashMap<Integer,UIColor> backupColor = new HashMap<>();
	
	private IPlayer extendedPlayer;
			
	public PaypalApps currentPage;
	
	public static String phoneNumber = "";
	
	private ScrollView scrollView;
	
	public Paypal(String name, ResourceLocation ico, PhoneUI phone) 
	{
		super(name, ico, phone);
		
		extendedPlayer = MinecraftUtils.getPlayerCapability(Minecraft.getMinecraft().player);
	}
	
	@Override
	public void initializeComponent()
	{
		super.initializeComponent();
		
		addComponent(new UIRect(currentPage instanceof PaypalAccount ? new UIColor("#F5F7FA") : new UIColor(255,255,255)).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), getWindowHeight()));

		if(currentPage == null)
		{
			addComponent(new UIRect(new UIColor(40,68,209)).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), 5));
			addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/paypal-ico.png")).setPosition(getWindowPosX() + (getWindowWidth() - 30) / 2, getWindowPosY() + 10, 30, 30));
	
			addComponent(new UIText("Application de paiement sécurisé",new UIColor("#3d3e40"),0.8f).setTextCentered(true).setPosition(getWindowPosX() + 13, getWindowPosY() + 60,95,0));
			
			addComponent(new UIButton(UIButton.Type.SQUARE,"Connexion", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
			{
				
				@Override
				public void call()
				{
					currentPage = new PaypalConnexion((byte)0);
					updateGuiState();
				}
				
			}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 90, 80, 15).setZIndex(900));
	
			addComponent(new UIButton(UIButton.Type.SQUARE, "Ouvrir un compte", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
			{
				
				@Override
				public void call()
				{
					currentPage = new PaypalRegistering((byte)0);
					updateGuiState();
				}
				
			}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 110, 80, 15).setZIndex(900));
		}
		
	}
	
	@Override
	public void initGui()
	{
		ModCore.getPackethandler().sendToServer(PacketPaypal.syncPaypalData(Minecraft.getMinecraft().player));
		super.initGui();
		if(currentPage != null)
		{
			currentPage.init();
			backupColor.clear();
			errorComponents.clear();
		}
	}
	
	@Override	
	public void initWindows()
	{
		PhoneUI.GuiApps guiApps = (PhoneUI.GuiApps) phone.getChild(0);
		this.setWindowSize(guiApps.getWindowWidth()+3, guiApps.getWindowHeight()+2);
		this.setWindowPosition(guiApps.getWindowPosX()-2, guiApps.getWindowPosY()-1);
	}
	
	@Override
	public void registerChilds()
	{
		scrollView = (ScrollView) addChild(new ScrollView());
		scrollView.setVisible(false);
		super.registerChilds();
	}


	@Override
    public void mouseClicked(int x, int y, int mouseBtn)
    {
		super.mouseClicked(x, y, mouseBtn);
    }
	
	@Override
	public void mouseClickMove(int x, int y, int buttonId, long timeSinceLastClick) {
	}
	

	@Override
	public void onGuiClosed() 
	{
	
	}
	

	@Override
	public void updateScreen() 
	{
		super.updateScreen();
		if(getGuiTicks() % 10 == 0)
		{
			if(currentPage != null)
			{
				currentPage.updateForm();
			}
			
		}
	}

	@Override
	public void keyTyped(char par1, int par2) {
		super.keyTyped(par1, par2);
	}

	
	public void updateGuiState()
	{
		initGui();
	}

	@Override
	public void back() 
	{
		if(currentPage != null)
		{
			if(currentPage instanceof PaypalAccount)
			{
				PaypalAccount paypalGui = (PaypalAccount) currentPage;
				if(paypalGui.guiType == 0)
				{
					return;
				}
				else if(paypalGui.guiType > 0)
				{
					currentPage = new PaypalConnexion((byte)0);
				}
			}
			else if(currentPage instanceof PaypalRegistering)
			{
				PaypalRegistering paypalGui = (PaypalRegistering) currentPage;
				if(paypalGui.registeringStep == 0)
				{
					currentPage = null;
				}
				else if(paypalGui.registeringStep > 0)
				{
					currentPage = new PaypalRegistering((byte)(paypalGui.registeringStep-1));
				}
			}
			else if(currentPage instanceof PaypalConnexion)
			{
				PaypalConnexion paypalGui = (PaypalConnexion) currentPage;
				if(paypalGui.guiType == 0)
				{
					currentPage = null;
				}
				else if(paypalGui.guiType > 0)
				{
					currentPage = new PaypalConnexion((byte)(paypalGui.guiType-1));
				}
			}
		}
		else
		{
			phone.currentApp = null;
		}
		updateGuiState();

	}
	
	public void setPage(int guiId)
	{
		if(guiId == 0)
		{
			currentPage = null;
		}
		else if(guiId == 1)
		{
			currentPage = new PaypalRegistering((byte) 1);		
		}
		else if(guiId == 2)
		{
			currentPage = new PaypalRegistering((byte) 2);		
		}
		else if(guiId == 3)
		{
			currentPage = new PaypalAccount((byte)0);
		}
		else if(guiId == 4)
		{
			currentPage = new PaypalConnexion((byte)3);

		}
		else if(guiId == 5)
		{
			currentPage = new PaypalConnexion((byte)2);
		}
		else if(guiId == 6)
		{
			currentPage = new PaypalConnexion((byte)3);
		}
		
		updateGuiState();
	}

	public void clearErrors()
	{
		for(GraphicObject errorComponent : errorComponents.values())
		{
			removeComponent(errorComponent);
		}
		
		for(Integer backupComponentIndex : backupColor.keySet())
		{
			GraphicObject object = getComponent(backupComponentIndex);
			object.setColor(backupColor.get(backupComponentIndex));
			setComponent(backupComponentIndex, object);
		}
		
		errorComponents.clear();
		backupColor.clear();
	}
	
	public void clearError(int formIndex)
	{
		removeComponent(errorComponents.get(formIndex));
		errorComponents.remove(formIndex);
		
		GraphicObject object = getComponent(formIndex);
		object.setColor(backupColor.get(formIndex));
		setComponent(formIndex, object);
		
	}
	
	public void displayError(byte errorType)
	{
		if(errorType == 0)
		{
			if(currentPage instanceof PaypalRegistering)
			{
				displayError("Numéro de téléphone invalide",8);
			}
			else
			{
				displayError("Numéro de téléphone invalide",8);
			}
		}
		else if(errorType == 1)
		{
			displayError("Code invalide",7);
		}
		else if(errorType == 3)
		{
			if(currentPage instanceof PaypalRegistering)
			{
				displayError("Mots de passe de 6 caractères min",8);
			}
			else
			{
				displayError("Mots de passe de 6 caractères min",5);
			}
		}
		else if(errorType == 4)
		{
			if(currentPage instanceof PaypalRegistering)
			{
				displayError("Mots de passe non identique",11);
			}
			else
			{
				displayError("Mots de passe non identique",8);
			}
		}
		else if(errorType == 5)
		{
			displayError("",5);
			displayError("Identifiants incorrect",8);
		}
		else if(errorType == 6)
		{
			displayError("Compte introuvable",7);
		}
		else if(errorType == 7)
		{
			displayError("Code incorrect",6);
		}
		else if(errorType == 8)
		{
			phone.displayToast("Utilisateur introuvable", 5);
		}
		else if(errorType == 9)
		{
			phone.displayToast("La somme doit être > 0", 5);
		}
		else if(errorType == 10)
		{
			phone.displayToast("La limite d'envoie est de 100 milliards", 5);
		}
		else if(errorType == 11)
		{
			displayError("Vous avez déjà un compte",8);
		}
		else if(errorType == 12)
		{
			phone.displayToast("Vous n'avez pas suffisament d'argent",8);
		}
		else
		{
			phone.displayToast("Une erreur s'est produite", 5);
		}
	}
	
	
	public void displayError(String message, int formIndex)
	{
		GraphicObject object = getComponent(formIndex);
	

		if(errorComponents.containsKey(formIndex))
		{
			return;
		}
		
		
		backupColor.put(formIndex,object.color);
		
		
		object.setColor(new UIColor("#fc1c14"));
		UIText text = (UIText) addComponent(new UIText(message,new UIColor("#fc1c14"),0.6f).setTextCentered(true).setPosition(getWindowPosX() + (getWindowWidth() - 120) / 2, object.getY()+5, 120 ,0));
		errorComponents.put(formIndex,text);
	}
	
	
	@Override
	public void openApps() {
		
	}
	
	public class PaypalRegistering implements PaypalApps
	{
		public byte registeringStep = 0;
		
		public PaypalRegistering(byte registeringStep)
		{
			this.registeringStep = registeringStep;
		}
		
		@Override
		public void init()
		{
			
			addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/back.png")).setPosition(getWindowPosX() + 5, getWindowPosY() + 5, 15, 15));
			addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/paypal-ico.png")).setPosition(getWindowPosX() + (getWindowWidth() - 20) / 2, getWindowPosY() + 10, 20, 20));

			addComponent(new UIProgressBar(new UIRect(new UIColor("#C1C1C1")),new UIRect(new UIColor("#53e061"))).setValue(0.25f).setPosition(getWindowPosX(), getWindowPosY()+35, getWindowWidth(), 2));

			if(registeringStep == 0)
			{
				addComponent(new UIText("Quel est votre numéro de mobile ?",new UIColor("#242624"),0.8f).setPosition(getWindowPosX() + 6, getWindowPosY() + 55,100,0));
				addComponent(new UIText("Nous devrons le confirmer en vous envoyant un SMS",new UIColor("#8d8f8d"),0.7f).setPosition(getWindowPosX() + 6, getWindowPosY() + 75,100,0));
				
				addComponent(new UIText("Numéro de téléphone",new UIColor("#009CDE"),0.7f).setPosition(getWindowPosX() + 10, getWindowPosY() + 90,100 ,0));
	
				UITextField numberField = (UITextField)addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.9f,UITextField.Type.NUMBER).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 100) / 2, getWindowPosY() + 100,100,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 100) / 2, getWindowPosY() + 114,100,1));
				
				addComponent(new UIButton(UIButton.Type.SQUARE,"Valider", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						phoneNumber = numberField.getText();
						ModCore.getPackethandler().sendToServer(PacketPaypal.startRegistration(numberField.getText()));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 135, 80, 15).setZIndex(900));
			}
			else if(registeringStep == 1)
			{
				((UIProgressBar)getComponent(3)).setValue(0.5f);
				
				addComponent(new UIText("Entrez le code",new UIColor("#242624"),1f).setTextCentered(true).setPosition(getWindowPosX() + (getWindowWidth() - 100) / 2, getWindowPosY() + 50,100,0));
				addComponent(new UIText("Nous vous avons envoyé un code à 6 chiffres",new UIColor(80,80,80),0.8f).setTextCentered(true).setPosition(getWindowPosX() + 5, getWindowPosY() + 70,110,0));
				UITextField textField = (UITextField) addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),1f,UITextField.Type.NUMBER).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 40) / 2, getWindowPosY() + 92,50,15));
				textField.setMaxStringLength(6);
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 50) / 2, getWindowPosY() + 104,50,1));

				addComponent(new UIButton(UIButton.Type.SQUARE,"Renvoyer le code", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.startRegistration(phoneNumber));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 117, 80, 15).setZIndex(900));
				
				addComponent(new UIButton(UIButton.Type.SQUARE,"Suivant", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.sendActivationCode(textField.getText()));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 134, 80, 15).setZIndex(900));
			}
			else if(registeringStep == 2)
			{
				((UIProgressBar)getComponent(3)).setValue(0.75f);
				
				addComponent(new UIText("Informations de connexion",new UIColor("#242624"),0.8f).setPosition(getWindowPosX() + 5, getWindowPosY() + 45,125,0));
				addComponent(new UIText("Gardez-les en sécurité.",new UIColor(80,80,80),0.6f).setPosition(getWindowPosX() + 5, getWindowPosY() + 56,100,0));
				
				addComponent(new UIText("Mots de passe",new UIColor("#0086D3"),0.55f).setPosition(getWindowPosX() + 5, getWindowPosY() + 68,100,0));
				
				UITextField passwordTextField = (UITextField)addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.8f,UITextField.Type.PASSWORD).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 76,110,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 89,110,1));
				
				addComponent(new UIText("Confirmation",new UIColor("#0086D3"),0.55f).setPosition(getWindowPosX() + 5, getWindowPosY() + 105,100,0));
				
				UITextField confirmationTextField = (UITextField)addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.8f,UITextField.Type.PASSWORD).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 115,110,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 128,110,1));

				addComponent(new UIButton(UIButton.Type.SQUARE, "Suivant", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.finalizeRegistering(passwordTextField.getText(), confirmationTextField.getText()));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 60) / 2, getWindowPosY() + 140, 60, 10).setZIndex(900));
			}
		}

		@Override
		public void updateForm() 
		{
			if(registeringStep == 0)
			{
				UITextField textField = (UITextField)getComponent(7);
				
				if(textField.isFocused() && textField.getText().length() != 10)
				{
					displayError("Entrez un numéro valide",8);
				}
				else if(textField.isFocused())
				{
					clearErrors();
				}
				
			}
			else if(registeringStep == 1)
			{
				UITextField textField = (UITextField)getComponent(6);
				
				if(textField.isFocused() && textField.getText().length() != 6)
				{
					displayError("Le code doit contenir 6 chiffres",7);
				}
				else if(textField.isFocused())
				{
					clearErrors();
				}
			}
			else if(registeringStep == 2)
			{
				UITextField textFieldPassword = (UITextField)getComponent(7);
				UITextField textFieldPasswordConfirmation = (UITextField)getComponent(10);

				if(textFieldPassword.isFocused() && textFieldPassword.getText().length() < 6)
				{
					displayError("Mots de passe à 6 caractères au moins",8);
				}
				else if(textFieldPassword.isFocused())
				{
					clearError(8);
				}
				
				if(textFieldPasswordConfirmation.isFocused() && !textFieldPassword.getText().equals(textFieldPasswordConfirmation.getText()))
				{
					displayError("Mots de passe non identique",11);
				}
				else if(textFieldPasswordConfirmation.isFocused())
				{
					clearError(11);
				}
			}
		}
		
	}
	
	public class ScrollView extends GuiScrollableView
	{
		
		@Override
		public void initGui()
		{
			super.initGui();
		}
		

		@Override	
		public void initWindows()
		{
			this.setWindowPosition(parent.getWindowPosX(), parent.getWindowPosY());
			this.setWindowSize(parent.getWindowWidth(), parent.getWindowHeight());
		}
		
		@Override
		public void initializeComponent() 
		{ 
			this.contentRect = new UIRect(new UIColor(0,0,0,0));
			this.viewport = new UIRect(new UIColor(0,0,0,0));
			
			this.scrollBarVertical = new UIScrollbarVertical(new UIColor("#47b4c4"),new UIColor("#144f58")).setHoverColor(new UIColor("#196975"));
			this.scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor("#47b4c4"),new UIColor("#144f58")).setHoverColor(new UIColor("#196975"));
			
			this.selectedScrollBar = scrollBarVertical;
					
			this.setScrollViewPosition(getWindowPosX(), getWindowPosY() + 24, getWindowWidth(), getWindowHeight()-22);
			parameterVerticalScrollbar(getWindowPosX()+getWindowWidth()-6,getWindowPosY()+23,6,getWindowHeight()-22);

		}
	}
	
	public class PaypalConnexion implements PaypalApps
	{
		
		public byte guiType = 0;
		
		public PaypalConnexion(byte guiType)
		{
			this.guiType = guiType;
		}
		
		@Override
		public void init()
		{
			
			addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/back.png")).setPosition(getWindowPosX() + 5, getWindowPosY() + 5, 15, 15));
			addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/paypal-ico.png")).setPosition(getWindowPosX() + (getWindowWidth() - 20) / 2, getWindowPosY() + 10, 20, 20));

			if(guiType == 0)
			{								
				addComponent(new UIText("Numéro de telephone",new UIColor("#0086D3"),0.55f).setPosition(getWindowPosX() + 5, getWindowPosY() + 50,100,0));
				
				UITextField numberTextField = (UITextField) addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.8f,UITextField.Type.TEXT).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 58,110,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 71,110,1));
				
				addComponent(new UIText("Mots de passe",new UIColor("#0086D3"),0.55f).setPosition(getWindowPosX() + 5, getWindowPosY() + 87,100, 0));
				
				UITextField passwordTextField = (UITextField) addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.8f,UITextField.Type.PASSWORD).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 97,75,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 110,110,1));

				
				addComponent(new UIButton("Vous n'avez pas encore de compte ?", new UIColor("#0086D3"),new UIColor(0, 134, 200),0.6f,new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						currentPage = new PaypalRegistering((byte)0);
						updateGuiState();
					}
					
				}).setPosition(getWindowPosX() + 5, getWindowPosY() + 126, 120, 5).setZIndex(900));
				
				addComponent(new UIRect(new UIColor(228, 232, 235)).setPosition(getWindowPosX() + getWindowWidth() - 34, getWindowPosY() + 99,1,10));
				addComponent(new UIButton("Oublié ?", new UIColor("#0086D3"),new UIColor(0, 134, 200),0.6f,new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						currentPage = new PaypalConnexion((byte)1);
						updateGuiState();
					}
					
				}).setPosition(getWindowPosX() + getWindowWidth() - 30, getWindowPosY() + 101, 23, 15).setZIndex(900));
				
				
				addComponent(new UIButton(UIButton.Type.SQUARE,"Suivant", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.connectAccount(numberTextField.getText(), passwordTextField.getText()));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 75) / 2, getWindowPosY() + 135, 75, 15).setZIndex(900));
			}
			else if(guiType == 1)
			{
				addComponent(new UIText("Quel est votre numéro de mobile ?",new UIColor("#242624"),0.8f).setPosition(getWindowPosX() + 6, getWindowPosY() + 55,120,0));
				addComponent(new UIText("Nous devrons le confirmer en vous envoyant un SMS",new UIColor("#8d8f8d"),0.7f).setPosition(getWindowPosX() + 6, getWindowPosY() + 75,100,0));
				
				addComponent(new UIText("Numéro de téléphone",new UIColor("#009CDE"),0.7f).setPosition(getWindowPosX() + 10, getWindowPosY() + 90,100,0));
	
				UITextField phoneTextField = (UITextField)addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.9f,UITextField.Type.NUMBER).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 100) / 2, getWindowPosY() + 100,100,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 100) / 2, getWindowPosY() + 114,100,1));
				
				addComponent(new UIButton(UIButton.Type.SQUARE,"Valider", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.startForgotPasswordRequest(phoneTextField.getText()));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 135, 80, 15).setZIndex(900));
			}
			else if(guiType == 2)
			{				
				addComponent(new UIText("Entrez le code",new UIColor("#242624"),1f).setTextCentered(true).setPosition(getWindowPosX() + getWindowWidth() / 2, getWindowPosY() + 50,100,0));
				addComponent(new UIText("Nous vous avons envoyé \\n   un code à 6 chiffres",new UIColor(80,80,80),0.8f).setPosition(getWindowPosX() + 10, getWindowPosY() + 70,100,0));
				UITextField textField = (UITextField) addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),1f,UITextField.Type.NUMBER).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 40) / 2, getWindowPosY() + 92,50,15));
				textField.setMaxStringLength(6);
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 50) / 2, getWindowPosY() + 104,50,1));

				addComponent(new UIButton(UIButton.Type.SQUARE,"Renvoyer le code", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						currentPage = new PaypalConnexion((byte)2);
						updateGuiState();
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 117, 80, 15).setZIndex(900));
				
				addComponent(new UIButton(UIButton.Type.SQUARE, "Suivant",new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.sendCode(textField.getText()));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 134, 80, 15).setZIndex(900));
			}
			else if(guiType == 3)
			{
				addComponent(new UIText("Mots de passe",new UIColor("#0086D3"),0.55f).setPosition(getWindowPosX() + 5, getWindowPosY() + 50, 100, 0));
				
				UITextField passwordField = (UITextField)addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.8f,UITextField.Type.PASSWORD).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 58,110,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 71,110,1));
				
				addComponent(new UIText("Confirmation",new UIColor("#0086D3"),0.55f).setPosition(getWindowPosX() + 5, getWindowPosY() + 87,100,0));
				
				UITextField confirmationField = (UITextField)addComponent(new UITextField(new UIRect(new UIColor(255,255,255,0)),0.8f,UITextField.Type.PASSWORD).setTextColor(new UIColor("#212121")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 97,75,15));
				addComponent(new UIRect(new UIColor("#0086D3")).setPosition(getWindowPosX() + (getWindowWidth() - 110) / 2, getWindowPosY() + 110,110,1));
			
				
				addComponent(new UIButton(UIButton.Type.SQUARE,"Changer", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.changePassword(passwordField.getText(), confirmationField.getText()));
					}
					
				}).setPosition(getWindowPosX() + (getWindowWidth() - 80) / 2, getWindowPosY() + 125, 80, 15).setZIndex(900));
				
			
				
			}

		}

		@Override
		public void updateForm() 
		{
			if(guiType == 0)
			{
				UITextField numberTextField = (UITextField)getComponent(4);
								
				if(numberTextField.isFocused() && !DataUtils.isValidNumber(numberTextField.getText()))
				{
					displayError("Entrez un numéro valide", 5);
				}
				else if(numberTextField.isFocused())
				{
					clearErrors();
				}
			}
			else if(guiType == 1)
			{
				UITextField numberTextField = (UITextField)getComponent(6);

				if(numberTextField.isFocused() && !DataUtils.isValidNumber(numberTextField.getText()))
				{
					displayError("Entrez un numéro valide", 7);
				}
				else if(numberTextField.isFocused())
				{
					clearErrors();
				}
			}
			else if(guiType == 2)
			{
				UITextField codeTextField = (UITextField)getComponent(5);

				if(codeTextField.isFocused() && codeTextField.getText().length() < 6)
				{
					displayError("Entrez un code valide", 6);
				}
				else if(codeTextField.isFocused())
				{
					clearErrors();
				}
			}
			else if(guiType == 3)
			{
				UITextField passwordField = (UITextField)getComponent(4);
				UITextField confirmationField = (UITextField)getComponent(7);

				if(passwordField.isFocused() && passwordField.getText().length() < 6)
				{
					displayError("Mots de passe à 6 caractères au moins",5);
				}
				else if(passwordField.isFocused())
				{
					clearError(5);
				}
				
				if(confirmationField.isFocused() && !confirmationField.getText().equals(passwordField.getText()))
				{
					displayError("Mots de passe non identique",8);
				}
				else if(confirmationField.isFocused())
				{
					clearError(8);
				}
			}
		}
		
	}
	
	public class PaypalAccount implements PaypalApps
	{
		
		public byte guiType = 0;
		
		public PaypalAccount(byte guiType)
		{
			this.guiType = guiType;
		}
		
		@Override
		public void init()
		{
			getChild(0).setVisible(true);
			addComponent(new UIRect(new UIColor(255,255,255))).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), 20);
			addComponent(new UIImage(new ResourceLocation("craftyourliferp","ui/phone/paypal-ico.png")).setPosition(getWindowPosX() + (getWindowWidth() - 15) / 2, getWindowPosY() + 3, 15, 15));
			addComponent(new UIRect(new UIColor(180,180,180))).setPosition(getWindowPosX(), getWindowPosY()+21, getWindowWidth(), 2);
			
			addComponent(new UIButton(UIButton.Type.SQUARE, "logout", new ResourceLocation("craftyourliferp","ui/phone/logout.png"), null,false,new UIButton.CallBackObject()
			{
				
				@Override
				public void call()
				{
					ModCore.getPackethandler().sendToServer(PacketPaypal.disconnectAccount());
				}
				
			}).setPosition(getWindowPosX() + getWindowWidth() - 16, getWindowPosY() + 6, 10, 10).setZIndex(900));
		
			if(guiType == 0)
			{
				scrollView.addToContainer(new MoneyDisplayContainer().setPosition(0, 0, getWindowWidth()-4, 72));
			}
			else if(guiType == 1)
			{
				scrollView.addToContainer(new SendMoneyContainer().setPosition(0, 0, getWindowWidth()-4, 120));
			}
		}

		@Override
		public void updateForm() 
		{
			if(guiType == 1)
			{
				SendMoneyContainer container = (SendMoneyContainer) scrollView.contentRect.childs.get(0);
				if(!DataUtils.isValidNumber(container.numberField.getText()))
				{
					container.sendBtn.setEnabled(false);
				}
				else
				{
					float money = 0;
					try
					{
						money = Float.parseFloat(container.moneyField.getText());
					}
					catch(Exception e)
					{
						container.sendBtn.setEnabled(false);
						return;
					}
					
					
					if(DataUtils.isLimitValue(money))
					{
						container.sendBtn.setEnabled(false);
					}
					else
					{
						container.sendBtn.setEnabled(true);
					}
				}
			}
		}
		
		
		
		class MoneyDisplayContainer extends UIRect implements IGuiClickableElement
		{
			
			private UIText title = new UIText("Solde",new UIColor("#009CDE"),0.8f);
			private UIText sold = new UIText(MinecraftUtils.getMoneyDisplay("%.2f",Minecraft.getMinecraft().player, MinecraftUtils.getPlayerCapability(Minecraft.getMinecraft().player).getPhoneData().userMoney) + " EUR",new UIColor(150,150,150),1f);
			private UIText text = new UIText("Disponible",new UIColor(100,100,100),0.5f);

			private UIButton sendBtn;

			
			public MoneyDisplayContainer() 
			{
				super(new UIColor(230,230,230));
				sendBtn = new UIButton(UIButton.Type.SQUARE, "Envoyer de l'argent", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(20,20,209)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						currentPage = new PaypalAccount((byte)1); 
						updateGuiState();
					}
					
				});
			}
			
			@Override
			public GraphicObject setPosition(int x, int y, int width, int height)
			{
				title.setPosition(x + 5, y + 5,100,0);
				sold.setPosition(x+5, y+20,100,0);
				text.setPosition(x+5, y+32,100,0);
				
				sendBtn.setPosition(x+ 5, y + 50, 90,12);

				super.setPosition(x, y, width, height);
				
				return this;
			}
			
			@Override
			public void draw(int x, int y)
			{
				super.draw(x, y);
				title.draw(x, y);
				sold.draw(x, y);
				text.draw(x, y);
				sendBtn.draw(x, y);
			}

			@Override
			public boolean onRightClick(int x, int y)
			{
				return false;
			}

			@Override
			public boolean onLeftClick(int x, int y)
			{
				if(sendBtn.isClicked(x, y))
				{
					sendBtn.onLeftClick(x, y);
					return true;
				}
				return false;
			}

			@Override
			public boolean onWheelClick(int x, int y)
			{
				return false;
			}
			
		}
		
		class SendMoneyContainer extends UIRect implements IGuiKeytypeElement, IGuiClickableElement
		{
			
			private UIText title = new UIText("Solde",new UIColor("#009CDE"),0.8f);
			private UIText sold = new UIText(MinecraftUtils.getMoneyDisplay("%.2f",Minecraft.getMinecraft().player, MinecraftUtils.getPlayerCapability(Minecraft.getMinecraft().player).getPhoneData().userMoney) + " EUR",new UIColor(80,80,80),1.1f).setTextCentered(true);
			private UIText text = new UIText("Disponible",new UIColor(100,100,100),0.7f).setTextCentered(true);
			private UIRect separator = new UIRect(new UIColor(200,200,200));
			
			private UIText numberText = new UIText("Numéro de téléphone destinataire",new UIColor(100,100,100),0.6f);
			private UITextField numberField = new UITextField(new UIRect(new UIColor(0,0,0,0)),0.7f,UITextField.Type.NUMBER).setTextColor(new UIColor("#212121"));
			private UIRect numberFieldDesign = new UIRect(new UIColor("#0086D3"));

			private UIText moneyText = new UIText("Somme à envoyé",new UIColor(100,100,100),0.6f);
			private UITextField moneyField = new UITextField(new UIRect(new UIColor(0,0,0,0)),0.7f,UITextField.Type.TEXT).setTextColor(new UIColor("#212121"));
			private UIRect moneyFieldDesign = new UIRect(new UIColor("#0086D3"));
			private UIRect separator2 = new UIRect(new UIColor(200,200,200));
			private UIText euroTxt = new UIText("EUR",new UIColor(100,100,100),0.7f);

			private UIButton sendBtn;

			
			public SendMoneyContainer() 
			{
				super(new UIColor(0,0,0,0));

				sendBtn = new UIButton(UIButton.Type.SQUARE,  "Envoyer", new UIRect(new UIColor(40,68,209)), new UIRect(new UIColor(130, 148, 237)),new UIButton.CallBackObject()
				{
					
					@Override
					public void call()
					{
						ModCore.getPackethandler().sendToServer(PacketPaypal.sendMoney(Float.parseFloat(moneyField.getText()), numberField.getText()));
					}
					
				});
			}
			
			@Override
			public GraphicObject setPosition(int x, int y, int width, int height)
			{
				title.setPosition(x + 5, y + 5, 100, 0);
				sold.setPosition(x, y+22,getWindowWidth(), 0);
				text.setPosition(x, y+34,getWindowWidth(),0);

				separator.setPosition(x+5, y+45, width-10, 1);

				sendBtn.setPosition(x + (width - 90) / 2, y + 112, 90,15);
				
				separator2.setPosition(x+width-25, y+88, 1, 15);
				
				numberText.setPosition(x+6, y+52,120,0);
				numberField.setPosition(x+5, y+58, width-10, 15);
				numberFieldDesign.setPosition(x+5, y+70, width-10, 1);

				moneyText.setPosition(x+5, y+84,100,0);
				moneyField.setPosition(x + 5, y + 90, width-32, 15);
				moneyFieldDesign.setPosition(x+5, y+103, width-10, 1);
				euroTxt.setPosition(x+width-20, y + 93,100,0);

				super.setPosition(x, y, width, height);
				
				return this;
			}
			
			@Override
			public void draw(int x, int y)
			{
				super.draw(x, y);
				title.draw(x, y);
				sold.draw(x, y);
				text.draw(x, y);
				separator.draw(x, y);
				
				numberText.draw(x, y);
				numberField.draw(x, y);
				numberFieldDesign.draw(x, y);

				moneyText.draw(x, y);
				moneyField.draw(x, y);
				moneyFieldDesign.draw(x, y);
				separator2.draw(x, y);
				euroTxt.draw(x, y);
				
				sendBtn.draw(x, y);
			}

			@Override
			public boolean keyTyped(char character, int keycode) 
			{
				moneyField.keyTyped(character, keycode);
				numberField.keyTyped(character, keycode);

				return false;
			}

			@Override
			public boolean onRightClick(int x, int y) {
				return false;
			}

			@Override
			public boolean onLeftClick(int x, int y) {
				if(sendBtn.isClicked(x, y))
				{
					sendBtn.callback.call();
					return true;
				}
				
				if(moneyField.onLeftClick(x, y))
				{
					numberField.setFocused(false);
					return true;
				}
				
				
				if(numberField.onLeftClick(x, y))
				{
					moneyField.setFocused(false);

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
	
	



}

