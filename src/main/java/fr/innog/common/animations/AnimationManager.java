package fr.innog.common.animations;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.network.PacketCollection;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.structural.Tuple;
import net.minecraft.entity.player.EntityPlayer;

public class AnimationManager {

	private HashMap<Integer,Tuple<String,Class<? extends PlayerAnimator>>> registeredAnimations = new HashMap<Integer,Tuple<String,Class<? extends PlayerAnimator>>>();
	
	private int animationsIndex = 0;
	
	public void registerAnimation(String animationName, Class<? extends PlayerAnimator> anim)
	{
		System.out.println("animation " + anim.getName() + " registered with id " + animationsIndex);
		registeredAnimations.put(animationsIndex, new Tuple<String, Class<? extends PlayerAnimator>>(animationName,anim));
		animationsIndex++;
	}
	
	public PlayerAnimator getAnimation(int animationId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		PlayerAnimator animator = (PlayerAnimator)registeredAnimations.get(animationId).getItem2().getConstructor(int.class).newInstance(new Object[] {animationId});
		return animator;
	}
	
	public List<Tuple<String,Class<? extends PlayerAnimator>>> getAnimations() 
	{
		return new ArrayList<>(registeredAnimations.values());
	}
	
	public PlayerAnimator unlockAnimation(EntityPlayer player, int id)
	{
		try {
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

			PlayerAnimator animator = getAnimation(id);
			
			if(!animator.getClass().isAnnotationPresent(PremiumAnimation.class))
			{
				return null;
			}
			
			if(!isAnimationUnlocked(player, id))
			{
				playerData.getUnlockedAnimations().add(id);
				return animator;
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public PlayerAnimator lockAnimation(EntityPlayer player, int id)
	{
		try {
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

			PlayerAnimator animator = getAnimation(id);
			
			if(!animator.getClass().isAnnotationPresent(PremiumAnimation.class))
			{
				return null;
			}
			
			if(isAnimationUnlocked(player, id))
			{
				playerData.getUnlockedAnimations().remove(new Integer(id));
				return animator;
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean isAnimationUnlocked(EntityPlayer player, int id)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		if(playerData.getUnlockedAnimations().contains(id)) return true;
		return false;
	}
	
	public void playAnimation(EntityPlayer player, int id)
	{
		try {
			IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

			
			
			if(player.world.isRemote)
			{
				if(playerData.getCurrentPlayingAnimation() != null)
					if(!playerData.getCurrentPlayingAnimation().canPlayAnotherAnimationWhenActive())
						return;
				
				
				try {
					PlayerAnimator animator = getAnimation(id);
					
					if(animator.activeableOnlyServerSide()) return;

					playerData.setCurrentPlayingAnimation(getAnimation(id));
				} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				PacketCollection.playAnimation(id);
			}
			else
			{
				try {
					playerData.setCurrentPlayingAnimation(getAnimation(id));
				} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
				PacketCollection.playAnimation(player, player, id);
				playerData.updateRenderer();
			}

		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
	public void stopAnimation(EntityPlayer player)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		
				
		if(player.world.isRemote)
		{
			if(playerData.getCurrentPlayingAnimation() != null)
			{
				if(playerData.getCurrentPlayingAnimation().activeableOnlyServerSide())
				{
					return;
				}
			}
			playerData.setCurrentPlayingAnimation(null);
			PacketCollection.stopAnimation();
		}
		else
		{
			playerData.setCurrentPlayingAnimation(null);
			PacketCollection.stopAnimation(player, player);
			playerData.updateRenderer();
		}
	}
	
}
