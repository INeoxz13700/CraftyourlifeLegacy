package fr.innog.handler;

import java.util.Arrays;
import java.util.List;

import fr.innog.common.ModCore;
import fr.innog.utils.MinecraftUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ModCore.MODID)
public class BlockHandler {

	public static final List<Integer> trashBlock = Arrays.asList(new Integer[] { 6236 });
	
	@SubscribeEvent
	public static void onInterractTrash(PlayerInteractEvent.RightClickBlock event)
	{
		BlockPos pos = event.getPos();
		EntityPlayer player = event.getEntityPlayer();
		
		World world = player.world;
		
		int id = Block.getIdFromBlock(world.getBlockState(pos).getBlock());
		if(trashBlock.contains(id))
		{
			MinecraftUtils.dispatchConsoleCommand("apidata trash " + player.getName());
		}
	}
	
}
