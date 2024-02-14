package fr.innog.common.penalty;

import java.util.ArrayList;
import java.util.List;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.data.ISaveHandler;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class PenaltyManager implements ISaveHandler
{

	private List<Penalty> playerPenalties = new ArrayList<>();
	
	private IPlayer persistantPlayerData;
	
	public PenaltyManager(IPlayer persistantPlayerData)
	{
		this.persistantPlayerData = persistantPlayerData;
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		NBTTagList nbtList = new NBTTagList();
		
		for(Penalty penalty : playerPenalties)
		{
			NBTTagCompound penaltyCompound = new NBTTagCompound();
			penalty.writeToNBT(penaltyCompound);
			nbtList.appendTag(penaltyCompound);
		}
		
		compound.setTag("Penalty", nbtList);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) 
	{	

		NBTTagList nbtList = (NBTTagList) compound.getTag("Penalty");
				
		if(nbtList == null) return;
		
		for(int i = 0; i < nbtList.tagCount(); i++)
		{
			NBTTagCompound penaltyCompound = nbtList.getCompoundTagAt(i);
			
			Penalty penalty = null;
			/*if(penaltyCompound.hasKey("VehicleId"))
			{
				penalty = new VehiclePenalty();
			}
			else
			{*/
				 penalty = new Penalty();
			//}
			
			penalty.readFromNBT(penaltyCompound);
						
			playerPenalties.add(penalty);
		}	
	}
	
	public void resetPenalty()
	{
		playerPenalties.clear();
	}
	
	//Impound
	/*public void addPenalty(EntityVehicle vehicle, int price, String reason,EntityPlayer owner, boolean isImpound)
	{

		VehiclePenalty penalty = null;
		
		//PlayerCachedData data = PlayerCachedData.getData(owner);	
		ExtendedPlayer extendedPlayer = ExtendedPlayer.get(owner);	

		//penalty = new VehiclePenalty(Item.getIdFromItem(vehicle.attribuatedItemStack.getItem()),price,reason ,data == null ? "policier" : data.serverData.job,isImpound);
		penalty = new VehiclePenalty(Item.getIdFromItem(vehicle.attribuatedItemStack.getItem()),price,reason ,extendedPlayer == null ? "policier" : extendedPlayer.serverData.job,isImpound);

		if(isImpound)
		{
			if(owner != null)
			{
				File impoundLogFile = FlansMod.impoundLogFile;
					
				try(FileWriter fw = new FileWriter(impoundLogFile, true); 
				BufferedWriter bw = new BufferedWriter(fw);
					
				PrintWriter out = new PrintWriter(bw))
				{
					out.println("[" + penalty.getFormattedDate() + "] Penalty giver name "  + owner.getCommandSenderName() + "  Vehicle position : {" +(int)vehicle.posX + " " +  (int)vehicle.posY + " " +  (int)vehicle.posZ + "} reason : " + "Impound");
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		playerPenalties.add(penalty);
		
		ServerUtils.sendChatMessage(persistantPlayerData.getPlayer(), "§cVous avez reçu une amende!");
	}*/

	
	
	public void addPenalty(int price, String reason, String treasuryOwner)
	{
		playerPenalties.add(new Penalty(price,reason,treasuryOwner));
				
		MinecraftUtils.sendMessage(persistantPlayerData.getPlayer(), "§cVous avez reçu une amende!");
	}
	
	public boolean removePenalty(String penaltyId)
	{
		Penalty toRemove = null;
		for(Penalty penalty : playerPenalties)
		{
			if(penalty.getId().equals(penaltyId))
			{
				toRemove = penalty;
			}
		}
		
		if(toRemove != null)
		{
			playerPenalties.remove(toRemove);
			return true;
		}
		return false;
	}
	
	public boolean extractPenalty(Penalty penalty)
	{
		MinecraftUtils.removeMoney(persistantPlayerData.getPlayer(), penalty.getPrice());
		if(penalty.getTreasuryOwner() != null)
		{
			MinecraftUtils.dispatchConsoleCommand("tresorerie add " + penalty.getTreasuryOwner() + " " + penalty.getPrice());
		}
		
		removePenalty(penalty.getId());
		return true;
	}
	
	public void extractPenalties(List<Penalty> penalties)
	{
		if(penalties == null) return;
		
		for(Penalty penalty : penalties)
		{
			extractPenalty(penalty);
		}
	}
	
	public boolean extractPenalty(String penaltyId)
	{
		Penalty penalty = getPenalty(penaltyId);
		
		if(penalty == null) return false;
		
		return extractPenalty(penalty);
	}
	
	public Penalty getPenalty(String id)
	{
		for(Penalty penalty : playerPenalties)
		{
			if(penalty.getId().equals(id))
			{
				return penalty;
			}
		}
		return null;
	}
	
	public int getPenaltyCount()
	{
		return playerPenalties.size();
	}
	
	public Penalty getPenaltyAt(int index)
	{
		return playerPenalties.get(index);
	}
	
	public void updatePenalties()
	{
				
		List<Penalty> forcePenaltiesExtraction = new ArrayList();
		
		int forcePenaltyCount = 0;
		for(Penalty penalty : playerPenalties)
		{														   
			if((System.currentTimeMillis() - penalty.getDate()) >= 86400000 * 14) //2 semaines s'ést écoulé
			{
				penalty.setPrice((int)(penalty.getPrice() * 1.5f));
				forcePenaltiesExtraction.add(penalty);
				forcePenaltyCount++;
				continue;
			}
		}
		
		if(forcePenaltyCount > 0)
		{
			MinecraftUtils.sendMessage(persistantPlayerData.getPlayer(), "§c[Amendes] " + forcePenaltyCount + " prélevée automatiquement car ils n'ont pas été payés avant la limite de temps fixé");
			
			extractPenalties(forcePenaltiesExtraction);
			
			playerPenalties.removeAll(forcePenaltiesExtraction);
		}
		
		if(getPenaltyCount() > 0)
		{
			MinecraftUtils.sendMessage(persistantPlayerData.getPlayer(), "§cVous avez des amendes impayés!");
		}
	}
	
	/*public List<Penalty> getImpoundVehicles()
	{
		return playerPenalties.stream().filter(x -> x instanceof VehiclePenalty && ((VehiclePenalty)x).isImpound()).collect(Collectors.toList());
	}*/
	
	
}