package fr.innog.commands;

import fr.innog.capability.playercapability.IPlayer;
import fr.innog.common.items.Items;
import fr.innog.common.world.WorldDataManager;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;

public class CommandEthylotest  extends ExtendedCommandBase {

	public CommandEthylotest(String commandName) {
		super(commandName);
	}

	@Override
	protected void buildCommand() {
		try {
			addCommandArgument(new CommandStructure("Accepte/Refuse la requête du FDL pour l'éthylotest").addArg("[<value>]", String.class).setCommandCallback(new ICommandCallback()
			{
				@Override
				public void execute(MinecraftServer server,ICommandSender sender, Object... value) 
				{
					String result = value[0].toString();
					
					if(sender instanceof EntityPlayer)
					{
						EntityPlayer player = (EntityPlayer) sender;
						IPlayer playerData = MinecraftUtils.getPlayerCapability(player);
						
						if(playerData.getEthylotestRequest() == null) 
						{
							return;
						}
						
						String ownerName = playerData.getEthylotestRequest().getRequestOwnerName();
						
						
						EntityPlayer owner = server.getPlayerList().getPlayerByUsername(ownerName);
						
						playerData.setEthylotestRequest(null);
						
						if(owner != null)
						{
							if(owner.getHeldItemMainhand().getItem() != Items.ethylotest)
							{
								MinecraftUtils.sendMessage(sender, "§aRequête annulée le FDL n'a pas gardé l'éthylotest en main.");
								MinecraftUtils.sendMessage(owner, "§cRequête annulée vous n'avez pas gardé l'éthylotest en main.");
								return;
							}
								
							if(result.equals("OUI"))
							{
								MinecraftUtils.sendMessage(sender, "§aVous avez soufflé sur l'éthylotest.");
								MinecraftUtils.sendMessage(owner, "§6Taux d'alcoolémie dans le sang : §e" + playerData.getHealthData().getAlcolInBlood() + "§6g");
								if(owner.getHeldItemMainhand().getItem() == Items.ethylotest)
								{
									owner.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
								}
							}
							else
							{
								MinecraftUtils.sendMessage(sender, "§aVous avez refusé de souffler sur l'éthylotest.");
								MinecraftUtils.sendMessage(owner, "§cVotre cible refuse de souffler sur l'éthylotest.");
							}
						}
						
						
					}
					
					
					
				}
			}));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
}
