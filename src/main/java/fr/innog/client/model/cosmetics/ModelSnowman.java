package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelBody;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelSnowman extends ModModelBody {
	

	public IModelCustom model;
		
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/snowman.png");
	

	public ModelSnowman()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/snowman.obj"));
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

	@Override
	public void applyTransformations(Entity entity) {
		 GL11.glScalef(1f, 1f, 1f);
		 GL11.glRotatef(180, 1, 0, 0);
		 GL11.glTranslatef(0.4f, 0.3f, 0f);		
	}
	 
		
		
}