package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.addons.basics.common.modules.FuelTankModule;
import fr.dynamx.addons.basics.utils.FuelJerrycanUtils;
import fr.dynamx.api.contentpack.object.part.InteractivePart;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.PackPhysicsEntity;
import fr.dynamx.common.entities.PhysicsEntity;
import fr.dynamx.common.items.DynamXItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

@RegisteredSubInfoType(name = "FuelTank", registries = {SubInfoTypeRegistries.WHEELED_VEHICLES}, strictName = false)
public class FuelTankInfos extends InteractivePart<BaseVehicleEntity<?>, ModularVehicleInfo> {
  private static final ResourceLocation FUEL_CROSS_HAIRS_ICON = new ResourceLocation("dynamx_basics", "textures/fuel.png");
  
  @PackFileProperty(configNames = {"TankSize"}, type = DefinitionType.DynamXDefinitionTypes.FLOAT, defaultValue = "100")
  protected float tankSize;
  
  @PackFileProperty(configNames = {"FuelConsumption"}, type = DefinitionType.DynamXDefinitionTypes.FLOAT, defaultValue = "1")
  protected float fuelConsumption;
  
  public FuelTankInfos(ModularVehicleInfo owner, String partName) {
    super(owner, partName, 0.5F, 0.5F);
  }
  
  public float getTankSize() {
    return this.tankSize;
  }
  
  public float getFuelConsumption() {
    return this.fuelConsumption;
  }
  
  public boolean interact(BaseVehicleEntity<?> entity, EntityPlayer with) {
    if (FuelJerrycanUtils.isJerrycanItem(with.getHeldItemMainhand())) {
      BasicsItemInfo jerrycan = (BasicsItemInfo)((DynamXItem)with.getHeldItemMainhand().getItem()).getInfo().getSubPropertyByType(BasicsItemInfo.class);
      if (FuelJerrycanUtils.hasFuel(with.getHeldItemMainhand())) {
        FuelTankModule tank = (FuelTankModule)entity.getModuleByType(FuelTankModule.class);
        if (tank != null) {
          float left = Math.min(FuelJerrycanUtils.getFuel(with.getHeldItemMainhand()), tank.getInfo().getTankSize() - tank.getFuel());
          FuelJerrycanUtils.setFuel(with.getHeldItemMainhand(), FuelJerrycanUtils.getFuel(with.getHeldItemMainhand()) - left);
          tank.setFuel(tank.getFuel() + left);
        } 
      } else {
        FuelJerrycanUtils.setFuel(with.getHeldItemMainhand(), jerrycan.getFuelCapacity());
      } 
    } 
    return false;
  }
  
  public String getName() {
    return "FuelTank of " + ((ModularVehicleInfo)getOwner()).getName();
  }
  
  public void addModules(PackPhysicsEntity<?, ?> entity, ModuleListBuilder modules) {
    if (modules.hasModuleOfClass(FuelTankModule.class))
      throw new IllegalStateException("More than one fuel tank infos (" + getFullName() + ") added to " + entity.getPackInfo().getFullName() + " " + entity); 
    modules.add((IPhysicsModule)new FuelTankModule(entity, this));
  }
  
  public ResourceLocation getHudCursorTexture() {
    return FUEL_CROSS_HAIRS_ICON;
  }
}
