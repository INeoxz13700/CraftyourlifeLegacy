package fr.innog.commands;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public interface ITabulationCompletion {

	public abstract List<String> getTabCompletion(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos);
	
}
