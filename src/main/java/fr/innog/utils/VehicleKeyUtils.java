package fr.innog.utils;

import java.util.UUID;

import fr.dynamx.common.entities.BaseVehicleEntity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;

public class VehicleKeyUtils {

    public static boolean isKeyItem(ItemStack stack) {
        return stack.getItem() == Item.getItemFromBlock(Blocks.DIRT);
    }

    public static boolean hasLinkedVehicle(ItemStack key) {
        return key.hasTagCompound() && key.getTagCompound().hasKey("VehicleId", Constants.NBT.TAG_STRING);
    }

    public static void setLinkedVehicle(ItemStack key, BaseVehicleEntity<?> vehicle) {
        if (!key.hasTagCompound()) {
            key.setTagCompound(new NBTTagCompound());
        }
        key.getTagCompound().setString("VehicleId", vehicle.getPersistentID().toString());
        key.getTagCompound().setString("VehicleName", vehicle.getPackInfo().getName());
    }

    public static UUID getLinkedVehicle(ItemStack key) {
        return UUID.fromString(key.getTagCompound().getString("VehicleId"));
    }

    public static ItemStack getKeyForVehicle(BaseVehicleEntity<?> entity) {
        ItemStack stack = new ItemStack(Blocks.DIRT);
        setLinkedVehicle(stack, entity);
        return stack;
    }
	
}
