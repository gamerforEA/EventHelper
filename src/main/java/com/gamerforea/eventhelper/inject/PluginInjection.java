package com.gamerforea.eventhelper.inject;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface PluginInjection
{
	boolean isInPrivate(World world, int x, int y, int z);

	boolean isPrivateMember(Player player, int x, int y, int z);

	boolean isPrivateOwner(Player player, int x, int y, int z);
}
