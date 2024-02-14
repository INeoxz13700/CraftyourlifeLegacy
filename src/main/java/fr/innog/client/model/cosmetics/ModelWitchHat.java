package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelWitchHat extends ModModelHead {

	public IModelCustom model;
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/WitchHat.png");
	

	public ModelWitchHat()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/WitchHat.obj"));
	}
	 
	@Override
	public void render(float partTicks)
	{
		super.render(partTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();
	}
	
	@Override
	public void applyTransformations(Entity entity)
	{
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		GL11.glTranslatef(0.0F, 0.77F, 0.07F);
		
		
		GL11.glScalef(1F, 1F, 1F);
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
		
}