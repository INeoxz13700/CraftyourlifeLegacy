package fr.innog.common.entity;

import com.jme3.math.Vector3f;

import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityLootableBody extends EntityLiving {
    private static final DataParameter<String> SKIN = EntityDataManager.createKey(EntityLootableBody.class, DataSerializers.STRING);
    private static final float entitiesDeathTimeInSeconds = 600f;
	private long deathTime;


    public EntityLootableBody(World world) {
        super(world);
		deathTime = System.currentTimeMillis();
		this.setSize(1F, 0.5F);
    }

    public EntityLootableBody(World world, float spawnRotationAngle, Vector3f pos, String skin) {
        this(world);
        this.setSkin(skin);
        this.rotationYaw = spawnRotationAngle;
        this.setPosition(pos.x, pos.y, pos.z);
    }

	@Override
    public boolean handleWaterMovement()
    {
		if(isInWater())
		{
			this.motionY +=0.05f;
		}
		return super.handleWaterMovement();
    }
	
	@Override
    public boolean isEntityInvulnerable(DamageSource source)
    {
		return true;
    }
	


    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SKIN, "");
    }

    /**
     * @param skin A player name or a string resource location
     */
    public void setSkin(String skin) {
        dataManager.set(SKIN, skin);
    }

    public String getSkin() {
        return dataManager.get(SKIN);
    }

    @Override
	public void readEntityFromNBT(NBTTagCompound compound) {
    	super.readEntityFromNBT(compound);
    	setSkin(compound.getString("skin"));
    	if(compound.hasKey("DeathTime"))
    	{
    		deathTime = compound.getLong("DeathTime");
    	}
    }

    @Override
	public void writeEntityToNBT(NBTTagCompound compound) {
    	super.writeEntityToNBT(compound);
        compound.setString("skin", getSkin());
        compound.setLong("DeathTime", deathTime);

    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

  

    @Override
    public void onUpdate() {
        super.onUpdate();
        
        if(world.isRemote)
        {
        	return;
        }
        
		if((System.currentTimeMillis() - deathTime) / 1000 >= EntityLootableBody.entitiesDeathTimeInSeconds)
		{
			setDead();
		}
    }
    
	@Override
	protected boolean canDespawn() 
	{
		return false;
	}

    @Override
    public String getName() {
        return "" + getEntityId();
    }

    @Override
    public void setDead() {
        super.setDead();
    }

}