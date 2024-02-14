package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.client.BasicsAddonController;
import fr.dynamx.addons.basics.common.infos.BasicsAddonInfos;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable.SynchronizedPhysicsModule;
import fr.dynamx.client.ClientProxy;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import fr.dynamx.utils.optimization.Vector3fPool;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SynchronizedPhysicsModule(modid = "dynamx_basics")
public class BasicsAddonModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
  private BasicsAddonController controller;
  
  private final BaseVehicleEntity<?> entity;
  
  private final BasicsAddonInfos infos;
  
  @SynchronizedEntityVariable(name = "state")
  private final EntityVariable<Integer> state = new EntityVariable<Integer>(SynchronizationRules.CONTROLS_TO_SPECTATORS, 
      Integer.valueOf(0));
  
  public BasicsAddonModule(BaseVehicleEntity<?> entity, BasicsAddonInfos infos) {
    this.entity = entity;
    this.infos = infos;
    if (entity.world.isRemote)
      this.controller = new BasicsAddonController(entity, this); 
  }
  
  public boolean hasKlaxon() {
    return (this.infos != null && this.infos.klaxonSound != null);
  }
  
  public boolean hasSiren() {
    return (this.infos != null && (this.infos.sirenSound != null || this.infos.sirenLightSource != 0));
  }
  
  public boolean hasSirenSound() {
    return (this.infos != null && this.infos.sirenSound != null);
  }
  
  public boolean hasHeadLights() {
    return (this.infos != null && this.infos.headLightsSource != 0);
  }
  
  public boolean hasDRL() {
    return (this.infos != null && this.infos.drLightSource != 0);
  }
  
  public boolean listenEntityUpdates(Side side) {
    return (side.isClient() && (hasKlaxon() || hasSirenSound()));
  }
  
  public void updateEntity() {
    if (playKlaxon() && this.entity.world.isRemote && hasKlaxon())
      ClientProxy.SOUND_HANDLER.playSingleSound(Vector3fPool.get(this.entity.posX, this.entity.posY, this.entity.posZ), this.infos.klaxonSound, 1.0F, 1.0F); 
    if (this.controller != null)
      this.controller.updateSiren(); 
  }
  
  @Nullable
  @SideOnly(Side.CLIENT)
  public IVehicleController createNewController() {
    return (IVehicleController)this.controller;
  }
  
  public boolean playKlaxon() {
    return ((((Integer)this.state.get()).intValue() & 0x1) == 1);
  }
  
  public void playKlaxon(boolean playKlaxon) {
    this.state.set(Integer.valueOf(playKlaxon ? (((Integer)this.state.get()).intValue() | 0x1) : (((Integer)this.state.get()).intValue() & 0xFFFFFFFE)));
  }
  
  public boolean isSirenOn() {
    return ((((Integer)this.state.get()).intValue() & 0x2) == 2);
  }
  
  public void setSirenOn(boolean sirenOn) {
    this.state.set(Integer.valueOf(sirenOn ? (((Integer)this.state.get()).intValue() | 0x2) : (((Integer)this.state.get()).intValue() & 0xFFFFFFFD)));
  }
  
  public boolean isBeaconsOn() {
    return ((((Integer)this.state.get()).intValue() & 0x4) == 4);
  }
  
  public void setBeaconsOn(boolean beaconsOn) {
    this.state.set(Integer.valueOf(beaconsOn ? (((Integer)this.state.get()).intValue() | 0x4) : (((Integer)this.state.get()).intValue() & 0xFFFFFFFB)));
  }
  

  
  public boolean isLocked() {
    return ((((Integer)this.state.get()).intValue() & 0x10) == 16);
  }
  
  public void setLocked(boolean locked) {
    this.state.set(Integer.valueOf(locked ? (((Integer)this.state.get()).intValue() | 0x10) : (((Integer)this.state.get()).intValue() & 0xFFFFFFEF)));
  }
  
  public boolean isHeadLightsOn() {
    return ((((Integer)this.state.get()).intValue() & 0x20) == 32);
  }
  
  public void setHeadLightsOn(boolean headLightsOn) {
    this.state.set(Integer.valueOf(headLightsOn ? (((Integer)this.state.get()).intValue() | 0x20) : (((Integer)this.state.get()).intValue() & 0xFFFFFFDF)));
  }
  
  public boolean isDRLOn() {
    return ((((Integer)this.state.get()).intValue() & 0x100) == 256);
  }
  
  public void setDRLOn(boolean drlOn) {
    this.state.set(Integer.valueOf(drlOn ? (((Integer)this.state.get()).intValue() | 0x100) : (((Integer)this.state.get()).intValue() & 0xFFFFFEFF)));
  }
  
  public boolean hasTurnSignals() {
    return (this.infos != null && this.infos.turnLeftLightSource != 0 && this.infos.turnRightLightSource != 0);
  }
  
  public boolean isTurnSignalLeftOn() {
    return ((((Integer)this.state.get()).intValue() & 0x40) == 64);
  }
  
  public void setTurnSignalLeftOn(boolean turnSignalLeftOn) {
    this.state.set(Integer.valueOf(turnSignalLeftOn ? (((Integer)this.state.get()).intValue() | 0x40) : (((Integer)this.state.get()).intValue() & 0xFFFFFFBF)));
  }
  
  public boolean isTurnSignalRightOn() {
    return ((((Integer)this.state.get()).intValue() & 0x80) == 128);
  }
  
  public void setTurnSignalRightOn(boolean turnSignalRightOn) {
    this.state.set(Integer.valueOf(turnSignalRightOn ? (((Integer)this.state.get()).intValue() | 0x80) : (((Integer)this.state.get()).intValue() & 0xFFFFFF7F)));
  }
  
  public BasicsAddonInfos getInfos() {
    return this.infos;
  }
  
  public void writeToNBT(NBTTagCompound tag) {
    byte vars = 0;
    if (isSirenOn())
      vars = (byte)(vars | 0x2); 
    if (isHeadLightsOn())
      vars = (byte)(vars | 0x4); 
    if (isTurnSignalLeftOn())
      vars = (byte)(vars | 0x8); 
    if (isTurnSignalRightOn())
      vars = (byte)(vars | 0x10); 
    if (isBeaconsOn())
      vars = (byte)(vars | 0x20); 
    if (isLocked())
      vars = (byte)(vars | 0x40); 
    if (isDRLOn())
      vars = (byte)(vars | 0x100); 
    tag.setByte("BasAddon.vals", vars);
  }
  
  public void readFromNBT(NBTTagCompound tag) {
    if (tag.hasKey("BasAddon.vals", 1)) {
      byte vars = tag.getByte("BasAddon.vals");
      setSirenOn(((vars & 0x2) == 2));
      setHeadLightsOn(((vars & 0x4) == 4));
      setTurnSignalLeftOn(((vars & 0x8) == 8));
      setTurnSignalRightOn(((vars & 0x10) == 16));
      setBeaconsOn(((vars & 0x20) == 32));
      setLocked(((vars & 0x40) == 64));
      setDRLOn(((vars & 0x100) == 256));
    } else {
      setLocked(tag.getBoolean("BasAdd.locked"));
    } 
  }
  
  public void addPassenger(Entity passenger) {

  }
  
  public void removePassenger(Entity passenger) {

  }
}
