package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.client.FuelTankController;
import fr.dynamx.addons.basics.common.infos.FuelTankInfos;
import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable.SynchronizedPhysicsModule;
import fr.dynamx.common.entities.PackPhysicsEntity;
import fr.dynamx.common.entities.modules.CarEngineModule;
import fr.dynamx.common.entities.vehicles.CarEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import javax.annotation.Nullable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SynchronizedPhysicsModule(modid = "dynamx_basics")
public class FuelTankModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
  private final PackPhysicsEntity<?, ?> entity;
  
  private final FuelTankInfos info;
  
  private FuelTankController controller;
  
  @SynchronizedEntityVariable(name = "fuel")
  private final EntityVariable<Float> fuel = new EntityVariable<Float>(SynchronizationRules.SERVER_TO_CLIENTS, Float.valueOf(Float.MAX_VALUE));
  
  public FuelTankModule(PackPhysicsEntity<?, ?> entity, FuelTankInfos info) {
    this.info = info;
    this.entity = entity;
    if (entity.world.isRemote)
      this.controller = new FuelTankController(this); 
    setFuel(info.getTankSize());
  }
  
  public float getFuel() {
    return ((Float)this.fuel.get()).floatValue();
  }
  
  public void setFuel(float fuel) {
    this.fuel.set(Float.valueOf(Math.max(Math.min(fuel, this.info.getTankSize()), 0.0F)));
  }
  
  public FuelTankInfos getInfo() {
    return this.info;
  }
  
  @Nullable
  @SideOnly(Side.CLIENT)
  public IVehicleController createNewController() {
    return (IVehicleController)this.controller;
  }
  
  public void updateEntity() {
    if (this.entity instanceof CarEntity && getFuel() > 0.0F) {
      CarEntity<?> carEntity = (CarEntity<?>)this.entity;
      CarEngineModule engine = (CarEngineModule)carEntity.getModuleByType(CarEngineModule.class);
      if (engine != null) {
        float RPM = engine.getEngineProperty(VehicleEntityProperties.EnumEngineProperties.REVS);
        setFuel(
            (float)(getFuel() - (RPM * getInfo().getFuelConsumption() / 120.0F) * (engine.isAccelerating() ? 1.1D : 0.9D)));
      } 
    } 
  }
}
