package fr.innog.phone.web.page;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.data.MarketItem;
import fr.innog.network.packets.decrapted.PacketBMGPage;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class BMGPage extends WebPageData {

	
	public IPlayer persistentData;
	
	public EntityPlayer player;
	
	public List<MarketItem> items = new ArrayList<>();
	
	public float bitcoinPrice;
	
	public boolean transactionSuccess = false;
	
	public BMGPage(EntityPlayer player)
	{
		this.player = player;
		
		persistentData = MinecraftUtils.getPlayerCapability(player);
	}
	
	
	public void addItemToCart(MarketItem item)
	{
		if(player.world.isRemote)
		{
			persistentData.getPhoneData().getShopData().addItemToCart(item);
		}
		else
		{
			if(itemAvaibleInMarket(item))
			{
				persistentData.getPhoneData().getShopData().addItemToCart(item);
			}
		}
	

	}
	

	public void removeItemFromCart(MarketItem item)
	{
		persistentData.getPhoneData().getShopData().removeItemFromCart(item);
	}
	
	public boolean itemAvaibleInMarket(MarketItem item)
	{
		for(MarketItem marketItem : items)
		{
			if(marketItem.isEqual(item, player)) return true;
		}
		return false;
	}

	
	public List<MarketItem> getItemsAvaibleInMarket()
	{
		return items;
	}
	
	
	public float getTotalPrice()
	{
		float total = 0.0f;
		for(MarketItem data : persistentData.getPhoneData().getShopData().shopCart)
		{
			total += (data.getPriceInBitcoin(bitcoinPrice).floatValue() * data.getQuantity());
		}
		return total;
	}
	
	public void confirmTransaction()
	{
		if(player.world.isRemote)
		{
			ModCore.getPackethandler().sendToServer(PacketBMGPage.transactionPacket(persistentData.getPhoneData().getShopData().shopCart));
		}
		else
		{
			if(persistentData.getPhoneData().getShopData().shopCart.size() == 0)
			{
				MinecraftUtils.sendMessage(player,"§cIl n'y a pas d'article!");
				return;
			}
			
			float price = getTotalPrice();
			
			if(persistentData.getPhoneData().bitcoin >= price)
			{
				persistentData.getPhoneData().bitcoin -= price;
				
				for(MarketItem item : persistentData.getPhoneData().getShopData().shopCart)
				{
					persistentData.getPhoneData().itemStockage.add(item.getItem());
				}
				
				persistentData.getPhoneData().getShopData().shopCart.clear();
				
				updatePageData();

				ModCore.getPackethandler().sendTo(new PacketBMGPage(5), (EntityPlayerMP)player);
			}
			else
			{
				persistentData.getPhoneData().getShopData().shopCart.clear();

				ModCore.getPackethandler().sendTo(new PacketBMGPage(6),  (EntityPlayerMP)player);
			}
		}
	}
	

	public void syncMarketItem()
	{
		if(!persistentData.getPlayer().world.isRemote)
		{
			 ModCore.getPackethandler().sendTo(PacketBMGPage.syncMarketPacket(items), (EntityPlayerMP)persistentData.getPlayer());
		}
	}

	
	public void initPage() 
	{
		if(!persistentData.getPlayer().world.isRemote)
		{
			items = WorldDataManager.get(persistentData.getPlayer().world).getPhoneAppData().getMarketData().getItems();
			syncMarketItem();
			updatePageData();
		}
	}
	
	public void initPage(float bitcoinPrice) 
	{
		this.bitcoinPrice = bitcoinPrice;
	}
	
	
	@Override
	public byte pageId() 
	{
		return 2;
	}

	@Override
	public void updatePageData()
	{
		persistentData.getPhoneData().syncMoney();
	}

}
