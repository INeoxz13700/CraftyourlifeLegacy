package fr.innog.client.ui.ingame;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import fr.innog.advancedui.gui.GuiScrollableView;
import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.advancedui.guicomponents.UIButton.Type;
import fr.innog.advancedui.guicomponents.UIColor;
import fr.innog.advancedui.guicomponents.UIRect;
import fr.innog.advancedui.guicomponents.UIScrollbarHorizontal;
import fr.innog.advancedui.guicomponents.UIScrollbarVertical;
import fr.innog.advancedui.guicomponents.UIText;
import fr.innog.common.ModCore;
import fr.innog.common.animations.PlayerAnimator;
import fr.innog.common.animations.PremiumAnimation;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.ui.remote.IRemoteUI;
import fr.innog.ui.remote.RemoteAnimationUI;
import fr.innog.ui.remote.RemoteUIProcessor;
import fr.innog.utils.structural.Tuple;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.server.permission.PermissionAPI;

public class AnimationUI extends GuiScrollableView implements IRemoteUI {
	
	private RemoteAnimationUI remoteUI;
	
	@Override
	public void initGui() 
	{
		setWindowSize(80, 120);
		setWindowPosition(5, 5);
		super.initGui();
	}

	@Override
	public void initializeComponent() 
	{ 
		
		guiRect = (UIRect) new UIRect(new UIColor("#2d2d2e")).setPosition(getWindowPosX(), getWindowPosY(), getWindowWidth(), getWindowHeight());
		
		UIText text = addComponent(new UIText("Animations",new UIColor(255,255,255),1.1f));
		text.setPosition(getWindowPosX() + (getWindowWidth() - 100) / 2, getWindowPosY() + 4,100,0);
		text.setTextCentered(true);
		
		addComponent(new UIRect(new UIColor(36, 46, 227,180)).setPosition(getWindowPosX() + (getWindowWidth() - (getWindowWidth()-20))  / 2,  getWindowPosY() + 14 , getWindowWidth()-20, 1));
		
		contentRect = new UIRect(new UIColor(0,0,0,0));
		viewport = new UIRect(new UIColor(0,0,0,0));
		
		this.scrollBarVertical = new UIScrollbarVertical(new UIColor("#3A3B37"),new UIColor("#3A3B37"));
		
		this.scrollBarHorizontal = new UIScrollbarHorizontal(new UIColor("#3A3B37"),new UIColor("#3A3B37"));
		
		this.selectedScrollBar = scrollBarVertical;
		
		guiRect.color = new UIColor(32,32,32,100);
		
		setScrollViewPosition(getWindowPosX(), getWindowPosY() + 25,getWindowWidth(),130);

		updateScrollviewContents();
	}
	
	@Override
    protected void keyTyped(char character, int keycode)
    {
        if (keycode == 1)
        {
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
            return;
        }
        
        try {
			super.keyTyped(character, keycode);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void updateScrollviewContents()
	{
		contentRect.childs.clear();
		
		addToContainer(new UIButton(Type.SQUARE, "Stop animation",new UIRect(new UIColor(22, 22, 23,120)), new UIRect(new UIColor(51, 51, 156)), new UIButton.CallBackObject()
		{
			public void call()
			{
				if(ClientProxy.modClient.getLocalPlayerCapability().getCurrentPlayingAnimation() != null)
				{
					ModCore.getAnimationManager().stopAnimation(mc.player);
				}
			}
		}).setPosition(0,0,getWindowWidth(), 10));
		
		int i = 0;
		
		List<Integer> unlockedAnimations = (List<Integer>)remoteUI.getCacheData().getData("UnlockedAnimations").getData();

		for(Tuple<String, Class<? extends PlayerAnimator>> anim : ModCore.getAnimationManager().getAnimations())
		{
			final int id = i;

			try {
				PlayerAnimator animator = anim.getItem2().getConstructor(int.class).newInstance(new Object[] { id });
			
				if(animator.getClass().isAnnotationPresent(PremiumAnimation.class))
				{
					if(!unlockedAnimations.contains(id))
					{
						i++;
						continue;
					}
				}
				
				if(animator.activeableOnlyServerSide())
				{
					i++;
					continue;
				}
				
				addToContainer(new UIButton(Type.SQUARE, anim.getItem1(), new UIRect(new UIColor(22, 22, 23,120)), new UIRect(new UIColor(51, 51, 156)), new UIButton.CallBackObject()
				{
					public void call()
					{
						ModCore.getAnimationManager().playAnimation(mc.player, id);
					}
				}).setPosition(0,0,getWindowWidth(), 10));
				
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				i++;
				continue;
			}
			i++;
		}
		


		super.updateScrollviewContents();
	}

	@Override
	public void setRemoteUI(RemoteUIProcessor remoteUI) {
		this.remoteUI = (RemoteAnimationUI) remoteUI;
	}
	
	
}
