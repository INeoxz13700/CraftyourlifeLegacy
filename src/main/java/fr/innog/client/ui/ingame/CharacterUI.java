package fr.innog.client.ui.ingame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UITextField;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.ui.remote.IRemoteUI;
import fr.innog.ui.remote.RemoteCharacterUI;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.ui.remote.data.RemoteMethodCallback;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CharacterUI extends GuiScreen implements IRemoteUI {
	
	private float ticks;
	private float displayError;
	private boolean isDictatel;
	
	private String displayedMessage;
	private int messageIndex = 0;
	private int messageCounter = 0;
	private boolean countMessage = false;

	private List<String> dictatelMessage = new ArrayList<String>();	
	
	public UITextField lastname;
	public UITextField name;
	public UITextField birthdayDay;
	public UITextField birthdayMonth;
	public UITextField birthdayYear;

	public UITextField gender;
	
	private boolean isError;
	private String errorMessage;
		
	private String id;
	
	protected UIColor buttonColor = new UIColor(0,135,212,255);
	protected UIColor buttonColor_hover = new UIColor(26, 146, 214,255);
	
	private UIButton confirmButton;
	
	private RemoteCharacterUI remoteUI;

	
	public CharacterUI() {
		id = "IDCRAFTYOURL<<<<<<<<<<<<<<<<<<<<";
		for(int i = 0; i < 18; i++)
		{
			id += ThreadLocalRandom.current().nextInt(1, 11);
		}
		id += "CYL<<<<<<<<<<<<";
		for(int i = 0; i < 11; i++)
		{
			id += ThreadLocalRandom.current().nextInt(0, 11);
		}
		isDictatel = true;
		this.countMessage = true;
		this.dictatelMessage.add("Bonjour il semblerait que vous êtes nouveau dans cette ville . Je me présente je suis le maire de cette belle ville.");
		this.dictatelMessage.add("Pour commencer, comme tout le monde vous allez procéder à la création de votre carte d'identité.");
		this.dictatelMessage.add("Vous êtes maintenant un citoyen de notre ville %gender% %name% et comme chaque citoyen vous devez à tous pris respecter les lois ne l'oubliez pas.");
		this.dictatelMessage.add("Pour votre arrivée en ville je vous offre ce dont vous avez besoin pour bien débuter , il est temps pour moi de vous laisser à bientôt !");
	
		confirmButton = new UIButton(Type.SQUARE, "Confirmer", new UIRect(buttonColor), new UIRect(buttonColor_hover), new UIButton.CallBackObject()
		{
			public void call()
			{
				if(!isDictatel)
				{
					if(!gender.getText().equalsIgnoreCase("M") && !gender.getText().equalsIgnoreCase("F"))
					{
						displayMessage("Veuillez entrer un genre valide Ex : M (Pour masculin)");
						return;
					}
					
					String gender = CharacterUI.this.gender.getText().equals("M") ? "Masculin" : "Feminin";
						
					
					IPlayer pData = MinecraftUtils.getPlayerCapability(mc.player);

					pData.getIdentityData().lastname = name.getText();
					
					pData.getIdentityData().name = lastname.getText();
					
					if(!birthdayDay.getText().isEmpty() && !birthdayMonth.getText().isEmpty() && !birthdayYear.getText().isEmpty())
					{
					    pData.getIdentityData().birthday = birthdayDay.getText() + "/" + birthdayMonth.getText() + "/" + birthdayYear.getText();
					}

					pData.getIdentityData().gender = gender;
						
					remoteUI.executeMethodWithResult("confirmIdentity", new RemoteMethodCallback()
					{

							@Override
							public void call(ActionResult result) {
								if(result == ActionResult.SUCCESS)
								{
									dictatelMessage.set(2, "Vous êtes maintenant " + (pData.getIdentityData().gender.equalsIgnoreCase("Masculin") ? "un citoyen" : "une citoyenne") + " de notre ville " + (pData.getIdentityData().gender.equalsIgnoreCase("Masculin") ? "monsieur" : "madame") + " " + pData.getIdentityData().lastname + " et, comme chaque citoyen, vous devez à tout prix respecter les lois. Ne l'oubliez pas."); 
							   		
					        		countMessage = true;
									isDictatel = true;
					        		messageCounter = 0;
					        		ticks = 0;
					        		messageIndex++;
								}
							}
							
					}, new Object[] { pData.getIdentityData().lastname, pData.getIdentityData().name, pData.getIdentityData().birthday,  pData.getIdentityData().gender});
				}
			}
			
		});
	}
	
	
	public void initGui() {
		this.buttonList.clear();
		this.lastname = new UITextField(new UIRect(new UIColor(0,0,0,100)), 1F, UITextField.Type.TEXT);
		this.lastname.setMaxStringLength(35);
		this.name = new UITextField(new UIRect(new UIColor(0,0,0,100)), 1F, UITextField.Type.TEXT);
		this.name.setMaxStringLength(35);

		this.birthdayDay = new UITextField(new UIRect(new UIColor(0,0,0,100)), 1F, UITextField.Type.NUMBER);
		this.birthdayDay.setMaxStringLength(2);
		
		this.birthdayMonth = new UITextField(new UIRect(new UIColor(0,0,0,100)), 1F, UITextField.Type.NUMBER);
		this.birthdayMonth.setMaxStringLength(2);
		
		this.birthdayYear = new UITextField(new UIRect(new UIColor(0,0,0,100)), 1F, UITextField.Type.NUMBER);
		this.birthdayYear.setMaxStringLength(4);
		
		this.gender = new UITextField(new UIRect(new UIColor(0,0,0,100)), 1F, UITextField.Type.TEXT);
		this.gender.setMaxStringLength(1);
		
		confirmButton.setPosition((this.width/2) - 50, this.height - 20,80,15);
	}
	
	
	@Override
    protected void keyTyped(char ch, int key)
    {
		if(!this.isDictatel)
		{
			this.name.keyTyped(ch, key);
			this.birthdayDay.keyTyped(ch, key);
			this.birthdayMonth.keyTyped(ch, key);
			this.birthdayYear.keyTyped(ch, key);
			this.lastname.keyTyped(ch, key);
			this.gender.keyTyped(ch, key);
		}
	}
	
    public void drawScreen(int x, int y, float fl)
    {
    	if(this.isDictatel)
    	{
    		GuiUtils.drawImage(30, this.height - 80, new ResourceLocation("craftyourliferp", "ui/character/president.png"), 42, 80,0);
    		this.drawDialogBox(70, this.height - 50, (this.width - 10) - 70, 40);
    	}
    	else
    	{
    		GuiUtils.drawRect(0, 0, width, height, 0, 0, 0.6F);
			
        	if(this.isError)
        	{
        		GuiUtils.drawRect((width-300)/2, ((height-180)/2)+182, 300, 15, 0, 16515843, 1f);
        		GuiUtils.renderCenteredText(this.errorMessage, width/2, (int) ((((height-180)/2)+185)));
        	}
			
        	
        	GuiUtils.renderCenteredText("Créer votre identité !", this.width / 2, 10);
        	
        	GuiUtils.drawImage((width-300)/2, (height-180)/2, new ResourceLocation("craftyourliferp", "ui/cardidentity/background.png"), 300, 180, 0);
	    	GuiUtils.drawImage((width-300)/2, (height-180)/2, new ResourceLocation("craftyourliferp", "ui/cardidentity/logo.png"), 300, 30, 0);
	    	
	    	GuiUtils.drawImage(((width-300)/2) + 5, ((height-180)/2)+35, new ResourceLocation("craftyourliferp", "ui/cardidentity/container.png"), 225, 115, 0);
        	
			GuiUtils.renderTextWithShadow("Nom : ", ((width-300)/2) + 10, ((height-180)/2)+60);
    		GuiUtils.renderTextWithShadow("Prenom : ", ((width-300)/2) + 10, ((height-180)/2)+80);
    		GuiUtils.renderTextWithShadow("Genre (M/F) : ", ((width-300)/2) + 10, ((height-180)/2)+100);
    		GuiUtils.renderTextWithShadow("Date de naissance : ", ((width-300)/2) + 10, ((height-180)/2)+120);
    		
    		this.name.setPosition(((width-300)/2) + 45, ((height-180)/2)+57, 100, 15);
        	this.name.draw(x, y);
        	
    		this.lastname.setPosition(((width-300)/2) + 62, ((height-180)/2)+77, 100, 15);
			this.lastname.draw(x, y);

			this.gender.setInputInset(5);
    		this.gender.setPosition(((width-300)/2) + 85, ((height-180)/2)+97, 20, 15);
			this.gender.draw(x, y);

			this.birthdayDay.setInputInset(3);
    		this.birthdayDay.setPosition(((width-300)/2) + 115, ((height-180)/2)+117, 22, 15);
    		this.birthdayDay.draw(x, y);
    		GuiUtils.renderTextWithShadow("/", ((width-300)/2) + 140, ((height-180)/2)+120);

			this.birthdayMonth.setInputInset(3);
    		this.birthdayMonth.setPosition(((width-300)/2) + 148, ((height-180)/2)+117, 22, 15);
    		this.birthdayMonth.draw(x, y);
    		GuiUtils.renderTextWithShadow("/", ((width-300)/2) + 174, ((height-180)/2)+120);
    		
			this.birthdayYear.setInputInset(3);
    		this.birthdayYear.setPosition(((width-300)/2) + 183, ((height-180)/2)+117, 34, 15);
    		this.birthdayYear.draw(x, y);
    		
    		confirmButton.draw(x, y);
			

	    	mc.fontRenderer.drawSplitString(this.id, ((width-300)/2)+5, ((height-180)/2)+155, 295, 0);

    	}
    }
    
    protected void mouseClicked(int x, int y, int btn) 
    {
    	if(this.isDictatel)
    	{
	    	if (btn == 0)
	        {

		        if(!this.displayedMessage.equalsIgnoreCase(this.dictatelMessage.get(this.messageIndex)))
		        {
		        	this.countMessage = false;
		        	this.displayedMessage = this.dictatelMessage.get(this.messageIndex);
		        }
		        else
		        {
		        	
		    		        		
			    	if(this.messageIndex == 1)
			    	{
			    	   this.isDictatel = false;
			    	    return;
			    	}
			    	else if(this.messageIndex == 3)
			    	{
			    	    this.isDictatel = false;
			    	    
			    	    this.mc.displayGuiScreen(null);
			    	    return;
			    	}
			    	    	
			        this.messageIndex++;
			        this.messageCounter = 0;
			        this.countMessage = true;	
		        }
	    		
	       }  	
    	}
    	else
    	{
    	   	if (btn == 0)
        	{
        		this.name.onLeftClick(x, y);
        		this.birthdayDay.onLeftClick(x, y);
        		this.birthdayMonth.onLeftClick(x, y);
        		this.birthdayYear.onLeftClick(x, y);
        		this.lastname.onLeftClick(x, y);
        		this.gender.onLeftClick(x, y);
        	}
    	}
    	
    	confirmButton.onLeftClick(x, y);
    	
    	try 
    	{
			super.mouseClicked(x, y, btn);
		} catch (IOException e) {
			e.printStackTrace();
		}
 
    }
    
    public boolean doesGuiPauseGame()
    {
        return false;
    }
    
    public void updateScreen() {
    	
    	if(this.isError && this.displayError++ == 40)
    	{
    		this.displayError = 0;
    		this.isError = false;
    		this.errorMessage = "";
    	}
    	if(this.isDictatel && (ticks+= 0.001f) >= 0.001f && this.countMessage)
    	{
    		ticks = 0;
    		this.displayedMessage = this.dictatelMessage.get(Math.min(this.messageIndex, this.dictatelMessage.size() - 1)).substring(0, Math.min(this.dictatelMessage.get(this.messageIndex).length(), this.messageCounter));
    		this.messageCounter++;
    	}
    	else
    	{
    		this.name.updateCursorCounter();
    		this.birthdayDay.updateCursorCounter();
    		this.birthdayMonth.updateCursorCounter();
    		this.birthdayYear.updateCursorCounter();
    		this.lastname.updateCursorCounter();
    		this.gender.updateCursorCounter();
    	}
    }
    
    public void drawDialogBox(int x, int y, int width, int height) {
    	
    	
    	new UIRect(buttonColor).setPosition(x, y-9, 50, 10).draw(0, 0);
    	new UIRect(new UIColor(255,255,255)).setPosition(x, y, width, height).draw(0, 0);
    	GuiUtils.renderText("Mr le maire", x + 5, (y-10) + 3,GuiUtils.gameColor,0.6f);
    	
    	if(this.displayedMessage != null)
    		mc.fontRenderer.drawSplitString(this.displayedMessage, x+5, y + 5, width-10, buttonColor.toRGB());
    }


	@Override
	public void setRemoteUI(RemoteUIProcessor remoteUI) {
		this.remoteUI = (RemoteCharacterUI) remoteUI;
	}
	
	public void displayMessage(String message)
	{
		isError = true;
		errorMessage = message;
	}

}
