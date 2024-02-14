package fr.innog.common.inventory;

import javax.annotation.Nonnull;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class CustomInventoryPlayer extends ItemStackHandler  {
    
	private EntityPlayer player;
	
    public CustomInventoryPlayer(EntityPlayer player) 
    {
    	super(2);
    	this.player = player;
    }
    
    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack)
    {
        validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        onContentsChanged(slot);
    }
    
    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot)
    {
        validateSlotIndex(slot);
        return this.stacks.get(slot);
    }
    
    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        
        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        int limit = getStackLimit(slot, stack);

        if (!existing.isEmpty())
        {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;

            limit -= existing.getCount();
        }

        if (limit <= 0)
            return stack;

        boolean reachedLimit = stack.getCount() > limit;

        if (!simulate)
        {
            if (existing.isEmpty())
            {
                this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }
    
    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {    	
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                this.stacks.set(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
            return existing;
        }
        else
        {
            if (!simulate)
            {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }
    
    @Override
    protected void onContentsChanged(int slot)
    {
    	if(!this.player.world.isRemote)
    	{
        	IPlayer player = MinecraftUtils.getPlayerCapability(this.player);
        	player.updateRenderer();
    	}
    }
    




    
   

}
