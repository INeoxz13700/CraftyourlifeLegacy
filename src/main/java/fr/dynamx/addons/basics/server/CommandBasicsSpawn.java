package fr.dynamx.addons.basics.server;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.jme3.math.Vector3f;

import fr.dynamx.addons.basics.common.modules.BasicsAddonModule;
import fr.dynamx.addons.basics.utils.VehicleKeyUtils;
import fr.dynamx.api.entities.callbacks.ModularEntityInitCallback;
import fr.dynamx.api.entities.modules.IPhysicsModule;
import fr.dynamx.common.contentpack.DynamXObjectLoaders;
import fr.dynamx.common.entities.BaseVehicleEntity;
import fr.dynamx.common.entities.ModularPhysicsEntity;
import fr.dynamx.common.entities.vehicles.CarEntity;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class CommandBasicsSpawn extends CommandBase {
  public String getName() {
    return "basicsspawn";
  }
  
  public String getUsage(ICommandSender sender) {
    return "/basicspawn <vehicle> <x> <y> <z> <yaw_angle> [metadata] [owner[s]]";
  }
  
  public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if (args.length >= 5) {
      String id = args[0];
      if (DynamXObjectLoaders.WHEELED_VEHICLES.findInfo(id) == null)
        throw new WrongUsageException("Unknown vehicle name " + id, new Object[0]); 
      BlockPos pos = parseBlockPos(sender, args, 1, true);
      int meta = 0;
      if (args.length >= 6)
        meta = parseInt(args[5]); 
      CarEntity<?> e = new CarEntity(id.replace("car:", ""), sender.getEntityWorld(), new Vector3f(pos.getX(), pos.getY(), pos.getZ()), parseInt(args[4]), meta);
      if (args.length == 7)
    	  e.setInitCallback(new ModularEntityInitCallback()
			{

				@Override
				public void onEntityInit(ModularPhysicsEntity<?> modularEntity,
						List<IPhysicsModule<?>> modules) 
				{
					ItemStack key = VehicleKeyUtils.getKeyForVehicle((BaseVehicleEntity<?>)modularEntity);
		            try {
		              List<EntityPlayerMP> players = getPlayers(server, sender, args[6]);
		              //players.forEach(());
		            } catch (CommandException ex) {
		              sender.sendMessage((ITextComponent)new TextComponentString("Failed to give key of the vehicle : " + ex.getLocalizedMessage()));
		              ex.printStackTrace();
		            } 
		            BasicsAddonModule m = (BasicsAddonModule)modularEntity.getModuleByType(BasicsAddonModule.class);
		            sender.sendMessage((ITextComponent)new TextComponentString("Vehicle successfully spawned !"));
				}
		
	});
      sender.getEntityWorld().spawnEntity((Entity)e);
    } else {
      throw new WrongUsageException(getUsage(sender), new Object[0]);
    } 
  }
  
  public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
    if (args.length == 1)
      return getListOfStringsMatchingLastWord(args, DynamXObjectLoaders.WHEELED_VEHICLES.getInfos().keySet()); 
    if (args.length == 2 || args.length == 3 || args.length == 4)
      return CommandBase.getTabCompletionCoordinate(args, 2, targetPos); 
    if (args.length == 7)
      return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()); 
    return Collections.emptyList();
  }
}
