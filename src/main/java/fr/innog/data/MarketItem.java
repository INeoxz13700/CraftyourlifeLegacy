package fr.innog.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class MarketItem {
	
	private ItemStack item;
		
	private double priceInEuro;
	
	public MarketItem() 
	{
		
	}
	
	public MarketItem(ItemStack item, double priceInEuro)
	{
		this.setItem(item);
		this.setPriceInEuro(priceInEuro);
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	public double getPriceInEuro() {
		return priceInEuro;
	}
	
	public BigDecimal getPriceQuantityInBitcoin(float bitcoinPrice)
	{
		BigDecimal priceEuro = new BigDecimal(priceInEuro * item.getCount());
        BigDecimal priceBitcoin = priceEuro.divide(new BigDecimal(bitcoinPrice), 6, RoundingMode.HALF_UP);

		return priceBitcoin;
	}
	public BigDecimal getPriceInBitcoin(float bitcoinPrice)
	{
        BigDecimal priceEuro = new BigDecimal(priceInEuro);
        BigDecimal priceBitcoin = priceEuro.divide(new BigDecimal(bitcoinPrice), 6, RoundingMode.HALF_UP);
        return priceBitcoin;
	}
	
	public boolean isEqual(MarketItem item, EntityPlayer player)
	{
		return this.item.getItem() == item.getItem().getItem() && this.item.hasDisplayName() == item.getItem().hasDisplayName() && this.item.getDisplayName().equalsIgnoreCase(item.getItem().getDisplayName()) && priceInEuro == item.priceInEuro;
	}
	
	public int getQuantity()
	{
		return item.getCount();
	}

	public void setPriceInEuro(double priceInEuro) {
		this.priceInEuro = priceInEuro;
	}
	
	public void encode(ByteBuf data)
	{
		ByteBufUtils.writeItemStack(data, item);
		data.writeDouble(priceInEuro);
	}
	
	
	public void decode(ByteBuf data)
	{
		item = ByteBufUtils.readItemStack(data);
		priceInEuro = data.readDouble();
	}
		

}