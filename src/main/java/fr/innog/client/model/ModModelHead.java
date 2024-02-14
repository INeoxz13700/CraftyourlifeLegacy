package fr.innog.client.model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public abstract class ModModelHead extends ModModelBase {
	
	
	 public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	 }
	 
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity)
	{
		super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
	}
	
	 @Override
	 public void render(Entity entity, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
		 
		 if(entity.isInvisible()) return;

		 GL11.glPushMatrix();
		 super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
		 	        
	     EntityPlayer player = (EntityPlayer) entity;
	     IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
	         
	     float yawHead = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partTicks;
		 float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partTicks;
		 float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partTicks;

	     EntityPlayer localPlayer = Minecraft.getMinecraft().player;
	     EntityPlayer renderTarget = player;
	        
	     double tarX1 = renderTarget.prevPosX + (renderTarget.posX - renderTarget.prevPosX) * partTicks;
	     double tarX2 = localPlayer.prevPosX + (localPlayer.posX - localPlayer.prevPosX) * partTicks;
	        
	     double tarY1 = renderTarget.prevPosY + (renderTarget.posY - renderTarget.prevPosY) * partTicks;
	     double tarY2 = localPlayer.prevPosY + (localPlayer.posY - localPlayer.prevPosY) * partTicks;
	        
	     double tarZ1 = renderTarget.prevPosZ + (renderTarget.posZ - renderTarget.prevPosZ) * partTicks;
	     double tarZ2 = localPlayer.prevPosZ + (localPlayer.posZ - localPlayer.prevPosZ) * partTicks;
	        
	     double x = tarX1 - tarX2;
	     double y = tarY1 - tarY2;
	     double z = tarZ1 - tarZ2; 
	   
	     y += 26D * 0.0625F;
	     if(player.isSneaking())
	     {
				y-= 0.38D;
	     }


	     GL11.glTranslated(x, y, z);

	     GL11.glScalef(1f, -1f, -1f);
	     
	     GL11.glTranslatef(0, 3F * 0.0625F, 0F);
	        
	        
	     GL11.glTranslatef(0, 0.6F * 0.0625F, 0F);

	     if(player.isPlayerSleeping())
	     {		     
	    	 GL11.glRotatef(MinecraftUtils.getPlayerYawFromBedDirection(player), 0, 1, 0);
	    	 
	    	 GL11.glRotatef(-90, 1, 0, 0);	    	 

	    	 GL11.glTranslatef(0, 0.39f, 1.4f);
	    	 GL11.glRotatef(yawHead, 0, 1, 0);
	    	 GL11.glRotatef(yawOffset, 0, -1, 0);
	     }
	     else
	     {
	    	 /*if(ClientLitener.clientPlayerState.isSitting)
	         {
	        		GL11.glTranslatef(0, 0.5F, 0);
	         }
	    	 if(ClientLitener.clientPlayerState.isCrawling)
	    	 {
	        	 GL11.glTranslatef(0F, 1.4F, 0); 

	    	 }	   */
	    	 
		     GL11.glRotatef(yawHead, 0, 1, 0);

			 GL11.glRotatef(pitch, 1F, 0F, 0F);

	

	     }

		 if(playerData.getCurrentPlayingAnimation() != null)
		 {
			 GL11.glRotatef(playerData.getCurrentPlayingAnimation().bipedHead.rotationZ,0,0,1);
			 GL11.glRotatef(playerData.getCurrentPlayingAnimation().bipedHead.rotationY,0,1,0);
			 GL11.glRotatef(playerData.getCurrentPlayingAnimation().bipedHead.rotationX,1,0,0);

		 }


	     
	     

		 applyTransformations(entity);
		 
		
		 render(partTicks);
		 
		 GL11.glPopMatrix();

		 
	 }
	 
	 @Override
	 public void applyTransformations(Entity entity)
	 {
		 
	 
	 }
	 
	 @Override
	 public void render(float partTicks)
	 {
		 GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	     RenderHelper.enableGUIStandardItemLighting();
	 }
	
}