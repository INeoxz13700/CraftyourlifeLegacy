package fr.innog.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface ICommandCallback {

	public abstract void execute(MinecraftServer server, ICommandSender sender, Object... args);
	
}
