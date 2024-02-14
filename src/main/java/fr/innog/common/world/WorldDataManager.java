package fr.innog.common.world;

import fr.innog.common.ModCore;
import fr.innog.common.vehicles.VehicleEntityManager;
import fr.innog.common.world.hospitals.HospitalBed;
import fr.innog.data.BlocksBackup;
import fr.innog.data.FireData;
import fr.innog.data.PhoneWorldData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class WorldDataManager extends WorldSavedData {

	private static final String DATA_NAME = ModCore.MODID + "_CYL";

	private World worldObj;
	
	private VehicleEntityManager vehicleManager = new VehicleEntityManager();
	
	private HospitalBed hospitalData = new HospitalBed();
	
	private FireData fireData = new FireData();
		
	private PhoneWorldData phoneAppData = new PhoneWorldData();
	
	private BlocksBackup worldBlocksBackup = new BlocksBackup();

	
	
	public WorldDataManager() {
		super(DATA_NAME);
	}
	
	public WorldDataManager(String s) {
		super(s);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		if(compound.hasKey("VehicleManager"))
		{
			NBTTagCompound vehicleManagerTag = (NBTTagCompound) compound.getTag("VehicleManager");
			vehicleManager.readFromNbt(vehicleManagerTag);	
		}

		if(compound.hasKey("HospitalData"))
		{
			NBTTagCompound hospitalTag = (NBTTagCompound) compound.getTag("HospitalData");
			hospitalData.readFromNBT(hospitalTag);
		}
		
		if(compound.hasKey("FireData"))
		{
			NBTTagCompound fireTag = (NBTTagCompound) compound.getTag("FireData");
			fireData.readFromNBT(fireTag);
		}
		
		if(compound.hasKey("PhoneAppData"))
		{
			NBTTagCompound phoneTag = (NBTTagCompound) compound.getTag("PhoneAppData");
			phoneAppData.readFromNBT(phoneTag);
		}
		
		if(compound.hasKey("WorldBackup"))
		{
			NBTTagCompound worldTag = (NBTTagCompound) compound.getTag("WorldBackup");
			worldBlocksBackup.readFromNBT(worldTag);
		}
		
		
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		ModCore.log("Sauvegarde des données du monde...");
		NBTTagCompound vehicleManagerTag = new NBTTagCompound();
		vehicleManager.writeToNbt(vehicleManagerTag);
		
		compound.setTag("VehicleManager", vehicleManagerTag);
		
		
		NBTTagCompound hospitalTag = new NBTTagCompound();

		hospitalData.writeToNBT(hospitalTag);
		
		compound.setTag("HospitalData", hospitalTag);
		
		NBTTagCompound fireTag = new NBTTagCompound();

		fireData.writeToNBT(fireTag);
		
		compound.setTag("FireData", fireTag);
		
		NBTTagCompound phoneTag = new NBTTagCompound();

		phoneAppData.writeToNBT(phoneTag);
		
		compound.setTag("PhoneAppData", phoneTag);
		
		
		NBTTagCompound worldTag = new NBTTagCompound();
		
		worldBlocksBackup.writeToNBT(worldTag);
		
		compound.setTag("WorldBackup", worldTag);

		return compound;
	}
	
	public static WorldDataManager get(World world) {

		  WorldDataManager instance = (WorldDataManager) world.loadData(WorldDataManager.class, DATA_NAME);

		  if (instance == null) {
		     instance = new WorldDataManager();
			 instance.setWorldObj(world);

		     world.setData(DATA_NAME, instance);
		  }
		  else
		  {
			  instance.setWorldObj(world);
		  }
		  		  
		  return instance;
	}

	public World getWorldObj() {
		return worldObj;
	}

	public void setWorldObj(World worldObj) {
		this.worldObj = worldObj;
	}
	
	public VehicleEntityManager getVehicleManager()
	{
		return vehicleManager;
	}
	
	public HospitalBed getHospitalData()
	{
		return hospitalData;
	}
	
	public FireData getFireData()
	{
		return fireData;
	}	
	
	public PhoneWorldData getPhoneAppData()
	{
		return phoneAppData;
	}
	
	public BlocksBackup getBlocksBackup()
	{
		return worldBlocksBackup;
	}
	
}
