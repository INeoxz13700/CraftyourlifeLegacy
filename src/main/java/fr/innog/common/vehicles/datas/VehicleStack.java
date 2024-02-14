package fr.innog.common.vehicles.datas;

import fr.dynamx.api.events.PhysicsEntityEvent;
import fr.dynamx.common.entities.PackPhysicsEntity;
import fr.dynamx.common.items.DynamXItem;
import fr.dynamx.common.items.ItemModularEntity;
import fr.dynamx.utils.optimization.Vector3fPool;
import fr.innog.common.ModCore;
import fr.innog.data.ISaveHandler;
import fr.innog.network.INetworkElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class VehicleStack implements INetworkElement, ISaveHandler {

	private DynamXItem<?> vehicleItem;
	
	private byte meta;
	
	private NBTTagCompound vehicleCompound;
		
	public VehicleStack()
	{
		
	}
	
	public VehicleStack(DynamXItem<?> vehicleItem, byte meta)
	{
		this(vehicleItem, meta, new NBTTagCompound());
	}
	
	public VehicleStack(DynamXItem<?> vehicleItem, byte meta, NBTTagCompound data)
	{
		this.vehicleItem = vehicleItem;
		this.meta = meta;
		this.vehicleCompound = new NBTTagCompound();
		this.vehicleCompound.setTag("VehicleDatas", data);
	}
	
	public DynamXItem<?> getItem()
	{
		return vehicleItem;
	}
	
	public String getDisplayName()
	{
		return vehicleItem.getInfo().getDefaultName();
	}
	
	
	public byte getMeta()
	{
		return meta;
	}
	
	public void setMeta(byte meta)
	{
		this.meta = meta;
	}
	
	public NBTTagCompound getVehicleDatas()
	{
		return vehicleCompound.getCompoundTag("VehicleDatas");
	}
	
	public boolean hasVehicleData()
	{
		return vehicleCompound.hasKey("VehicleDatas");
	}

	public void setVehicleData(NBTTagCompound tagCompound) 
	{
		this.vehicleCompound.setTag("VehicleDatas", tagCompound);
	}

	@Override
	public void encodeInto(ByteBuf data) {
		data.writeInt(Item.getIdFromItem(vehicleItem));
		data.writeByte(meta);
	}

	@Override
	public void decodeInto(ByteBuf data) {
		vehicleItem = (DynamXItem<?>) Item.getItemById(data.readInt());
		meta = data.readByte();
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagCompound vehicleCompound = new NBTTagCompound();
		
		
		vehicleCompound.setInteger("VehicleId", Item.getIdFromItem(vehicleItem));
		vehicleCompound.setByte("VehicleMeta", meta);
		vehicleCompound.setTag("VehicleDatas", this.vehicleCompound);
		
		compound.setTag("VehicleStack", vehicleCompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {

		NBTTagCompound vehicleCompound = compound.getCompoundTag("VehicleStack");
		
		vehicleItem = (DynamXItem<?>) Item.getItemById(vehicleCompound.getInteger("VehicleId"));
		meta = vehicleCompound.getByte("VehicleMeta");

		this.vehicleCompound = vehicleCompound.getCompoundTag("VehicleDatas");
	}
	
    public boolean spawnEntity(World worldIn, EntityPlayer playerIn, Vec3d blockPos) {
    {
		ItemModularEntity modularEntity = (ItemModularEntity) getItem();
    
        if (!worldIn.isRemote) {
            PackPhysicsEntity<?, ?> entity = modularEntity.getSpawnEntity(worldIn, playerIn, Vector3fPool.get((float) blockPos.x, (float) blockPos.y + 1F, (float) blockPos.z), playerIn.rotationYaw % 360.0F, meta);
            if (!MinecraftForge.EVENT_BUS.post(new PhysicsEntityEvent.Spawn(worldIn, entity, playerIn, modularEntity, blockPos)))
                worldIn.spawnEntity(entity);
        }
        return true;
    }
   }
    
    
   public void displayData()
   {
	   ModCore.log("Data : \n");
	   for(String keys : this.getVehicleDatas().getKeySet())
	   {
		   ModCore.log(keys);
	   }
	   
	   NBTTagList list = this.getVehicleDatas().getTagList("StorageInv0", Constants.NBT.TAG_COMPOUND);
   
	   for(int i = 0; i < list.tagCount(); i++)
	   {
		   ModCore.log(new ItemStack(list.getCompoundTagAt(i)));
	   }
   }
   
}
	
	
	
	
	

