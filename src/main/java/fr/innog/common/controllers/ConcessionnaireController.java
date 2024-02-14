package fr.innog.common.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.dynamx.common.items.ItemModularEntity;
import fr.dynamx.common.items.vehicle.ItemCar;
import fr.innog.api.informations.mysql.MysqlManager;
import fr.innog.capability.playercapability.IPlayer;
import fr.innog.capability.playercapability.PlayerProvider;
import fr.innog.common.ModControllers;
import fr.innog.common.ModCore;
import fr.innog.common.vehicles.datas.VehicleStack;
import fr.innog.data.Concessionnaire;
import fr.innog.data.ConcessionnaireItem;
import fr.innog.ui.remote.RemoteConcessionnaireUI;
import fr.innog.ui.remote.RemoteGarageUI;
import fr.innog.ui.remote.data.RemoteUICache;
import fr.innog.utils.structural.Tuple;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ConcessionnaireController {
	
	private List<Concessionnaire> concessionnaires = new ArrayList<Concessionnaire>();

	private MysqlManager sqlManager;
	
	public ConcessionnaireController(MysqlManager sqlManager)
	{
		this.sqlManager = sqlManager;
	}
	
	public void loadConcessionnaires()
	{
		Connection conn = sqlManager.getConn();
			
		try
		{
			Statement stmt = conn.createStatement();
		
			ResultSet rs = stmt.executeQuery("SELECT * FROM concessionnaires");
			
			while(rs.next())
			{
				Concessionnaire concessionnaire = new Concessionnaire(rs.getInt(1), rs.getString(2));
				
				concessionnaires.add(concessionnaire);
			
		        PreparedStatement preparedState = conn.prepareStatement("SELECT * FROM concessionnaires_items WHERE concessionnaire_id = ?");

		        
		        preparedState.setInt(1, concessionnaire.getId());
				
		        ResultSet rs2 = preparedState.executeQuery();
		        
		        while(rs2.next())
		        {
		        	concessionnaire.addVehicle(rs2.getInt(1), rs2.getString(3), rs2.getDouble(4));
		        }
		        
		        rs2.close();
		        preparedState.close();
		       

			}
			
			rs.close();
			stmt.close();
		}
		catch (SQLException ex)
		{
		    ModCore.log("SQLException: " + ex.getMessage());
		    ModCore.log("SQLState: " + ex.getSQLState());
		    ModCore.log("VendorError: " + ex.getErrorCode());
		}

	}
	
	public boolean concessionnaireNameExist(String name)
	{
		for(Concessionnaire concessionnaire : getConcessionnaireList())
		{
			if(name.equalsIgnoreCase(concessionnaire.getName()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public Tuple<Boolean, String> removeVehicleFromConcessionnaire(String concessionnaireName, int id, ICommandSender sender)
	{
		Concessionnaire concessionnaire = this.getConcessionnaireFromName(concessionnaireName);
		
		if(concessionnaire == null)
		{
			return new Tuple<Boolean, String>(false, "Ce concessionnaire n'existe pas");
		}
		
		ConcessionnaireItem item = concessionnaire.getItem(id);
		if(item != null)
		{

			concessionnaire.removeItem(id);
			
			try {
				Connection conn = sqlManager.getConn();

				PreparedStatement preparedState = conn.prepareStatement("DELETE FROM concessionnaires_items WHERE concessionnaire_id = ? AND id = ?");
			     
			    preparedState.setInt(1, concessionnaire.getId());
			    preparedState.setInt(2, id);
    
			    preparedState.executeUpdate();
			} catch (SQLException e) {
				return new Tuple<Boolean, String>(false, e.getMessage());
			}
		   
	        RemoteUICache.setDirtyForPlayers(RemoteConcessionnaireUI.class.getSimpleName() + "_" + concessionnaire.getName(), "ConcessionnaireItems", sender.getServer());

			return new Tuple<Boolean, String>(true, "Le véhicule avec l'id : §b" + item.getVehicleItemId() + " §aa été retiré du concessionnaire");
		}
		else
		{
			return new Tuple<Boolean, String>(false, "Ce véhicule n'est pas en vente dans ce concessionnaire.");
		}
		
	}
	
	public Tuple<Boolean, String> clearConcessionnaire(String concessionnaireName, ICommandSender sender)
	{
		Concessionnaire concessionnaire = this.getConcessionnaireFromName(concessionnaireName);
		
		if(concessionnaire == null)
		{
			return new Tuple<Boolean, String>(false, "Ce concessionnaire n'existe pas");
		}
		try
		{
			Connection conn = sqlManager.getConn();
	
			
			concessionnaire.clearVehicles();
			
	        PreparedStatement preparedState = conn.prepareStatement("DELETE FROM concessionnaires_items WHERE concessionnaire_id = ?");
	        
	        preparedState.setInt(1, concessionnaire.getId());
	        
	        preparedState.executeUpdate();
	        
	        RemoteUICache.setDirtyForPlayers(RemoteConcessionnaireUI.class.getSimpleName() + "_" + concessionnaire.getName(), "ConcessionnaireItems", sender.getServer());

			return new Tuple<Boolean, String>(true, "Les véhicules du concessionnaire ont été supprimés");

		}
		catch(SQLException e)
		{
			return new Tuple<Boolean, String>(false, e.getMessage());
		}
	}
	
	public Tuple<Boolean, String> removeConcessionnaire(String concessionnaireName, ICommandSender sender)
	{
		Concessionnaire concessionnaire = this.getConcessionnaireFromName(concessionnaireName);
		
		if(concessionnaire == null)
		{
			return new Tuple<Boolean, String>(false, "Ce concessionnaire n'existe pas");
		}
		
		if(concessionnaire.getVehicleCount() > 0)
		{
			Tuple<Boolean, String> result = clearConcessionnaire(concessionnaireName, sender);
			if(!result.getItem1()) return result;
		}
		
		try
		{
			Connection conn = sqlManager.getConn();
	
			
			this.concessionnaires.remove(concessionnaire);
			
	        PreparedStatement preparedState = conn.prepareStatement("DELETE FROM concessionnaires WHERE id = ?");
	        
	        preparedState.setInt(1, concessionnaire.getId());
	        
	        preparedState.executeUpdate();
	        
			return new Tuple<Boolean, String>(true, "Le concessionnaire a bien été supprimé");
		}
		catch(SQLException e)
		{
			return new Tuple<Boolean, String>(false, e.getMessage());
		}
		
	} 
	
	public Tuple<Boolean, String> createConcessionnaire(String name)
	{		
		if(concessionnaireNameExist(name))
		{
			return new Tuple<Boolean, String>(false, "Un concessionnaire avec nom existe déjà");
		}
		
		try
		{
			Connection conn = sqlManager.getConn();
	        
	        PreparedStatement preparedState = conn.prepareStatement("INSERT INTO concessionnaires (CONCESSIONNAIRE_NAME) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
	        
	        preparedState.setString(1, name);
	        
	        preparedState.executeUpdate();
	        
	        ResultSet rs = preparedState.getGeneratedKeys();
	        rs.next();
	        int id = rs.getInt(1);
	        	        
	        concessionnaires.add(new Concessionnaire(id, name));
	        
	        return new Tuple<Boolean, String>(true, "");
		}
		catch(SQLException e)
		{
			return new Tuple<Boolean, String>(false, e.getMessage());
		}
	}
	
	public Tuple<Boolean, String> addVehicleToConcessionnaire(String concessionnaireName, String vehicleId, double price, ICommandSender sender)
	{
		if(!concessionnaireNameExist(concessionnaireName))
		{
			return new Tuple<Boolean, String>(false, "Ce concessionnaire n'existe pas");
		}
		
		Concessionnaire concessionnaire = getConcessionnaireFromName(concessionnaireName);
		
		if(concessionnaire.containItemId(vehicleId))
		{
			return new Tuple<Boolean, String>(false, "Ce véhicule est déjà présent dans le concessionnaire");
		}
		
		try
		{
			Connection conn = sqlManager.getConn();
	        
	        PreparedStatement preparedState = conn.prepareStatement("INSERT INTO concessionnaires_items (CONCESSIONNAIRE_ID, ITEM_ID, PRICE) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
	        
	        preparedState.setInt(1, concessionnaire.getId());
	        preparedState.setString(2, vehicleId);
	        preparedState.setDouble(3, price);

	        preparedState.executeUpdate();
	        
	        ResultSet rs = preparedState.getGeneratedKeys();
	        rs.next();
	        int id = rs.getInt(1);
	        	        
	        concessionnaire.addVehicle(id, vehicleId, price);
	        
	        RemoteUICache.setDirtyForPlayers(RemoteConcessionnaireUI.class.getSimpleName() + "_" + concessionnaire.getName(), "ConcessionnaireItems", sender.getServer());
	        
	        return new Tuple<Boolean, String>(true, "");
		}
		catch(SQLException e)
		{
			return new Tuple<Boolean, String>(false, e.getMessage());
		}
		
	}
	
	public Concessionnaire getConcessionnaireFromName(String name)
	{
		for(Concessionnaire concessionnaire : concessionnaires)
		{
			if(concessionnaire.getName().equalsIgnoreCase(name))
			{
				return concessionnaire;
			}
		}
		return null;
		
	}
	
	public void displayVehiclesInConcessionnaire(ICommandSender sender, String name)
	{
		Concessionnaire concessionnaire = getConcessionnaireFromName(name);
	
		if(concessionnaire == null)
		{
			sender.sendMessage(new TextComponentString("Ce concessionnaire n'existe pas!"));
		}
		
		sender.sendMessage(new TextComponentString("§aListes des véhicules : "));
		sender.sendMessage(new TextComponentString(""));
				
		for(Map.Entry<Integer, ConcessionnaireItem> entry : concessionnaire.getVehicles().entrySet())
		{
			String vehicleIdStr = entry.getValue().getVehicleItemId();
			int vehicleId;
			int subId = 0;
			
			if(vehicleIdStr.contains(":"))
			{
				String[] splittedId = vehicleIdStr.split(":");
				vehicleId = Integer.parseInt(splittedId[0]);
				subId = Integer.parseInt(splittedId[1]);
			}
			else
			{
				vehicleId = Integer.parseInt(vehicleIdStr);
			}
			
			ItemStack is = new ItemStack(Item.getItemById(vehicleId),1);

			is.setItemDamage(subId);

			ItemCar item = (ItemCar) is.getItem();

			
			
			
			sender.sendMessage(new TextComponentString("§b(ID : " + entry.getKey() + ") §aName : §b" + item.getInfo().getName() + ":" + subId + " §aprix : §b" + entry.getValue().getPrice() + "§a$"));
		}
	}
	
	public void buyVehicle(RemoteConcessionnaireUI remoteUI, EntityPlayer player, int itemConcessionnaireId)
	{
		
		Concessionnaire concessionnaire = remoteUI.getConcessionnaire();
		
		ConcessionnaireItem item = concessionnaire.getItem(itemConcessionnaireId);
		
		double money = 0;
		
		if(player.getServer() != null && player.getServer().isDedicatedServer())
		{
			money = ModCore.getEssentials().getUserMoney(player);
		}
		else
		{
			money = Double.MAX_VALUE;
		}

		if(money >= item.getPrice())
		{
			VehicleStack is = remoteUI.getVehicleStackById(item.getVehicleItemId());

			IPlayer playerData = player.getCapability(PlayerProvider.PLAYER_CAP, null);
			
			List<VehicleStack> vehicles = playerData.getVehicleDatas().getVehicles(((ItemModularEntity)is.getItem()).getClass());
			
			if(vehicles != null && vehicles.size()+1 > playerData.getVehicleDatas().getUnlockedSlot())
			{
				remoteUI.displayMessage("Votre garage est plein agrandissez le!");
				return;
			}
			
			playerData.getVehicleDatas().addVehicle(is, ((ItemModularEntity)is.getItem()).getClass());
			
			remoteUI.displayMessage("Le véhicule a été acheté avec succès!");
			
			RemoteUICache.setDirtyForPlayer(player, RemoteGarageUI.class.getSimpleName(), "Vehicles");
			
			if(player.getServer() != null && player.getServer().isDedicatedServer()) ModCore.getEssentials().takeMoney(player, item.getPrice());
		} 
		else
		{
			remoteUI.displayMessage("Vous n'avez pas suffisament d'argent dans votre compte bancaire.");
		}
		
	}
	
	public void displayConcessionnaireUI(EntityPlayer targetPlayer, String concessionnaireName)
	{
		ModControllers.uiController.displayUI(targetPlayer, 1, new Object[] {concessionnaireName});
	}
	
	
	
	public List<Concessionnaire> getConcessionnaireList()
	{
		return this.concessionnaires;
	}
	
}
