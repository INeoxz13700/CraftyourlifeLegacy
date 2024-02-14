package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelRabbitEars extends ModModelHead {
	

	public IModelCustom model;
		
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/RabbitEars.png");
	

	public ModelRabbitEars()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/RabbitEars.obj"));
	}
	
	@Override
	public void applyTransformations(Entity entity)
	{
		GL11.glRotatef(180, 1, 0, 0);		
		GL11.glTranslatef(0F, 0.01F, 0F);
			

		GL11.glScalef(0.95F, 0.95F, 0.95F);
	}
	
	@Override
	public void render(float partTicks)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();
	}

	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);		
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
}