package fr.innog.common.tiles;

import fr.innog.common.ModCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityManager {

	public static void registerTileEntity() 
	{
		 GameRegistry.registerTileEntity(TileEntityGoldIngot.class, new ResourceLocation(ModCore.MODID, "gold_ingot"));
		 GameRegistry.registerTileEntity(TileEntityPainting.class, new ResourceLocation(ModCore.MODID, "painting"));

	}
	
	@SideOnly(Side.CLIENT)
	public static void registerTileEntityRenderer() 
	{ 
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJadeChest.class, new TileEntityJadeChestRenderer());
	}
	
}
