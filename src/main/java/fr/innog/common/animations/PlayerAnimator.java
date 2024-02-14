package fr.innog.common.animations;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.event.ModelPlayerEvent;
import fr.innog.common.ModCore;
import fr.innog.network.PacketCollection;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = ModCore.MODID, value = Side.CLIENT)
public abstract class PlayerAnimator {

	private int currentState = 0;
	
	protected boolean animationIsPlaying = false;
		
	protected float speed = 5F;
	
	public AnimationParamater previousBipedBody = new AnimationParamater();
	public AnimationParamater bipedBody = new AnimationParamater();
	
	public AnimationParamater previousBipedLeftLeg = new AnimationParamater();
	public AnimationParamater bipedLeftLeg = new AnimationParamater();
	
	public AnimationParamater previousBipedRightLeg = new AnimationParamater();
	public AnimationParamater bipedRightLeg = new AnimationParamater();
	
	public AnimationParamater previousBipedHead = new AnimationParamater();
	public AnimationParamater bipedHead = new AnimationParamater();
	
	public AnimationParamater previousBipedLeftArm = new AnimationParamater();
	public AnimationParamater bipedLeftArm = new AnimationParamater();
	
	public AnimationParamater previousBipedRightArm = new AnimationParamater();
	public AnimationParamater bipedRightArm = new AnimationParamater();
	
	private int animationId;
	
	public PlayerAnimator(int animationId)
	{
		this.animationId = animationId;
	}
	
	public int getAnimationId()
	{
		return animationId;
	}
	
	public void nextState()
	{
		currentState++;
	}
	
	public void previousState()
	{
		currentState--;
	}
	
	public int getCurrentState()
	{
		return currentState;
	}
	
	public void setState(int state)
	{
		this.currentState = state;
	}
	
	public abstract boolean activeableOnlyServerSide();
	
	
	public void playAnimation(IPlayer extendedPlayer, EntityPlayer player)
	{

	}
	
	
	
	public void startAnimation(IPlayer playerData)
	{
		animationIsPlaying = true;
	}
	
	public void stopAnimation(IPlayer playerData)	{
		animationIsPlaying = false;
		playerData.setCurrentPlayingAnimation(null);
		
		PacketCollection.stopAnimation();
	}
	
	public abstract boolean canPlayAnotherAnimationWhenActive();
	

	public final boolean isAnimationPlaying()
	{
		return animationIsPlaying;
	}
	
	@SideOnly(Side.CLIENT)
	public void applyAnimation(ModelBiped model, float partialTicks, IPlayer thePlayerCacheData, EntityPlayer player)
	{

	}
	
	
    @SideOnly(Side.CLIENT)
    public  void copyModel(ModelRenderer source, ModelRenderer dest)
    {
        dest.rotateAngleX = source.rotateAngleX;
        dest.rotateAngleY = source.rotateAngleY;
        dest.rotateAngleZ = source.rotateAngleZ;
        dest.rotationPointX = source.rotationPointX;
        dest.rotationPointY = source.rotationPointY;
        dest.rotationPointZ = source.rotationPointZ;
        
        dest.offsetX = source.offsetX;
        dest.offsetY = source.offsetY;
        dest.offsetZ = source.offsetZ;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onAnimation(PlayerTickEvent event)
    {
    	EntityPlayer player = event.player;
    	
    	if(!player.world.isRemote) return;
    	
    	IPlayer playerData = MinecraftUtils.getPlayerCapability(player);

    	if(playerData.getCurrentPlayingAnimation() != null)
    	{
    		if(playerData.getCurrentPlayingAnimation().isAnimationPlaying())
    		{
    			playerData.getCurrentPlayingAnimation().playAnimation(playerData, player);
    		}
    		else
    		{
    			playerData.getCurrentPlayingAnimation().startAnimation(playerData);
    		}
    	}
    }
    
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onAnimation(ModelPlayerEvent.SetupAngles.Post event)
    {
    	EntityPlayer player = event.getEntityPlayer();
    	IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
    	
    	if(playerData.getCurrentPlayingAnimation() != null)
    	{
    		playerData.getCurrentPlayingAnimation().applyAnimation(event.getModelPlayer(),event.getPartialTicks(),playerData,player);
    	}
    }


}
