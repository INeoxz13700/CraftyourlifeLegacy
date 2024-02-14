package fr.innog.network.packets;

import java.lang.reflect.InvocationTargetException;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.animations.PlayerAnimator;
import fr.innog.common.animations.PremiumAnimation;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketAnimation extends PacketBase {
	
	public byte animation;
	
	public int entityId;
	
	public PacketAnimation() {
		
	}
	
	//Client side packet update animation
	public static PacketAnimation setAnimation(byte animation)
	{
		PacketAnimation packet = new PacketAnimation();
		packet.animation = animation;
		return packet;
	}
	
	public static PacketAnimation setAnimation(byte animation, int entityId)
	{
		PacketAnimation packet = new PacketAnimation();
		packet.animation = animation;
		packet.entityId = entityId;
		return packet;
	}
	
	public static PacketAnimation stopAnimation()
	{
		PacketAnimation packet = new PacketAnimation();
		packet.animation = -1;
		return packet;
	}
	
	public static PacketAnimation stopAnimation(int entityId)
	{
		PacketAnimation packet = new PacketAnimation();
		packet.animation = -1;
		packet.entityId = entityId;
		return packet;
	}
	

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(animation);
		data.writeInt(entityId);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		animation = data.readByte();
		entityId = data.readInt();
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(playerEntity);

		try {
			
			if(animation == -1)
			{
				if(playerData.getCurrentPlayingAnimation() != null) if(playerData.getCurrentPlayingAnimation().activeableOnlyServerSide()) return;
				
				playerData.setCurrentPlayingAnimation(null);
			}
			else
			{
				PlayerAnimator animator = ModCore.getAnimationManager().getAnimation(animation);
				
				if(animator.activeableOnlyServerSide()) return;
				
				if(animator.getClass().isAnnotationPresent(PremiumAnimation.class))
					if(!playerData.getUnlockedAnimations().contains((int)animation))
						return;	
				
				if(playerData.getCurrentPlayingAnimation() != null)
					if(!playerData.getCurrentPlayingAnimation().canPlayAnotherAnimationWhenActive())
						return;
				
				
				playerData.setCurrentPlayingAnimation(ModCore.getAnimationManager().getAnimation(animation));
			}
			
			
			playerData.updateRenderer();
			
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void handleClientSide(EntityPlayer clientPlayer)
	{				
		Entity entity = clientPlayer.world.getEntityByID(entityId);
		if(entity instanceof EntityPlayer)
		{
			EntityPlayer entityPlayer = (EntityPlayer)entity;
			IPlayer playerData = MinecraftUtils.getPlayerCapability(entityPlayer);
			if(playerData != null)
			{
				if(animation == -1)
				{
					playerData.setCurrentPlayingAnimation(null);
				}
				else
				{
					try {
						playerData.setCurrentPlayingAnimation(ModCore.getAnimationManager().getAnimation((int)animation));
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else
		{
			ModCore.log("error not a player");
		}
		
		
	}


}