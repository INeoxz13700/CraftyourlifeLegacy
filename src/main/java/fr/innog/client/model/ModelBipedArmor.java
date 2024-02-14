package fr.innog.client.model;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

public class ModelBipedArmor extends ModelBiped 
{
	    private ModelBiped source;

	    public ModelBipedArmor(ModelBiped source, float modelSize)
	    {
	        super(modelSize);
	        this.source = source;
	    }

	    @Override
	    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
	    {
	        copyModelAngles(source.bipedHeadwear, bipedHeadwear);
	        copyModelAngles(source.bipedHead, bipedHead);
	        copyModelAngles(source.bipedBody, bipedBody);
	        copyModelAngles(source.bipedRightArm, bipedRightArm);
	        copyModelAngles(source.bipedLeftArm, bipedLeftArm);
	        copyModelAngles(source.bipedRightLeg, bipedRightLeg);
	        copyModelAngles(source.bipedLeftLeg, bipedLeftLeg);
	    }
}