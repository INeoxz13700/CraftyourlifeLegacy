package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.audio.EnumSoundState;
import fr.dynamx.api.audio.IDynamXSound;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.client.ClientProxy;
import fr.dynamx.client.handlers.ClientEventHandler;
import fr.dynamx.client.sound.VehicleSound;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.CarEngineModule;
import fr.dynamx.utils.optimization.Vector3fPool;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasicsAddonController implements IVehicleController {
  public static final ResourceLocation STYLE = new ResourceLocation("dynamx_basics", "css/vehicle_hud.css");
  
  @SideOnly(Side.CLIENT)
  public static final KeyBinding hornKey = new KeyBinding("Horn", 37, "DynamX basics");
  
  @SideOnly(Side.CLIENT)
  public static final KeyBinding beaconKey = new KeyBinding("Beacons", 25, "DynamX basics");
  
  @SideOnly(Side.CLIENT)
  public static final KeyBinding sirenKey = new KeyBinding("Siren", 23, "DynamX basics");
  
  @SideOnly(Side.CLIENT)
  public static final KeyBinding headlights = new KeyBinding("HeadLights", 22, "DynamX basics");
  
  @SideOnly(Side.CLIENT)
  public static final KeyBinding turnLeft = new KeyBinding("TurnLeft", 203, "DynamX basics");
  
  @SideOnly(Side.CLIENT)
  public static final KeyBinding turnRight = new KeyBinding("TurnRight", 205, "DynamX basics");
  
  @SideOnly(Side.CLIENT)
  public static final KeyBinding warnings = new KeyBinding("Warnings", 208, "DynamX basics");
  
  public static IDynamXSound indicatorsSound;
  
  private final BaseVehicleEntity<?> entity;
  
  private final BasicsAddonModule module;
  
  @SideOnly(Side.CLIENT)
  private SirenSound sirenSound;
  
  private byte klaxonHullDown;
  
  private boolean warningsOn;
  
  public BasicsAddonController(BaseVehicleEntity<?> entity, BasicsAddonModule module) {
    this.entity = entity;
    this.module = module;
    this.sirenSound = new SirenSound(entity, module);
    unpress(hornKey);
    unpress(beaconKey);
    unpress(sirenKey);
    unpress(headlights);
    unpress(turnLeft);
    unpress(turnRight);
    unpress(warnings);
  }
  
  private void unpress(KeyBinding key) {
    while (key.isPressed());
  }
  
  @SideOnly(Side.CLIENT)
  public void updateSiren() {
    if (this.module.isSirenOn() && this.module.hasSirenSound() && 
      !ClientProxy.SOUND_HANDLER.getPlayingSounds().contains(this.sirenSound)) {
      ClientProxy.SOUND_HANDLER.playStreamingSound(Vector3fPool.get(this.entity.posX, this.entity.posY, this.entity.posZ), this.sirenSound);
      ClientProxy.SOUND_HANDLER.setSoundDistance(this.sirenSound, 100.0F);
    } 
  }
  
  @SideOnly(Side.CLIENT)
  public void update() {
    if (this.klaxonHullDown > 0)
      this.klaxonHullDown = (byte)(this.klaxonHullDown - 1); 
    if (this.module.hasKlaxon()) {
      this.module.playKlaxon((hornKey.isPressed() && this.klaxonHullDown == 0));
      if (this.module.playKlaxon())
        this.klaxonHullDown = (this.module.getInfos()).klaxonCooldown; 
    } 
    if (this.module.hasSiren()) {
      if (sirenKey.isPressed())
        this.module.setSirenOn(!this.module.isSirenOn()); 
      if (beaconKey.isPressed())
        this.module.setBeaconsOn(!this.module.isBeaconsOn()); 
    } 
    if (this.module.hasHeadLights() && 
      headlights.isPressed())
      this.module.setHeadLightsOn(!this.module.isHeadLightsOn()); 
    if (this.module.hasTurnSignals()) {
      if (turnLeft.isPressed()) {
        System.out.println(this.module.isTurnSignalLeftOn() + " wtf " + this.module.isTurnSignalRightOn());
        if (!this.warningsOn) {
          this.module.setTurnSignalLeftOn(!this.module.isTurnSignalLeftOn());
        } else {
          this.warningsOn = false;
        } 
        this.module.setTurnSignalRightOn(false);
      } else if (turnRight.isPressed()) {
        this.module.setTurnSignalLeftOn(false);
        if (!this.warningsOn) {
          this.module.setTurnSignalRightOn(!this.module.isTurnSignalRightOn());
        } else {
          this.warningsOn = false;
        } 
      } else if (warnings.isPressed()) {
        this.warningsOn = !this.warningsOn;
        this.module.setTurnSignalLeftOn(this.warningsOn);
        this.module.setTurnSignalRightOn(this.warningsOn);
      } 
      String sound = (this.module.getInfos()).indicatorsSound;
      if (!StringUtils.isNullOrEmpty(sound) && (this.module.isTurnSignalLeftOn() || this.module.isTurnSignalRightOn()) && indicatorsSound == null) {
        indicatorsSound = (IDynamXSound)new VehicleSound(sound, this.entity) {
            public boolean isSoundActive() {
              return (ClientEventHandler.MC.player.getRidingEntity() == BasicsAddonController.this.entity && (BasicsAddonController.this.module.isTurnSignalLeftOn() || BasicsAddonController.this.module.isTurnSignalRightOn()));
            }
            
            public void setState(EnumSoundState state) {
              super.setState(state);
              if (state == EnumSoundState.STOPPING)
                ClientProxy.SOUND_HANDLER.stopSound((IDynamXSound)this); 
            }
            
            public boolean tryStop() {
              BasicsAddonController.indicatorsSound = null;
              return true;
            }
          };
        ClientProxy.SOUND_HANDLER.playStreamingSound(Vector3fPool.get(this.entity.posX, this.entity.posY, this.entity.posZ), indicatorsSound);
      } 
    } 
    if (this.module.hasDRL())
      this.module.setDRLOn(((CarEngineModule)this.entity.getModuleByType(CarEngineModule.class)).isEngineStarted()); 
  }
  
  public GuiComponent<?> createHud() {
    return null;
  }
  
  public List<ResourceLocation> getHudCssStyles() {
    return Collections.EMPTY_LIST;
  }
}
