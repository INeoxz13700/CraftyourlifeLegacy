package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.style.AutoStyleHandler;
import fr.aym.acsguis.component.style.ComponentStyleManager;
import fr.aym.acsguis.cssengine.selectors.EnumSelectorContext;
import fr.aym.acsguis.cssengine.style.EnumCssStyleProperties;
import fr.aym.acsguis.utils.GuiTextureSprite;
import fr.aym.acsguis.utils.IGuiTexture;
import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.client.handlers.hud.HudIcons;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.modules.CarEngineModule;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.util.ResourceLocation;

public class BasicsAddonHudIcons implements HudIcons {
  private final BasicsAddonModule module;
  
  private final BaseVehicleEntity<?> entity;
  
  private boolean wasLocked;
  
  public BasicsAddonHudIcons(BasicsAddonModule module, BaseVehicleEntity<?> entity) {
    this.module = module;
    this.entity = entity;
    this.wasLocked = module.isLocked();
  }
  
  public void tick(GuiComponent<?>[] components) {
    if (this.wasLocked != this.module.isLocked()) {
      this.wasLocked = this.module.isLocked();
      components[2].getStyle().refreshCss(false, "bas_lock");
    } 
  }
  
  public boolean isVisible(int componentId) {
    switch (componentId) {
      case 0:
        return this.module.isHeadLightsOn();
      case 1:
        if (this.entity.getModuleByType(CarEngineModule.class) != null)
          return (((CarEngineModule)this.entity.getModuleByType(CarEngineModule.class)).getSpeedLimit() != Float.MAX_VALUE); 
      case 3:
        return false;
      case 4:
        return (this.module.isTurnSignalLeftOn() && this.entity.ticksExisted % 20 < 10);
      case 5:
        return (this.module.isTurnSignalRightOn() && this.entity.ticksExisted % 20 < 10);
    } 
    return true;
  }
  
  public int iconCount() {
    return 7;
  }
  
  public void initIcon(int componentId, GuiComponent<?> component) {
    if (componentId == 2)
      component.getStyle().addAutoStyleHandler(new AutoStyleHandler<ComponentStyleManager>() {
            public boolean handleProperty(EnumCssStyleProperties property, EnumSelectorContext context, ComponentStyleManager target) {
              if (property == EnumCssStyleProperties.TEXTURE) {
                if (BasicsAddonHudIcons.this.module.isLocked()) {
                  target.setTexture((IGuiTexture)new GuiTextureSprite(new ResourceLocation("dynamx_basics", "textures/lock.png")));
                } else {
                  target.setTexture((IGuiTexture)new GuiTextureSprite(new ResourceLocation("dynamx_basics", "textures/unlock.png")));
                } 
                return true;
              } 
              return false;
            }
            
            public Collection<EnumCssStyleProperties> getModifiedProperties(ComponentStyleManager target) {
              return Collections.singletonList(EnumCssStyleProperties.TEXTURE);
            }
          }); 
  }
}
