package fr.innog.common.animations;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.utils.MathsUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.player.EntityPlayer;

@PremiumAnimation
public class FlossDanceAnimation extends PlayerAnimator {

	
	public FlossDanceAnimation(int animationId) {
		super(animationId);
	}

	@Override
	public void stopAnimation(IPlayer data) 
	{
		super.stopAnimation(data);
	}
	
	@Override
	public void startAnimation(IPlayer data) 
	{
		speed = 0.487F;
		super.startAnimation(data);
	}

	
	public void applyAnimation(ModelBiped model, float partialTicks, IPlayer data,EntityPlayer player) 
	{
        float interpolatedRotation = previousBipedBody.rotationZ + (bipedBody.rotationZ - previousBipedBody.rotationZ) * partialTicks;
	  
        model.bipedBody.rotateAngleZ = MathsUtils.Deg2Rad * interpolatedRotation;
	    
        interpolatedRotation = previousBipedLeftLeg.rotationZ + (bipedLeftLeg.rotationZ - previousBipedLeftLeg.rotationZ) * partialTicks;
		model.bipedLeftLeg.rotateAngleZ = MathsUtils.Deg2Rad * interpolatedRotation;
		
        interpolatedRotation = previousBipedRightLeg.rotationZ + (bipedRightLeg.rotationZ - previousBipedRightLeg.rotationZ) * partialTicks;
		model.bipedRightLeg.rotateAngleZ = MathsUtils.Deg2Rad * interpolatedRotation;
		
        interpolatedRotation = previousBipedHead.rotationZ + (bipedHead.rotationZ - previousBipedHead.rotationZ) * partialTicks;
		model.bipedHead.rotateAngleZ = MathsUtils.Deg2Rad * interpolatedRotation;
		
		model.bipedHeadwear.rotateAngleZ = model.bipedHead.rotateAngleZ;
		
		
        interpolatedRotation = previousBipedLeftArm.rotationX + (bipedLeftArm.rotationX - previousBipedLeftArm.rotationX) * partialTicks;
		model.bipedLeftArm.rotateAngleX = MathsUtils.Deg2Rad * interpolatedRotation;
		
        interpolatedRotation = previousBipedLeftArm.rotationZ + (bipedLeftArm.rotationZ - previousBipedLeftArm.rotationZ) * partialTicks;
		model.bipedLeftArm.rotateAngleZ = MathsUtils.Deg2Rad * interpolatedRotation;
		
        interpolatedRotation = previousBipedRightArm.rotationZ + (bipedRightArm.rotationZ - previousBipedRightArm.rotationZ) * partialTicks;
		model.bipedRightArm.rotateAngleZ = MathsUtils.Deg2Rad * interpolatedRotation;
		
        interpolatedRotation = previousBipedRightArm.rotationX + (bipedRightArm.rotationX - previousBipedRightArm.rotationX) * partialTicks;
		model.bipedRightArm.rotateAngleX = MathsUtils.Deg2Rad * interpolatedRotation;
	
		if(this.getCurrentState() == 1)
		{
			model.bipedBody.offsetX = -0.02f;
		}
		else if(this.getCurrentState() == 2)
		{
			model.bipedBody.offsetX = 0.02f;
		}
		else if(this.getCurrentState() == 3)
		{
			model.bipedBody.offsetX = -0.05f;
		}
		else if(this.getCurrentState() == 4)
		{
			model.bipedBody.offsetX = 0.015f;
		}
		else if(this.getCurrentState() == 5)
		{
			model.bipedBody.offsetX = -0.05f;
		}
		else if(this.getCurrentState() == 6)
		{
			model.bipedBody.offsetX = 0.015f;
		}
		else if(this.getCurrentState() == 7)
		{
			model.bipedBody.offsetX = -0.05f;
		}
		else if(this.getCurrentState() == 8)
		{
			model.bipedBody.offsetX = 0.02f;
		}
		
	}
	
