package fr.innog.client.model;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelCustom 
{

    String getType();
    @SideOnly(CLIENT)
    void renderAll();
    @SideOnly(CLIENT)
    void renderOnly(String... groupNames);
    @SideOnly(CLIENT)
    void renderPart(String partName);
    @SideOnly(CLIENT)
    void renderAllExcept(String... excludedGroupNames);
	
}