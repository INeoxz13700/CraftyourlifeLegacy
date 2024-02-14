package fr.dynamx.addons.basics.client;

import fr.dynamx.client.handlers.hud.CircleCounterPanel;
import net.minecraft.util.ResourceLocation;

public class FuelLevelPanel extends CircleCounterPanel {
  private final FuelTankController tankController;
  
  public FuelLevelPanel(FuelTankController tankController, float scale, float maxRpm) {
    super(new ResourceLocation("dynamx_basics", "textures/fuelbar.png"), true, 300, 300, scale, maxRpm);
    this.tankController = tankController;
  }
  
  public void tick() {
    super.tick();
    this.prevValue = this.value;
    this.value = this.tankController.getModule().getFuel() / 2.0F;
  }
}
