package fr.innog.common.items;

import fr.innog.client.blocks.ICustomModel;
import fr.innog.client.items.renderer.IItemRenderer;
import fr.innog.common.ModCore;
import fr.innog.common.proxy.ClientProxy;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ModItem {

	public String itemName;
	
	private Item item;
	
	private IItemRenderer scriptRendering;
			
	
	public ModItem(String itemName, Item item)
	{
		this.itemName = itemName;
		this.item = item;
		registerItem();
	}
	
	private void registerItem()
	{
		item.setRegistryName(new ResourceLocation(ModCore.MODID,itemName));
		item.setUnlocalizedName(itemName);		
		if(FMLCommonHandler.instance().getSide() == Side.CLIENT) item.setCreativeTab(ClientProxy.creativeTabs);
		
		ModCore.log("Item named " + item.getUnlocalizedName() + " registered");
	}
	
	public ModItem constructRendering(IItemRenderer scriptRendering)
	{
		this.scriptRendering = scriptRendering;
		return this;
	}
	
	public boolean hasScriptRendering()
	{
		return this.scriptRendering != null;
	}
	
	public IItemRenderer getRenderer()
	{
		return this.scriptRendering;
	}
	
	
	public String getItemName()
	{
		return itemName;
	}
	
	public Item getItem()
	{
		return this.item;
	}
	
	public void registerRender()
	{
		if(item instanceof ICustomModel)
		{
			((ICustomModel) item).registerCustomRender(item);
		}
		ModelLoader.setCustomModelResourceLocation(getItem(), 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
}
