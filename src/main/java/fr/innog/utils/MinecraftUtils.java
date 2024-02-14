package fr.innog.utils;

import java.util.Random;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.common.ModCore;
import fr.innog.common.proxy.ClientProxy;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class MinecraftUtils {

	
	public static void sendMessage(ICommandSender sender, String message)
	{
		sender.sendMessage(new TextComponentString(message));
	}
	
	public static void broadcastMessage(String message)
	{
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		for(EntityPlayer player : server.getPlayerList().getPlayers())
		{
			sendMessage(player, message);
		}
	}
	
	public static void sendHudMessage(EntityPlayer player, String message)
	{
		player.sendStatusMessage(new TextComponentString(message), true);
	}
	
	public static IPlayer getPlayerCapability(EntityPlayer player)
	{		
		if(player.world.isRemote)
		{
			if(player == Minecraft.getMinecraft().player)
			{
				return ClientProxy.modClient.getLocalPlayerCapability();
			}
			else
			{
				return ClientProxy.modClient.getPlayerCapability(player);
			}
		}
		
		
		return player.getCapability(PlayerProvider.PLAYER_CAP, null);
	}
	
	public static RayTraceResult rayTraceServer(EntityPlayer player, double range, float partialTickTime)
    {
        Vec3d vec3 = player.getPositionEyes(partialTickTime);
        Vec3d vec31 = player.getLookVec();
        Vec3d vec32 = vec3.addVector(vec31.x * range, vec31.y * range, vec31.z * range);
        return player.world.rayTraceBlocks(vec3, vec32, false, false, true);
    }
 
    public static Vec3d getLookServer(EntityPlayer player, float partialTicks)
    {
    	if (partialTicks == 1.0F)
        {
            return getVectorForRotation(player.rotationPitch, player.rotationYaw);
        }
        else
        {
            float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
            float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
            return getVectorForRotation(f, f1);
        }
    }
    
    public static final Vec3d getVectorForRotation(float pitch, float yaw)
    {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }
 
    public static Vec3d getPositionServer(EntityPlayer player, float p_70666_1_)
    {
        if(p_70666_1_ == 1.0F)
        {
            return new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        }
        else
        {
            double d0 = player.prevPosX + (player.posX - player.prevPosX) * p_70666_1_;
            double d1 = player.prevPosY + (player.posY - player.prevPosY) * p_70666_1_;
            double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * p_70666_1_;
            return new Vec3d(d0, d1, d2);
        }
    }
    
	public static void setPlayerSize(EntityPlayer player, float width, float height)
	{
		AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
	 
		player.width = width;
		player.height = height;
	 
		player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,axisalignedbb.minX + (double) width, axisalignedbb.minY + (double) height,axisalignedbb.minZ + (double) width));
	}
	 
	public static void dispatchConsoleCommand(String cmd)
	{
		MinecraftServer serverInstance = FMLCommonHandler.instance().getMinecraftServerInstance();
		
		serverInstance.getCommandManager().executeCommand(serverInstance, cmd);
	}
	
	

	public static void resetPlayerSize(EntityPlayer player)
	{
		setPlayerSize(player, 0.6F, 1.8F);
	}
	
	public static float getPlayerYawFromBedDirection(EntityPlayer player)
    {
        if (player.bedLocation != null)
        {
            IBlockState state = player.world.getBlockState(player.bedLocation);
            
            if(state == null || state.getBlock() != Blocks.BED) return 0.0F;

            EnumFacing dir = state.getValue(BlockBed.FACING);
            
            switch (dir)
            {
                case EAST:
                {
                    return 90.0F;
                }
                case NORTH:
                {
                    return 0.0F;
                }
                case WEST:
                {
                    return 270.0F;
                }
                case SOUTH:
                {
                    return 180.0F;
                }
                default:
                	break;
            }
        }

        return 0.0F;
    }
	
	public static boolean areItemStacksEquals(ItemStack is1, ItemStack is2)
	{
		return is1.getCount() == is2.getCount() && is1.getItem() == is2.getItem();
	}
	
	public static double getRandomDoubleInRange(Random rnd, double min, double max) {
	       	if (min >= max) {
	            throw new IllegalArgumentException("La valeur minimale doit être inférieure à la valeur maximale.");
	        }
	       	return min + (max - min) * rnd.nextDouble();
	}
	    
    public static String getMoneyDisplay(String format,EntityPlayer player, double money)
    {
    	return String.format(format,money);
    }
    
    public static String getMoneyDisplay(String format,double money)
    {
    	return String.format(format,money);
    }
    
    public static void addMoney(EntityPlayer player, double money)
    {
    	if(ModCore.getEssentials().initialized())
    		ModCore.getEssentials().giveMoney(player, money);
    }
    
    public static void removeMoney(EntityPlayer player, double money)
    {
    	if(ModCore.getEssentials().initialized())
    		ModCore.getEssentials().takeMoney(player, money);
    }
    
    public static boolean haveMoney(EntityPlayer player, double money)
    {
    	if(ModCore.getEssentials().initialized())
    	{
    		if(ModCore.getEssentials().getUserMoney(player) >= money) return true;
    	}
    	
    	return false;    	
    }
    
    public static double getMoney(EntityPlayer player)
    {
    	if(ModCore.getEssentials().initialized())
    	{
    		return ModCore.getEssentials().getUserMoney(player);
    	}
    	
    	return 0.0D;    	
    }
    
    public static boolean isPlayerOp(EntityPlayerMP player)
    {
    	if (player.canUseCommand(2, "")) {
    		return true;
    	}
    	
    	return false;
    }
	
}
