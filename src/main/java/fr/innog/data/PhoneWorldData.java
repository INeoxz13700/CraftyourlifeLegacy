package fr.innog.data;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.innog.common.ModCore;
import fr.innog.common.world.WorldDataManager;
import fr.innog.network.packets.decrapted.PacketSendSms;
import fr.innog.utils.HTTPUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class PhoneWorldData implements ISaveHandler {

	private List<SmsData> smsToSends = new ArrayList<>();

	private BlackMarketData marketData = new BlackMarketData();

	private boolean marketUpdated = false;

	private float bitcoinInEuro = 1.0f;
	
	private ItemStackProbability itemsProbability = new ItemStackProbability();

    public long lastBitcoinUpdateTime;
    
    public long lastMarketUpdate;
    
    public long lastSmsRedistrubitionTime;
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList nbtList = new NBTTagList();
		
		for(SmsData data : smsToSends)
		{
			NBTTagCompound sms = new NBTTagCompound();
			sms.setString("SenderNumber", data.senderNumber);
			sms.setString("ReceiverNumber", data.receiverNumber);
			sms.setString("Message", data.message);
			nbtList.appendTag(sms);
		}
		
		compound.setTag("SmsData", nbtList);
		
		
		compound.setFloat("BitcoinInEuro", bitcoinInEuro);
		compound.setBoolean("MarketUpdated", marketUpdated);
		
		marketData.writeToNBT(compound);
		
		itemsProbability.writeToNBT("BlackMarketProbability", compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		if(compound.hasKey("SmsData"))
		{
			NBTTagList nbtList = (NBTTagList) compound.getTag("SmsData");
			for(int i = 0; i < nbtList.tagCount(); i++)
			{
				NBTTagCompound sms = nbtList.getCompoundTagAt(i);
				SmsData data = new SmsData(0, sms.getString("SenderNumber"), sms.getString("ReceiverNumber"), sms.getString("Message"), false);
				smsToSends.add(data);
			}
		}
		
		bitcoinInEuro = compound.getFloat("BitcoinInEuro");
		marketUpdated = compound.getBoolean("MarketUpdated");
		
		marketData.readFromNBT(compound);
		
		itemsProbability.readFromNBT("BlackMarketProbability", compound);
	}
	
	public List<SmsData> getSmsToSend()
	{
		return smsToSends;
	}
	
	public void distribuateSms(World world)
	{
		List<SmsData> toRemove = new ArrayList<>();
		
		for(SmsData data : getSmsToSend())
		{
			try {
				String username = NumberData.getUsernameByNumber(data.receiverNumber);
				if(username == null) continue;
				
				EntityPlayer receiverPlayer = world.getPlayerEntityByName(username);
								
				if(receiverPlayer == null) continue;
				
				ModCore.getPackethandler().sendTo(new PacketSendSms(data.message,data.receiverNumber,data.senderNumber), (EntityPlayerMP)receiverPlayer);
				
				toRemove.add(data);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		
		getSmsToSend().removeAll(toRemove);
		WorldDataManager.get(world).markDirty();
	}
	
	public void addSmsToSend(World world, SmsData data)
	{
		getSmsToSend().add(data);
		WorldDataManager.get(world).markDirty();
	}

	public float getBitcoinInEuro()
	{
		return bitcoinInEuro;
	}
	
	public void updateBitcoinPrice(World world) 
	{
		String data = HTTPUtils.doPostHttps("https://blockchain.info/ticker", "");

	      try {
	            ObjectMapper objectMapper = new ObjectMapper();
	            JsonNode jsonNode = objectMapper.readTree(data);

	            this.bitcoinInEuro = jsonNode.get("EUR").get("15m").floatValue();

	            System.out.println("Prix en euros (EUR) : " + bitcoinInEuro);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

		WorldDataManager.get(world).markDirty();
	}
	
	public BlackMarketData getMarketData()
	{
		return marketData;
	}
	
	public boolean addItemToBlackMarket(World world, String id, double priceInEuro, int probabilityToSpawn)
	{
		return addItemToBlackMarket(world,id,priceInEuro, probabilityToSpawn, null);
	}
	
	public boolean addItemToBlackMarket(World world, String id, double priceInEuro, int probabilityToSpawn, String displayName)
	{
		if(itemsProbability != null)
		{
			boolean success = itemsProbability.addItem(id, 1, priceInEuro, probabilityToSpawn,displayName);
			if(success)
			{				
				updateMarket(world);

				return success;
			}
			return false;
		}
		return false;
	}
	
	public boolean removeItemFromBlackMarket(World world, String id, int probabilityToSpawn)
	{
		if(itemsProbability != null)
		{
			boolean success = itemsProbability.remvoveItem(id, probabilityToSpawn);
			if(success)
			{
				WorldDataManager.get(world).markDirty();
				return success;
			}
			return false;
		}
		return false;
	}
	
	public void updateMarket(World world)
	{
		marketData.clear();
		
		int addedItem = 0;
		
		int attemptCount = 1000;
		
		while(addedItem <= 4)
		{
			if(addedItem == itemsProbability.getRegisteredItemCount()) break;

			MarketItem item = itemsProbability.getRandomDropItem();	
			
			if(item == null) continue;
			
			if(marketData.addItem(item)) addedItem++;
				

			if(attemptCount-- <= 0) break;
		}
		
		WorldDataManager.get(world).markDirty();
	}
	
	public ItemStackProbability getItemProbability()
	{
		return itemsProbability;
	}
	
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event)
	{
		if(event.world.isRemote) return;
		
	  	
		if(event.phase == Phase.END) return;
		
		World world = event.world;
		
    	WorldDataManager worldData = WorldDataManager.get(event.world);
    	
    	PhoneWorldData phoneData = worldData.getPhoneAppData();
    	
    	if((System.currentTimeMillis() - phoneData.lastBitcoinUpdateTime) / 1000 >= 60*15)
		{
    		phoneData.lastBitcoinUpdateTime = System.currentTimeMillis();
    		phoneData.updateBitcoinPrice(world);
		}
    	
    	if((System.currentTimeMillis() - phoneData.lastSmsRedistrubitionTime) / 1000 >= 5)
    	{
    		phoneData.lastSmsRedistrubitionTime = System.currentTimeMillis();	
    		phoneData.distribuateSms(world);
    	}

    	
    	Timestamp currentData = new Timestamp(System.currentTimeMillis());
    	if(currentData.getHours() > 0 && currentData.getMinutes() > 0)
    	{
    		if(!phoneData.marketUpdated)
    		{
    			phoneData.marketUpdated = true;
    			phoneData.updateMarket(world);
    		}
    	}
    	else
    	{
			phoneData.marketUpdated = false;
    	}
    	
	}
	
}
