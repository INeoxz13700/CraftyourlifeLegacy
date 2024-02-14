package fr.innog.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ExtendedTileEntityItemStackRenderer extends TileEntityItemStackRenderer 
{
    /*private final TileEntityJadeChest chestJade = new TileEntityJadeChest();
    private final TileEntityReversedCraftingTable reversedCraftingTable = new TileEntityReversedCraftingTable();
    
    private final ModelSyringeStand syringeStand = new ModelSyringeStand();
  
    private final ModelSpear modelSpear = new ModelSpear();
    private final ResourceLocation spearTexture = new ResourceLocation("erthilia","textures/items/spear.png");
    
    private final ModelSyringe modelSyringe = new ModelSyringe();
    private final ResourceLocation syringeTexture = new ResourceLocation("erthilia","textures/items/syringe.png");
         */
    
    
    public ExtendedTileEntityItemStackRenderer()
    {
    	
    }
    
    @Override
    public void renderByItem(ItemStack is, float partialTicks)
    {
        Item item = is.getItem();

        /*if(Block.getBlockFromItem(item) == ErthiliaBlocks.JADE_CHEST)
        { 
            TileEntityRendererDispatcher.instance.render(chestJade, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        }
        else if(Block.getBlockFromItem(item) == ErthiliaBlocks.REVERSE_CRAFTING_TABLE)
        {
            TileEntityRendererDispatcher.instance.render(reversedCraftingTable, 0.0D, 0.0D, 0.0D, 0.0F, partialTicks);
        }
        else if(Block.getBlockFromItem(item) == ErthiliaBlocks.SYRINGE_STAND_BLOCK)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(syringeStand.getTexture());
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F,-1.0F, -1.0F);
            syringeStand.render();
            GlStateManager.popMatrix();
        }
        else if(item == ErthiliaItems.SPEAR)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(spearTexture);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F,-1.0F, -1.0F);
            modelSpear.render();
            GlStateManager.popMatrix();
        }
        else if(item == ErthiliaItems.EMPTY_SYRINGE || item == ErthiliaItems.WATER_SYRINGE)
        {
            Minecraft.getMinecraft().getTextureManager().bindTexture(syringeTexture);
            GlStateManager.pushMatrix();
            GlStateManager.scale(1.0F,-1.0F, -1.0F);
            modelSyringe.render();
            GlStateManager.popMatrix();
        }*/
    }

}