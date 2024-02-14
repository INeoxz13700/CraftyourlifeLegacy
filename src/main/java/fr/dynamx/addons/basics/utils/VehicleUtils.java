package fr.dynamx.addons.basics.utils;

import fr.dynamx.api.entities.VehicleEntityProperties;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.BasicEngineModule;

public class VehicleUtils {
  public static int getSpeed(BaseVehicleEntity<?> entity) {
    if (entity == null)
      return -1; 
    BasicEngineModule engine = (BasicEngineModule)entity.getModuleByType(BasicEngineModule.class);
    if (engine != null) {
      float[] ab = engine.getEngineProperties();
      if (ab == null)
        return 0; 
      return (int)Math.abs(ab[VehicleEntityProperties.EnumEngineProperties.SPEED.ordinal()]);
    } 
    return -1;
  }
}
