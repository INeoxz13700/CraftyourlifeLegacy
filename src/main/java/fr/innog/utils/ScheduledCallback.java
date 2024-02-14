package fr.innog.utils;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.common.ModCore;

public class ScheduledCallback {

	public UIButton.CallBackObject callback;
	
	private int excuteAfterdelay; // in milliseconds
	
	private int ticks;
	
	public ScheduledCallback(int delay, UIButton.CallBackObject callback)
	{
		this.callback = callback;
		this.excuteAfterdelay = delay;
	}
	
	public void executeCallback()
	{
		if(callback != null)
		{
			callback.call();
		}
	}
	
	public boolean tick()
	{
		ticks++;
		
		if(ticks / 20F >= excuteAfterdelay / 1000F)
		{
			executeCallback();
			return true;
		}
		return false;
	}
	
}
