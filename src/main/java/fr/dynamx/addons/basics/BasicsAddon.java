package fr.dynamx.addons.basics;

import fr.aym.acsguis.api.ACsGuiApi;
import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.client.InteractionKeyController;
import fr.dynamx.addons.basics.common.infos.BasicsItemInfo;
import fr.dynamx.addons.basics.server.CommandBasicsSpawn;
import fr.dynamx.addons.basics.utils.FuelJerrycanUtils;
import fr.dynamx.addons.basics.utils.VehicleKeyUtils;
import fr.dynamx.api.contentpack.DynamXAddon;
import fr.dynamx.api.contentpack.DynamXAddon.AddonEventSubscriber;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.common.contentpack.type.objects.ItemObject;
import fr.dynamx.common.items.DynamXItem;
import fr.dynamx.common.items.DynamXItemRegistry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.command.ICommand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "dynamx_basics", version = "1.0.8", name = "DynamX Basics Addon", dependencies = "before: dynamxmod", acceptedMinecraftVersions = "[1.12.2]")
@DynamXAddon(modid = "dynamx_basics", name = "DynamX Basics", version = "1.0.7")
public class BasicsAddon {
  public static final String ID = "dynamx_basics";
  
  public static final Map<String, SoundEvent> soundMap = new HashMap<>();
  
  public static DynamXItem<?> keysItem;
  
  public static DynamXItem<?> jerrycanItem;
  
  @AddonEventSubscriber
  public static void initAddon() {
    if (FMLCommonHandler.instance().getSide().isClient())
      setupClient(); 
    registerKey();
    registerJerrycan();
  }
  
  private static void registerKey() {
    keysItem = new DynamXItem("dynamx_basics", "car_keys", new ResourceLocation("dynamxmod", "disable_rendering")) {};
    keysItem.setCreativeTab(DynamXItemRegistry.objectTab);
    ItemObject<?> info = (ItemObject)keysItem.getInfo();
    BasicsItemInfo bas = new BasicsItemInfo((ISubInfoTypeOwner)info);
    bas.setKey(true);
    info.addSubProperty((ISubInfoType)bas);
  }
  
  private static void registerJerrycan() {
    jerrycanItem = new DynamXItem("dynamx_basics", "fuel_jerrycan", new ResourceLocation("dynamxmod", "models/item/jerrycan.obj")) {
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
          if (FuelJerrycanUtils.hasFuel(stack))
            tooltip.add(I18n.format("basadd.fuel.jerrycan", new Object[] { Float.valueOf(FuelJerrycanUtils.getFuel(stack)) })); 
        }
      };
    jerrycanItem.setCreativeTab(DynamXItemRegistry.objectTab);
    ItemObject<?> info = (ItemObject)jerrycanItem.getInfo();
    BasicsItemInfo bas = new BasicsItemInfo((ISubInfoTypeOwner)info);
    bas.setFuelCapacity(60);
    info.addSubProperty((ISubInfoType)bas);
  }
  
  @SideOnly(Side.CLIENT)
  private static void setupClient() {
    ClientRegistry.registerKeyBinding(BasicsAddonController.hornKey);
    ClientRegistry.registerKeyBinding(BasicsAddonController.sirenKey);
    ClientRegistry.registerKeyBinding(BasicsAddonController.beaconKey);
    ClientRegistry.registerKeyBinding(BasicsAddonController.headlights);
    ClientRegistry.registerKeyBinding(BasicsAddonController.turnLeft);
    ClientRegistry.registerKeyBinding(BasicsAddonController.turnRight);
    ClientRegistry.registerKeyBinding(BasicsAddonController.warnings);
    ClientRegistry.registerKeyBinding(InteractionKeyController.interaction);
    ACsGuiApi.registerStyleSheetToPreload(BasicsAddonController.STYLE);
  }
  
  @EventHandler
  public void serverStarting(FMLServerStartingEvent event) {
    event.registerServerCommand((ICommand)new CommandBasicsSpawn());
  }
}
