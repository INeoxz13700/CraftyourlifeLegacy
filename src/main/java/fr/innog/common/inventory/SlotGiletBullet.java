package fr.innog.common.inventory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import fr.dynamx.common.items.DynamXItemArmor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotGiletBullet extends Slot {

	private CustomInventoryPlayer customInventory;
	
    private static IInventory emptyInventory = new InventoryBasic("[Null]", true, 1);

	
	public SlotGiletBullet(CustomInventoryPlayer customInventory, int index, int xPosition, int yPosition) {
		super(emptyInventory, index, xPosition, yPosition);
		this.customInventory = customInventory;
	}

	
	 @Nullable
     @SideOnly(Side.CLIENT)
     public String getSlotTexture()
     {
         return "craftyourliferp:items/empty_armor_slot_gilet";
     }
     
     public int getSlotStackLimit()
     {
         return 1;
     }
     
     public boolean isItemValid(ItemStack stack)
     {
     	if(stack.getItem() instanceof DynamXItemArmor)
     	{
     		DynamXItemArmor<?> itemArmor = (DynamXItemArmor<?>) stack.getItem();
     		if(itemArmor.getInfo().getDescription().toLowerCase().contains("gilet pare-balles"))
     		{
                 return true;
     		}
     	}
     	return false;
     }
     
     @Override
     @Nonnull
     public ItemStack getStack() {
         return this.customInventory.getStackInSlot(this.getSlotIndex());
     }
     
     @Override
     public void putStack(@Nonnull ItemStack stack) {
     	customInventory.setStackInSlot(getSlotIndex(), stack);
         this.onSlotChanged();
     }
     
     @Override
     public void onSlotChange(@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {

     }
     
     @Override
     public int getItemStackLimit(@Nonnull ItemStack stack)  {
         ItemStack maxAdd = stack.copy();
         int maxInput = stack.getMaxStackSize();
         maxAdd.setCount(maxInput);

         ItemStack currentStack = customInventory.getStackInSlot(this.getSlotIndex());
         customInventory.setStackInSlot(this.getSlotIndex(), ItemStack.EMPTY);
         
         ItemStack remainder = customInventory.insertItem(this.getSlotIndex(), maxAdd, true);
         customInventory.setStackInSlot(this.getSlotIndex(), currentStack);

         return maxInput - remainder.getCount();
     }
     
     @Override
     public boolean canTakeStack(EntityPlayer playerIn) {
    	 return true;
     }
     
     @Override
     @Nonnull
     public ItemStack decrStackSize(int amount) {
         return this.customInventory.extractItem(this.getSlotIndex(), amount, false);
     }
     
     @Override
     public boolean isSameInventory(Slot other) {
         return other instanceof SlotBraid && ((SlotBraid) other).customInventory == this.customInventory;
     }
	
}
