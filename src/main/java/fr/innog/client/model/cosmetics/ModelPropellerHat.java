package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelPropellerHat extends ModModelHead {
	

	public IModelCustom[] models = new IModelCustom[] { AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/HeliceHat.obj")), AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/Helice.obj")) };
		
	
	public ResourceLocation[] textures = new ResourceLocation[] { new ResourceLocation("craftyourliferp","textures/cosmetics/HeliceHat.png"),   new ResourceLocation("craftyourliferp","textures/cosmetics/Helice.png")};
	
	private int rotation;

	public ModelPropellerHat()
	{

	}
	
	@Override
	public void render(float partTicks)
	{
		super.render(partTicks);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(textures[0]);
		models[0].renderAll();
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(textures[1]);
		
		GL11.glPushMatrix();
		GL11.glTranslatef(0, -0.55f, 0);
		GL11.glScalef(2f, 2f, 2f);
	
		GL11.glRotatef(rotation++,0f,1f, 0f);
		models[1].renderAll();
		GL11.glPopMatrix();
	}
	
	@Override
	public void applyTransformations(Entity entity)
	{
		GL11.glRotatef(180, 1, 0, 0);
	}


	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

	@Override
	public ResourceLocation getTexture() {
		return null;
	}
}