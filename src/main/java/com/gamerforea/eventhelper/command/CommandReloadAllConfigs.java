package com.gamerforea.eventhelper.command;

import com.gamerforea.eventhelper.config.ConfigUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public final class CommandReloadAllConfigs extends CommandBase
{
	private static final String NAME = "reloadallconfigs";

	@Override
	public String getCommandName()
	{
		return NAME;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return '/' + NAME;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		for (String configName : ConfigUtils.reloadAllConfigs())
		{
			sender.addChatMessage(new ChatComponentText(configName + " config has been reloaded"));
		}
	}
}
