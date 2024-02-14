package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelColorfulMask extends ModModelHead {

	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private final ModelRenderer bone4;
	private final ModelRenderer bone8;
	private final ModelRenderer bone9;
	private final ModelRenderer cube_r1;
	private final ModelRenderer bone10;
	private final ModelRenderer bone5;
	private final ModelRenderer bone6;
	private final ModelRenderer cube_r2;
	private final ModelRenderer bone7;
	
	private final ResourceLocation texture;

	public ModelColorfulMask(ResourceLocation texture) {
		this.texture = texture;
		textureWidth = 64;
		textureHeight = 64;

		bone = new ModelRenderer(this);
		bone.setRotationPoint(1.0F, -1.0F, -4.5F);
		

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(3.0F, 1.0F, -0.25F);
		bone.addChild(bone2);
		setRotationAngle(bone2, 0.0F, 0.0F, 0.2182F);
		bone2.cubeList.add(new ModelBox(bone2, 40, 28, -5.0028F, -2.897F, 0.025F, 5, 3, 1, 0.0F, false));

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(-2.0F, 0.0F, 0.0F);
		bone.addChild(bone3);
		bone3.cubeList.add(new ModelBox(bone3, 40, 24, -3.025F, -1.9F, 0.0F, 8, 3, 1, 0.0F, false));

		bone4 = new ModelRenderer(this);
		bone4.setRotationPoint(-3.0F, 1.0F, -0.25F);
		bone3.addChild(bone4);
		setRotationAngle(bone4, 0.0F, 0.0F, -0.2182F);
		bone4.cubeList.add(new ModelBox(bone4, 24, 0, -0.0461F, -2.9078F, 0.0F, 5, 3, 1, 0.0F, false));

		bone8 = new ModelRenderer(this);
		bone8.setRotationPoint(-4.5F, 0.0F, -3.25F);
		setRotationAngle(bone8, 0.3054F, 0.0F, 0.0F);
		bone8.cubeList.add(new ModelBox(bone8, 40, 16, -0.025F, -1.1546F, -0.5301F, 1, 1, 7, 0.0F, false));

		bone9 = new ModelRenderer(this);
		bone9.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone8.addChild(bone9);
		setRotationAngle(bone9, 0.2182F, 0.0F, 0.0F);
		

		cube_r1 = new ModelRenderer(this);
		cube_r1.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone9.addChild(cube_r1);
		setRotationAngle(cube_r1, -0.1309F, 0.0F, 0.0F);
		cube_r1.cubeList.add(new ModelBox(cube_r1, 32, 32, -0.025F, -1.4076F, -0.0383F, 1, 1, 7, 0.0F, false));

		bone10 = new ModelRenderer(this);
		bone10.setRotationPoint(0.0F, -1.75F, 1.0F);
		bone9.addChild(bone10);
		setRotationAngle(bone10, -0.4363F, 0.0F, 0.0F);
		bone10.cubeList.add(new ModelBox(bone10, 0, 0, -0.15F, -0.9004F, 5.4913F, 1, 3, 1, 0.0F, false));

		bone5 = new ModelRenderer(this);
		bone5.setRotationPoint(4.5F, 0.0F, -3.25F);
		setRotationAngle(bone5, 0.3054F, 0.0F, 0.0F);
		bone5.cubeList.add(new ModelBox(bone5, 40, 16, -1.025F, -1.1546F, -0.5301F, 1, 1, 7, 0.0F, true));

		bone6 = new ModelRenderer(this);
		bone6.setRotationPoint(0.0F, -2.0F, 0.0F);
		bone5.addChild(bone6);
		setRotationAngle(bone6, 0.2182F, 0.0F, 0.0F);
		

		cube_r2 = new ModelRenderer(this);
		cube_r2.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone6.addChild(cube_r2);
		setRotationAngle(cube_r2, -0.1309F, 0.0F, 0.0F);
		cube_r2.cubeList.add(new ModelBox(cube_r2, 32, 32, -1.025F, -1.4076F, -0.0383F, 1, 1, 7, 0.0F, true));

		bone7 = new ModelRenderer(this);
		bone7.setRotationPoint(0.0F, -1.75F, 1.0F);
		bone6.addChild(bone7);
		setRotationAngle(bone7, -0.4363F, 0.0F, 0.0F);
		bone7.cubeList.add(new ModelBox(bone7, 0, 0, -0.9F, -0.9004F, 5.4913F, 1, 3, 1, 0.0F, true));
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void applyTransformations(Entity entity)
	{
		GL11.glScalef(6, 6, 6);
	}
	
	@Override
	public void render(float partTicks) {
		super.render(partTicks);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());

		bone.render(0.01F);
		bone8.render(0.01F);
		bone5.render(0.01F);
	}

	@Override
	public ResourceLocation getTexture() 
	{
		return texture;
	}

}