package fr.innog.network.packets;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketAtm;
import fr.innog.network.packets.decrapted.PacketBMGPage;
import fr.innog.network.packets.decrapted.PacketBitcoinPage;
import fr.innog.network.packets.decrapted.PacketConnectingCall;
import fr.innog.network.packets.decrapted.PacketFinishCall;
import fr.innog.network.packets.decrapted.PacketListTransmission;
import fr.innog.network.packets.decrapted.PacketOpenTelephone;
import fr.innog.network.packets.decrapted.PacketPaypal;
import fr.innog.network.packets.decrapted.PacketPenalty;
import fr.innog.network.packets.decrapted.PacketSendSms;
import fr.innog.network.packets.decrapted.PacketSendVoice;
import fr.innog.network.packets.decrapted.PacketStartCall;
import fr.innog.network.packets.decrapted.PacketSyncPhone;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ChannelHandler.Sharable
public class PacketHandler extends MessageToMessageCodec<FMLProxyPacket, PacketBase>
{
	//Map of channels for each side
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	//The list of registered packets. Should contain no more than 256 packets.
	private LinkedList<Class<? extends PacketBase>> packets = new LinkedList<>();
	

	private boolean modInitialised = false;
	
	/**
	 * Store received packets in these queues and have the main Minecraft threads use these
	 */
	private ConcurrentLinkedQueue<PacketBase> receivedPacketsClient = new ConcurrentLinkedQueue<>();
		
	public static final ConcurrentHashMap<String, ConcurrentLinkedQueue<PacketBase>> receivedPacketsServer = new ConcurrentHashMap<>();

