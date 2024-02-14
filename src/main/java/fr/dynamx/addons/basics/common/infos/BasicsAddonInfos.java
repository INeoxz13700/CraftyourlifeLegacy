package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.addons.basics.BasicsAddon;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.common.contentpack.type.vehicle.ModularVehicleInfo;
import fr.dynamx.utils.RegistryNameSetter;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

@RegisteredSubInfoType(name = "BasicsAddon", registries = {SubInfoTypeRegistries.WHEELED_VEHICLES, SubInfoTypeRegistries.HELICOPTER}, strictName = false)
public class BasicsAddonInfos implements ISubInfoType<ModularVehicleInfo> {
  private final ModularVehicleInfo owner;
  
  @PackFileProperty(configNames = {"HornSound", "KlaxonSound"}, required = false)
  public String klaxonSound;
  
  @PackFileProperty(configNames = {"HornCooldown", "KlaxonCooldown"}, required = false)
  public byte klaxonCooldown = 20;
  
  @PackFileProperty(configNames = {"SirenSound"}, required = false)
  public String sirenSound;
  
  @PackFileProperty(configNames = {"SirenDistance"}, required = false)
  public float sirenDistance = 50.0F;
  
  @PackFileProperty(configNames = {"SirenLightSource"}, required = false)
  public int sirenLightSource = 0;
  
  @PackFileProperty(configNames = {"DRLightSource"}, required = false)
  public int drLightSource = 0;
  
  @PackFileProperty(configNames = {"HeadLightsSource"}, required = false)
  public int headLightsSource = 0;
  
  @PackFileProperty(configNames = {"BackLightsSource"}, required = false)
  public int backLightsSource = 0;
  
  @PackFileProperty(configNames = {"BrakeLightsSource"}, required = false)
  public int brakeLightsSource = 0;
  
  @PackFileProperty(configNames = {"ReverseLightsSource"}, required = false)
  public int reverseLightsSource = 0;
  
  @PackFileProperty(configNames = {"TurnSignalLeftLightSource"}, required = false)
  public int turnLeftLightSource = 0;
  
  @PackFileProperty(configNames = {"TurnSignalRightLightSource"}, required = false)
  public int turnRightLightSource = 0;
  
  @PackFileProperty(configNames = {"IndicatorsSound"}, required = false)
  public String indicatorsSound;
  
  @PackFileProperty(configNames = {"LockSound"}, required = false)
  public String lockSound;
  
  public BasicsAddonInfos(ISubInfoTypeOwner<ModularVehicleInfo> owner) {
    this.owner = (ModularVehicleInfo)owner;
  }
  
  public void appendTo(ModularVehicleInfo owner) {
    if (this.klaxonSound != null) {
      ResourceLocation r = new ResourceLocation(this.klaxonSound);
      SoundEvent event = new SoundEvent(r);
      RegistryNameSetter.setRegistryName((IForgeRegistryEntry.Impl)event, this.klaxonSound);
      BasicsAddon.soundMap.put(this.klaxonSound, event);
    } 
    if (this.sirenSound != null) {
      ResourceLocation r = new ResourceLocation(this.sirenSound);
      SoundEvent event = new SoundEvent(r);
      RegistryNameSetter.setRegistryName((IForgeRegistryEntry.Impl)event, this.sirenSound);
      BasicsAddon.soundMap.put(this.sirenSound, event);
    } 
    if (this.lockSound != null) {
      ResourceLocation r = new ResourceLocation(this.lockSound);
      SoundEvent event = new SoundEvent(r);
      RegistryNameSetter.setRegistryName((IForgeRegistryEntry.Impl)event, this.lockSound);
      BasicsAddon.soundMap.put(this.lockSound, event);
    } 
    owner.addSubProperty(this);
  }
  
  @Nullable
  public ModularVehicleInfo getOwner() {
    return this.owner;
  }
  
  public String getName() {
    return "BasicsAddonInfos of " + this.owner.getName();
  }
  
  public String getPackName() {
    return this.owner.getPackName();
  }
}
