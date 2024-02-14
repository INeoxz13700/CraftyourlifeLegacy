package fr.innog.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import fr.innog.advancedui.guicomponents.UIButton;
import fr.innog.common.ModCore;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.utils.ScheduledCallback;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TicksHandler {

    public static int elapsedTicks;
    
    private static ConcurrentLinkedQueue<ScheduledCallback> scheduledCallback = new ConcurrentLinkedQueue<>();
	
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void tick(TickEvent.ServerTickEvent event)
    {
    	switch(event.phase)
    	{
    		case START:
    		{
    			ModCore.getPackethandler().handleServerPackets();
    			
    			if(FMLCommonHandler.instance().getMinecraftServerInstance().isDedicatedServer())
    			{
    				List<ScheduledCallback> toRemove = new ArrayList<>();
    				
    				for(ScheduledCallback sc : scheduledCallback)
    				{
    					if(sc.tick()) toRemove.add(sc);
    				}
    				
    				scheduledCallback.removeAll(toRemove);
    			}
  
    			break;
    		}
    		case END:
    		{
    			break;
    		}
    	}
    }
    
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SideOnly(Side.CLIENT)
    public void tick(TickEvent.ClientTickEvent event)
    {
    	if(ClientProxy.forceExit)
    	{
    		for(int i = 0; i < 50; i++)System.out.print("#");
    		System.out.println("");
    		System.out.print("\n");
    		System.out.println("Les cheats sont interdit sur CYLRP! \n");
    		System.out.println("");
    		for(int i = 0; i < 50; i++)System.out.print("#");
    		ClientProxy.exit();
    	}
    	
    	switch(event.phase)
    	{
			case START:
			{
				ModCore.getPackethandler().handleClientPackets();
				
				List<ScheduledCallback> toRemove = new ArrayList<>();
				
				for(ScheduledCallback sc : scheduledCallback)
				{
					if(sc.tick()) toRemove.add(sc);
				}
				
				scheduledCallback.removeAll(toRemove);

				break;
			}
			case END:
			{
		        ++elapsedTicks;
				break;
			}
    	}
    }
    
    public static void scheduleCallback(int delay, UIButton.CallBackObject callback)
    {
    	scheduledCallback.add(new ScheduledCallback(delay, callback));
    }
	
}
