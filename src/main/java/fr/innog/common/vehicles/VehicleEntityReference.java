package fr.innog.common.vehicles;

import java.util.List;
import java.util.UUID;

import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.common.entities.ModularPhysicsEntity;
import fr.dynamx.common.entities.modules.SeatsModule;
import fr.innog.common.ModCore;
import fr.innog.common.vehicles.datas.VehicleStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class VehicleEntityReference {
	
    private ModularPhysicsEntity<?> entity;
    private UUID entityUuid;
    private UUID ownerUuid;
    private VehicleStack attribuatedStack;
    private BlockPos lastPos;
    
    public VehicleEntityReference() {

    }
    
    public VehicleEntityReference(UUID entityUuid, ModularPhysicsEntity<?> entity, UUID ownerUuid, VehicleStack attribuatedStack) {
        this.entityUuid = entityUuid;
    	this.ownerUuid = ownerUuid;
        this.attribuatedStack = attribuatedStack;
        this.entity = entity;
        this.lastPos = entity.getPosition();
    }

    
    public ModularPhysicsEntity<?> getEntity()
    {
    	return entity;
    }
    
    public UUID getEntityUuid() {
        return entityUuid;
    }
    
    public UUID getOwnerUuid() {
        return ownerUuid;
    }
    
    public BlockPos getLastPos()
    {
    	return lastPos;
    }
    
    public void setLastPos(BlockPos pos)
    {
    	this.lastPos = pos;
    }
    
    public void setEntity(ModularPhysicsEntity<?> entity)
    {
    	this.entity = entity;
    }
    
    public VehicleStack getAttribuatedStack()
    {
    	return attribuatedStack;
    }
    
    public NBTTagCompound getVehicleData()
    {
    	return attribuatedStack.getVehicleDatas();
    }        

	public void writeToNbt(NBTTagCompound tag) {
		ModCore.log("Sauvegarde des données du véhicule de : " + getOwnerUuid());
		tag.setString("UUID", getEntityUuid().toString());
		tag.setString("OwnerUUID", ownerUuid.toString());	
		
		if(entity != null)
		{
			List<IPhysicsModule<?>> modules = entity.getModules();

			for(IPhysicsModule<?> module : modules)
			{
				if(module instanceof SeatsModule) continue;
				
				try
				{
					module.writeToNBT(attribuatedStack.getVehicleDatas());
				}
				catch(NullPointerException e)
				{
					e.printStackTrace();
					ModCore.log("Un problème a eu lieu au moment de la sauvegarde du véhicule");
					continue;
				}
			}
		}
		
		attribuatedStack.writeToNBT(tag);
		tag.setInteger("PosX", lastPos.getX());
		tag.setInteger("PosY", lastPos.getY());
		tag.setInteger("PosZ", lastPos.getZ());

	}
	
	public void readFromNbt(NBTTagCompound tag) {
		this.entityUuid =  UUID.fromString(tag.getString("UUID"));
		this.ownerUuid = UUID.fromString(tag.getString("OwnerUUID"));
		
		ModCore.log("Chargement des données du véhicule de : " + getOwnerUuid());

		attribuatedStack = new VehicleStack();
		attribuatedStack.readFromNBT(tag);
		
		this.lastPos = new BlockPos(tag.getInteger("PosX"),tag.getInteger("PosY"),tag.getInteger("PosZ"));
	}
}
