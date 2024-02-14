package fr.innog.common.blocks;

import java.util.Random;

import fr.innog.common.ModCore;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BlockTabac extends BlockBush {
		
	private int minQuantity;
	
	private int maxQuantity;
		
	public BlockTabac(int minQuantity, int maxQuantity)
	{
		this.minQuantity = minQuantity;
		this.maxQuantity = maxQuantity;
		this.setHardness(5.0F);
		this.setResistance(5.0F);
	}
	
	
    public int quantityDropped(Random random)
    {
        return MathHelper.getInt(random, minQuantity, maxQuantity);
    }
    
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
    	return fr.innog.common.items.Items.tabac_leaf;
    }
    
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return true;
    }
    


}
