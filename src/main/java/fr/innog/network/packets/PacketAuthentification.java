package fr.innog.network.packets;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import fr.innog.common.ModControllers;
import fr.innog.common.proxy.ClientProxy;
import fr.innog.network.PacketCollection;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketAuthentification extends PacketBase {

	private String cryptedPassword = "";
	
	private String connectionData;
	
	public PacketAuthentification()
	{
		
	}
	
	public PacketAuthentification(String connectionData, String password)
	{
		this.cryptedPassword = password;
		this.connectionData = connectionData;
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		ByteBufUtils.writeUTF8String(data, cryptedPassword);
		ByteBufUtils.writeUTF8String(data, connectionData);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		cryptedPassword = ByteBufUtils.readUTF8String(data);
		connectionData = ByteBufUtils.readUTF8String(data);
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) {
		ModControllers.authController.handleAuthentification(playerEntity, cryptedPassword, connectionData);
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) 
	{

		if(ClientProxy.modClient.getCurrentSession() == null)
		{
			System.out.println("Impossible de récupérer la session contacter KarmaOwner");
		}
		else
		{
			if(!Minecraft.getMinecraft().isIntegratedServerRunning())
			{
				String uid;
				try 
				{
					uid = ClientProxy.modClient.getComputerUID();
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
					uid = "undefined";
				}
				
				String macAdress;
				try {
					macAdress = ClientProxy.modClient.getMacAdress();
				} catch (UnknownHostException | SocketException e) {
					e.printStackTrace();
					macAdress = "undefined";
				}
				
				String connectionData = uid + "," + macAdress;

				PacketCollection.authenticate(connectionData, ClientProxy.modClient.getCurrentSession().getCryptedPassword());
			}
		}

	}

}
	

