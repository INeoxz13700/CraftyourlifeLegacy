package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.ModModelHead;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ModelWitchHat2 extends ModModelHead {
	
	private static ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/witch_hat2.png");
	
	private final ModelRenderer bone202;
	private final ModelRenderer bone168;
	private final ModelRenderer bone175;
	private final ModelRenderer bone176;
	private final ModelRenderer bone177;
	private final ModelRenderer bone178;
	private final ModelRenderer bone179;
	private final ModelRenderer bone180;
	private final ModelRenderer bone181;
	private final ModelRenderer bone102;
	private final ModelRenderer bone103;
	private final ModelRenderer bone104;
	private final ModelRenderer bone105;
	private final ModelRenderer bone106;
	private final ModelRenderer bone107;
	private final ModelRenderer bone108;

	public ModelWitchHat2() {
		textureWidth = 64;
		textureHeight = 64;

		bone202 = new ModelRenderer(this);
		bone202.setRotationPoint(0.3F, -6.725F, 0.025F);
		setRotationAngle(bone202, 0.0F, 3.1416F, 0.0F);
		

		bone168 = new ModelRenderer(this);
		bone168.setRotationPoint(-2.0F, -2.5F, -3.75F);
		bone202.addChild(bone168);
		setRotationAngle(bone168, 0.0F, -1.4835F, 0.0F);
		

		bone175 = new ModelRenderer(this);
		bone175.setRotationPoint(7.7942F, 0.0F, -4.5F);
		bone168.addChild(bone175);
		setRotationAngle(bone175, 0.0F, 2.0944F, 0.0F);
		bone175.cubeList.add(new ModelBox(bone175, 0, 0, -1.0F, -4.0F, 0.0F, 3, 4, 2, 0.0F));
		bone175.cubeList.add(new ModelBox(bone175, 8, 0, 0.75F, -0.5F, 1.0F, 1, 2, 0, 0.0F));
		bone175.cubeList.add(new ModelBox(bone175, 0, 7, 0.75F, 1.5F, 1.0F, 1, 0, 1, 0.0F));
		bone175.cubeList.add(new ModelBox(bone175, 0, 0, -0.75F, -0.5F, 1.0F, 1, 2, 0, 0.0F));
		bone175.cubeList.add(new ModelBox(bone175, 0, 6, -0.75F, 1.5F, 1.0F, 1, 0, 1, 0.0F));

		bone176 = new ModelRenderer(this);
		bone176.setRotationPoint(-0.5F, -1.0F, 0.5F);
		bone175.addChild(bone176);
		bone176.cubeList.add(new ModelBox(bone176, 0, 12, 0.0F, -4.7281F, 0.1268F, 2, 2, 2, 0.0F));

		bone177 = new ModelRenderer(this);
		bone177.setRotationPoint(-0.5F, -0.8686F, -0.4783F);
		bone176.addChild(bone177);
		setRotationAngle(bone177, -0.4363F, 0.0F, 0.0F);
		bone177.cubeList.add(new ModelBox(bone177, 23, 25, 1.0F, -3.7281F, 0.1268F, 1, 1, 2, 0.0F));

		bone178 = new ModelRenderer(this);
		bone178.setRotationPoint(0.0F, -1.0941F, -1.6928F);
		bone177.addChild(bone178);
		setRotationAngle(bone178, -0.4363F, 0.0F, 0.0F);
		bone178.cubeList.add(new ModelBox(bone178, 5, 16, 1.025F, -3.8424F, 1.1574F, 1, 1, 1, 0.0F));

		bone179 = new ModelRenderer(this);
		bone179.setRotationPoint(0.5F, 0.0F, 0.0F);
		bone175.addChild(bone179);
		setRotationAngle(bone179, -0.6981F, 0.0F, 0.0F);
		bone179.cubeList.add(new ModelBox(bone179, 7, 6, -0.475F, -1.2648F, -0.7094F, 1, 2, 1, 0.0F));

		bone180 = new ModelRenderer(this);
		bone180.setRotationPoint(-0.25F, -0.25F, -0.5F);
		bone175.addChild(bone180);
		setRotationAngle(bone180, -0.1745F, 0.0F, 0.0873F);
		bone180.cubeList.add(new ModelBox(bone180, 0, 21, -1.1463F, -3.9783F, -0.0843F, 0, 4, 2, 0.0F));

		bone181 = new ModelRenderer(this);
		bone181.setRotationPoint(3.1698F, -1.1494F, -0.7147F);
		bone180.addChild(bone181);
		setRotationAngle(bone181, -0.1745F, 0.0F, -0.4363F);
		bone181.cubeList.add(new ModelBox(bone181, 0, 21, 0.1726F, -3.577F, 0.2188F, 0, 4, 2, 0.0F));

		bone102 = new ModelRenderer(this);
		bone102.setRotationPoint(0.2F, 0.1F, -0.775F);
		bone202.addChild(bone102);
		bone102.cubeList.add(new ModelBox(bone102, 0, 0, -6.0F, -1.0F, -5.0F, 11, 1, 11, 0.0F));

		bone103 = new ModelRenderer(this);
		bone103.setRotationPoint(0.0F, -0.05F, 0.25F);
		bone102.addChild(bone103);
		setRotationAngle(bone103, 0.1745F, 0.0873F, 0.0F);
		bone103.cubeList.add(new ModelBox(bone103, 0, 12, -5.0F, -2.0F, -4.0F, 9, 2, 9, 0.0F));

		bone104 = new ModelRenderer(this);
		bone104.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone103.addChild(bone104);
		setRotationAngle(bone104, 0.1745F, 0.1745F, 0.0F);
		bone104.cubeList.add(new ModelBox(bone104, 0, 23, -4.0F, -4.0F, -3.0F, 7, 3, 7, 0.0F));

		bone105 = new ModelRenderer(this);
		bone105.setRotationPoint(0.0F, 0.0F, 0.0F);
		bone104.addChild(bone105);
		setRotationAngle(bone105, 0.3491F, -0.3491F, 0.0F);
		bone105.cubeList.add(new ModelBox(bone105, 23, 28, -3.0F, -6.0F, -1.0F, 5, 5, 5, 0.0F));

		bone106 = new ModelRenderer(this);
		bone106.setRotationPoint(0.0F, -1.9151F, 1.607F);
		bone105.addChild(bone106);
		setRotationAngle(bone106, 0.3491F, -0.2618F, 0.0F);
		bone106.cubeList.add(new ModelBox(bone106, 27, 12, -2.0F, -6.0F, 0.0F, 3, 4, 3, 0.0F));

		bone107 = new ModelRenderer(this);
		bone107.setRotationPoint(0.159F, -6.8401F, 2.4281F);
		bone106.addChild(bone107);
		setRotationAngle(bone107, 2.2689F, 0.0F, 0.0F);
		bone107.cubeList.add(new ModelBox(bone107, 0, 6, -1.89F, -2.3294F, -1.0969F, 2, 2, 3, 0.0F));

		bone108 = new ModelRenderer(this);
		bone108.setRotationPoint(-0.3157F, -2.5573F, 1.7458F);
		bone107.addChild(bone108);
		setRotationAngle(bone108, -2.5307F, 0.0F, 0.0F);
		bone108.cubeList.add(new ModelBox(bone108, 0, 16, -0.89F, -1.3294F, -1.0969F, 1, 1, 3, 0.0F));

	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
	 
	@Override
	public void applyTransformations(Entity entity)
	{
		 GL11.glTranslatef(-0.025F, 0.0F, 0.0F);
	     GL11.glScalef(6.0F, 6.0F, 6.0F);		    	
	}
	 
	@Override
	public void render(float partTicks)
	{
		super.render(partTicks);
		
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		 
		bone202.render(0.01f);
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}
	 

}