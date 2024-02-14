package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.client.InteractionKeyController;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable.SynchronizedPhysicsModule;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SynchronizedPhysicsModule(modid = "dynamx_basics")
public class InteractionKeyModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>>, IPhysicsModule.IEntityUpdateListener {
  private final BaseVehicleEntity<?> entity;
  
  private InteractionKeyController controller;
  
  @SynchronizedEntityVariable(name = "activate")
  private final EntityVariable<Boolean> activate = new EntityVariable<Boolean>(SynchronizationRules.SERVER_TO_CLIENTS, Boolean.valueOf(false));
  
  public InteractionKeyModule(BaseVehicleEntity<?> entity) {
    this.entity = entity;
    if (entity.world.isRemote)
      this.controller = new InteractionKeyController(entity, this); 
  }
  
  public boolean isActivateOn() {
    return ((Boolean)this.activate.get()).booleanValue();
  }
  
  public void setActivate(boolean activate) {
    this.activate.set(Boolean.valueOf(activate));
  }
  
  @Nullable
  @SideOnly(Side.CLIENT)
  public IVehicleController createNewController() {
    return (IVehicleController)this.controller;
  }
  
  public void writeToNBT(NBTTagCompound tag) {
    tag.setBoolean("bas_interactionkey", isActivateOn());
  }
  
  public void readFromNBT(NBTTagCompound tag) {
    setActivate(tag.getBoolean("bas_interactionkey"));
  }
}
