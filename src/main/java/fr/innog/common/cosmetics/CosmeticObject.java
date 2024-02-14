package fr.innog.common.cosmetics;

import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.model.ModModelBase;
import fr.innog.common.ModCore;
import fr.innog.network.INetworkCustomizedDeserialization;
import fr.innog.network.PacketCollection;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CosmeticObject implements INetworkCustomizedDeserialization<CosmeticObject> {
		
	private boolean isEquipped;
	
	private boolean isLocked;
	
	private String cosmeticName;
	
	private int id;

	private ModModelBase renderModel;
	
	private String specialMessage;
	
	private ICosmeticSetup cosmeticGuiSetup;
		
	/*
	 * 0:hat
	 * 1:face
	 * 2:body
	 * 3:companion
	 */
	private byte type;
	
	public CosmeticObject() { }
	
	
	public CosmeticObject(String name, boolean unlockedDefault, byte type, int id)
	{
		this.cosmeticName = name;
		this.isLocked = unlockedDefault;
		this.type = type;
		this.id = id;
	}
	
	@SideOnly(Side.CLIENT)
	public CosmeticObject setupRender(ICosmeticSetup setup, ModModelBase model)
	{
		this.cosmeticGuiSetup = setup;

		renderModel = model;
		return this;
	} 
	
	@SideOnly(Side.CLIENT)
	public ICosmeticSetup getCosmeticRenderSetup()
	{
		return this.cosmeticGuiSetup;
	}
	

	@SideOnly(Side.CLIENT)
	public ModModelBase getModel()
	{
		return renderModel;
	}
	
	
	public String getSpecialMessage() {
		return this.specialMessage;
	}
	
	public void setSpecialMessage(String message) {
	    this.specialMessage = message;
	}
	
	public boolean getIsLocked()
	{
		return this.isLocked;
	}
	
	public boolean getIsEquipped()
	{
		return this.isEquipped;
	}
	
	public void setEquipped(boolean equipped)
	{
		this.isEquipped = equipped;
	}
	
	public void setLocked(boolean locked)
	{
		this.isLocked = locked;
	}
	
	public String getName()
	{
		return cosmeticName;
	}
	
	public byte getType()
	{
		return type;
	}
	
	public int getId()
	{
		return this.id;
	}

	
	public void writeToNBT(NBTTagCompound compound)
	{
		compound.setBoolean("Equip", isEquipped);
		compound.setBoolean("isLocked", isLocked);
	}
	
    public void loadNBTData(NBTTagCompound compound) 
    {
    	if(compound.hasKey("Equip"))
    	{
        	isEquipped = compound.getBoolean("Equip");
    	}
    	if(compound.hasKey("isLocked"))
    	{
        	isLocked = compound.getBoolean("isLocked");
    	}
    }
    
    public static CosmeticObject setCosmetiqueUnlocked(EntityPlayer player, int id)
	{
		CosmeticObject cosmetic = MinecraftUtils.getPlayerCapability(player).getCosmeticDatas().getCosmeticById(id);
		cosmetic.setLocked(false);
		MinecraftUtils.sendMessage(player,"§6" + cosmetic.getName() + " §aunlocked!");
		return cosmetic;
	}
    
    public static CosmeticObject setCosmetiqueLocked(EntityPlayer player, int id)
	{
		CosmeticObject cosmetic = MinecraftUtils.getPlayerCapability(player).getCosmeticDatas().getCosmeticById(id);
		cosmetic.setLocked(true);
		return cosmetic;
	}
	
	public static boolean equipCosmetic(EntityPlayer player, int id)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
		CosmeticObject cosmetic = playerData.getCosmeticDatas().getCosmeticById(id);
			
		if(cosmetic.isEquipped)
		{
			return false;
		}
		
		if(cosmetic.isLocked)
		{
			return false;
		}
		
		 cosmetic.isEquipped = true;
		
		 
		if(player.world.isRemote)
		{
			PacketCollection.equipCosmetic(id);	
		}
		else
		{
			playerData.updateRenderer();
		}
		
		
		return true;
		
	}
	
	public static List<CosmeticObject> getEquippedCosmeticFromSameType(EntityPlayer player, byte type)
	{
		IPlayer ep = (IPlayer) MinecraftUtils.getPlayerCapability(player);
				
		return ep.getCosmeticDatas().getEquippedCosmetics().stream().filter(x -> x.type == type).collect(Collectors.toList());
	}

	
	public static boolean unequipCosmetic(EntityPlayer player, int id)
	{
		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
				
		CosmeticObject cosmetic = playerData.getCosmeticDatas().getCosmeticById(id);
		
		if(!cosmetic.isEquipped)
		{
			return false;
		}
		
		cosmetic.isEquipped = false;
		
		if(player.world.isRemote)
		{
			PacketCollection.unequipCosmetic(id);
		}
		else
		{
			playerData.updateRenderer();
		}
		
		
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void setModel(ModModelBase model) 
	{
		this.renderModel = model;
	}
	
	@SideOnly(Side.CLIENT)
	public void renderModelInGui(int posX, int posY, float rotation)
	{

		GL11.glPushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        
		GL11.glTranslatef(posX, posY+16, 100);
        GL11.glScalef(-1, -1, -1);
        GL11.glRotatef(-230, 0, 1, 0);
		GL11.glPushMatrix();

		if(this.getCosmeticRenderSetup() != null)
		{
			this.getCosmeticRenderSetup().setupCosmeticGuiDisplay();
			this.getModel().render(0f);
		}

		GL11.glPopMatrix();
        GlStateManager.disableAlpha();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();
		GL11.glPopMatrix();
	}

	@Override
	public void encodeInto(ByteBuf data) {
		data.writeInt(id);
		data.writeBoolean(isEquipped);
		data.writeBoolean(isLocked);
	}

	@Override
	public void decodeInto(ByteBuf data) {
		id = data.readInt();
		isEquipped = data.readBoolean();
		isLocked = data.readBoolean();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public CosmeticObject getDeserializationInstance() {
		CosmeticObject object = MinecraftUtils.getPlayerCapability(Minecraft.getMinecraft().player).getCosmeticDatas().getCosmeticById(id);

		object.isEquipped = isEquipped;
		object.isLocked = isLocked;
		return object;
	}
	
	
	


}
