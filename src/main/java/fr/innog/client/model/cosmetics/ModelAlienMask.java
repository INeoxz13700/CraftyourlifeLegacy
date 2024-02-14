package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelAlienMask extends ModModelHead {
	
	private static ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/alien_mask.png");
	
	private final ModelRenderer bone4;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer bone;
	private final ModelRenderer bone7;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;

	public ModelAlienMask() {
		textureWidth = 64;
		textureHeight = 64;

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(1.25F, -4.25F, -0.5F);
		bone4.cubeList.add(new ModelBox(bone4, 24, 15, -3.0F, -6.0F, -5.0F, 3, 3, 4, 0.0F));
		bone4.cubeList.add(new ModelBox(bone4, 30, 0, -2.5F, -5.5F, -5.5F, 2, 2, 1, 0.0F));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(2.0F, 0.5F, 0.25F);
		bone4.addChild(bone5);
		bone5.cubeList.add(new ModelBox(bone5, 14, 19, -3.0F, -6.0F, -5.0F, 3, 3, 4, 0.0F));
		bone5.cubeList.add(new ModelBox(bone5, 27, 29, -2.5F, -5.5F, -5.5F, 2, 2, 1, 0.0F));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone5.addChild(bone6);
		bone6.cubeList.add(new ModelBox(bone6, 0, 19, -7.0F, -6.0F, -5.0F, 3, 3, 4, 0.0F));
		bone6.cubeList.add(new ModelBox(bone6, 0, 7, -6.5F, -5.5F, -5.5F, 2, 2, 1, 0.0F));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, -5.25F, 0.0F);
		bone.cubeList.add(new ModelBox(bone, 0, 0, -5.0F, -3.0F, -5.0F, 10, 3, 10, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 23, 13, -4.0F, 0.0F, -5.0F, 8, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 0, 13, -5.0F, 2.75F, -4.75F, 10, 3, 3, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 30, 5, -5.0F, 1.75F, -4.75F, 2, 1, 1, 0.0F));
		bone.cubeList.add(new ModelBox(bone, 30, 3, 3.0F, 1.75F, -4.75F, 2, 1, 1, 0.0F));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(4.5F, 0.0F, 0.0F);
		bone.addChild(bone7);
		setRotationAngle(bone7, -0.3927F, 0.0F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 23, 29, -5.0F, -7.0F, 1.0F, 1, 4, 1, 0.0F));
		bone7.cubeList.add(new ModelBox(bone7, 0, 0, -6.0F, -8.0F, 0.0F, 3, 0, 3, 0.0F));
		bone7.cubeList.add(new ModelBox(bone7, 4, 3, -5.25F, -8.75F, 0.5F, 2, 0, 2, 0.0F));
		bone7.cubeList.add(new ModelBox(bone7, 0, 0, -4.75F, -9.5F, 1.0F, 1, 0, 1, 0.0F));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(1.0F, -1.75F, 0.25F);
		bone.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, -0.3927F);
		bone2.cubeList.add(new ModelBox(bone2, 0, 26, -6.0F, -8.0F, -5.5F, 3, 3, 3, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 18, 26, -5.0F, -5.0F, -5.0F, 1, 2, 2, 0.0F));
		bone2.cubeList.add(new ModelBox(bone2, 10, 19, -5.5F, -7.5F, -6.0F, 2, 2, 2, 0.0F));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-1.0F, -7.0F, 0.25F);
		setRotationAngle(bone3, 0.0F, 0.0F, 0.3927F);
		bone3.cubeList.add(new ModelBox(bone3, 25, 23, 3.0F, -8.0F, -5.5F, 3, 3, 3, 0.0F));
		bone3.cubeList.add(new ModelBox(bone3, 12, 26, 4.0F, -5.0F, -5.0F, 1, 2, 2, 0.0F));
		bone3.cubeList.add(new ModelBox(bone3, 0, 3, 3.5F, -7.5F, -6.0F, 2, 2, 2, 0.0F));
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	 
	@Override
	public void applyTransformations(Entity entity)
	{
	    GL11.glTranslatef(0.0F, 0.0F, -0.07F);
	    GL11.glScalef(5.0F, 5.0F, 5.0F);
	}
	 
	public void render(float partTicks)
	{
		 super.render(partTicks);
		 Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		 
		 bone4.render(0.01f);
		 bone.render(0.01f);
		 bone3.render(0.01f);
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
	 

}