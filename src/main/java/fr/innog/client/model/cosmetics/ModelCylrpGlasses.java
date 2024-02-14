package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelCylrpGlasses extends ModModelHead {
	

	public IModelCustom model;
		
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/CYLRPGlasses.png");
	

	public ModelCylrpGlasses()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/CYLRPGlasses.obj"));
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);		
	}
	
	@Override
	public void applyTransformations(Entity entity)
	{
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glTranslatef(0, -0.01F, 0F);
		GL11.glScalef(1F, 1F, 1F);

	}
	
	
	@Override
	public void render(float partTicks)
	{
		super.render(partTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
}