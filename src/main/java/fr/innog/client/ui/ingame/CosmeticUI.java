package fr.innog.client.ui.ingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import fr.innog.advancedui.gui.GuiBase;
import fr.innog.advancedui.gui.GuiGridView;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIDropdown;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.ui.remote.IRemoteUI;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CosmeticUI extends GuiGridView implements IRemoteUI {

	private List<CosmeticObject> contents = new ArrayList<>();
	
	private List<CosmetiqueSlot> slots = new ArrayList<>();
	
	protected UIColor buttonColor = new UIColor(0,135,212,255);
	protected UIColor buttonColor_hover = new UIColor(26, 146, 214,255);
	
	private String lastSelectedElement;
	
	public static IPlayer pe;
	
	public static EntityPlayer player;
	
	private RemoteUIProcessor remoteUI;
	
	public CosmeticUI()
	{
		elementSize = 23;
		spacing = 7;
		player = Minecraft.getMinecraft().player;
		CosmeticUI.pe = MinecraftUtils.getPlayerCapability(player);
	}
	
	
	@Override
	public void initGui() 
	{
		super.initGui();
		
		List<CosmeticObject> cosmetics = (List<CosmeticObject>)remoteUI.getCacheData().getData("Cosmetics").getData();
		for(int i = 0; i < pe.getCosmeticDatas().cosmeticsData.size(); i++)
		{
			pe.getCosmeticDatas().cosmeticsData.get(i).setLocked(cosmetics.get(i).getIsLocked());
		}
	}
	
	@Override	
	public void initWindows()
	{
		setWindowSize(250, 200);
		setWindowPosition((this.width-250) / 2, (this.height - 200)/2);
	}
	
	public void updateContents()
	{
		List<CosmeticObject> updateContent = new ArrayList<>();
				
		List<CosmeticObject> cosmetics = MinecraftUtils.getPlayerCapability(player).getCosmeticDatas().getCosmeticsDatas();
		
		for(int i = 0; i < cosmetics.size(); i++)
		{
			CosmeticObject cosmetic = cosmetics.get(i);
			updateContent.add(cosmetic);
		}
		
		contents = updateContent;
	}
	
	@Override
	public void drawScreen(int x, int y, float partialTicks)
	{
		super.drawScreen(x, y, partialTicks);
		GuiUtils.renderCenteredText("Cosmetiques", guiRect.getX() + guiRect.getWidth() / 2, guiRect.getY() + 10,1f, 5723991);
		if(this.mouseOnDropdown(x, y)) return;
		for(GraphicObject component : contentRect.visibleChilds)
		{
			if(component.isHover(x, y))
			{
				CosmetiqueSlot slot = (CosmetiqueSlot) component;
				if(mc.player.capabilities.isCreativeMode)
				{
	 				drawHoveringText(Arrays.asList(new String[] {slot.getCosmeticObj().getName(),slot.getCosmeticObj().getSpecialMessage(), "ID : §b" + slot.getCosmeticObj().getId()}),x,y,mc.fontRenderer);
				}
				else
				{
	 				drawHoveringText(Arrays.asList(new String[] {slot.getCosmeticObj().getName(),slot.getCosmeticObj().getSpecialMessage()}),x,y,mc.fontRenderer);
				}
			}
		}
	}
	
	@Override
	public void initializeComponent() 
	{ 
		slots.clear();
		updateContents();
		
		this.addComponent(new UIButton(Type.SQUARE,null,new ResourceLocation("craftyourliferp","ui/cosmetics/close.png"),null,false,new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				mc.currentScreen = null;
				mc.setIngameFocus();
			}
			
		}).setPosition(guiRect.getX2()-15, guiRect.getY()+5,10,10));
		
		this.addComponent(new UIDropdown(10, Arrays.asList(new String[] {"tout","équipé","débloqué","tête","visage","corps"}),buttonColor).setPosition(guiRect.getX() + (guiRect.getWidth() - 222) / 2, guiRect.getY() + 22 , 222,10));
		
		this.contentRect = new UIRect(new UIColor(0,0,0,0));
		
		this.viewport = new UIRect(new UIColor(232, 232, 232));
		
		this.scrollBarVertical = new UIScrollbarVertical(new UIColor("#3A3B37"),new UIColor("#3A3B37"));
		this.scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor("#3A3B37"),new UIColor("#3A3B37"));
		this.selectedScrollBar = scrollBarVertical;
		
		guiRect.color = new UIColor("#FFFFFF");
		
		setScrollViewPosition(guiRect.getX() + (guiRect.getWidth() - 222) / 2, guiRect.getY()+35, 222, 160);
		

		updateScrollviewContents();

		
		updateContentElements();
	}
	
	public void updateScrollviewContents()
	{
		slots.clear();
		UIDropdown dropdown = (UIDropdown) getComponent(1);
		for(CosmeticObject co : getCosmeticsByFilter(dropdown.getSelectedElement()))
		{
				CosmetiqueSlot slot = new CosmetiqueSlot(co,Type.SQUARE,new UIRect(buttonColor), null, new UIRect(buttonColor_hover), new UIButton.CallBackObject()
				{
					@Override
					public void call()
					{
						if(co.getIsEquipped())
						{
							CosmeticObject.unequipCosmetic(player, co.getId());
						}
						else
						{							
							List<CosmeticObject> toUnEquip = CosmeticObject.getEquippedCosmeticFromSameType(player, co.getType());
							for(CosmeticObject obj : toUnEquip)
							{
								CosmeticObject.unequipCosmetic(player, obj.getId());
							}
							CosmeticObject.equipCosmetic(player,co.getId());
						}
					}				
				});
				slots.add(slot);
		}
		this.updateContentElements();
	}
	
	public List<CosmeticObject> getCosmeticsByFilter(String filter)
	{
		if(filter.equalsIgnoreCase("tout"))
		{
			return contents;
		}
		else if(filter.equalsIgnoreCase("débloqué"))
		{
			return contents.stream().filter(x -> !x.getIsLocked()).collect(Collectors.toList());
		}
		else if(filter.equalsIgnoreCase("équipé"))
		{
			return contents.stream().filter(x -> x.getIsEquipped()).collect(Collectors.toList());
		}
		else if(filter.equalsIgnoreCase("tête"))
		{
			return contents.stream().filter(x -> x.getType() == 0).collect(Collectors.toList());
		}
		else if(filter.equalsIgnoreCase("visage"))
		{
			return contents.stream().filter(x -> x.getType() == 1).collect(Collectors.toList());
		}
		else if(filter.equalsIgnoreCase("corps"))
		{
			return contents.stream().filter(x -> x.getType() == 2).collect(Collectors.toList());
		}
		return null;
	}
	
	public void updateContentElements()
	{
		contentRect.childs.clear();
		this.resetContainerLayout();
		for(GraphicObject obj : slots)
		{
			obj.localPosX = 0;
			obj.localPosY = 0;
			addToContainer(obj.setPosition(0, 0, elementSize,elementSize));
		}
		contentRect.setHeight(contentRect.getHeight() + spacing + 1);
	}
	
	public GraphicObject addToContainer(GraphicObject object)
	{
		 if(object == null)  return null;
		 
		 if(contentRect.getChilds().size() == 0) 
		 {
			 lastElementposX = object.localPosX += 1+lastElementposX + spacing;
			 lastElementposY = object.localPosY += 1+spacing;
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
			 object.localPosX = lastElementposX = spacing+1;
			 contentRect.setHeight(contentRect.getHeight() + elementSize + spacing);
		 }
		 		 
		 contentRect.addChild(object);
		 
		 return object;
	 }
	
	 @Override
	 public void updateScreen()
	 {
		 UIDropdown dropdown = (UIDropdown)getComponent(1);
		 if(this.lastSelectedElement != dropdown.getSelectedElement())
		 {
			 this.lastSelectedElement = dropdown.getSelectedElement();
			 this.updateScrollviewContents();
		 }
	 }
	 
	 @Override
	 public void onGuiClosed() 
	 {
		 
	 }


	@Override
	public void setRemoteUI(RemoteUIProcessor remoteUI) {
		this.remoteUI = remoteUI;
	}

}

