package fr.innog.common.tiles;

import javax.annotation.Nullable;

import fr.innog.utils.MinecraftUtils;
import fr.innog.utils.ServerUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityGoldIngot extends TileEntity implements IStealingTileEntity {
			
	public EntityLivingBase entityStealing;
	
	public long stealingStartedTime;
	
	public TileEntityGoldIngot() 
	{
		
	}

   
   @Override
   public void readFromNBT(NBTTagCompound tag)
   {
	   super.readFromNBT(tag);
   }
   
   @Override
   public NBTTagCompound writeToNBT(NBTTagCompound tag)
   {
	   super.writeToNBT(tag);
	   return tag;
   }
   
   @Nullable
   public SPacketUpdateTileEntity getUpdatePacket()
   {
	   NBTTagCompound compound = new NBTTagCompound();
       this.writeToNBT(compound);
       return new SPacketUpdateTileEntity(pos, 0, compound);
   }

   
   @Override
   public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
   {
   		this.readFromNBT(pkt.getNbtCompound());
   }
   
	@Override
	public int getStealingTime() 
	{
		return 4;
	}

	@Override
	public String getDisplayMessageInLook() 
	{
		return "Pour dérober le lingot";
	}

	@Override
	public EntityLivingBase getThiefEntity()
	{
		return entityStealing;
	}


	@Override
	public long getStealingStartedTime() 
	{
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
		
		Item goldIngot = Item.getItemFromBlock(block);
		
		world.setBlockToAir(pos);
		
		if(entityStealing instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) entityStealing;
			player.inventory.addItemStackToInventory(new ItemStack(goldIngot, 1));
			MinecraftUtils.sendMessage(entityStealing, "§aVous venez de dérober un lingot d'or!");
			MinecraftUtils.dispatchConsoleCommand("wanted add " + player.getName() + " 4 braquage de banque");
			ServerUtils.broadcastMessage(player.world, "§9[Force de l'ordre] §cLa banque a été braqué, des renforts sont demandés.");

			MinecraftUtils.getPlayerCapability(player).stopStealing();

		}		
	}
   
}