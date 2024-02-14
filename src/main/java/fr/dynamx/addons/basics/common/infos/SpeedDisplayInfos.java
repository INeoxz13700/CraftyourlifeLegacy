package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.utils.TextUtils;
import fr.dynamx.addons.basics.utils.VehicleUtils;
import fr.dynamx.api.contentpack.object.part.BasePart;
import fr.dynamx.api.contentpack.object.part.IDrawablePart;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.client.renders.RenderPhysicsEntity;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.ModularPhysicsEntity;
import javax.annotation.Nullable;

@RegisteredSubInfoType(name = "SpeedDisplay", registries = {SubInfoTypeRegistries.WHEELED_VEHICLES, SubInfoTypeRegistries.HELICOPTER}, strictName = false)
public class SpeedDisplayInfos extends BasePart<ModularVehicleInfo> implements IDrawablePart<BaseVehicleEntity<?>> {
  @PackFileProperty(configNames = {"Rotation"}, type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
  protected Vector3f rotation;
  
  @PackFileProperty(configNames = {"Font"}, required = false)
  protected String font = "dynamx_basics:e";
  
  @PackFileProperty(configNames = {"Color"}, description = "common.color", required = false)
  protected int[] color = new int[] { 10, 10, 10 };
  
  public SpeedDisplayInfos(ModularVehicleInfo owner, String partName) {
    super((ISubInfoTypeOwner)owner, partName);
  }
  
  public void appendTo(ModularVehicleInfo owner) {
    owner.addSubProperty((ISubInfoType)this);
  }
  
  public Vector3f getRotation() {
    return this.rotation;
  }
  
  public int[] getColor() {
    return this.color;
  }
  
  public String getFont() {
    return this.font;
  }
  
  public String getName() {
    return "PartShape named " + getPartName() + " in " + ((ModularVehicleInfo)getOwner()).getName();
  }
  
  public void drawParts(@Nullable BaseVehicleEntity<?> entity, RenderPhysicsEntity<?> renderPhysicsEntity, ModularVehicleInfo modularVehicleInfo, byte b, float v) {
    if (entity == null)
      return; 
    String speed = "" + VehicleUtils.getSpeed(entity);
    for (SpeedDisplayInfos info : modularVehicleInfo.getPartsByType(SpeedDisplayInfos.class))
      TextUtils.drawText(info.getPosition(), info.getScale(), info.getRotation(), speed, info.getColor(), info.getFont()); 
  }
  
  public String[] getRenderedParts() {
    return new String[0];
  }
}
