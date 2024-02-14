package fr.innog.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public abstract class ModModelBase extends ModelBase {

	public abstract void render(float partTicks);
	
	public abstract void applyTransformations(Entity entity);
	
	public abstract ResourceLocation getTexture();
	
	public float field_74209_t;
	
	public float field_74208_u;
	
}