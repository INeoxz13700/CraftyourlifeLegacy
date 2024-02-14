package fr.innog.common.animations;

import net.minecraft.util.math.Vec3d;

public class AnimationParamater implements Cloneable {

	
	public float rotationX;
	public float rotationY;
	public float rotationZ;
	
	public float translationX;
	public float translationY;
	public float translationZ;

	
	public AnimationParamater()
	{
		this.rotationX = 0;
		this.rotationY = 0;
		this.rotationZ = 0;
		
		this.translationX = 0;
		this.translationY = 0;
		this.translationZ = 0;
	}
	
	public AnimationParamater(Vec3d initRotation, Vec3d initTranslation)
	{
		this.rotationX = (float)initRotation.x;
		this.rotationY = (float)initRotation.y;
		this.rotationZ = (float)initRotation.z;
		
		this.translationX = (float)initRotation.x;
		this.translationY = (float)initRotation.y;
		this.translationZ = (float)initRotation.z;
	}
	
    @Override
    public AnimationParamater clone() throws CloneNotSupportedException {
        try {
            return (AnimationParamater) super.clone();
        } catch (CloneNotSupportedException e) {
        	   System.err.println("La classe n'implémente pas Cloneable, le clonage doit être pris en charge.");
               return null;
        }
    }    
	
	
}
