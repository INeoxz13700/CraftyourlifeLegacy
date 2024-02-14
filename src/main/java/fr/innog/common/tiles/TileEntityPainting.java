package fr.innog.common.tiles;

import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityPainting extends TileEntity implements IStealingTileEntity {

	public EntityLivingBase entityStealing;
	
	public long stealingStartedTime;
	
	private byte paintingType;
	
	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		paintingType = tag.getByte("PaintingType");
		super.readFromNBT(tag);
	}
	   
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag.setByte("PaintingType", paintingType);
		return super.writeToNBT(tag);
	}
	
	
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){
	    NBTTagCompound nbtTag = new NBTTagCompound();
	    
	    writeToNBT(nbtTag);
	    
	   
	    
	    return new SPacketUpdateTileEntity(getPos(), 1, nbtTag);
	}
	
	@Override
	public NBTTagCompound getUpdateTag()
	{
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
    public void handleUpdateTag(NBTTagCompound tag)
    {
        this.readFromNBT(tag);
    }


	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
	    NBTTagCompound tag = pkt.getNbtCompound();

	    readFromNBT(tag);
	    	    
	    
	}
	

	@Override
	public int getStealingTime() {
		return 10;
	}

	@Override
	public String getDisplayMessageInLook() {
		return "Pour dérober le tableau";
	}

	@Override
	public EntityLivingBase getThiefEntity() {
		return entityStealing;
	}

	@Override
	public long getStealingStartedTime() {
		return stealingStartedTime;
	}

	@Override
	public void resetStealing() {
		entityStealing = null;
		stealingStartedTime = 0;		
	}

	@Override
	public void setStealingEntity(EntityLivingBase entity) {
		entityStealing = entity;
		stealingStartedTime = System.currentTimeMillis();		
	}

	@Override
	public void finalizeStealing() {
		Block block = world.getBlockState(pos).getBlock();
		
		if(block == null) return;
		
		Item paintingItem = Item.getItemFromBlock(block);
		
		world.setBlockToAir(pos);

		if(entityStealing instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entityStealing;
			ItemStack itemstack = new ItemStack(paintingItem,1);
			
			itemstack.setTagCompound(new NBTTagCompound());
			itemstack.getTagCompound().setByte("PaintingType", paintingType);

			ServerUtils.broadcastMessage(player.world, "§9[Force de l'ordre] §cLe musée a été braqué, des renforts sont demandés.");
		
			MinecraftUtils.dispatchConsoleCommand("wanted add " + player.getName() + " 4 braquage de musée");
			
			player.inventory.addItemStackToInventory(itemstack);

			MinecraftUtils.sendMessage(entityStealing, "§aVous venez de dérober un tableau!");

			
			MinecraftUtils.getPlayerCapability(player).stopStealing();
		}
		
	}
	
	public byte getPaintingType()
	{
		return paintingType;
		
	}

	public void setPaintingType(byte paintingType)
	{
		this.paintingType = paintingType;
	}
}
