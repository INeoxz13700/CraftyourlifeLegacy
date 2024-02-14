package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class Model3DGlasses extends ModModelHead {
	
	private final ModelRenderer bipedHead;
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	
	private static ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/3d_glasses.png");

	public Model3DGlasses() {
		textureWidth = 32;
		textureHeight = 32;

		bipedHead = new ModelRenderer(this);
		bipedHead.setRotationPoint(0.0F, 23.0F, 0.0F);
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 24, -5.0F, -6.0F, -5.0F, 5, 3, 1, -0.3F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 28, 0.0F, -6.0F, -5.0F, 5, 3, 1, -0.3F));
		bipedHead.cubeList.add(new ModelBox(bipedHead, 0, 21, -1.0F, -5.75F, -5.0F, 2, 2, 1, -0.3F));

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedHead.addChild(bone);
		bone.cubeList.add(new ModelBox(bone, 0, 15, 4.0F, -30.0F, -4.6F, 1, 3, 3, -0.3F));
		bone.cubeList.add(new ModelBox(bone, 12, 25, 4.0F, -30.0F, -2.2F, 1, 2, 5, -0.3F));
		bone.cubeList.add(new ModelBox(bone, 6, 20, 4.0F, -28.6F, 0.8F, 1, 2, 2, -0.3F));

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 24.0F, 0.0F);
		bipedHead.addChild(bone2);
		bone2.cubeList.add(new ModelBox(bone2, 0, 15, -5.0F, -30.0F, -4.6F, 1, 3, 3, -0.3F));
		bone2.cubeList.add(new ModelBox(bone2, 12, 25, -5.0F, -30.0F, -2.2F, 1, 2, 5, -0.3F));
		bone2.cubeList.add(new ModelBox(bone2, 6, 20, -5.0F, -28.6F, 0.8F, 1, 2, 2, -0.3F));
	}

	
	public void render(float partTicks)
	{
		super.render(partTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		bipedHead.render(0.01f);
	}


	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}


	@Override
	public void applyTransformations(Entity entity)
	{
		GL11.glTranslatef(0.0F, -1.15F, -0.02F);
		GL11.glScalef(5F, 5F, 5F);
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
}