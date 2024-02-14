package fr.dynamx.addons.basics.utils;

import com.jme3.math.Vector3f;
import fr.aym.acsguis.cssengine.font.CssFontHelper;
import java.util.Collections;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class TextUtils {
  public static void drawText(Vector3f pos, Vector3f scale, Vector3f rotation, String text, int[] color, String font) {
    drawText(pos, scale, rotation, text, color, font, 0.0F);
  }
  
  public static void drawText(Vector3f pos, Vector3f scale, Vector3f rotation, String text, int[] color, String font, float spacing) {
    if (pos != null) {
      GlStateManager.pushMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.translate(pos.x, pos.y, pos.z);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      float rotate = rotation.x;
      if (rotate != 0.0F)
        GlStateManager.rotate(rotate, 1.0F, 0.0F, 0.0F); 
      rotate = rotation.y;
      if (rotate != 0.0F)
        GlStateManager.rotate(rotate, 0.0F, 1.0F, 0.0F); 
      rotate = rotation.z;
      if (rotate != 0.0F)
        GlStateManager.rotate(rotate, 0.0F, 0.0F, 1.0F); 
      GlStateManager.scale(scale.x / 40.0F, scale.y / 40.0F, scale.z / 40.0F);
      GlStateManager.disableLighting();
      CssFontHelper.pushDrawing(new ResourceLocation(font), Collections.emptyList());
      GlStateManager.scale(0.05D, 0.05D, 0.05D);
      String[] lines = text.split("\\\\n");
      for (String line : lines) {
        String line2 = line.replace("\\n", "");
        CssFontHelper.draw((-CssFontHelper.getBoundFont().getWidth(line2) / 2), 0.0F, line2, color[0] << 16 | color[1] << 8 | color[2]);
        GlStateManager.translate(0.0F, (spacing + 1.0F) * 100.0F, 0.0F);
      } 
      CssFontHelper.popDrawing();
      GlStateManager.enableLighting();
      GlStateManager.resetColor();
      GlStateManager.popMatrix();
    } 
  }
}
