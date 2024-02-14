package fr.innog.data;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.network.packets.decrapted.PacketBitcoinPage;
import fr.innog.network.packets.decrapted.PacketSyncPhone;
import fr.innog.phone.Sms;
import fr.innog.phone.web.page.BMGPage;
import fr.innog.phone.web.page.BitcoinConverterPage;
import fr.innog.phone.web.page.WebPageData;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PhoneData implements ISaveHandler {
	
	public List<SmsData> sms = new ArrayList<SmsData>();
	
	public List<ContactData> contacts = new ArrayList<ContactData>();

	private String number = "";
	
	private ShopData shopData = new ShopData();
	
	private PaypalData paypalData = new PaypalData();
	
	public float bitcoin;
	
	public WebPageData currentPageData;
	
	private EntityPlayer player;
	
    public List<ItemStack> itemStockage = new ArrayList<>();
    
    public double userMoney;
		
	public PhoneData(EntityPlayer player)
	{
		this.player = player;
	}
	
	public void setNumber(String number)
	{
		this.number = number;
	}
	
	public String getNumber()
	{
		return this.number;
	}
	
	@SideOnly(Side.CLIENT)
	public static ContactData getContact(String number)
	{
		IPlayer p = MinecraftUtils.getPlayerCapability(Minecraft.getMinecraft().player);
		for(ContactData c : p.getPhoneData().contacts)
		{
			if(c.number.equalsIgnoreCase(number))
				return c;
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public void loadContacts()
	{
		String sql = "SELECT NAME, NUMBER FROM CONTACTS";
        
        try (Connection conn = openConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            // loop through the result set
            while (rs.next()) {
            	
            	ContactData data = new ContactData();
            	data.name = rs.getString("NAME");
            	data.number = rs.getString("NUMBER");

            	contacts.add(data);
            }
            
            System.out.println("All contacts loaded!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
	
	@SideOnly(Side.CLIENT)
	public void loadGeneralData()
	{
		PhoneUI phone = PhoneUI.getPhone();
		
		String sql = "SELECT DEVELOPPERACTIVE FROM GENERALDATA";
        
        try (Connection conn = openConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            while (rs.next()) 
            {   
            	phone.settings.setDevelopperMode(rs.getBoolean(1));
            }
            
            System.out.println("General data loaded!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
	
	@SideOnly(Side.CLIENT)
	public ContactData addContact(String name, String number)
	{
		ContactData data = new ContactData();
		data.name = name;
		data.number = number;
		contacts.add(data);
		
		String sql = "INSERT INTO CONTACTS(NAME,NUMBER) VALUES(?,?)";

		try(Connection con = openConnection())
		{
			PreparedStatement statement = con.prepareStatement(sql);
			
			statement.setString(1, name);
			statement.setString(2, number);

			statement.execute();
			
			System.out.println("Contact " + data.name + " added to database");
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	@SideOnly(Side.CLIENT)
	public ContactData editContact(ContactData data, String name, String number)
	{	
		String sql = "UPDATE CONTACTS SET NAME = ?, NUMBER = ? WHERE NAME = ? AND NUMBER = ?";

		try(Connection con = openConnection())
		{
			PreparedStatement statement = con.prepareStatement(sql);
			
			statement.setString(1, name);
			statement.setString(2, number);
			statement.setString(3, data.name);
			statement.setString(4, data.number);


			statement.execute();
			
			System.out.println("Contact with name " + data.name + " edited");
			
			con.close();
			
			data.name = name;
			data.number = number;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}
	
	@SideOnly(Side.CLIENT)
	public void deleteContact(ContactData data)
	{
		contacts.remove(data);
		
		String sql = "DELETE from CONTACTS where NAME= ?";

		
		try(Connection con = openConnection())
		{
			PreparedStatement statement = con.prepareStatement(sql);
			
			statement.setString(1, data.name);
			
			statement.execute();

			System.out.println("Contact " + data.name + " deleted from database");
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void deleteSms(String conversationNumber)
	{
		List<SmsData> toDelete = sms.stream().filter(x -> x.receiverNumber.equalsIgnoreCase(conversationNumber) || x.senderNumber.equalsIgnoreCase(conversationNumber)).collect(Collectors.toList());
				
		String sql = "DELETE from SMS where SENDERNUMBER = ? OR RECEIVERNUMBER = ?";

		try(Connection con = openConnection())
		{
			PreparedStatement statement = con.prepareStatement(sql);
			
			statement.setString(1, conversationNumber);
			statement.setString(2, conversationNumber);
			
			statement.execute();

			System.out.println("All sms which sendernumber =  " + conversationNumber + " or receivernumber = " + conversationNumber + " deleted from database");
			
			con.close();
			
			sms.removeAll(toDelete);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	@SideOnly(Side.CLIENT)
	public void setSmsReaded(String conversationNumber)
	{		
		String sql = "UPDATE SMS SET READED = ? WHERE SENDERNUMBER = ? AND RECEIVERNUMBER = ? OR SENDERNUMBER = ? OR RECEIVERNUMBER = ?";

		
		try(Connection con = openConnection())
		{
			PreparedStatement statement = con.prepareStatement(sql);
			
			statement.setBoolean(1, true);
			statement.setString(2, conversationNumber);
			statement.setString(3, getNumber());
			statement.setString(4, getNumber());
			statement.setString(5, conversationNumber);
			
			statement.execute();
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@SideOnly(Side.CLIENT)
	public void loadSms()
	{
		String sql = "SELECT SENDERNUMBER, RECEIVERNUMBER, MESSAGE, DATE, READED FROM SMS";
        
        try (Connection conn = openConnection();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            while (rs.next()) {
            	
            	SmsData data = new SmsData(rs.getLong("DATE"),rs.getString("SENDERNUMBER"),rs.getString("RECEIVERNUMBER"),rs.getString("MESSAGE"),rs.getBoolean("READED"));

            	sms.add(data);
            }
            
            System.out.println("All sms loaded!");

            conn.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
	}
	
	@SideOnly(Side.CLIENT)
	public SmsData addSms(String message, String senderNumber, String receiverNumber)
	{
		long date = System.currentTimeMillis();
		boolean senderIsClient = senderNumber.equalsIgnoreCase(getNumber());
		
		PhoneUI phone = PhoneUI.getPhone();
		
		
		SmsData data = new SmsData(date,senderNumber, receiverNumber, message,senderIsClient);
		
		if(phone.currentApp instanceof Sms)
		{
			Sms app = (Sms) phone.currentApp;
			app.addMessage(data);
		}
		
		
		sms.add(data);
		

		String sql = "INSERT INTO SMS(SENDERNUMBER,RECEIVERNUMBER,MESSAGE,DATE,READED) VALUES(?,?,?,?,?)";

		try(Connection con = openConnection())
		{
			PreparedStatement statement = con.prepareStatement(sql);
			
			statement.setString(1, senderNumber);
			statement.setString(2, receiverNumber);
			statement.setString(3, message);
			statement.setLong(4, date);
			statement.setBoolean(5, senderIsClient);


			statement.execute();
			
			System.out.println("Sms added to database");
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return data;
	}
	
	@SideOnly(Side.CLIENT)
	public void setDevelopperModeActive()
	{
		String sql = "UPDATE GENERALDATA SET DEVELOPPERACTIVE = ?";

		try(Connection con = openConnection())
		{
			PreparedStatement statement = con.prepareStatement(sql);
			
			statement.setBoolean(1, true);
			statement.execute();
			
			System.out.println("Developper mode activated");
			
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public Connection openConnection() throws ClassNotFoundException, SQLException
	{
		  Class.forName("org.sqlite.JDBC");
	      
		  File file = new File("PhoneData");
		  
		  if(!file.exists())
		  {
			  file.mkdir();
		  }
		  
	      return DriverManager.getConnection("jdbc:sqlite:PhoneData/phone-" + Minecraft.getMinecraft().player.getName() +".db");
	}
	
	@SideOnly(Side.CLIENT)
	public void initDatabase()
	{
		 Connection c = null;
	     Statement stmt = null;
	     
	     try {
	    	 
	         c = openConnection();
	         
	         System.out.println("Opened database successfully");
	         
	         DatabaseMetaData dbm = c.getMetaData();
		     // check if "employee" table is there
		     ResultSet tables = dbm.getTables(null, null, "CONTACTS", null);
		     if (tables.next()) {
		    	c.close();
		        return;
		     }

	         stmt = c.createStatement();
	         
	         String sql = "CREATE TABLE CONTACTS " +
	                      "(ID             INT PRIMARY KEY," +
	                      " NAME           TEXT                 NOT NULL, " + 
	                      " NUMBER         CHAR(11)             NOT NULL)"; 
	         
	         stmt.executeUpdate(sql);
	         
	         stmt = c.createStatement();

	         sql = "CREATE TABLE SMS " +
                     "(ID             INT PRIMARY KEY," +
                     " SENDERNUMBER   CHAR(11)             NOT NULL, " + 
                     " RECEIVERNUMBER CHAR(11)             NOT NULL, " + 
                     " MESSAGE        TEXT                 NOT NULL, " +
                     " DATE           BIGINT               NOT NULL, " +
                     " READED         BOOLEAN              NOT NULL) "; 
	         
	         stmt.executeUpdate(sql);
	         
	         stmt = c.createStatement();
	         
	         sql = "CREATE TABLE GENERALDATA " +
                   "(DEVELOPPERACTIVE BOOLEAN            NOT NULL)"; 
        
	         stmt.executeUpdate(sql);
        
	         stmt = c.createStatement();
	         
	         sql = "INSERT INTO GENERALDATA(DEVELOPPERACTIVE) VALUES(?)";

	
	 		PreparedStatement statement = c.prepareStatement(sql);
	 			
	 		statement.setBoolean(1, false);

	 		statement.execute();
	 		

	         stmt.close();
	         c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         return;
	      }
	      System.out.println("Table created successfully");
	}
	
	public ShopData getShopData()
	{
		return shopData;
	}
	
	public PaypalData getPaypalData()
	{
		return paypalData;
	}
	
	public WebPageData openPage(byte pageId)
	{
		switch(pageId)
		{
			case 1:
			{
				currentPageData = new BitcoinConverterPage(player);
				break;
			}
			case 2:
			{
				currentPageData = new BMGPage(player);
				break;
			}
		}
		
		currentPageData.initPage();
		
		return currentPageData;
	}
	
	public void syncMoney()
	{
		if(!player.world.isRemote)
    	{
    		ModCore.getPackethandler().sendTo(PacketBitcoinPage.syncPlayerMoney(bitcoin, MinecraftUtils.getMoney(player)), (EntityPlayerMP)player);
    	}
    	else
    	{
    		ModCore.getPackethandler().sendToServer(PacketBitcoinPage.syncPlayerMoney(bitcoin,0));
    	}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound) {
		compound.setString("Number", number);
		compound.setFloat("Bitcoin", bitcoin);
		NBTTagList stockageList = new NBTTagList();
    	for(ItemStack is : itemStockage)
    	{
    		NBTTagCompound itemstackCompound = new NBTTagCompound();
    		is.writeToNBT(itemstackCompound);
    		stockageList.appendTag(itemstackCompound);
    	}
    	compound.setTag("Stockage", stockageList);
    	
    	NBTTagCompound paypalTag = new NBTTagCompound();
        paypalData.writeToNBT(paypalTag);
    	compound.setTag("PaypalData", paypalTag);

	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		number = compound.getString("Number");
		bitcoin = compound.getFloat("Bitcoin");
		
		NBTTagList stockageList =  (NBTTagList) compound.getTag("Stockage");

	    for(int i = 0; i < stockageList.tagCount(); i++)
	    {
	    	NBTTagCompound itemstackCompound = stockageList.getCompoundTagAt(i);
	    	ItemStack is = new ItemStack(itemstackCompound);
	    	itemStockage.add(is);
	    }
	    
	     NBTTagCompound paypalTag = compound.getCompoundTag("PaypalData");  
	     paypalData.readFromNBT(paypalTag);
	}
	
	 public void syncPhone() {
	    	if(!player.world.isRemote)
	    	{
	    		ModCore.getPackethandler().sendTo(new PacketSyncPhone(getNumber()), (EntityPlayerMP)player);
	    	}
	    	else
	    	{
	    		ModCore.getPackethandler().sendToServer(new PacketSyncPhone(""));
	    	}
	}
	
	

}