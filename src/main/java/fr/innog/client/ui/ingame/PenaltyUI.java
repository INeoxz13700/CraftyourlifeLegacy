package fr.innog.client.ui.ingame;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UICheckBox;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.common.ModCore;
import fr.innog.common.penalty.Penalty;
import fr.innog.network.packets.decrapted.PacketPenalty;
import fr.innog.ui.remote.IRemoteUI;
import fr.innog.ui.remote.RemotePenaltyUI;
import fr.innog.ui.remote.RemoteUIProcessor;

public class PenaltyUI extends GuiScrollableView implements IRemoteUI {

	
	public PenaltyObject selectedObj;
	
	public PacketPenalty guiElementsSync;
	
	private RemotePenaltyUI remoteUI;
	
	
	@Override
	public void  initGui()
	{
		super.initGui();
		
		guiElementsSync = new PacketPenalty(Math.round((float)viewport.getHeight() / (spacing+20)) + 1);
		ModCore.getPackethandler().sendToServer(guiElementsSync);
	}
	
	@Override	
	public void initWindows()
	{
		setWindowSize(230, 200);
		setWindowPosition((width-230) / 2, (height-200) / 2);
	}
	
	@Override
	public void initializeComponent()
	{
		addComponent(new UIRect(new UIColor(255,255,255)).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), getWindowHeight()));
		addComponent(new UIText("Mes amendes",new UIColor(80,80,80),1.2f).setPosition(getWindowPosX() + 14, getWindowPosY() + 15, 1000, 0));
		
		addComponent(new UIText("Date ",new UIColor(80,80,80),1f).setPosition(getWindowPosX() + getWindowWidth() - 110, getWindowPosY() + 40, 1000, 0));
		addComponent(new UIText("Raison ",new UIColor(80,80,80),1f).setPosition(getWindowPosX() + getWindowWidth() - 110, getWindowPosY() + 55, 1000, 0));
		addComponent(new UIText("Prix ",new UIColor(80,80,80),1f).setPosition(getWindowPosX() + getWindowWidth() - 110, getWindowPosY() + 70, 1000, 0));
		addComponent(new UIText("Id ",new UIColor(80,80,80),1f).setPosition(getWindowPosX() + getWindowWidth() - 110, getWindowPosY() + 85, 1000, 0));

		addComponent(new UIText("Total : " + totalPrice + " €",new UIColor(80,80,80),1f).setPosition(getWindowPosX() + getWindowWidth() - 108, getWindowPosY() + getWindowHeight() - 50, 1000, 0));
		
		
		addComponent(new UIButton(Type.SQUARE,"Payer",new UIRect(new UIColor(37, 154, 247)),new UIRect(new UIColor(35, 131, 207)),new UIButton.CallBackObject()
		{
			@Override
			public void call()
			{
				payPenalty();
			}
		})).setPosition(getWindowPosX() + getWindowWidth() - 65, getWindowPosY() + getWindowHeight() - 25, 60, 18);

		
		UIButton selectionBtn = (UIButton) addComponent(new UIButton("Tout Selectionner",new UIColor(80,80,80),new UIColor(10, 10, 120),0.8f,null).setPosition(getWindowPosX() + 8, getWindowPosY() + getWindowHeight() - 15, 60, 18));
		selectionBtn.callback = new UIButton.CallBackObject()
		{
			public void call()
			{
				if(selectionBtn.getText().equalsIgnoreCase("Tout Selectionner"))
				{
					selectAll();
				}
				else
				{
					unSelectAll();
				}
			}
		};
		
		contentRect = new UIRect(new UIColor(0,0,0,0));
		
		viewport = new UIRect(new UIColor(220,220,220,255));
		
		scrollBarVertical = new UIScrollbarVertical(new UIColor(235,235,235),new UIColor("#0180FF")).setHoverColor(new UIColor("#006bd6"));
		scrollBarVertical.setButtonColor(new UIColor(80,80,80));
		scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor(235,235,235),new UIColor("#0180FF")).setHoverColor(new UIColor("#006bd6"));
		scrollBarHorizontal.setButtonColor(new UIColor(80,80,80));

		
		this.selectedScrollBar = scrollBarVertical;
				
		this.setScrollViewPosition(getWindowPosX() + 5, getWindowPosY() + 30, 100, getWindowHeight() - 60);
		
		resetScrollView();
	}
	
	public void updateScreen()
	{
		if(contentRect.childs.size() == 0)
		{
			addToContainer(new InformationObj(new UIColor(37, 154, 247)).setPosition(0, 0, getWindowWidth(), 20));
		}
				
		if(getGuiTicks() % 10 == 0)
		{
			if(selectedObj == null)
			{
				UIText text = (UIText)getComponent(2);
				text.setText("Date ");
				text = (UIText)getComponent(3);
				text.setText("Raison ");
				text = (UIText)getComponent(4);
				text.setText("Prix ");
				text = (UIText)getComponent(5);
				text.setText("Id ");
			}
			
			((UIText)getComponent(6)).setText("Total " + getTotalPrice() + " €");
		}
		
		if(getGuiTicks() % 20 == 0)
		{
			if(scrollBarVertical.getValue() >= 0.8f)
			{
				if(guiElementsSync.received) ModCore.getPackethandler().sendToServer(guiElementsSync);
			}
		}
		super.updateScreen();
		
	}
	
	
	@Override
	protected void keyTyped(char character, int keycode)
	{	  
		if(keycode == 1)
		{
			 mc.displayGuiScreen(null);
			 return;
		}
		
		try {
			super.keyTyped(character, keycode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void resetScrollView()
	{
		contentRect.childs.clear();
		resetContentLayout();
	}
	
	@Override
	public void updateScrollviewContents()
	{
		super.updateScrollviewContents();
	}
	
	public boolean isAllItemsSelected()
	{
		for(GraphicObject obj : contentRect.childs)
		{
			if(obj instanceof PenaltyObject)
			{
				PenaltyObject penaltyObject = (PenaltyObject)obj;
				if(!penaltyObject.checkbox.checked)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public void addPenalty(Penalty penalty)
	{
		if(contentRect.childs.get(0) instanceof InformationObj) 
		{
			resetContentLayout();
			contentRect.childs.remove(0);
		}
		((UIButton)getComponent(8)).setDisplayText("Tout Selectionner");

		addToContainer(new PenaltyObject(this,"Amendes " + (contentRect.childs.size() + 1), penalty).setPosition(0, 0, viewport.getWidth(), 20));
	}
	
	public void payPenalty()
	{
		ModCore.getPackethandler().sendToServer(PacketPenalty.payPenalties(getSelectedPenaltiesId()));
		unSelectAll();
		selectedObj = null;
	}
	
	public void removePenalties(List<String> penaltiesId)
	{
		List<PenaltyObject> penalties = contentRect.getChilds().stream().filter(x -> x instanceof PenaltyObject && penaltiesId.contains(((PenaltyObject)x).penalty.getId())).map(x -> (PenaltyObject) x).collect(Collectors.toList());
		contentRect.childs.removeAll(penalties);
		rebuildLayout();
	}
	
	@Override
	protected void rebuildLayout()
	{

		if(guiElementsSync.received) ModCore.getPackethandler().sendToServer(guiElementsSync);
		super.rebuildLayout();
	}
	
	private int totalPrice;
	private int getTotalPrice()
	{
		totalPrice = 0;
		contentRect.childs.stream().filter(x -> x instanceof PenaltyObject).map(x -> (PenaltyObject)x).filter(x -> x.checkbox.checked).forEach(action ->
		{
			totalPrice += action.penalty.getPrice();
		});
		return totalPrice;
	}
	
	private void selectAll()
	{
		contentRect.childs.stream().filter(x -> x instanceof PenaltyObject).map(x -> (PenaltyObject)x).forEach(action ->
		{
			action.checkbox.checked = true;
		});
		((UIButton)getComponent(8)).setDisplayText("Tout Déselectionner");
	}
	
	private void unSelectAll()
	{
		contentRect.childs.stream().filter(x -> x instanceof PenaltyObject).map(x -> (PenaltyObject)x).forEach(action ->
		{
			action.checkbox.checked = false;
		});
		((UIButton)getComponent(8)).setDisplayText("Tout Selectionner");
	}
	
	public List<String> getSelectedPenaltiesId()
	{
		return contentRect.childs.stream().filter(x -> x instanceof PenaltyObject && ((PenaltyObject)x).checkbox.checked).map(x -> ((PenaltyObject)x).penalty.getId()).collect(Collectors.toList());
	}

	@Override
	public void setRemoteUI(RemoteUIProcessor remoteUI) {
		this.remoteUI = (RemotePenaltyUI) remoteUI;
	}

}

class PenaltyObject extends UIButton
{
	
	public UICheckBox checkbox;
	
	public Penalty penalty;

	public PenaltyObject(PenaltyUI gui,String name,Penalty penalty) {
		super(Type.SQUARE,name, new UIRect(new UIColor(37, 154, 247)), new UIRect(new UIColor(35, 131, 207)),null);
		
		this.callback = new UIButton.CallBackObject()
		{
			public void call()
			{
				if(gui.selectedObj != null)
				{
					gui.selectedObj.enabled = true;
				}
				gui.selectedObj = PenaltyObject.this;
				gui.selectedObj.checkbox.checked = true;
				gui.selectedObj.enabled = false;
				
				UIText text = (UIText)gui.getComponent(2);
				text.setText("Date : " + gui.selectedObj.penalty.getFormattedDate());
				text = (UIText)gui.getComponent(3);
				text.setText("Raison : " + gui.selectedObj.penalty.getReason());
				text = (UIText)gui.getComponent(4);
				text.setText("Prix : " + gui.selectedObj.penalty.getPrice() + " €");
				text = (UIText)gui.getComponent(5);
				text.setText("Id : " + gui.selectedObj.penalty.getId());
			}
		};
		
		this.penalty = penalty;
		
		checkbox = new UICheckBox(Type.SQUARE);
		

	}
	
	public GraphicObject setPosition(int x, int y, int width, int height)
	{
		super.setPosition(x, y, width, height);
		checkbox.setPosition(x + 2, y + (height - 15) / 2, 15, 15);
		return this;
	}
	
	@Override
	public void draw(int x, int y)
	{
		super.draw(x, y);
		checkbox.draw(x, y);
	}
	
	@Override
	public boolean onLeftClick(int x, int y) {
		if(checkbox.isClicked(x, y))
		{
			checkbox.onLeftClick(x, y);
			return true;
		}
		
		return super.onLeftClick(x, y);
	}
	
}

class InformationObj extends UIRect
{
	

	public InformationObj(UIColor color) {
		super(color);
	}

	public GraphicObject setPosition(int x, int y, int width, int height)
	{
		super.setPosition(x, y, width, height);
		return this;
	}
	
	@Override
	public void draw(int x, int y)
	{
		super.draw(x, y);
		GuiUtils.renderCenteredText("Vous n'avez pas d'amende", posX + width / 2, posY + (height-1) / 2,0.75f);
	}
	
	
}