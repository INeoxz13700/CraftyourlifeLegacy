package fr.innog.network;


import java.util.HashMap;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.cosmetics.CosmeticObject;
import fr.innog.network.packets.PacketAnimation;
import fr.innog.network.packets.PacketAuthentification;
import fr.innog.network.packets.PacketAvatar;
import fr.innog.network.packets.PacketCardIdentity;
import fr.innog.network.packets.PacketCosmetic;
import fr.innog.network.packets.PacketCrochetDoor;
import fr.innog.network.packets.PacketExtendedArmorSynchronizer;
import fr.innog.network.packets.PacketItemInteract;
import fr.innog.network.packets.PacketPlayerSleep;
import fr.innog.network.packets.PacketStealing;
import fr.innog.network.packets.PacketSyncData;
import fr.innog.network.packets.PacketThirst;
import fr.innog.network.packets.PacketUIController;
import fr.innog.network.packets.PacketUpdateRendererSynchronizer;
import fr.innog.ui.remote.data.RemoteMethodCallback.ActionResult;
import fr.innog.ui.remote.data.SyncStruct;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketCollection {

	public static void openUIToPlayer(int ID, EntityPlayerMP player, HashMap<String, SyncStruct<?>> initDatas, Object... args)
	{
		ModCore.getPackethandler().sendTo(PacketUIController.openUI(ID, initDatas, args), (EntityPlayerMP) player);
	}
	
	@SideOnly(Side.CLIENT)
	public static void askToServerOpenUI(int ID)
	{
		ModCore.getPackethandler().sendToServer(PacketUIController.openUI(ID));
	}
	
	public static void syncDataRemoteUI(HashMap<String, SyncStruct<?>> datas, EntityPlayerMP player)
	{
		if(datas.size() > 0) ModCore.getPackethandler().sendTo(PacketUIController.syncDataToClient(datas), player);
	}
	
	public static void executeRemoteMethod(EntityPlayer player, String methodName, Object... args)
	{
		if(player.world.isRemote)
		{
			ModCore.getPackethandler().sendToServer(PacketUIController.executeMethod(methodName, args));
		}
		else
		{
			ModCore.getPackethandler().sendTo(PacketUIController.executeMethod(methodName, args), (EntityPlayerMP)player);
		}
	}
	
	public static void executeRemoteMethodWithResult(EntityPlayer player, String methodName, int methodId, Object... args)
	{
		if(player.world.isRemote)
		{
			ModCore.getPackethandler().sendToServer(PacketUIController.executeMethodWithResult(methodName, methodId, args));
		}
	}
	
	public static void sendResult(int methodId, ActionResult result, EntityPlayer player)
	{
		if(!player.world.isRemote)
		{
			ModCore.getPackethandler().sendTo(PacketUIController.sendRemoteMethodResult(methodId, result), (EntityPlayerMP)player);
		}
	}
	
	public static void notificateClientStealing(EntityPlayer client, BlockPos tilePos)
	{
		ModCore.getPackethandler().sendTo(PacketStealing.notificateClient(tilePos), (EntityPlayerMP)client);
	}
	
	public static void updatePlayerCosmeticRenderData(EntityPlayer updateFor, EntityPlayer player, List<CosmeticObject> cosmetics)
	{
		ModCore.getPackethandler().sendTo(PacketCosmetic.updateRender(player.getEntityId(), cosmetics), (EntityPlayerMP)updateFor);
	}
	
	@SideOnly(Side.CLIENT)
	public static void updateRendererSynchronizer()
	{
		ModCore.getPackethandler().sendToServer(new PacketUpdateRendererSynchronizer());
	}
	
	@SideOnly(Side.CLIENT)
	public static void equipCosmetic(int cosmeticId)
	{
		ModCore.getPackethandler().sendToServer(PacketCosmetic.equipCosmetic(cosmeticId));
	}
	
	@SideOnly(Side.CLIENT)
	public static void unequipCosmetic(int cosmeticId)
	{
		ModCore.getPackethandler().sendToServer(PacketCosmetic.unequipCosmetic(cosmeticId));
	}
	
	@SideOnly(Side.CLIENT)
	public static void stopAnimation()
	{
		PacketAnimation packet = PacketAnimation.stopAnimation();
		ModCore.getPackethandler().sendToServer(packet);
	}
	
	public static void stopAnimation(EntityPlayer stopFor, EntityPlayer player)
	{
		PacketAnimation packet = PacketAnimation.stopAnimation(player.getEntityId());
		ModCore.getPackethandler().sendTo(packet, (EntityPlayerMP)stopFor);
	}
	
	public static void synchronizeExtendedArmor(EntityPlayerMP forPlayer, EntityPlayerMP player)
	{
		ModCore.getPackethandler().sendTo(PacketExtendedArmorSynchronizer.syncExtendedArmor(player), forPlayer);
	}
	
	
	@SideOnly(Side.CLIENT)
	public static void playAnimation(int animationId)
	{
		PacketAnimation packet = PacketAnimation.setAnimation((byte)animationId);
		ModCore.getPackethandler().sendToServer(packet);
	}
	
	public static void playAnimation(EntityPlayer playFor, EntityPlayer player, int animationId)
	{
		PacketAnimation packet = PacketAnimation.setAnimation((byte)animationId, player.getEntityId());
		ModCore.getPackethandler().sendTo(packet,(EntityPlayerMP) playFor);
	}
	
	@SideOnly(Side.CLIENT)
	public static void openCardIdentityOf(EntityPlayer targetPlayer)
	{
		ModCore.getPackethandler().sendToServer(PacketCardIdentity.openCardIdentityOf(targetPlayer));
	}
	
	public static void openCardIdentityOf(EntityPlayer openFor, EntityPlayer targetPlayer)
	{
		if(openFor.world.isRemote) return;
		
		IPlayer targetData = MinecraftUtils.getPlayerCapability(targetPlayer);
				
		ModCore.getPackethandler().sendTo(PacketCardIdentity.openCardIdentityOf(targetData.getIdentityData()), (EntityPlayerMP) openFor);
	}
	
	public static void syncDataTo(String key, SyncStruct<?> syncData, EntityPlayer to)
	{
		if(to.world.isRemote) return;
		
		ModCore.getPackethandler().sendTo(PacketSyncData.syncData(key, syncData), (EntityPlayerMP) to);
	}
	
	@SideOnly(Side.CLIENT)
	public static void drinkWater()
	{
		ModCore.getPackethandler().sendToServer(PacketThirst.drinkWater());
	}
	
	@SideOnly(Side.CLIENT)
	public static void setUnSleep()
	{
		ModCore.getPackethandler().sendToServer(PacketPlayerSleep.unSleep());
	}
	
	@SideOnly(Side.CLIENT)
	public static void subitDeath()
	{
		ModCore.getPackethandler().sendToServer(PacketPlayerSleep.subitDeath());
	}
	
	@SideOnly(Side.CLIENT)
	public static void notifyServerMouseState()
	{
		ModCore.getPackethandler().sendToServer(PacketItemInteract.syncMouseIsReleased());
	}
	
	public static void notifyClientsUsingItem(EntityPlayer player)
	{
		if(player.world.isRemote) return;
		
		ModCore.getPackethandler().sendToAllAround(PacketItemInteract.syncUsingItem(player), player.posX, player.posY, player.posZ, 64.0F, player.dimension);
	}
	
	@SideOnly(Side.CLIENT)
	public static void authenticate(String connectionData, String cryptedPass)
	{
		ModCore.getPackethandler().sendToServer(new PacketAuthentification(connectionData, cryptedPass));
	}
	
	public static void askClientLoginData(EntityPlayerMP player)
	{
		ModCore.getPackethandler().sendTo(new PacketAuthentification("",""), player);
	}
	
	@SideOnly(Side.CLIENT)
	public static void updateAvatar()
	{
		ModCore.getPackethandler().sendToServer(PacketAvatar.updateAvatar());
	}
	
	@SideOnly(Side.CLIENT)
	public static void crochetDoor(BlockPos doorPos)
	{
		ModCore.getPackethandler().sendToServer(PacketCrochetDoor.crochetDoor(doorPos));
	}
	

	
} 