	private final ReentrantLock serverPacketLock = new ReentrantLock();
	/**
	 * Registers a packet with the handler
	 */
	public boolean registerPacket(Class<? extends PacketBase> cl)
	{
		if(packets.size() > 256)
		{
			ModCore.log("Packet limit exceeded in Flan's Mod packet handler by packet " + cl.getCanonicalName() + ".");
			return false;
		}
		if(packets.contains(cl))
		{
			ModCore.log("Tried to register " + cl.getCanonicalName() + " packet class twice.");
			return false;
		}
		if(modInitialised)
		{
			ModCore.log("Tried to register packet " + cl.getCanonicalName() + " after mod initialisation.");
			return false;
		}
		
		packets.add(cl);
		return true;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, PacketBase msg, List<Object> out) throws Exception
	{
		try
		{
			//Define a new buffer to store our data upon encoding
			ByteBuf encodedData = Unpooled.buffer();
			//Get the packet class
			Class<? extends PacketBase> cl = msg.getClass();
			
			//If this packet has not been registered by our handler, reject it
			if(!packets.contains(cl))
				throw new NullPointerException("Packet not registered : " + cl.getCanonicalName());
			
			//Like a packet ID. Stored as the first entry in the packet code for recognition
			byte discriminator = (byte)packets.indexOf(cl);
			encodedData.writeByte(discriminator);
			//Get the packet class to encode our packet
			msg.encodeInto(ctx, encodedData);
			
			//Convert our packet into a Forge packet to get it through the Netty system
			FMLProxyPacket proxyPacket = new FMLProxyPacket(new PacketBuffer(encodedData.copy()), ctx.channel().attr(NetworkRegistry.FML_CHANNEL).get());
			//Add our packet to the outgoing packet queue
			out.add(proxyPacket);
		}
		catch(Exception e)
		{
			ModCore.log("ERROR encoding packet");
			ModCore.log(e.toString());
		}
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, FMLProxyPacket msg, List<Object> out) throws Exception
	{
		try
		{
		
			
			//Get the encoded data from the incoming packet
			ByteBuf encodedData = msg.payload();
			//Get the class for interpreting this packet
			byte discriminator = encodedData.readByte();
			Class<? extends PacketBase> cl = packets.get(discriminator);
			
			//If this discriminator returns no class, reject it
			if(cl == null)
				throw new NullPointerException("Packet not registered for discriminator : " + discriminator);
			
			//Create an empty packet and decode our packet data into it
			PacketBase packet = cl.getConstructor().newInstance();
			packet.decodeInto(ctx, encodedData.slice());
			
			packet.received = true;

			//Check the side and handle our packet accordingly
			switch(FMLCommonHandler.instance().getEffectiveSide())
			{
				case CLIENT:
				{
					receivedPacketsClient.offer(packet);
					break;
				}
				case SERVER:
				{
					INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
					EntityPlayer player = ((NetHandlerPlayServer)netHandler).player;
					
					if(!receivedPacketsServer.containsKey(player.getName()))
						receivedPacketsServer.put(player.getName(), new ConcurrentLinkedQueue<>());
					receivedPacketsServer.get(player.getName()).offer(packet);
				
					break;
				}
			}
		}
		catch(Exception e)
		{
			ModCore.log("ERROR decoding packet");
			e.printStackTrace();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void handleClientPackets()
	{
		while(!receivedPacketsClient.isEmpty())
		{
			PacketBase packet = receivedPacketsClient.poll();
			packet.handleClientSide(getClientPlayer());
		}
	}
	
	public void handleServerPackets()
	{
		List<String> playersToRemove = new ArrayList<String>();

	    for(String playerName : receivedPacketsServer.keySet())
		{
			ConcurrentLinkedQueue<PacketBase> receivedPacketsFromPlayer = receivedPacketsServer.get(playerName);
			EntityPlayerMP player = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(playerName);
				
				
			if(player == null)
			{
				playersToRemove.add(playerName);
				receivedPacketsFromPlayer.clear();
				continue;
			}
				
			while(!receivedPacketsFromPlayer.isEmpty())
			{
				PacketBase packet = receivedPacketsFromPlayer.poll();
				packet.handleServerSide(player);
			}
		}
	    	
		receivedPacketsServer.keySet().removeAll(playersToRemove);
					
	}
	
	public void initialise()
	{
		channels = NetworkRegistry.INSTANCE.newChannel("Craftyourliferp", this);
		
		registerPacket(PacketUIController.class);
		registerPacket(PacketStealing.class);
		registerPacket(PacketCosmetic.class);
		registerPacket(PacketAnimation.class);
		registerPacket(PacketUpdateRendererSynchronizer.class);
		registerPacket(PacketCardIdentity.class);
		registerPacket(PacketSyncData.class);
		registerPacket(PacketThirst.class);
		registerPacket(PacketAvatar.class);
		registerPacket(PacketPlayerSleep.class);
		registerPacket(PacketItemInteract.class);
		registerPacket(PacketAuthentification.class);
		registerPacket(PacketExtendedArmorSynchronizer.class);
		registerPacket(PacketCrochetDoor.class);

		//decrapted
		registerPacket(PacketAtm.class);
		registerPacket(PacketBitcoinPage.class);
		registerPacket(PacketBMGPage.class);
		registerPacket(PacketConnectingCall.class);
		registerPacket(PacketFinishCall.class);
		registerPacket(PacketOpenTelephone.class);
		registerPacket(PacketPaypal.class);
		registerPacket(PacketSendSms.class);
		registerPacket(PacketSendVoice.class);
		registerPacket(PacketStartCall.class);
		registerPacket(PacketSyncPhone.class);
		registerPacket(PacketPenalty.class);
		registerPacket(PacketListTransmission.class);

	}
	

	public void postInitialise()
	{
		if(modInitialised)
			return;
		
		modInitialised = true;

		packets.sort((c1, c2) ->
		{
			int com = String.CASE_INSENSITIVE_ORDER.compare(c1.getCanonicalName(), c2.getCanonicalName());
			if(com == 0)
				com = c1.getCanonicalName().compareTo(c2.getCanonicalName());
			return com;
		});
	}
	
	@SideOnly(Side.CLIENT)
	private EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
	}
	
	/**
	 * Send a packet to all players
	 */
	public void sendToAll(PacketBase packet)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	/**
	 * Send a packet to a player
	 */
	public void sendTo(PacketBase packet, EntityPlayerMP player)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	/**
	 * Send a packet to all around a point
	 */
	public void sendToAllAround(PacketBase packet, NetworkRegistry.TargetPoint point)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(point);
		channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	/**
	 * Send a packet to all in a dimension
	 */
	public void sendToDimension(PacketBase packet, int dimensionID)
	{
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.DIMENSION);
		channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(dimensionID);
		channels.get(Side.SERVER).writeAndFlush(packet);
	}
	
	/**
	 * Send a packet to the server
	 */
	public void sendToServer(PacketBase packet)
	{
		channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channels.get(Side.CLIENT).writeAndFlush(packet);
	}
	
	//Vanilla packets follow
	
	/**
	 * Send a packet to all players
	 */
	public void sendToAll(Packet<?> packet)
	{
		FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayers(packet);
	}
	
	/**
	 * Send a packet to a player
	 */
	public void sendTo(Packet<?> packet, EntityPlayerMP player)
	{
		player.connection.sendPacket(packet);
	}
	
	/**
	 * Send a packet to all around a point
	 */
	public void sendToAllAround(Packet<?> packet, NetworkRegistry.TargetPoint point)
	{
		FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendToAllNearExcept(null, point.x, point.y, point.z, point.range, point.dimension, packet);
	}
	
	/**
	 * Send a packet to all in a dimension
	 */
	public void sendToDimension(Packet<?> packet, int dimensionID)
	{
		FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().sendPacketToAllPlayersInDimension(packet, dimensionID);
	}
	
	/**
	 * Send a packet to the server
	 */
	public void sendToServer(Packet<?> packet)
	{
		Minecraft.getMinecraft().player.connection.sendPacket(packet);
	}
	
	/**
	 * Send a packet to all around a point without having to create one's own TargetPoint
	 */
	public void sendToAllAround(PacketBase packet, double x, double y, double z, float range, int dimension)
	{
		sendToAllAround(packet, new NetworkRegistry.TargetPoint(dimension, x, y, z, range));
	}
}