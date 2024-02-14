package fr.dynamx.addons.basics.common.infos;

import com.jme3.math.Vector3f;
import fr.dynamx.addons.basics.common.modules.LicensePlateModule;
import fr.dynamx.addons.basics.utils.TextUtils;
import fr.dynamx.api.contentpack.object.INamedObject;
import fr.dynamx.api.contentpack.object.part.BasePart;
import fr.dynamx.api.contentpack.object.part.IDrawablePart;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.DefinitionType;
import fr.dynamx.api.contentpack.registry.IPackFilePropertyFixer;
import fr.dynamx.api.contentpack.registry.IPackFilePropertyFixer.PackFilePropertyFixer;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.ModuleListBuilder;
import fr.dynamx.client.renders.RenderPhysicsEntity;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.ModularPhysicsEntity;
import fr.dynamx.common.entities.PackPhysicsEntity;
import javax.annotation.Nullable;

@RegisteredSubInfoType(name = "ImmatriculationPlate", registries = {SubInfoTypeRegistries.WHEELED_VEHICLES, SubInfoTypeRegistries.HELICOPTER}, strictName = false)
public class LicensePlateInfos extends BasePart<ModularVehicleInfo> implements IDrawablePart<BaseVehicleEntity<?>> {
  @PackFileProperty(configNames = {"Rotation"}, type = DefinitionType.DynamXDefinitionTypes.VECTOR3F, description = "common.rotation")
  protected Vector3f rotation = new Vector3f();
  
  @PackFileProperty(configNames = {"Pattern"}, required = false)
  protected String pattern = "aa-111-aa";
  
  @PackFileProperty(configNames = {"Font"}, required = false)
  protected String font = "dynamx_basics:e";
  
  @PackFileProperty(configNames = {"Color"}, description = "common.color", required = false)
  protected int[] color = new int[] { 10, 10, 10 };
  
  @PackFileProperty(configNames = {"LineSpacing"}, required = false)
  protected float lineSpacing = 0.0F;
  
  @PackFilePropertyFixer(registries = {SubInfoTypeRegistries.WHEELED_VEHICLES})
  public static final IPackFilePropertyFixer PROPERTY_FIXER;
  
  static {
    PROPERTY_FIXER = ((object, key, value) -> {
        if (key.startsWith("Immatriculation")) {
          key = key.replaceAll("Immatriculation", "");
          IPackFilePropertyFixer.FixResult superr = BasePart.PROPERTY_FIXER.fixInputField(object, key, value);
          return (superr != null) ? superr : new IPackFilePropertyFixer.FixResult(key, true);
        } 
        return null;
      });
  }
  
  public LicensePlateInfos(ModularVehicleInfo owner, String partName) {
    super((ISubInfoTypeOwner)owner, partName);
  }
  
  public void appendTo(ModularVehicleInfo owner) {
    owner.addPart(this);
  }
  
  public Vector3f getRotation() {
    return this.rotation;
  }
  
  public String getFont() {
    return this.font;
  }
  
  public int[] getColor() {
    return this.color;
  }
  
  public String getPattern() {
    return this.pattern;
  }
  
  public float getLineSpacing() {
    return this.lineSpacing;
  }
  
  public void addModules(PackPhysicsEntity<?, ?> entity, ModuleListBuilder modules) {
    if (modules.hasModuleOfClass(LicensePlateModule.class)) {
      ((LicensePlateModule)modules.getByClass(LicensePlateModule.class)).addInformation(this);
    } else {
      modules.add((IPhysicsModule)new LicensePlateModule(this));
    } 
  }
  
  public String getName() {
    return "PlateInfo named " + getPartName() + " in " + ((ModularVehicleInfo)getOwner()).getName();
  }
  
  public void drawParts(@Nullable BaseVehicleEntity<?> baseVehicleEntity, RenderPhysicsEntity<?> renderPhysicsEntity, ModularVehicleInfo modularVehicleInfo, byte b, float v) {
    if (baseVehicleEntity == null)
      return; 
    LicensePlateModule module = (LicensePlateModule)baseVehicleEntity.getModuleByType(LicensePlateModule.class);
    for (LicensePlateInfos licensePlateInfos : modularVehicleInfo.getPartsByType(LicensePlateInfos.class))
      TextUtils.drawText(licensePlateInfos
          .getPosition(), licensePlateInfos
          .getScale(), licensePlateInfos
          .getRotation(), module
          .getPlate(), licensePlateInfos
          .getColor(), licensePlateInfos
          .getFont(), licensePlateInfos
          .getLineSpacing()); 
  }
  
  public String[] getRenderedParts() {
    return new String[0];
  }
}
