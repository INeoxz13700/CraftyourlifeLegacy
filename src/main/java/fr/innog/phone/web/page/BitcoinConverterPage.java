package fr.innog.phone.web.page;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.network.packets.decrapted.PacketBitcoinPage;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class BitcoinConverterPage extends WebPageData{
	
	public IPlayer playerData;
		
	private float bitcoinInEuro;
	
	public BitcoinConverterPage(EntityPlayer player)
	{
		playerData = MinecraftUtils.getPlayerCapability(player);
	}
	
	public void convert(byte type, float price)
	{

		WorldDataManager data = WorldDataManager.get(playerData.getPlayer().world);
		
		float bitcoinPrice = data.getPhoneAppData().getBitcoinInEuro();
		
		if(bitcoinPrice <= 0)
		{
			ModCore.getPackethandler().sendTo(PacketBitcoinPage.displayError((byte)2),(EntityPlayerMP) playerData.getPlayer());
			return;
		}
		
		
		//Euro to bictoin
		if(type == 0)
		{
			if(MinecraftUtils.haveMoney(playerData.getPlayer(), price))
			{
				float totalPrice = price / bitcoinPrice;
				MinecraftUtils.removeMoney(playerData.getPlayer(), price);
				playerData.getPhoneData().bitcoin += totalPrice;
			}
			else 
			{
				ModCore.getPackethandler().sendTo(PacketBitcoinPage.displayError((byte)0),(EntityPlayerMP) playerData.getPlayer());
			}
		}
		//Bitcoin to euro
		else
		{
			if(playerData.getPhoneData().bitcoin >= price)
			{
				float totalPrice = price * bitcoinPrice;
				playerData.getPhoneData().bitcoin  -= price;
				MinecraftUtils.addMoney(playerData.getPlayer(), totalPrice);
			}
			else
			{
				ModCore.getPackethandler().sendTo(PacketBitcoinPage.displayError((byte)1),(EntityPlayerMP) playerData.getPlayer());
			}
		}
		initPage();
	}
	
	
	public void initPage()
	{
		World world = playerData.getPlayer().world;
		if(!world.isRemote)
		{
			bitcoinInEuro = WorldDataManager.get(world).getPhoneAppData().getBitcoinInEuro();
			ModCore.getPackethandler().sendTo(PacketBitcoinPage.syncPageData(bitcoinInEuro),(EntityPlayerMP) playerData.getPlayer());
			updatePageData();
		}
	}
	
	public float getBitcoinInEuro()
	{
		return bitcoinInEuro;
	}
	
	public void initPage(float bitcoinInEuro)
	{
		this.bitcoinInEuro = bitcoinInEuro;
	}
	
	@Override
	public byte pageId() {
		return 1;
	}

	@Override
	public void updatePageData()
	{
		playerData.getPhoneData().syncMoney();	
	}
	
	

}
