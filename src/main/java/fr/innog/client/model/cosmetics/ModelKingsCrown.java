package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelKingsCrown extends ModModelHead {
	
	public IModelCustom model;
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp", "textures/cosmetics/kings_crown.png");	
	
	public ModelKingsCrown() {
	    this.model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp", "models/cosmetics/kings_crown.obj"));
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	 
	@Override
	public void applyTransformations(Entity entity)
	{
		 GL11.glRotatef(180,0.0F, 0.0F, 1.0F);

		 GL11.glTranslatef(0F, 0.8F, 0.0F);
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