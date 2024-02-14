package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelMarioHat extends ModModelHead {
	
	private static ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/mario_hat.png");

	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone4;

	public ModelMarioHat() {
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 19.4167F, 0.1667F);
		bone.cubeList.add(new ModelBox(bone, 0, 1, 3.5F, -1.4167F, -2.6667F, 1, 2, 9, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 1, -4.5F, -1.4167F, -2.6667F, 1, 2, 9, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 6, -1.5F, -0.4167F, 5.3333F, 3, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 28, 1.5F, -1.4167F, 5.3333F, 2, 2, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 28, -3.5F, -1.4167F, 5.3333F, 2, 2, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 31, -4.5F, -3.4167F, -1.6667F, 9, 2, 8, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 40, 16, -4.5F, -0.4167F, -5.6667F, 9, 1, 3, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 48, 13, -3.5F, -0.4167F, -6.6667F, 7, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 48, 4, -3.5F, -1.4167F, -5.6667F, 7, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 40, 0, -4.5F, -1.4167F, -4.6667F, 9, 1, 2, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 40, 0, -4.5F, -1.4167F, -4.6667F, 9, 1, 2, 0.0F));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, -3.9167F, 1.8333F);
		bone.addChild(bone2);
		

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(0.0F, 0.6F, 0.7F);
		bone2.addChild(bone4);
		setRotationAngle(bone4, -0.3927F, 0.0F, 0.0F);
		bone4.cubeList.add(new ModelBox(bone4, 0, 47, -4.0F, -1.0F, -4.5F, 8, 5, 8, 0.0F));
		bone4.cubeList.add(new ModelBox(bone4, 48, 24, -3.8F, -1.0F, -3.62F, 8, 5, 0, -0.9F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, -1.6F, 0.5F, -4.62F, 1, 1, 1, 0.0F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, 0.6F, 0.5F, -4.62F, 1, 1, 1, 0.0F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, 1.0F, 0.3F, -4.82F, 1, 2, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, 1.1F, 2.3F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, -2.1F, 2.3F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, -2.1F, 1.9F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, 1.1F, 1.9F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, 1.3F, 1.9F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, -2.3F, 1.9F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, -2.3F, 2.3F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, 1.3F, 2.3F, -4.82F, 1, 1, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, -2.0F, 0.3F, -4.82F, 1, 2, 1, -0.2F));
		bone4.cubeList.add(new ModelBox(bone4, 10, 36, -0.5F, 1.0F, -4.52F, 1, 1, 1, 0.1F));
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	 
	@Override
	public void render(float partTicks)
	{
		super.render(partTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		bone.render(0.01f);
	}
	
	@Override
	public void applyTransformations(Entity entity)
	{
	    GL11.glTranslatef(0.0F, -1.5F, -0.13F);
	    GL11.glScalef(6F,6F, 6F);
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
	 

}