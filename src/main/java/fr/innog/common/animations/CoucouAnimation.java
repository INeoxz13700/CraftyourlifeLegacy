package fr.innog.common.animations;

import fr.innog.advancedui.utils.MathsUtils;
import fr.innog.capability.playercapability.IPlayer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class CoucouAnimation extends PlayerAnimator {
	
	public CoucouAnimation(int animationId) {
		super(animationId);
	}

	private int repeatTime = 0;
	
	protected AnimationParamater currentParam;

	protected AnimationParamater previousParam;
	
	
	@Override
	public void playAnimation(IPlayer data, EntityPlayer player) 
	{
		try {
			previousParam = currentParam.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		if(getCurrentState() == 0)
		{						
			currentParam.rotationZ -= speed;
			
			if(Math.abs(-60 - currentParam.rotationZ) <= 0.1)
			{
				nextState();
			}
		}
		else if(getCurrentState() == 1)
		{
			currentParam.rotationZ += speed;

			if(Math.abs(-10 - currentParam.rotationZ) <= 0.1)
			{

				if(repeatTime == 3)
				{
					nextState();
					
					return;
				}
					
				repeatTime++;
					
				previousState();
			}		
		}
		else if(getCurrentState() == 2)
		{
			stopAnimation(data);	
		}	
	}
	
	@Override
	public void applyAnimation(ModelBiped model, float partialTicks, IPlayer thePlayerCacheData, EntityPlayer player)
	{
        float interpolatedRotation = previousParam.rotationZ + (currentParam.rotationZ - previousParam.rotationZ) * partialTicks;

		
		model.bipedRightArm.rotateAngleX = MathsUtils.Deg2Rad * -170;
		
		model.bipedRightArm.rotateAngleZ = MathsUtils.Deg2Rad * interpolatedRotation;
	}

	@Override
	public void startAnimation(IPlayer data)
	{
		currentParam = new AnimationParamater(Vec3d.ZERO, Vec3d.ZERO);
		previousParam = new AnimationParamater(Vec3d.ZERO, Vec3d.ZERO);

		super.startAnimation(data);
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