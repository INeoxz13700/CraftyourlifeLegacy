package fr.dynamx.addons.basics.client;

import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.api.audio.IDynamXSound;
import fr.dynamx.client.sound.DynamXSoundHandler;
import fr.dynamx.common.entities.BaseVehicleEntity;

public class SirenSound implements IDynamXSound {
  private final BaseVehicleEntity<?> entity;
  
  private final BasicsAddonModule module;
  
  public SirenSound(BaseVehicleEntity<?> entity, BasicsAddonModule module) {
    this.entity = entity;
    this.module = module;
  }
  
  public void update(DynamXSoundHandler handler) {
    handler.setPosition(this, (float)this.entity.posX, (float)this.entity.posY, (float)this.entity.posZ);
    if (this.entity.isDead || !this.module.isSirenOn())
      handler.stopSound(this); 
  }
  
  public String getSoundUniqueName() {
    return this.entity.getEntityId() + "_" + (this.module.getInfos()).sirenSound;
  }
  
  public void onStarted() {}
  
  public boolean tryStop() {
    return true;
  }
  
  public float getVolume() {
    return 1.0F;
  }

  @Override
  public float getDistanceToPlayer() {
	return 0;
  }

  @Override
   public void onMuted() {
	
	}
}
