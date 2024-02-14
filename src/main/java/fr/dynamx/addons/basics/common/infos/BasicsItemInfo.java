package fr.dynamx.addons.basics.common.infos;

import fr.dynamx.api.contentpack.object.subinfo.ISubInfoType;
import fr.dynamx.api.contentpack.object.subinfo.ISubInfoTypeOwner;
import fr.dynamx.api.contentpack.object.subinfo.SubInfoType;
import fr.dynamx.api.contentpack.registry.PackFileProperty;
import fr.dynamx.api.contentpack.registry.RegisteredSubInfoType;
import fr.dynamx.api.contentpack.registry.SubInfoTypeRegistries;
import fr.dynamx.common.contentpack.type.objects.ItemObject;

@RegisteredSubInfoType(name = "basicsitem", registries = {SubInfoTypeRegistries.ITEMS})
public class BasicsItemInfo<T extends ItemObject<T>> extends SubInfoType<T> {
  @PackFileProperty(configNames = {"IsVehicleKey"}, required = false, defaultValue = "false")
  protected boolean isKey;
  
  @PackFileProperty(configNames = {"FuelCapacity"}, required = false, defaultValue = "0")
  protected int fuelCapacity;
  
  public BasicsItemInfo(ISubInfoTypeOwner<T> owner) {
    super(owner);
  }
  
  public void appendTo(T owner) {
    owner.addSubProperty((ISubInfoType)this);
  }
  
  public String getName() {
    return "BasicsItemInfos of " + ((ItemObject)getOwner()).getFullName();
  }
  
  public boolean isKey() {
    return this.isKey;
  }
  
  public void setKey(boolean key) {
    this.isKey = key;
  }
  
  public boolean isFuelContainer() {
    return (this.fuelCapacity > 0);
  }
  
  public int getFuelCapacity() {
    return this.fuelCapacity;
  }
  
  public void setFuelCapacity(int fuelCapacity) {
    this.fuelCapacity = fuelCapacity;
  }
}
