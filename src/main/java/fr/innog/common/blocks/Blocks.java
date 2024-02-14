package fr.innog.common.blocks;

import java.util.ArrayList;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.common.tiles.TileEntityManager;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class Blocks {

	
	public final static List<ModBlock> blocks = new ArrayList<ModBlock>();
	
	public static Block goldIngotBlock;
	public static Block atmBlock;
	public static Block paintingBlock;
	public static Block corpseFreezerBlock;
	public static Block cannabisPlant;
	public static Block cocainePlant;
	public static Block tabacPlant;
	

    private static Block registerBlock(String blockName, Block block)
    {
    	return registerBlock(blockName, block, null);
    }
    
    private static Block registerBlock(String blockName, Block block, ItemBlock itemBlock)
    {
    	ModBlock modBlock = new ModBlock(blockName, block, itemBlock);
    	blocks.add(modBlock);
    	return block;
    }
    
	
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) 
	{
		goldIngotBlock = registerBlock("gold_ingot",new BlockGoldIngot(Material.ROCK));
		atmBlock = registerBlock("atm",new BlockAtm(Material.ROCK));
		paintingBlock = registerBlock("painting", new BlockPainting(Material.ROCK));
		corpseFreezerBlock = registerBlock("corpse_freezer",new BlockCorpseFreezer(Material.ROCK));
		
		
		
		cannabisPlant = registerBlock("cannabis_plant",new BlockCannabis(1,3));
		cocainePlant = registerBlock("cocaine_plant",new BlockCocaine(1,3));
		tabacPlant = registerBlock("tabac_plant",new BlockTabac(1,3));
		
		TileEntityManager.registerTileEntity();
		
		blocks.forEach(v -> 
		{
			event.getRegistry().register(v.getBlock());
		});
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void registerRenderers(ModelRegistryEvent event) 
	{
		blocks.forEach(v -> v.registerRender());
		
		TileEntityManager.registerTileEntityRenderer();
	}
	
	
}
