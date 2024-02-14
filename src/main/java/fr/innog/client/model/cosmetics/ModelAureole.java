package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelAureole extends ModModelHead {
	
	private final ModelRenderer bone2;
	
	private static ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/aureole.png");

	public ModelAureole() {
		textureWidth = 32;
		textureHeight = 32;

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -11.0F, -5.0F);
		bone2.cubeList.add(new ModelBox(bone2, 18, 2, -3.0F, -1.0F, 0.0F, 6, 1, 1, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 18, 0, -3.0F, -1.0F, 9.0F, 6, 1, 1, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, 3.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, 3.0F, -1.0F, 8.0F, 1, 1, 1, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -4.0F, -1.0F, 1.0F, 1, 1, 1, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -4.0F, -1.0F, 8.0F, 1, 1, 1, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, -5.0F, -1.0F, 2.0F, 1, 1, 6, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 0, 0, 4.0F, -1.0F, 2.0F, 1, 1, 6, 0.0F));
	}
	 
	public void render(float partTicks)
	{
		super.render(partTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		bone2.render(0.01f);
	}
	
	@Override
	public void applyTransformations(Entity entity)
	{
		GL11.glTranslatef(0, -0.1F, 0F);
		GL11.glScalef(5F, 5F, 5F);
	}
	 
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
}