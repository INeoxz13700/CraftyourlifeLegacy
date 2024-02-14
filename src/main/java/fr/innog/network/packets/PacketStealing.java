package fr.innog.network.packets;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.tiles.IStealingTileEntity;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketStealing extends PacketBase {

	private BlockPos stealingBlockPos;

	    
	public PacketStealing()
	{
	    	
	}
	   
	public static PacketStealing notificateClient(BlockPos pos)
	{
		PacketStealing packet = new PacketStealing();
		
		packet.stealingBlockPos = pos;
	    	
	    return packet;
	}
	  
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{    	
	    data.writeInt(stealingBlockPos.getX());
	    data.writeInt(stealingBlockPos.getY());
	    data.writeInt(stealingBlockPos.getZ());
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		this.stealingBlockPos = new BlockPos(data.readInt(), data.readInt(), data.readInt());
	}

	@Override
	public void handleServerSide(EntityPlayerMP player)
	{
	   
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientSide(EntityPlayer client)
	{
		TileEntity tile = client.world.getTileEntity(this.stealingBlockPos);
	    	
	    if(tile == null) return;
	    	
	    if(tile instanceof IStealingTileEntity)
	    {
	    	IStealingTileEntity stealingTile = (IStealingTileEntity) tile;
	    	
	    	IPlayer playerData = MinecraftUtils.getPlayerCapability(client);

	        if(playerData.isStealing())
	        {
	        	playerData.stopStealing();
	        }
	        else
	        {
	        	playerData.steal(stealingTile);
	        	stealingTile.setStealingEntity(client);
	        }
	    }
	}
	
}
