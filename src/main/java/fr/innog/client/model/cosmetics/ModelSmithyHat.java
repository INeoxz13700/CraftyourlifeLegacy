package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelSmithyHat extends ModModelHead {
	

	public IModelCustom model;
		
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/smithy_hat.png");
	

	public ModelSmithyHat()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/smithy_hat.obj"));
	}
	
	 @Override
	 public void applyTransformations(Entity entity)
	 {
		 GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		 GL11.glRotatef(170.0F, 0.0F, 1.0F, 0.0F);
		 GL11.glTranslatef(0.0F, 0.8F, 0.0F);
		 

		 GL11.glScalef(0.04F, 0.04F, 0.04F);
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