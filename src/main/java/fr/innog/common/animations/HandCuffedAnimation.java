package fr.innog.common.animations;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.utils.MathsUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

public class HandCuffedAnimation extends PlayerAnimator 
{

	public HandCuffedAnimation(int animationId) {
		super(animationId);
	}

	@Override
	public void applyAnimation(ModelBiped model, float partialTicks, IPlayer data, EntityPlayer player) 
	{
		if(player.isSneaking())
		{
			model.bipedLeftArm.rotateAngleX = MathsUtils.Deg2Rad * 50;
			model.bipedRightArm.rotateAngleX = MathsUtils.Deg2Rad * 50;

			model.bipedLeftArm.rotateAngleZ = MathsUtils.Deg2Rad * 50;
			model.bipedRightArm.rotateAngleZ = -MathsUtils.Deg2Rad * 50;
		}
		else
		{
			model.bipedLeftArm.rotateAngleX = MathsUtils.Deg2Rad * 25;
			model.bipedRightArm.rotateAngleX = MathsUtils.Deg2Rad * 25;

			model.bipedLeftArm.rotateAngleZ = MathsUtils.Deg2Rad * 25;
			model.bipedRightArm.rotateAngleZ = -MathsUtils.Deg2Rad * 25;
		}
	}

	@Override
	public boolean activeableOnlyServerSide() {
		return true;
	}

	@Override
	public boolean canPlayAnotherAnimationWhenActive() {
		return false;
	}

}