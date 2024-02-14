package fr.innog.common.items;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class Items {

	
	public final static List<ModItem> items = new ArrayList<ModItem>();
	
    public static Item itemBillet5; 
    public static Item itemBillet10; 
    public static Item itemBillet20; 
    public static Item itemBillet50; 
    public static Item itemBillet100; 
    public static Item itemBillet200;
    public static Item itemBillet500; 
    public static Item itemCoin1Euro; 
    public static Item itemCoin2Euro;
    
    public static Item bankCard;
    public static Item identityCard;
    public static Item bulletGilet;
    public static Item syringe;
    public static Item wine;
    public static Item beer;
    public static Item watterBottle2L;
    public static Item watterBottle1L;
    public static Item ethylotest;
    public static Item extinguisher;
    public static Item kamsung;
    public static Item cocaine_leaf;
    public static Item cocaine_powder;
    public static Item cannabis_leaf;
    public static Item cannabis_dried;
    public static Item joint;
    public static Item medikit;

    public static Item tabac_leaf;
    public static Item tabac_dried;
    public static Item cigarette;
    public static Item npcAmethyst;
    public static Item npcLocket;
    public static Item npcNecklace;
    public static Item npcPendant;
    public static Item npcRing;
    public static Item npcRuby;
    public static Item npcSaphire;

    
    private static Item registerItem(String itemName, Item item)
    {
    	ModItem modItem = new ModItem(itemName, item);
    	items.add(modItem);
    	return item;
    }
    
	
	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) 
	{
		itemCoin1Euro = registerItem("coin_1euro",new Item());
		itemCoin2Euro = registerItem("coin_2euro",new Item());
        itemBillet5 = registerItem("billet5",new Item());
        itemBillet10 = registerItem("billet10",new Item());
        itemBillet20 = registerItem("billet20",new Item());
        itemBillet50 = registerItem("billet50",new Item());
        itemBillet100 = registerItem("billet100",new Item());
        itemBillet200 = registerItem("billet200",new Item());
        itemBillet500 = registerItem("billet500",new Item());
	
        bankCard = registerItem("bank_card",new Item().setMaxStackSize(1));
        identityCard = registerItem("identity_card",new ItemCardIdentity());
        syringe = registerItem("syringe",new Item().setMaxStackSize(1));
        wine = registerItem("wine",new ItemAlcohol(0.1F,25,1f));
        beer = registerItem("beer", new ItemAlcohol(0.1F,25,0.5f));
    	watterBottle2L = registerItem("waterbottle_1",new ItemWaterBottle(2,100));
    	watterBottle1L = registerItem("waterbottle_2",new ItemWaterBottle(1,100));
    	ethylotest = registerItem("ethylotest",new Item().setMaxStackSize(1));
    	extinguisher = registerItem("extinguisher",new ItemExtinguisher(1200));
    	cocaine_leaf = registerItem("cocaine_leaf",new Item().setMaxStackSize(3));
    	cocaine_powder = registerItem("cocaine_powder",new Item().setMaxStackSize(1));
    	cannabis_leaf = registerItem("cannabis_leaf",new Item().setMaxStackSize(3));
    	cannabis_dried = registerItem("cannabis_dried",new Item().setMaxStackSize(1));
    	joint = registerItem("joint",new Item().setMaxStackSize(1));
    	cigarette = registerItem("cigarette",new Item().setMaxStackSize(1));
    	tabac_leaf = registerItem("tabac_leaf",new Item().setMaxStackSize(3));
    	tabac_dried = registerItem("tabac_dried",new Item().setMaxStackSize(1));
        npcAmethyst = registerItem("npc_amethyst",new Item().setMaxStackSize(64));
        npcLocket = registerItem("npc_locket",new Item().setMaxStackSize(64));
        npcNecklace = registerItem("npc_necklace",new Item().setMaxStackSize(64));
        npcPendant = registerItem("npc_pendant",new Item().setMaxStackSize(64));
        npcRing = registerItem("npc_ring",new Item().setMaxStackSize(64));
        npcRuby = registerItem("npc_ruby",new Item().setMaxStackSize(64));
        npcSaphire = registerItem("npc_saphire",new Item().setMaxStackSize(64));
        medikit = registerItem("medikit",new Item().setMaxStackSize(1));

    	kamsung = registerItem("kamsung",new ItemKamsung());

    	items.forEach(v -> event.getRegistry().register(v.getItem()));
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerRenderers(ModelRegistryEvent event) 
	{
		items.forEach(v -> v.registerRender());
	}
	
	
}
