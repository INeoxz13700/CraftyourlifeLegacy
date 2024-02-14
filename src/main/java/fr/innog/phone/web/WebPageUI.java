package fr.innog.phone.web;

import java.io.IOException;

import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.guicomponents.UITextField;
import fr.innog.phone.Tor;
import fr.innog.phone.web.page.WebPageData;

public abstract class WebPageUI extends GuiScrollableView
{
	
	private String pageUrl;
	
	private String pageTittle;
	
	private String pageDescription;
	
	private WebPageData webPageData;
	
	public WebPageUI(WebPageData webPageData, String pageUrl, String pageTittle, String pageDescription)
	{
		this.webPageData = webPageData;
		this.pageUrl = pageUrl;
		this.pageTittle = pageTittle;
		this.pageDescription = pageDescription;
	}
	
	public String getPageUrl()
	{
		return pageUrl;
	}
	
	public String getPageTittle()
	{
		return pageTittle;
	}
	
	public String getPageDescription()
	{
		return pageDescription;
	}
	
	public void back()
	{
		
	}
	
	public void next()
	{
		
	}
	
	public void refreshPage()
	{
		
	}
	
	public void onPageOpen()
	{
		
	}
	
	
	@Override
	public void initGui()
	{
		Tor tor  = (Tor)parent.getParent();
		UITextField textField = (UITextField)tor.getComponent(4);
		textField.setText(pageUrl);
		this.spacing = 5;

		this.onPageOpen();
		super.initGui();
	}
	
	@Override	
	public void initWindows()
	{
		this.setWindowSize(parent.getWindowWidth(), 135);
		this.setWindowPosition(parent.getWindowPosX(), parent.getWindowPosY());
	}
	
	@Override
	public void initializeComponent() 
	{ 
		this.contentRect = new UIRect(new UIColor(0,0,0,0));
		this.viewport = new UIRect(new UIColor(255,255,255,255));
		
		this.scrollBarVertical = new UIScrollbarVertical(new UIColor(240,240,240),new UIColor(205,205,205)).setHoverColor(new UIColor(190,190,190));
		this.scrollBarVertical.setButtonColor(new UIColor(100,100,100));
		this.scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor(240,240,240),new UIColor(205,205,205)).setHoverColor(new UIColor(190,190,190));
		this.scrollBarHorizontal.setButtonColor(new UIColor(100,100,100));

		this.selectedScrollBar = scrollBarVertical;
		
		
		setScrollViewPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), getWindowHeight());
		parameterVerticalScrollbar(getWindowPosX()+getWindowWidth()-5,getWindowPosY(),6,getWindowHeight());
				
		updateScrollviewContents();
	}
	
	protected void setScrollViewPosition(int x, int y, int width, int height)
	{
		this.viewport.setPosition(x, y, width, height);
		this.contentRect.setPosition(x, y, width, 0);
				
		this.scrollBarVertical.setPosition(viewport.getX2()+1, y-1, 6, height+2);
		this.scrollBarHorizontal.setPosition(viewport.getX()-1, viewport.getY2()+1, width+2, 6);
	}
	
	@Override
	public void mouseClicked(int x, int y, int mouseBtn)
	{
		try {
			super.mouseClicked(x, y, mouseBtn);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void drawScreen(int x, int y, float pt)
	{
		super.drawScreen(x, y, pt);
	}
	
	 @Override
	public void mouseClickMove(int x, int y, int state, long timeSinceLastClick)	 
	{
		super.mouseClickMove(x, y, state,timeSinceLastClick);
	}
	 
	 @Override
	 public void mouseReleased(int mouseX, int mouseY, int state)
	 {
		 super.mouseReleased(mouseX, mouseY, state);
	 }
	 
	@Override
	public void keyTyped(char character, int keycode)
	{	  
		try {
			super.keyTyped(character, keycode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 
	@Override
	public void onScroll(int i)
    {
		super.onScroll(i);
	}
	 
	@Override
	public void updateScreen()
	{
		super.updateScreen();
	}
	
	@Override
	public void onGuiClosed()
	{
		onPageClosed();
		super.onGuiClosed();
	}
	
	public void onPageClosed()
	{
		
	}
	
	 
	public WebPageData getWebPageData()
	{
		return webPageData;
	}
	
	
	

}
