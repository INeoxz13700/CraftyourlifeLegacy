package fr.innog.network.packets.decrapted;

import fr.innog.network.packets.PacketBase;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketAtm extends PacketBase
{
	
	/*
	 * 0: put
	 * 1: take
	 */
    private byte action;
    
    private int value;
    
    
    public PacketAtm()
    {
    	
    }
   
    public static PacketAtm packetOpenAtm()
    {
    	PacketAtm packet = new PacketAtm();
    	packet.action = 2;
    	return packet;
    }
    
    @SideOnly(Side.CLIENT)
    public static PacketAtm packetMoneyInterraction(byte action, int value)
    {
    	PacketAtm packet = new PacketAtm();
    	packet.action = action;
    	packet.value = value;
    	return packet;
    }
    
    public PacketAtm(byte action, int value)
    {
    	this.action = action;
    	this.value = value;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf data)
    {    	
    	data.writeByte(action);
  
	    data.writeInt(value);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf data)
    {
        action = data.readByte();
        
	    value = data.readInt();
        
    }

    @Override
    public void handleServerSide(EntityPlayerMP player)
    {
    	if(action == 0)
    	{
    		MinecraftUtils.dispatchConsoleCommand("atm admin put " + player.getName() + " " + value + " 20");
    	}
        else
    	{
    		MinecraftUtils.dispatchConsoleCommand("atm admin take " + player.getName() + " " + value + " 20");
    	}
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void handleClientSide(EntityPlayer client)
    {

    }


         
}