class CosmetiqueSlot extends UIButton
{
	private CosmeticObject attribuatedCosmetic;
	
	private final static ResourceLocation lockedTexture = new ResourceLocation("craftyourliferp","ui/cosmetics/locked.png");
	private final static ResourceLocation selectedTexture = new ResourceLocation("craftyourliferp","ui/cosmetics/selected.png");
	
	
	private UIImage lockedImage = new UIImage(lockedTexture);
	private UIImage selectedImage = new UIImage(selectedTexture);

	public CosmetiqueSlot(CosmeticObject co,Type type, String text, ResourceLocation resource,ResourceLocation hoverTexture, boolean displayText, CallBackObject callback) {
		super(type, text, resource,null, displayText, callback);
		lockedImage.color = new UIColor(255,215,0,255);
		selectedImage.color = new UIColor("#5ee609");
		this.attribuatedCosmetic = co;
	}
	
	public CosmetiqueSlot(CosmeticObject co,Type type , UIRect rect, String text, UIRect hoverRect, CallBackObject callback) {
		super(type, text,rect, hoverRect, callback);
		lockedImage.color = new UIColor(255,215,0,255);
		selectedImage.color = new UIColor("#5ee609");
		this.attribuatedCosmetic = co;
	}
	
	public CosmeticObject getCosmeticObj()
	{
		return attribuatedCosmetic;
	}
	
	public void setCosmeticObj(CosmeticObject co)
	{
		attribuatedCosmetic = co;
	}
	
	public boolean isLocked()
	{
		return attribuatedCosmetic.getIsLocked();
	}
	
	public boolean isEquipped()
	{
		return attribuatedCosmetic.getIsEquipped();
	}
	
	@Override
	public GraphicObject setPosition(int x, int y, int width, int height)
	{
		super.setPosition(x, y, width, height);
		lockedImage.setPosition(x+(width-10)/2, y+(height-10)/2, 10, 10);
		selectedImage.setPosition(x+width-5, y+height-5, 5, 5);
		return this;
	}

	@Override
	public void draw(int x, int y)
	{
		super.draw(x, y);
		if(attribuatedCosmetic != null)
		{
			
			
			GL11.glPushMatrix();
						
			attribuatedCosmetic.renderModelInGui(posX, posY, 0);
			
			if(isEquipped())
			{
				selectedImage.draw(x, y);
			}
			
			GuiBase gui = (GuiBase) Minecraft.getMinecraft().currentScreen;
			if(gui.mouseOnDropdown(x,y))
			{
				GL11.glPopMatrix();
				return;
			}
			
			
			if(isHover(x,y))
			{
				GL11.glTranslatef(0, 0, 999);

				if(isLocked())
				{
					lockedImage.draw(x, y);
				}
			}
			GL11.glPopMatrix();
		}

	}

}
	