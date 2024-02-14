package fr.innog.common.blocks;

import fr.innog.client.blocks.ICustomModel;
import fr.innog.client.creativetab.CYLCreativeTab;
import fr.innog.client.items.renderer.IItemRenderer;
import fr.innog.common.ModCore;
import fr.innog.common.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModBlock {
		
		private String blockName;
		
		private Block block;
		
		private ItemBlock itemBlock;
		
		private IItemRenderer scriptRendering;
		
		public ModBlock(String blockName, Block block, ItemBlock itemBlock)
		{
			this.blockName = blockName;
			this.block = block;
			this.itemBlock = itemBlock;
			registerBlock();
		}	
				
		public ModBlock(String blockName, Block block)
		{
			this(blockName, block, null);
		}	
		
		private void registerBlock()
		{
			block.setRegistryName(new ResourceLocation(ModCore.MODID,blockName));
			block.setUnlocalizedName(blockName);
			
			ItemBlock ib = null;
			if(itemBlock != null)
			{
				ib = itemBlock;
				ib.setRegistryName(block.getRegistryName() + "_item");
			}
			else
			{
				ib = new ItemBlock(block);
				ib.setRegistryName(block.getRegistryName());
			}
			
			
			GameRegistry.findRegistry(Item.class).register(ib);
						
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT) block.setCreativeTab(ClientProxy.creativeTabs);
												
			ModCore.log("Block named " + block.getUnlocalizedName() + " registered");
		}
		
		public ModBlock constructRendering(IItemRenderer scriptRendering)
		{
			this.scriptRendering = scriptRendering;
			return this;
		}
		
		public IItemRenderer getRenderer()
		{
			return scriptRendering;
		}
		
		public boolean hasScriptRendering()
		{
			return this.scriptRendering != null;
		}
		
		public String getBlockName()
		{
			return blockName;
		}
		
		public Block getBlock()
		{
			return this.block;
		}
		
		
		public boolean hasCustomItemBlock()
		{
			return this.itemBlock != null;
		}
		
		public void registerRender()
		{
			Item item = Item.getItemFromBlock(block);
						
			if(block instanceof ICustomModel)
			{
				((ICustomModel) block).registerCustomRender(item);
			}

			
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));

		}
		
	
}
