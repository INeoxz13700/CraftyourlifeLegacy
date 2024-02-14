package fr.innog.client.ui;

import java.awt.Color;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ch.jamiete.mcping.MinecraftPing;
import ch.jamiete.mcping.MinecraftPingOptions;
import ch.jamiete.mcping.MinecraftPingReply;
import fr.innog.advancedui.gui.Anchor;
import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.guicomponents.GraphicObject;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.CallBackObject;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIImage;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.advancedui.utils.GuiUtils;
import fr.innog.common.ModCore;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.data.NewsData;
import fr.innog.utils.HTTPUtils;
import fr.innog.utils.TextureLocation;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MainMenuUI extends GuiScrollableView
{
		
	private UIColor logoColor = new UIColor(0,135,212,255);
	private UIColor logoColor_hover = new UIColor(26, 146, 214,255);
	
	private UIButton leftButton;
	private UIButton rightButton;
	
	private List<NewsData> news = new ArrayList<NewsData>();
	
	private MinecraftPingReply serverData;
	
	private boolean firstPing;
	
	private int lastUpdateTick;
	
	public MainMenuUI()
	{
		lastUpdateTick = 20 * 100;
		firstPing = false;
		news = getNews();
	}

	@Override
	public void initGui()
	{
		super.initGui();
		firstPing = false;
	}
	
	@Override
	public void initializeComponent() 
	{ 
		spacing = 10;
		
		this.contentRect = new UIRect(new UIColor(255,255,255,0));
		this.viewport = new UIRect(new UIColor(255,0,0,0));
		this.viewport.contourColor = new UIColor(0,0,255,0);
		this.scrollBarVertical = new UIScrollbarVertical(new UIColor(0,0,0,255),new UIColor(255,255,255,255));
		this.scrollBarVertical.setButtonColor(new UIColor(80,80,80));
		this.scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor(0,0,0,255),new UIColor(255,255,255,255));
		this.scrollBarHorizontal.setButtonColor(new UIColor(80,80,80));
		this.scrollBarVertical.setEnabled(false);
		this.scrollBarHorizontal.setEnabled(false);
		
		selectedScrollBar = scrollBarHorizontal;
				
		
		addComponent((new UIImage(TextureLocation.getTextureLocation("ui/background.png"))).setPosition(0, 0, width, height));

		
		if(mc.gameSettings.guiScale == 1)
		{
			this.setScrollViewPosition(10, (height - 120*2) / 2, width - 20, 120*2);

			addComponent(new UIImage(TextureLocation.getTextureLocation("ui/logo.png")).setAnchoredPosition(this, 5, 5, 35*2, 35*2, Anchor.TOP_LEFT));

			addComponent(new UIButton(Type.SQUARE, "Jouer en ligne", new UIRect(logoColor), new UIRect(logoColor_hover), new CallBackObject()
			{
				@Override
				public void call()
				{
	        		FMLClientHandler.instance().connectToServerAtStartup("play.craftyourliferp.fr", 25568);
				}
			}).setTextScale(1.25f)).setAnchoredPosition(this, -10, 15, 75*2, 20*2, Anchor.TOP_RIGHT);
		
			addComponent(new UIButton(Type.SQUARE, "Paramètres", new UIRect(new UIColor(0, 212, 138,255)), new UIRect(new UIColor(46, 209, 152)), new CallBackObject()
			{
				@Override
				public void call()
				{
		    		mc.displayGuiScreen(new GuiOptions(MainMenuUI.this, mc.gameSettings));
				}
			}).setTextScale(1.25f)).setAnchoredPosition(this, (int)(-85*2), 15, 75*2, 20*2, Anchor.TOP_RIGHT);
		
		
			UIButton discordButton = (UIButton) addComponent(new UIButton(Type.SQUARE, "DISCORD", new UIRect(new UIColor(118, 126, 138, 255)), new UIRect(new UIColor(143, 145, 148 ,255)), new CallBackObject()
			{
				@Override
				public void call()
				{
					try {
						Desktop.getDesktop().browse(new URI("https://discord.com/invite/4TbZdszJYV"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}).setTextScale(1.25f).setAnchoredPosition(this, (int)(-165*2), 15, 75*2, 20*2, Anchor.TOP_RIGHT));
			discordButton.setTextColor(new UIColor(150, 12, 237,255));

			UIImage icoImage = new UIImage(TextureLocation.getTextureLocation("ui/discord_ico.png"));
			icoImage.setColor(new UIColor(150, 12, 237,255));
			discordButton.setButtonIco(icoImage,10*2,Anchor.MIDDLE_LEFT, 5*2,0);
			
			
			int textWidth = GuiUtils.getStringWidth(mc, "Rejoignez les loading... connectés", 1.5f) + 5;

			addComponent(new ConnectedPlayerContainer(logoColor, new UIColor(Color.white), 1.5f).setPosition(viewport.getX() + (viewport.getWidth() - (textWidth + 5)) / 2, viewport.getY2() + 30, textWidth + 5, 25));
			
			addComponent(new UIText("© 2017-" + LocalDate.now().getYear() + " Craftyourliferp.fr - Tous droits réservés", new UIColor(Color.white), 1.5f).setAnchoredPosition(this, 0, -20, 500, 20, Anchor.BOTTOM_MIDDLE));
		
			addComponent(new UIText("version - " + ModCore.VERSION, new UIColor(Color.white), 1.5f).setAnchoredPosition(this, 1, 0, 200, 15, Anchor.BOTTOM_LEFT));
		}
		else
		{
			this.setScrollViewPosition(10, (height - 120) / 2, width - 20, 120);

			addComponent(new UIImage(TextureLocation.getTextureLocation("ui/logo.png")).setAnchoredPosition(this, 5, 5, 35, 35, Anchor.TOP_LEFT));
			
			addComponent(new UIButton(Type.SQUARE, "Jouer en ligne", new UIRect(logoColor), new UIRect(logoColor_hover), new CallBackObject()
			{
				@Override
				public void call()
				{
					if(!ModCore.debugMode) 
					{
						FMLClientHandler.instance().connectToServerAtStartup("play.craftyourliferp.fr", 25568);
					}
					else 
					{
						mc.displayGuiScreen(new GuiWorldSelection (MainMenuUI.this));
					}
				}
			})).setAnchoredPosition(this, -10, 15, 75, 20, Anchor.TOP_RIGHT);	
			
			addComponent(new UIButton(Type.SQUARE, "Paramètres", new UIRect(new UIColor(0, 212, 138,255)), new UIRect(new UIColor(46, 209, 152)), new CallBackObject()
			{
				@Override
				public void call()
				{
		    		mc.displayGuiScreen(new GuiOptions(MainMenuUI.this, mc.gameSettings));
				}
			})).setAnchoredPosition(this, -90, 15, 75, 20, Anchor.TOP_RIGHT);
			
			UIButton discordButton = (UIButton) addComponent(new UIButton(Type.SQUARE, "DISCORD", new UIRect(new UIColor(118, 126, 138, 255)), new UIRect(new UIColor(143, 145, 148 ,255)), new CallBackObject()
			{
				@Override
				public void call()
				{
					try {
						Desktop.getDesktop().browse(new URI("https://discord.com/invite/4TbZdszJYV"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}).setAnchoredPosition(this, -170, 15, 75, 20, Anchor.TOP_RIGHT));
			discordButton.setTextColor(new UIColor(150, 12, 237,255));

			UIImage icoImage = new UIImage(TextureLocation.getTextureLocation("ui/discord_ico.png"));
			icoImage.setColor(new UIColor(150, 12, 237,255));
			discordButton.setButtonIco(icoImage,10,Anchor.MIDDLE_LEFT, 5,0);
			
			int textWidth = GuiUtils.getStringWidth(mc, "Rejoignez les loading... connectés", 0.9f) + 5;

			addComponent(new ConnectedPlayerContainer(logoColor, new UIColor(Color.white), 0.9f).setPosition(viewport.getX() + (viewport.getWidth() - (textWidth + 5)) / 2, viewport.getY2() + 15, textWidth+5, 15));

			
			addComponent(new UIText("© 2017-" + LocalDate.now().getYear() + " Craftyourliferp.fr - Tous droits réservés", new UIColor(Color.white), 0.8f).setAnchoredPosition(this, 0, 0, 250, 20, Anchor.BOTTOM_MIDDLE));
		
			addComponent(new UIText("version - " + ModCore.VERSION, new UIColor(Color.white), 0.8f).setAnchoredPosition(this, 1, 0, 100, 10, Anchor.BOTTOM_LEFT));

		}
		
		leftButton = (UIButton) addComponent(new UIButton(Type.ROUNDED, "", TextureLocation.getTextureLocation("ui/left.png"), null, false, new CallBackObject()
		{
			@Override
			public void call()
			{
				MainMenuUI.this.scrollBarHorizontal.scroll(-1, 0.5f);
				MainMenuUI.this.scrollBarVertical.scroll(-1, 0.5f);

			}
		}).setPosition(viewport.getX(), viewport.getY() + (viewport.getHeight() - 25) / 2 , 25, 25));
		leftButton.setZIndex(100);
		
		rightButton = (UIButton) addComponent(new UIButton(Type.ROUNDED, "", TextureLocation.getTextureLocation("ui/right.png"), null, false, new CallBackObject()
		{
			@Override
			public void call()
			{
				MainMenuUI.this.scrollBarHorizontal.scroll(1, 0.5f);
				MainMenuUI.this.scrollBarVertical.scroll(1, 0.5f);

			}
		}).setPosition(viewport.getX() + viewport.getWidth() - 20, viewport.getY() + (viewport.getHeight() - 25) / 2 , 25, 25));
		rightButton.setZIndex(100);
		rightButton.setScale(1);
		
		
		
		updateScrollviewContents();
		

	}
	
	@Override
	protected void setScrollViewPosition(int x, int y, int width, int height) {
		this.viewport.setPosition(x, y, width, height);
		this.contentRect.setPosition(x, y, 0, height);
		
		this.scrollBarVertical.setPosition(this.viewport.getX2() + 1, y - 1, 6, height + 2);
		this.scrollBarHorizontal.setPosition(this.viewport.getX() - 1, this.viewport.getY2() + 1, width + 2, 6);
	}
	
	public void updateScrollviewContents() {
		updateContentElements();
	}
	
	public void updateContentElements() {
		
		this.contentRect.childs.clear();
	
		int i = 0;
		for(NewsData news : this.news)
		{
			if(mc.gameSettings.guiScale == 1)
			{
				NewsObject newsObject = new NewsObject(new UIImage(TextureLocation.downloadTexture(news.getImgUrl()))
						,new UIText(news.getTitle(), new UIColor(Color.white),2f)
						,new UIText(news.getMessage(), new UIColor(Color.white), 1.5f)
						,new UIText(news.getDate(), new UIColor(Color.white),1f));
					   
				newsObject.localPosX = 0;
				newsObject.localPosY = 0;
				

				addToContainer(newsObject.setPosition(0, 0, 150*2, this.viewport.getHeight()-20),i);
		
			}
			else
			{
				NewsObject newsObject = new NewsObject(new UIImage(TextureLocation.downloadTexture(news.getImgUrl()))
						,new UIText(news.getTitle(), new UIColor(Color.white),1f)
						,new UIText(news.getMessage(), new UIColor(Color.white), 0.6f)
						,new UIText(news.getDate(), new UIColor(Color.white),0.5f));
					   
				newsObject.localPosX = 0;
				newsObject.localPosY = 0;
				

				addToContainer(newsObject.setPosition(0, 0, 150, this.viewport.getHeight()-20),i);
			
			}
			++i;
		}
		
		

		this.contentRect.setWidth(this.contentRect.getWidth() + spacing);
	}
	
	public GraphicObject addToContainer(GraphicObject object, int index)
	{
		 if(object == null)  return null;
		
		 contentRect.addChild(object);
		 
		 positionElement(object, index);
		 
		 return object;
	}
	
	protected void positionElement(GraphicObject object, int index) {
		int initPos = (viewport.getWidth() - ((object.getWidth() + spacing) * news.size() + spacing)) / 2;
		if((object.getWidth() + spacing) * news.size() + spacing < viewport.getWidth())
		{
			object.localPosX = initPos + ((object.getWidth() + spacing) * index); 		
		}
		else
		{
			object.localPosX = this.contentRect.getWidth() + spacing;
		}
		object.localPosY = (this.contentRect.getHeight() - object.getHeight()) / 2;

		if (object.localPosX + object.getWidth() > this.contentRect.getWidth())
		{
			this.contentRect.setWidth(this.contentRect.getWidth() + object.getWidth() + this.spacing);
		}
	}
	
	@Override
	public void drawScreen(int x, int y, float partialTicks)
	{
		super.drawScreen(x, y, partialTicks);
		
		if(viewport.isHover(x, y))
		{
			leftButton.setVisible(true);
			rightButton.setVisible(true);
		}
		else
		{
			leftButton.setVisible(false);
			rightButton.setVisible(false);
		}
	}
	
	 @Override
	 public void updateScreen()
	 {
		  super.updateScreen();
		  
		  if(lastUpdateTick >= 20*100 && !firstPing)
		  {
			  firstPing = true;
			  lastUpdateTick = 0;
			  try {
				serverData = MinecraftPing.ping(new MinecraftPingOptions().setHostname("play.craftyourliferp.fr").setPort(25568));
			  } catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		  }
		  
		  this.lastUpdateTick++;
		  
		   if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))
	       {
	        	if(ClientProxy.modClient.getCurrentSession() != null)
	        	{
	        		FMLClientHandler.instance().connectToServerAtStartup("dev.craftyourliferp.fr", 25423);
	        	}

	       }

	 }
	 
	 private List<NewsData> getNews()
	 { 
		 try {
			 
			return parseToNewsObject(HTTPUtils.readFileFromUrl("https://api.craftyourliferp.fr/news.json"));
		 } catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<NewsData>();
		}
	 }
	 
	 private List<NewsData> parseToNewsObject(String content)
	 {
		List<NewsData> newsList = new ArrayList<NewsData>();
		Gson gson = new Gson();

        JsonElement jsonElement = gson.fromJson(content, JsonElement.class);

        JsonObject newsObject = jsonElement.getAsJsonObject().getAsJsonObject("news");

        for (Map.Entry<String, JsonElement> entry : newsObject.entrySet()) {

                JsonObject news = entry.getValue().getAsJsonObject();

                String title = news.get("title").getAsString();
                String message = news.get("message").getAsString();
                String date = news.get("date").getAsString();
                String imgUrl = news.get("imgurl").getAsString();
                
                NewsData newsData = new NewsData(title, message, date, imgUrl);
                
             
        		newsList.add(newsData);
        }
        return newsList;
	 }
	 
	 
	class ConnectedPlayerContainer extends GraphicObject
	{
		
		private UIRect container;
		
		private UIText text;
		
		public ConnectedPlayerContainer(UIColor containerColor, UIColor textColor, float textSize)
		{
			this.container = new UIRect(containerColor);
			this.text = new UIText("Rejoignez les loading... connectés", textColor, textSize);
			this.text.setTextCentered(true);
		}
		
		@Override
		public void draw(int x, int y)
		{
			super.draw(x, y);
			
			
			container.draw(x, y);
			
			text.draw(x, y);
			
			if(MainMenuUI.this.serverData != null)
			{
				int textWidth = GuiUtils.getStringWidth(mc, "Rejoignez les " + MainMenuUI.this.serverData.getPlayers().getOnline() + " connectés", text.getSize()) + 5;
				
				this.setPosition(viewport.getX() + (viewport.getWidth() - textWidth) / 2, getY(), textWidth, getHeight());
				
				text.setText("Rejoignez les " + MainMenuUI.this.serverData.getPlayers().getOnline() + " connectés");
			}
		}
		
		@Override
		public GraphicObject setPosition(int posX, int posY, int width, int height) {
			super.setPosition(posX, posY, width, height);
			container.setPosition(posX, posY, width, height);
			text.setPosition(posX+2, posY+5, width, height);
			return this;
		}
	}
	 
	class NewsObject extends GraphicObject
	{
		 private UIImage backgroundImg;
		 
		 private UIText newsTitle;
		 
		 private UIText newsDescription;
		 
		 private UIRect dateContainer;
		 
		 private UIImage calendarIco;
		 
		 private UIText newsDate;
		 
		 
		 public NewsObject(UIImage backgroundImg, UIText newsTitle, UIText newsDescription, UIText newsDate)
		 {
			 this.backgroundImg = backgroundImg;
			 this.newsTitle = newsTitle;
			 this.newsDescription = newsDescription;
			 this.newsDate = newsDate;
			 this.dateContainer = new UIRect(logoColor);
			 this.calendarIco = new UIImage(TextureLocation.getTextureLocation("ui/calendar.png"));
		 }
		 
		 public GraphicObject setPosition(int posX, int posY, int width, int height) {
			 super.setPosition(posX, posY, width, height);
			 
			 if(mc.gameSettings.guiScale == 1)
			 {
				 dateContainer.setPosition(posX + 5, posY+5, 50*2, 10*2);
				 calendarIco.setPosition(dateContainer.getX() + 2*2, dateContainer.getY()+(dateContainer.getHeight() - 6*2) / 2, 6*2, 6*2);
				 backgroundImg.setPosition(posX, posY, width, height);
				 newsDate.setPosition(calendarIco.getX() + 10*2, posY + 12,dateContainer.getWidth(),0);
				 newsTitle.setPosition(posX+5, posY + 40*2, width-10, 0);
				 newsDescription.setPosition(posX+6, posY + 55*2, width-10,0);
			 }
			 else
			 {
				 dateContainer.setPosition(posX + 5, posY+5, 50, 10);
				 calendarIco.setPosition(dateContainer.getX() + 2, dateContainer.getY()+(dateContainer.getHeight() - 6) / 2, 6, 6);
				 backgroundImg.setPosition(posX, posY, width, height);
				 newsDate.setPosition(calendarIco.getX() + 10, posY + 8, dateContainer.getWidth(), 0);
				 newsTitle.setPosition(posX+5, posY + 40, width-10,0);
				 newsDescription.setPosition(posX+6, posY + 55, width-10, 0);
			 }
			 return this;
		 }
		 
		public void draw(int x, int y)
		{
			super.draw(x, y);
			backgroundImg.draw(x, y);
			dateContainer.draw(x, y);
			backgroundImg.color = new UIColor(100,100,100,255);
			newsTitle.draw(x, y);
			newsDescription.draw(x, y);
			calendarIco.draw(x, y);
			newsDate.draw(x, y);
			newsTitle.draw(x, y);
			newsDescription.draw(x, y);
		}

	 }
	 
	
}
