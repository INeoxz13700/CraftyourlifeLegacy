package fr.dynamx.addons.basics.common.modules;

import fr.dynamx.addons.basics.common.infos.LicensePlateInfos;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.api.network.sync.EntityVariable;
import fr.dynamx.api.network.sync.SynchronizationRules;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable;
import fr.dynamx.api.network.sync.SynchronizedEntityVariable.SynchronizedPhysicsModule;
import fr.dynamx.common.physics.entities.AbstractEntityPhysicsHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;

@SynchronizedPhysicsModule(modid = "dynamx_basics")
public class LicensePlateModule implements IPhysicsModule<AbstractEntityPhysicsHandler<?, ?>> {
  private final List<LicensePlateInfos> info = new ArrayList<>();
  
  @SynchronizedEntityVariable(name = "plate")
  private final EntityVariable<String> plate = new EntityVariable<String>(SynchronizationRules.SERVER_TO_CLIENTS, "");
  
  public LicensePlateModule(LicensePlateInfos info) {
    this.info.add(info);
    String pattern = info.getPattern();
    StringBuilder builder = new StringBuilder();
    Random r = new Random();
    for (int i = 0; i < pattern.length(); i++) {
      char c = pattern.charAt(i);
      if (c == '%') {
        builder.append(r.nextInt(10));
      } else if (c == '@') {
        builder.append((char)(r.nextInt(26) + 65));
      } else {
        builder.append(c);
      } 
    } 
    setPlate(builder.toString());
  }
  
  public List<LicensePlateInfos> getInfo() {
    return this.info;
  }
  
  public void addInformation(LicensePlateInfos info) {
    this.info.add(info);
  }
  
  public String getPlate() {
    return (String)this.plate.get();
  }
  
  public void setPlate(String plate) {
    this.plate.set(plate);
  }
  
  public void writeToNBT(NBTTagCompound tag) {
    tag.setString("bas_immat_plate", getPlate());
  }
  
  public void readFromNBT(NBTTagCompound tag) {
    if (tag.hasKey("bas_immat_plate", 8))
      setPlate(tag.getString("bas_immat_plate")); 
  }
}
