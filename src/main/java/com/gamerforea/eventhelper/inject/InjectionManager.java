package com.gamerforea.eventhelper.inject;

import com.gamerforea.eventhelper.inject.gpp.GPPInjection;
import com.gamerforea.eventhelper.inject.wg.WGInjection;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class InjectionManager
{
	private static final List<PluginInjection> injections = new ArrayList<>();

	public static void init()
	{
		PluginInjection wgInj = WGInjection.getInjection();
		if (wgInj != null)
			injections.add(wgInj);

		PluginInjection gppInj = GPPInjection.getInjection();
		if (gppInj != null)
			injections.add(gppInj);
	}

	public static boolean isInPrivate(World world, int x, int y, int z)
	{
		for (PluginInjection inj : injections)
		{
			if (inj.isInPrivate(world, x, y, z))
				return true;
		}
		return false;
	}

	public static boolean isPrivateMember(Player player, int x, int y, int z)
	{
		for (PluginInjection inj : injections)
		{
			if (!inj.isPrivateMember(player, x, y, z))
				return false;
		}
		return true;
	}

	public static boolean isPrivateOwner(Player player, int x, int y, int z)
	{
		for (PluginInjection inj : injections)
		{
			if (!inj.isPrivateOwner(player, x, y, z))
				return false;
		}
		return true;
	}
}
