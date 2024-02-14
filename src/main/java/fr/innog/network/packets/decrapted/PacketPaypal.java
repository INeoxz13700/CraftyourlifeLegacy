package fr.innog.network.packets.decrapted;

import java.io.IOException;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.client.ui.ingame.PhoneUI;
import fr.innog.common.ModCore;
import fr.innog.data.NumberData;
import fr.innog.network.packets.PacketBase;
import fr.innog.phone.Paypal;
import fr.innog.phone.Paypal.PaypalAccount;
import fr.innog.utils.DataUtils;
import fr.innog.utils.MinecraftUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketPaypal extends PacketBase 
{
	/*
	 * 0: start registration
	 * 1: sendError
	 * 2: displayGui
	 * 3: send activation code
	 * 4: finalize registering
	 * 5: disconnect account
	 * 6: ask to server datas
	 * 7: send to client datas
	 * 8: connect Account
	 * 9: start forgot password request
	 * 10: send code forgot password request
	 * 11: change password
	 * 12: send money
	 * 13: resend code request
	 */
	public byte type;
	
	public String numberPhone;
	
	public byte errorType;
	
	public byte guiId;
	
	public String code;
	
	public String password;
	public String confirmationPassword;
	
	public float money;
	public boolean isAuthentified;
	
	public static PacketPaypal startRegistration(String numberPhone)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.numberPhone = numberPhone;
		packet.type = 0;
		return packet;
	}
	
	/** server only packet **/
	public static PacketPaypal throwError(int errorType)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.errorType = (byte)errorType;
		packet.type = 1;
		return packet;
	}
	
	public static PacketPaypal sendActivationCode(String code)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.code = code;
		packet.type = 3;
		return packet;
	}
	
	public static PacketPaypal switchGui(int guiId)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.guiId = (byte)guiId;
		packet.type = 2;
		return packet;
	}
	
	public static PacketPaypal finalizeRegistering(String password, String confirmationPassword)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.password = password;
		packet.confirmationPassword = confirmationPassword;
		packet.type = 4;
		return packet;
	}
	
	public static PacketPaypal disconnectAccount()
	{
		PacketPaypal packet = new PacketPaypal();
		packet.type = 5;
		return packet;
	}	
	
	public static PacketPaypal connectAccount(String phoneNumber, String password)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.type = 8;
		packet.numberPhone = phoneNumber;
		packet.password = password;
		return packet;
	}	
	
	public static PacketPaypal changePassword(String password, String confirmationPassword)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.password = password;
		packet.confirmationPassword = confirmationPassword;
		packet.type = 11;
		return packet;
	}
	
	public static PacketPaypal startForgotPasswordRequest(String phoneNumber)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.type = 9;
		packet.numberPhone = phoneNumber;
		return packet;
	}	
	
	
	public static PacketPaypal sendCode(String code)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.type = 10;
		packet.code = code;
		return packet;
	}	
	
	
	public static PacketPaypal syncPaypalData(EntityPlayer player)
	{
		PacketPaypal packet = new PacketPaypal();
		if(player.world.isRemote)
		{
			packet.type = 6;
		}
		else
		{
			IPlayer extendedPlayer = MinecraftUtils.getPlayerCapability(player);
			packet.type = 7;
			packet.isAuthentified = extendedPlayer.getPhoneData().getPaypalData().isAuthentified;
			if(packet.isAuthentified)packet.money = (float) MinecraftUtils.getMoney(player);
		}
		return packet;
	}	
	
	public static PacketPaypal sendMoney(float money, String numberPhone)
	{
		PacketPaypal packet = new PacketPaypal();
		packet.type = 12;
		packet.money = money;
		packet.numberPhone = numberPhone;
		return packet;
	}	
	

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		data.writeByte(type);
		if(type == 0 || type == 9)
		{
			ByteBufUtils.writeUTF8String(data, numberPhone);
		}
		else if(type == 1)
		{
			data.writeByte(errorType);
		}
		else if(type == 2)
		{
			data.writeByte(guiId);
		}
		else if(type == 3 || type == 10)
		{
			ByteBufUtils.writeUTF8String(data, code);
		}
		else if(type == 4 || type == 11)
		{
			ByteBufUtils.writeUTF8String(data, password);
			ByteBufUtils.writeUTF8String(data, confirmationPassword);
		}
		else if(type == 7)
		{
			data.writeBoolean(isAuthentified);
			if(isAuthentified)
			{
				data.writeFloat(money);
			}
		}
		else if(type == 8)
		{
			ByteBufUtils.writeUTF8String(data, numberPhone);
			ByteBufUtils.writeUTF8String(data, password);
		}
		else if(type == 12)
		{
			ByteBufUtils.writeUTF8String(data, numberPhone);
			data.writeFloat(money);
		}
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data)
	{
		type = data.readByte();
		if(type == 0 || type == 9)
		{
			numberPhone = ByteBufUtils.readUTF8String(data);
		}
		else if(type == 1)
		{
			errorType = data.readByte();
		}
		else if(type == 2)
		{
			guiId = data.readByte();
		}
		else if(type == 3 || type == 10)
		{
			code = ByteBufUtils.readUTF8String(data);
		}
		else if(type == 4 || type == 11)
		{
			password =  ByteBufUtils.readUTF8String(data);
			confirmationPassword = ByteBufUtils.readUTF8String(data);
		}
		else if(type == 7)
		{
			isAuthentified = data.readBoolean();
			if(isAuthentified)
			{
				money = data.readFloat();
			}
		}
		else if(type == 8)
		{
			numberPhone =  ByteBufUtils.readUTF8String(data);
			password = ByteBufUtils.readUTF8String(data);
		}
		else if(type == 12)
		{
			numberPhone =  ByteBufUtils.readUTF8String(data);
			money = data.readFloat();
		}
		
	}

	@Override
	public void handleServerSide(EntityPlayerMP playerEntity) 
	{
		IPlayer extendedPlayer = MinecraftUtils.getPlayerCapability(playerEntity);
		if(!extendedPlayer.getPhoneData().getPaypalData().accountIsRegistered())
		{
			if(type == 0)
			{
				if(!DataUtils.isValidNumber(numberPhone))
				{
					ModCore.getPackethandler().sendTo(throwError(0), playerEntity);
				}
				else
				{
					if(!numberPhone.equals(extendedPlayer.getPhoneData().getNumber()))
					{
						ModCore.getPackethandler().sendTo(throwError(0), playerEntity);
					}
					else
					{
						extendedPlayer.getPhoneData().getPaypalData().updateSmsCode();
						ModCore.getPackethandler().sendTo(new PacketSendSms("Votre code d'activation : " + extendedPlayer.getPhoneData().getPaypalData().smsCode,extendedPlayer.getPhoneData().getNumber(),"Paypal SMS service"), playerEntity);
						ModCore.getPackethandler().sendTo(switchGui(1), playerEntity);
					}
				}
			}
			else if(type == 3)
			{
				if(extendedPlayer.getPhoneData().getPaypalData().smsCode == null) return;
				
				if(extendedPlayer.getPhoneData().getPaypalData().smsCode.equalsIgnoreCase(code))
				{
					if(code.isEmpty())
					{
						ModCore.getPackethandler().sendTo(throwError(1), playerEntity);
						return;
					}
					ModCore.getPackethandler().sendTo(switchGui(2), playerEntity);
				}
				else
				{
					ModCore.getPackethandler().sendTo(throwError(1), playerEntity);
				}
			}
			else if(type == 4)
			{
				if(password.length() < 6)
				{
					ModCore.getPackethandler().sendTo(throwError(3), playerEntity);
				}
				else if(!password.equals(confirmationPassword))
				{
					ModCore.getPackethandler().sendTo(throwError(4), playerEntity);
				}
				else
				{
					extendedPlayer.getPhoneData().getPaypalData().password = password;
					extendedPlayer.getPhoneData().getPaypalData().isAuthentified = true;
					extendedPlayer.getPhoneData().getPaypalData().smsCode = "";
					ModCore.getPackethandler().sendTo(switchGui(3), playerEntity);
				}
			}
			else if(type == 8)
			{
				ModCore.getPackethandler().sendTo(throwError(5), playerEntity);
			}
			else if(type == 9)
			{
				ModCore.getPackethandler().sendTo(throwError(6), playerEntity);	
			}
		}
		else
		{
			if(type == 0)
			{
				if(!DataUtils.isValidNumber(numberPhone))
				{
					ModCore.getPackethandler().sendTo(throwError(0), playerEntity);
				}
				else
				{
					ModCore.getPackethandler().sendTo(throwError(11), playerEntity);
				}
			}
			if(type == 5)
			{
				extendedPlayer.getPhoneData().getPaypalData().isAuthentified = false;
				ModCore.getPackethandler().sendTo(switchGui(0), playerEntity);
			}
			else if(type == 8)
			{
				if(!extendedPlayer.getPhoneData().getNumber().equals(numberPhone))
				{
					ModCore.getPackethandler().sendTo(throwError(5), playerEntity);

				}
				else if(!extendedPlayer.getPhoneData().getPaypalData().password.equals(password))
				{
					ModCore.getPackethandler().sendTo(throwError(5), playerEntity);
				}
				else
				{
					extendedPlayer.getPhoneData().getPaypalData().isAuthentified = true;
					ModCore.getPackethandler().sendTo(switchGui(3), playerEntity);
				}
			}
			else if(type == 9)
			{
				if(numberPhone.equals(extendedPlayer.getPhoneData().getNumber()))
				{
					extendedPlayer.getPhoneData().getPaypalData().updateSmsCode();
					ModCore.getPackethandler().sendTo(new PacketSendSms("Votre code afin de vérifier votre identité : " + extendedPlayer.getPhoneData().getPaypalData().smsCode,extendedPlayer.getPhoneData().getNumber(),"Paypal SMS service"), playerEntity);
					ModCore.getPackethandler().sendTo(switchGui(5), playerEntity);
				}
				else
				{
					ModCore.getPackethandler().sendTo(throwError(6), playerEntity);
				}
			}
			else if(type == 10)
			{
				if(extendedPlayer.getPhoneData().getPaypalData().smsCode.equals(code))
				{
					if(code.isEmpty())
					{
						ModCore.getPackethandler().sendTo(throwError(7), playerEntity);
						return;
					}
					ModCore.getPackethandler().sendTo(switchGui(6), playerEntity);
				}
				else
				{
					ModCore.getPackethandler().sendTo(throwError(7), playerEntity);
				}
			}
			else if(type == 11)
			{
				if(password.length() < 6)
				{
					ModCore.getPackethandler().sendTo(throwError(3), playerEntity);
				}
				else if(!password.equals(confirmationPassword))
				{
					ModCore.getPackethandler().sendTo(throwError(4), playerEntity);
				}
				else
				{
					extendedPlayer.getPhoneData().getPaypalData().password = password;
					extendedPlayer.getPhoneData().getPaypalData().isAuthentified = true;
					extendedPlayer.getPhoneData().getPaypalData().smsCode = "";
					ModCore.getPackethandler().sendTo(switchGui(3), playerEntity);
				}
			}
			else if(type == 12)
			{
				if(money <= 0)
				{
					ModCore.getPackethandler().sendTo(throwError(9), playerEntity);
					return;
				}
				
				String username = null;
				try 
				{
					username = NumberData.getUsernameByNumber(numberPhone);
				} catch (IOException e) {
					ModCore.getPackethandler().sendTo(throwError(8), playerEntity);
					e.printStackTrace();
					return;
				}
				
				if(username == null)
				{
					ModCore.getPackethandler().sendTo(throwError(8), playerEntity);
					return;
				}
				else if(username.equalsIgnoreCase(playerEntity.getName()))
				{
					ModCore.getPackethandler().sendTo(throwError(100), playerEntity);
					return;
				}
				
				
				
				EntityPlayer receiverPlayer = playerEntity.world.getPlayerEntityByName(username);
				
				if(receiverPlayer == null)
				{
					ModCore.getPackethandler().sendTo(throwError(8), playerEntity);
				}
				else
				{
					IPlayer receiverPlayerExtended = MinecraftUtils.getPlayerCapability(receiverPlayer);
					if(!receiverPlayerExtended.getPhoneData().getPaypalData().accountIsRegistered())
					{
						ModCore.getPackethandler().sendTo(throwError(8), playerEntity);
					}
					else
					{
						if(DataUtils.isLimitValue(money))
						{
							ModCore.getPackethandler().sendTo(throwError(10), playerEntity);
						}
						else
						{
							if(!MinecraftUtils.haveMoney(playerEntity, money))
							{
								ModCore.getPackethandler().sendTo(throwError(12), playerEntity);
							}
							else
							{
								MinecraftUtils.removeMoney(playerEntity, money);
								MinecraftUtils.addMoney(receiverPlayer, money);
							}
						}
					}
				}
				
			}
			else if(type == 6)
			{
				ModCore.getPackethandler().sendTo(PacketPaypal.syncPaypalData(playerEntity), playerEntity);
			}
		}
	}

	@Override
	public void handleClientSide(EntityPlayer clientPlayer)
	{
		if(Minecraft.getMinecraft().currentScreen instanceof PhoneUI)
		{
			if(PhoneUI.getPhone().currentApp instanceof Paypal)
			{
				Paypal paypal = (Paypal) PhoneUI.getPhone().currentApp;
				if(type == 1)
				{
					paypal.displayError(errorType);
				}
				else if(type == 2)
				{
					paypal.setPage(guiId);
				}
				else if(type == 7)
				{
					IPlayer playerData = MinecraftUtils.getPlayerCapability(clientPlayer);
					playerData.getPhoneData().getPaypalData().isAuthentified = isAuthentified;
					playerData.getPhoneData().userMoney = money;

					if(isAuthentified && !(paypal.currentPage instanceof PaypalAccount))
					{
						paypal.setPage(3);
						return;
					}
				}
			}
		}
	}

}