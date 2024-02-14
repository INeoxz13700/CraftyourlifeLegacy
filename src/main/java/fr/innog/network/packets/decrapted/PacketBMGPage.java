package fr.innog.network.packets.decrapted;

import java.util.ArrayList;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.MarketItem;
import fr.innog.network.packets.PacketBase;
import fr.innog.phone.Tor;
import fr.innog.phone.web.BMGPageUI;
import fr.innog.phone.web.page.BMGPage;
import fr.innog.phone.web.page.WebPageData;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketBMGPage extends PacketBase 
{
	/*
	 *  0 : openPage
	 *  1 : sync market item to client
	 *  2 : add item to cart
	 *  3 : remove item from cart
	 *  4 : confirm transaction;
	 *  5 : redirect transaction successfull page
	 *  6 : redirect transaction failed page
	 */
	public byte action;
	
	public byte pageId;
	
	public float bitcoinPrice;
	
	public List<MarketItem> items = new ArrayList<>();
		
	//public int index;
	
	//private List<Integer> quantities = new ArrayList();
	
	
	public static PacketBMGPage openPagePacket(float bitcoinPrice)
	{
		PacketBMGPage packet = new PacketBMGPage();
		packet.action = 0;
		packet.bitcoinPrice = bitcoinPrice;
		return packet;
	}
	
	public static PacketBMGPage syncMarketPacket(List<MarketItem> items)
	{
		PacketBMGPage packet = new PacketBMGPage();
		packet.action = 1;
		packet.items = items;
		return packet;
	}
	
	/*public static PacketBMGPage addItemToCartPacket(int itemIndex)
	{
		PacketBMGPage packet = new PacketBMGPage();
		packet.action = 2;
		packet.index = itemIndex;
		return packet;
	}
	
	public static PacketBMGPage removeItemFromCartPacket(int itemIndex)
	{
		PacketBMGPage packet = new PacketBMGPage();
		packet.action = 3;
		packet.index = itemIndex;
		return packet;
	}*/
	
	public static PacketBMGPage transactionPacket(List<MarketItem> itemInCart)
	{
		PacketBMGPage packet = new PacketBMGPage();
		packet.action = 4;
		packet.items = itemInCart;
		return packet;
	}

	public PacketBMGPage(int action)
	{
		this.action = (byte) action;
	}
	
	public PacketBMGPage()
	{
		
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		data.writeByte(action);
		if(action == 0)
		{
			data.writeByte(pageId);
			data.writeFloat(bitcoinPrice);
		}
		else if(action == 1 || action == 4)
		{
			data.writeInt(items.size());
			for(MarketItem item : items)
			{
				item.encode(data);
			}
		}
		/*else if(action == 2 || action == 3)
		{
			data.writeInt(index);
		}*/
		/*else if(action == 4)
		{
			data.writeInt(items.size());
			for(MarketItem item : items)
			{
				data.writeInt(items.indexOf(item));
				data.writeInt(item.getQuantity());
			}
		}*/
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data) {
		action = data.readByte();
		if(action == 0)
		{
			pageId = data.readByte();
			bitcoinPrice = data.readFloat();
		}
		else if(action == 1 || action == 4)
		{
			int itemSize = data.readInt();
			for(int i = 0; i < itemSize; i++)
			{
				MarketItem marketItem = new MarketItem();
				marketItem.decode(data);
				items.add(marketItem);
			}
		}
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		/*if(action == 0)
		{
			BMGPage page = (BMGPage) PlayerCachedData.getData(playerEntity).openPage(pageId);
			WorldData worldData = WorldData.get(playerEntity.worldObj);
			page.bitcoinPrice = worldData.getBitcoinPrice();
			CraftYourLifeRPMod.packetHandler.sendTo(PacketBMGPage.openPagePacket(page.bitcoinPrice), playerEntity);
		}
		else if(action == 4)
		{
			PlayerCachedData playerData = PlayerCachedData.getData(playerEntity);
			WebPageData data = playerData.currentPageData;

			if(data instanceof BMGPage)
			{
				BMGPage page = (BMGPage) data;
				
				for(MarketItem item : items) 
				{
					page.addItemToCart(item);
				}
								
				page.confirmTransaction();
			}
		}*/
		
		if(action == 0)
		{
			BMGPage page = (BMGPage) MinecraftUtils.getPlayerCapability(playerEntity).getPhoneData().openPage(pageId);
			WorldDataManager worldData = WorldDataManager.get(playerEntity.world);
			page.bitcoinPrice = worldData.getPhoneAppData().getBitcoinInEuro();
			ModCore.getPackethandler().sendTo(PacketBMGPage.openPagePacket(page.bitcoinPrice), playerEntity);
		}
		else if(action == 4)
		{
			IPlayer extendedPlayer = MinecraftUtils.getPlayerCapability(playerEntity);
			WebPageData data = extendedPlayer.getPhoneData().currentPageData;

			if(data instanceof BMGPage)
			{
				BMGPage page = (BMGPage) data;
				
				for(MarketItem item : items) 
				{
					page.addItemToCart(item);
				}
								
				page.confirmTransaction();
			}
		}
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer) 
	{
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.currentScreen instanceof PhoneUI)
		{
			PhoneUI phone = PhoneUI.getPhone();
			if(phone.currentApp instanceof Tor)
			{
				Tor tor = (Tor) phone.currentApp;
				if(tor.getCurrentOpenPageData() instanceof BMGPage)
				{
					BMGPage pageData = (BMGPage)tor.getCurrentOpenPageData();
					BMGPageUI pageUI = (BMGPageUI) tor.getCurrentPageUI();

					if(action == 0)
					{
						pageData.initPage(bitcoinPrice);
					}
					else if(action == 1)
					{
						pageData.items = items;
						pageUI.updateGuiState();
					}
					else if(action == 5)
					{
						pageUI.state = pageUI.state.TRANSACTION_MESSAGE;
						pageData.transactionSuccess = true;
						pageData.persistentData.getPhoneData().getShopData().shopCart.clear();

						pageUI.updateGuiState();
					}
					else if(action == 6)
					{
						pageUI.state = pageUI.state.TRANSACTION_MESSAGE;
						pageData.transactionSuccess = false;
						pageUI.updateGuiState();
					}
				}
			}
		}
	}
	
	
}