	@Override
	public void playAnimation(IPlayer data,EntityPlayer player) 
	{

		try {
			previousBipedLeftArm = bipedLeftArm.clone();
			previousBipedRightArm = bipedRightArm.clone();
			previousBipedBody = bipedBody.clone();
			previousBipedHead = bipedHead.clone();
			previousBipedLeftLeg = bipedLeftLeg.clone();
			previousBipedRightLeg = bipedRightLeg.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		if(this.getCurrentState() == 0)
		{
			   bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, -20, speed);
			    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, 15, speed);
			    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, 20, speed);
			    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, 15, speed);

			    if (Math.abs(-20 - bipedLeftArm.rotationX) <= 0.01
			            && Math.abs(15 - bipedLeftArm.rotationZ) <= 0.01
			            && Math.abs(20 - bipedRightArm.rotationX) <= 0.01
			            && Math.abs(15 - bipedRightArm.rotationZ) <= 0.01)
			        nextState();
		}
		else if(this.getCurrentState() == 1)
		{
			bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, -5, speed);
		    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, 10, speed);
		    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, 10, speed);
		    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, -5, speed);

		    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, -20, speed);
		    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, 30, speed);
		    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, 20, speed);
		    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, 30, speed);

		    if (Math.abs(-5 - bipedBody.rotationZ) <= 0.01
		            && Math.abs(10 - bipedLeftLeg.rotationZ) <= 0.01
		            && Math.abs(10 - bipedRightLeg.rotationZ) <= 0.01
		            && Math.abs(-5 - bipedHead.rotationZ) <= 0.01
		            && Math.abs(-20 - bipedLeftArm.rotationX) <= 0.01
		            && Math.abs(30 - bipedLeftArm.rotationZ) <= 0.01
		            && Math.abs(20 - bipedRightArm.rotationX) <= 0.01
		            && Math.abs(30 - bipedRightArm.rotationZ) <= 0.01) return;
		        nextState();
		}
		else if(this.getCurrentState() == 2)
		{
			bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, 5, speed);
		    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, -10, speed);
		    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, -10, speed);
		    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, 5, speed);

		    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, -20, speed);
		    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, -15, speed);
		    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, 20, speed);
		    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, -15, speed);

		    if (Math.abs(5 - bipedBody.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedLeftLeg.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedRightLeg.rotationZ) <= 0.01
		            && Math.abs(5 - bipedHead.rotationZ) <= 0.01
		            && Math.abs(-20 - bipedLeftArm.rotationX) <= 0.01
		            && Math.abs(-15 - bipedLeftArm.rotationZ) <= 0.01
		            && Math.abs(20 - bipedRightArm.rotationX) <= 0.01
		            && Math.abs(-15 - bipedRightArm.rotationZ) <= 0.01)
		        nextState();

		}
		else if(this.getCurrentState() == 3)
		{
		    bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, -10, speed);
		    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, 15, speed);
		    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, 15, speed);
		    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, -7, speed);

		    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, -20, speed);
		    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, 40, speed);
		    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, 20, speed);
		    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, 40, speed);

		    if (Math.abs(-10 - bipedBody.rotationZ) <= 0.01
		            && Math.abs(15 - bipedLeftLeg.rotationZ) <= 0.01
		            && Math.abs(15 - bipedRightLeg.rotationZ) <= 0.01
		            && Math.abs(-7 - bipedHead.rotationZ) <= 0.01
		            && Math.abs(-20 - bipedLeftArm.rotationX) <= 0.01
		            && Math.abs(40 - bipedLeftArm.rotationZ) <= 0.01
		            && Math.abs(20 - bipedRightArm.rotationX) <= 0.01
		            && Math.abs(40 - bipedRightArm.rotationZ) <= 0.01)
		        nextState();
		}
		else if (this.getCurrentState() == 4) {
		    bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, 5, speed);
		    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, -10, speed);
		    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, -10, speed);
		    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, 5, speed);

		    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, 20, speed);
		    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, -30, speed);
		    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, -20, speed);
		    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, -30, speed);

		    if (Math.abs(5 - bipedBody.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedLeftLeg.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedRightLeg.rotationZ) <= 0.01
		            && Math.abs(5 - bipedHead.rotationZ) <= 0.01
		            && Math.abs(20 - bipedLeftArm.rotationX) <= 0.01
		            && Math.abs(-30 - bipedLeftArm.rotationZ) <= 0.01
		            && Math.abs(-20 - bipedRightArm.rotationX) <= 0.01
		            && Math.abs(-30 - bipedRightArm.rotationZ) <= 0.01)
		        nextState();
		}
		else if(this.getCurrentState() == 5)
		{
			   bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, -5, speed);
			    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, 10, speed);
			    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, 10, speed);
			    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, -5, speed);

			    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, 20, speed);
			    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, 20, speed);
			    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, -20, speed);
			    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, 20, speed);

			    if (Math.abs(-5 - bipedBody.rotationZ) <= 0.01
			            && Math.abs(10 - bipedLeftLeg.rotationZ) <= 0.01
			            && Math.abs(10 - bipedRightLeg.rotationZ) <= 0.01
			            && Math.abs(-5 - bipedHead.rotationZ) <= 0.01
			            && Math.abs(20 - bipedLeftArm.rotationX) <= 0.01
			            && Math.abs(20 - bipedLeftArm.rotationZ) <= 0.01
			            && Math.abs(-20 - bipedRightArm.rotationX) <= 0.01
			            && Math.abs(20 - bipedRightArm.rotationZ) <= 0.01)
			        nextState();
		}
		else if(this.getCurrentState() == 6)
		{
		    bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, 5, speed);
		    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, -10, speed);
		    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, -10, speed);
		    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, 5, speed);

		    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, 20, speed);
		    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, -30, speed);
		    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, -20, speed);
		    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, -30, speed);

		    if (Math.abs(5 - bipedBody.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedLeftLeg.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedRightLeg.rotationZ) <= 0.01
		            && Math.abs(5 - bipedHead.rotationZ) <= 0.01
		            && Math.abs(20 - bipedLeftArm.rotationX) <= 0.01
		            && Math.abs(-30 - bipedLeftArm.rotationZ) <= 0.01
		            && Math.abs(-20 - bipedRightArm.rotationX) <= 0.01
		            && Math.abs(-30 - bipedRightArm.rotationZ) <= 0.01)
		        nextState();
		}
		else if(this.getCurrentState() == 7)
		{
			   bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, -10, speed);
			    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, 15, speed);
			    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, 15, speed);
			    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, -10, speed);

			    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, -20, speed);
			    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, 40, speed);
			    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, 20, speed);
			    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, 40, speed);

			    if (Math.abs(-10 - bipedBody.rotationZ) <= 0.01
			            && Math.abs(15 - bipedLeftLeg.rotationZ) <= 0.01
			            && Math.abs(15 - bipedRightLeg.rotationZ) <= 0.01
			            && Math.abs(-10 - bipedHead.rotationZ) <= 0.01
			            && Math.abs(-20 - bipedLeftArm.rotationX) <= 0.01
			            && Math.abs(40 - bipedLeftArm.rotationZ) <= 0.01
			            && Math.abs(20 - bipedRightArm.rotationX) <= 0.01
			            && Math.abs(40 - bipedRightArm.rotationZ) <= 0.01)
			        nextState();
		}
		else if(this.getCurrentState() == 8)
		{
			bipedBody.rotationZ = MathsUtils.Lerp(bipedBody.rotationZ, 5, speed);
		    bipedLeftLeg.rotationZ = MathsUtils.Lerp(bipedLeftLeg.rotationZ, -10, speed);
		    bipedRightLeg.rotationZ = MathsUtils.Lerp(bipedRightLeg.rotationZ, -10, speed);
		    bipedHead.rotationZ = MathsUtils.Lerp(bipedHead.rotationZ, 5, speed);

		    bipedLeftArm.rotationX = MathsUtils.Lerp(bipedLeftArm.rotationX, -20, speed);
		    bipedLeftArm.rotationZ = MathsUtils.Lerp(bipedLeftArm.rotationZ, -15, speed);
		    bipedRightArm.rotationX = MathsUtils.Lerp(bipedRightArm.rotationX, 20, speed);
		    bipedRightArm.rotationZ = MathsUtils.Lerp(bipedRightArm.rotationZ, -15, speed);

		    if (Math.abs(5 - bipedBody.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedLeftLeg.rotationZ) <= 0.01
		            && Math.abs(-10 - bipedRightLeg.rotationZ) <= 0.01
		            && Math.abs(5 - bipedHead.rotationZ) <= 0.01
		            && Math.abs(-20 - bipedLeftArm.rotationX) <= 0.01
		            && Math.abs(-15 - bipedLeftArm.rotationZ) <= 0.01
		            && Math.abs(20 - bipedRightArm.rotationX) <= 0.01
		            && Math.abs(-15 - bipedRightArm.rotationZ) <= 0.01)
		        this.setState(2);  // Mettre à jour l'état ici si nécessaire
		}

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