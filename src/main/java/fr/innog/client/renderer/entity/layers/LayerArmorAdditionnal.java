package fr.innog.client.renderer.entity.layers;

import java.util.Map;

import com.google.common.collect.Maps;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.inventory.CustomInventoryPlayer;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LayerArmorAdditionnal<T extends ModelBiped> implements LayerRenderer<EntityLivingBase>
{
    private ModelBiped modelArmor;
    
    private final RenderLivingBase<?> renderer;
    
    private float alpha = 1.0F;
    private float colorR = 1.0F;
    private float colorG = 1.0F;
    private float colorB = 1.0F;
    private boolean skipRenderGlint;

    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.<String, ResourceLocation>newHashMap();



    public LayerArmorAdditionnal(RenderLivingBase<?> rendererIn) {
        super();

        this.renderer = rendererIn;
        this.initArmor();
    }

    protected void initArmor() {
        this.modelArmor = new ModelBiped(1.0F);
    }
    
    protected void setModelSlotVisible(ModelBiped p_188359_1_, EntityEquipmentSlot slotIn)
    {
        this.setModelVisible(p_188359_1_);

        switch (slotIn)
        {
            case HEAD:
                p_188359_1_.bipedHead.showModel = true;
                p_188359_1_.bipedHeadwear.showModel = true;
                break;
            case CHEST:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightArm.showModel = true;
                p_188359_1_.bipedLeftArm.showModel = true;
                break;
            case LEGS:
                p_188359_1_.bipedBody.showModel = true;
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
                break;
            case FEET:
                p_188359_1_.bipedRightLeg.showModel = true;
                p_188359_1_.bipedLeftLeg.showModel = true;
        }
    }
    

    protected void setModelVisible(ModelBiped model)
    {
        model.setVisible(false);
    }


    protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model)
    {
        return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
    }


    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
    	if(entitylivingbaseIn instanceof EntityPlayer)
    	{
    		EntityPlayer player = (EntityPlayer) entitylivingbaseIn;
    		IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
        	CustomInventoryPlayer customInventory = playerData.getInventory();
        	
        	 for(int i = 0; i < customInventory.getSlots(); i++)
             {
             	ItemStack is = customInventory.getStackInSlot(i);

             	if(is.isEmpty()) continue;
             
             	ItemArmor itemarmor = (ItemArmor)is.getItem();

                ModelBiped modelBiped = getArmorModelHook(entitylivingbaseIn, is, EntityEquipmentSlot.CHEST, modelArmor);

                modelBiped.setModelAttributes(this.renderer.getMainModel());
                modelBiped.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
                setModelSlotVisible(modelBiped, EntityEquipmentSlot.CHEST);
                boolean flag = false;
                this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, is, EntityEquipmentSlot.CHEST, null));

                {
                    if (itemarmor.hasOverlay(is)) // Allow this for anything, not only cloth
                    {
                        int i1 = itemarmor.getColor(is);
                        float f = (float)(i1 >> 16 & 255) / 255.0F;
                        float f1 = (float)(i1 >> 8 & 255) / 255.0F;
                        float f2 = (float)(i1 & 255) / 255.0F;
                        GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
                        modelBiped.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                        this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, is, EntityEquipmentSlot.CHEST, "overlay"));
                    }
                    
                    { // Non-colored
                        GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                        modelBiped.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                    } // Default
                }
                
             }
    	}
    }
    
    private ResourceLocation getArmorResource(ItemArmor armor, boolean p_177181_2_)
    {
        return this.getArmorResource(armor, p_177181_2_, (String)null);
    }

    @Deprecated //Use the more sensitive version getArmorResource below
    private ResourceLocation getArmorResource(ItemArmor armor, boolean p_177178_2_, String p_177178_3_)
    {
        String s = String.format("textures/models/armor/%s_layer_%d%s.png", armor.getArmorMaterial().getName(), p_177178_2_ ? 2 : 1, p_177178_3_ == null ? "" : String.format("_%s", p_177178_3_));
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s);

        if (resourcelocation == null)
        {
            resourcelocation = new ResourceLocation(s);
            ARMOR_TEXTURE_RES_MAP.put(s, resourcelocation);
        }

        return resourcelocation;
    }

    
    public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EntityEquipmentSlot slot, String type)
    {
        ItemArmor item = (ItemArmor)stack.getItem();
        String texture = item.getArmorMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1)
        {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1, type == null ? "" : String.format("_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = (ResourceLocation)ARMOR_TEXTURE_RES_MAP.get(s1);

        if (resourcelocation == null)
        {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
        }

        return resourcelocation;
    }

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}
}
