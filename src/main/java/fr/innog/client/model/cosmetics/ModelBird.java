package fr.innog.client.model.cosmetics;

import org.lwjgl.opengl.GL11;

import fr.innog.client.model.AdvancedModelLoader;
import fr.innog.client.model.IModelCustom;
import fr.innog.client.model.ModModelBody;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class ModelBird extends ModModelBody {
	
	
	public IModelCustom model;
	
	private long lastSoundTime;
	
	
	public ResourceLocation texture = new ResourceLocation("craftyourliferp","textures/cosmetics/bird.png");
	

	public ModelBird()
	{
		model = AdvancedModelLoader.loadModel(new ResourceLocation("craftyourliferp","models/cosmetics/bird.obj"));
	}
	
	public void render(float partTicks)
	{
		super.render(partTicks);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		model.renderAll();
	}


	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		
		EntityPlayer player = (EntityPlayer)entity;
		
		if(player.inventory.armorInventory.get(2).isEmpty())
		{
			GL11.glTranslatef(0, 0.05f, 0);
		}
	
		
		int rand = MathHelper.getInt(player.getRNG(), 0, 1000);
		if(rand == 50)
		{
			if((System.currentTimeMillis() - this.lastSoundTime) / 1000 >= 2L)
			{
				lastSoundTime = System.currentTimeMillis();
				entity.world.playSound(player, new BlockPos(entity.posX, entity.posY, entity.posZ), new SoundEvent(new ResourceLocation("craftyourliferp:bird")), SoundCategory.PLAYERS,1.0F, 1.0F);
			}
		}
		
		
	}

	@Override
	public ResourceLocation getTexture() {
		return texture;
	}

	@Override
	public void applyTransformations(Entity entity) {
		GL11.glRotatef(180, 1, 0, 0);
		GL11.glTranslatef(-0.225f, -0.2f, 0);
		GL11.glScalef(1.5f, 1.5f, 1.5f);		
	}
}