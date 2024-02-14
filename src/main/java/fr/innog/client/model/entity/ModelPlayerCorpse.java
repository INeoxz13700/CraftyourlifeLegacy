package fr.innog.client.model.entity;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class ModelPlayerCorpse extends ModelBiped {

	public ModelPlayerCorpse() {
		super(0.0f, 0.0f, 64, 32);
	}
	
	@Override
	public void setLivingAnimations(final EntityLivingBase e, final float f1, final float f2, final float f3) {

	}

	@Override
	public void setRotationAngles(final float f1, final float f2, final float f3, final float f4, final float f5,final float f6, final Entity e) 
	{
		super.setRotationAngles(f1, f2, f3, f4, f5, f6, e);
		this.bipedRightLeg.rotateAngleZ = 0.2f;
		this.bipedLeftLeg.rotateAngleZ = -0.2f;
		this.bipedRightArm.rotateAngleZ = 0.3f;
		this.bipedLeftArm.rotateAngleZ = -0.3f;
		this.bipedRightArm.rotateAngleY = 1.25f;
		this.bipedLeftArm.rotateAngleY = -0.5f;
		this.bipedRightArm.rotateAngleX = 0f;
		this.bipedLeftArm.rotateAngleX = 0f;
		this.bipedHead.rotateAngleX = 0f;
		this.bipedHead.rotateAngleY = 0f;
		this.bipedHead.rotateAngleZ = 0f;
	}
	
}