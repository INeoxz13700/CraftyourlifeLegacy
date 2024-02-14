package fr.innog.client.creativetab;

import java.util.Comparator;

import fr.innog.common.items.Items;
import fr.innog.common.items.ModItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CYLCreativeTab  extends CreativeTabs {

	public static CreativeTabs instance;

	
	public CYLCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(fr.innog.common.items.Items.itemBillet10);
	}
	
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_)
	{
	    	super.displayAllRelevantItems(p_78018_1_);
	    	
	    	
	    	p_78018_1_.sort(new Comparator<ItemStack>() {

				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					return Integer.compare(getIndex(o1),getIndex(o2));
				}
				
				public int getIndex(ItemStack o1)
				{
					int i = 0;
					for(ModItem item : Items.items)
					{
						if(o1.getItem() == item.getItem())
						{
							return i;
						}
						i++;
					}
					
					/*for(ErthiliaBlock block : ErthiliaBlock.blocks.values())
					{
						if(o1.getItem() == Item.getItemFromBlock(block.getBlock()))
						{
							return block.registeredTime;
						}
					}*/
					
					return -1;
				}
				
				
				
			});
	}
    
}
