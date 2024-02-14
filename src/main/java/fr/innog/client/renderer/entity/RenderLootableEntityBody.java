package fr.innog.client.renderer.entity;

import org.lwjgl.opengl.GL11;

import fr.dynamx.common.physics.entities.EnumRagdollBodyPart;
import fr.innog.common.ModCore;
import fr.innog.common.entity.EntityLootableBody;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class RenderLootableEntityBody<T extends EntityLootableBody> extends RenderEntity {

	   private final ModelPlayer modelFat = new ModelPlayer(0, false);
	    private final ModelPlayer modelLight = new ModelPlayer(0, true);

	    public RenderLootableEntityBody(RenderManager manager) {
	        super(manager);
	    }

	    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	    {
	    	renderMain((EntityLootableBody)entity,x,y+0.15F,z+0.5,partialTicks);
	    }

	    public void renderMain(EntityLootableBody entity, double x, double y, double z, float partialsTicks) {
	        if (entity.isInvisible()) return;

	        String useSkin = entity.getSkin();

	        ResourceLocation texture = new ResourceLocation("craftyourliferp", useSkin + "_skin"); 

	        ModelPlayer model = modelFat;
	        String skinType = DefaultPlayerSkin.getSkinType(EntityPlayer.getOfflineUUID(entity.getSkin()));
	                   
	        model = "default".equals(skinType) ? modelFat : modelLight;
	        
	        bindTexture(texture);

	        GlStateManager.pushMatrix();
	        GlStateManager.translate(x, y, z);

	        GlStateManager.rotate(-entity.rotationYaw, 0f,1f, 0f);
	        GlStateManager.rotate(270f, 1f,0f, 0f);

	        GL11.glScalef(1, 1, 1);
	        //Chest
	        renderBodyPart(entity, EnumRagdollBodyPart.CHEST, model.bipedBody, partialsTicks, texture);
	        //Right arm
	        renderBodyPart(entity, EnumRagdollBodyPart.RIGHT_ARM, model.bipedRightArm, partialsTicks, texture);
	        //Left Arm
	        renderBodyPart(entity, EnumRagdollBodyPart.LEFT_ARM, model.bipedLeftArm, partialsTicks, texture);
	        //Head
	        renderBodyPart(entity, EnumRagdollBodyPart.HEAD, model.bipedHead, partialsTicks, texture);
	        //Right Leg
	        renderBodyPart(entity, EnumRagdollBodyPart.RIGHT_LEG, model.bipedRightLeg, partialsTicks, texture);
	        //Left Leg
	        renderBodyPart(entity, EnumRagdollBodyPart.LEFT_LEG, model.bipedLeftLeg, partialsTicks, texture);
	         
	        
	        GlStateManager.popMatrix();

	    }

	    private void renderBodyPart(EntityLootableBody entity, EnumRagdollBodyPart enumBodyPart, ModelRenderer model, float partialTicks, ResourceLocation texture) {
	        GlStateManager.pushMatrix();


	        bindTexture(texture);
	        model.render(0.0625f);

	        GlStateManager.popMatrix();
	    }

	    public void renderParts(T entity, float partialTicks)
	    {
	    	
	    }

}
