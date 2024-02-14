package fr.dynamx.addons.basics.utils;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.addons.basics.common.infos.BasicsItemInfo;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.items.DynamXItem;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class VehicleKeyUtils {
  public static boolean isKeyItem(ItemStack stack) {
    return (stack.getItem() instanceof DynamXItem && ((DynamXItem)stack.getItem()).getInfo() instanceof fr.dynamx.common.contentpack.type.objects.ItemObject && ((DynamXItem)stack.getItem()).getInfo().getSubPropertyByType(BasicsItemInfo.class) != null && ((BasicsItemInfo)((DynamXItem)stack
      .getItem()).getInfo().getSubPropertyByType(BasicsItemInfo.class)).isKey());
  }
  
  public static void setLinkedVehicle(ItemStack key, BaseVehicleEntity<?> vehicle) {
    if (!key.hasTagCompound())
      key.setTagCompound(new NBTTagCompound()); 
    key.getTagCompound().setString("VehicleId", vehicle.getPersistentID().toString());
    key.getTagCompound().setString("VehicleName", ((ModularVehicleInfo)vehicle.getPackInfo()).getName());
  }
  
  public static ItemStack getKeyForVehicle(BaseVehicleEntity<?> entity) {
    ItemStack stack = new ItemStack((Item)BasicsAddon.keysItem);
    setLinkedVehicle(stack, entity);
    return stack;
  }
}
