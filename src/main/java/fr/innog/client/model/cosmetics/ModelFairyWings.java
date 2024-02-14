package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelBody;
import fr.innog.common.ModCore;
import fr.innog.handler.TicksHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelFairyWings extends ModModelBody {
	

	public IModelCustom model;

	
	public ModelFairyWings() 
	{
		model = (IModelCustom)AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/fairy_wings.obj"));
	}
	

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void render(float partTicks)
	{
		float baseRot = 20;
		float animation = (float) Math.abs(Math.sin((TicksHandler.elapsedTicks+partTicks) / 20));
				
			
		float aile1Rotation = baseRot - animation* -40;

		baseRot = 70;
		float aile2Rotation = baseRot + animation * -40;
		
		super.render(partTicks);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());
		
		
		 GL11.glPushMatrix();
		 GL11.glTranslatef(0.05f, 0.5f, 0.2f);

		 
		 GL11.glRotatef(100, 1, 0, 0);
		 GL11.glRotatef(aile1Rotation, 0, 0, 1);
		 model.renderAll();
		 GL11.glPopMatrix();
		 
		 GL11.glPushMatrix();
		 GL11.glTranslatef(-0.05f, 0.5f, 0.2f);

		 GL11.glRotatef(100, 1, 0, 0);
		 GL11.glRotatef(aile2Rotation, 0, 0, 1);
		 GL11.glPushMatrix();
		 GL11.glRotatef(90, 0, 0, 1);
		 model.renderAll();
		 GL11.glPopMatrix();
		 GL11.glPopMatrix();
	}


	@Override
	public ResourceLocation getTexture() {
		return new ResourceLocation(ModCore.MODID,"textures/cosmetics/fairy_wings.png");
	}


	@Override
	public void applyTransformations(Entity entity) {
		 if(entity.isSneaking())
		 {
			 GL11.glRotatef(20, 1, 0, 0);
		 }
	}
	

}