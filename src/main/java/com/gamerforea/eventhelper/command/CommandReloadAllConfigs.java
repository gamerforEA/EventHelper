package com.gamerforea.eventhelper.command;

import com.gamerforea.eventhelper.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public final class CommandReloadAllConfigs extends CommandBase
{
	private static final String NAME = "reloadallconfigs";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public String getUsage(ICommandSender sender)
	{
		return '/' + NAME;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args)
	{
		for (String configName : ConfigUtils.reloadAllConfigs())
		{
			sender.sendMessage(new TextComponentString(configName + " config has been reloaded"));
		}
	}
}
