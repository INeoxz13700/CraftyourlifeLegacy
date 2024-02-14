package fr.innog.common.animations;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.utils.MathsUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

public class HandUpAnimation extends PlayerAnimator {
	
	
	public HandUpAnimation(int animationId) {
		super(animationId);
	}

	@Override
	public void playAnimation(IPlayer data, EntityPlayer player) 
	{
		
	}
	
	@Override
	public void applyAnimation(ModelBiped model, float partialTicks,IPlayer data, EntityPlayer player) 
	{
		model.bipedLeftArm.rotateAngleX = -(MathsUtils.Deg2Rad * 180);
		model.bipedLeftArm.rotateAngleZ = MathsUtils.Deg2Rad * 10;
		
		model.bipedRightArm.rotateAngleX = -(MathsUtils.Deg2Rad * 180);
		model.bipedRightArm.rotateAngleZ =  -MathsUtils.Deg2Rad * 10;
	}

	@Override
	public boolean activeableOnlyServerSide() {
		return false;
	}

	@Override
	public boolean canPlayAnotherAnimationWhenActive() {
		return true;
	}


}