package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelNoelBonnet extends ModModelHead {
	

	public IModelCustom model;
		
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/noelBonnet.png");
	

	public ModelNoelBonnet()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/noelBonnet.obj"));
	}
	 
	 
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity)
	{ 
		super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
	}
	
	@Override
	public void applyTransformations(Entity entity)
	{
		 GL11.glRotatef(180, 0, 0, 1);
		 GL11.glTranslatef(-0.015F, 1.235F, -0.045F);
		 
		 GL11.glScalef(1.5F,1.5F,1.5F);
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