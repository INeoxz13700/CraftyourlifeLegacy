package fr.dynamx.addons.basics.client;

import fr.aym.acsguis.component.GuiComponent;
import fr.aym.acsguis.component.panel.GuiPanel;
import fr.dynamx.addons.basics.common.modules.FuelTankModule;
import fr.dynamx.api.entities.modules.IVehicleController;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public class FuelTankController implements IVehicleController {
  public static final ResourceLocation STYLE = new ResourceLocation("dynamx_basics", "css/vehicle_hud.css");
  
  private final FuelTankModule module;
  
  public FuelTankController(FuelTankModule module) {
    this.module = module;
  }
  
  public void update() {}
  
  public GuiComponent<?> createHud() {
    if (this.module != null) {
      GuiPanel panel = new GuiPanel();
      float maxFuel = this.module.getInfo().getTankSize();
      float scale = 0.3F;
      FuelLevelPanel fuelLevelPanel = new FuelLevelPanel(this, scale, maxFuel);
      fuelLevelPanel.setCssClass("speed_pane");
      fuelLevelPanel.setCssId("fuel_gauge");
      panel.add((GuiComponent)fuelLevelPanel);
      panel.setCssId("engine_hud");
      return (GuiComponent<?>)panel;
    } 
    return null;
  }
  
  public List<ResourceLocation> getHudCssStyles() {
    if (this.module != null)
      return Collections.singletonList(STYLE); 
    return null;
  }
  
  public FuelTankModule getModule() {
    return this.module;
  }
}
