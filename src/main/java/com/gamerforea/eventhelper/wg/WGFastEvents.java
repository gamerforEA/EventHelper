package com.gamerforea.eventhelper.wg;

import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class WGFastEvents
{
	private static final Method hasAccess;

	public static final boolean hasAccess(Player player, int x, int y, int z, boolean checkPvP) throws Throwable
	{
		return (Boolean) hasAccess.invoke(null, player, x, y, z);
	}

	static
	{
		try
		{
			Class<?> clazz = WGReflection.injectIntoWG(WGFastEvents.class);
			hasAccess = clazz.getDeclaredMethod("hasAccessInj", Player.class, int.class, int.class, int.class, boolean.class);
			hasAccess.setAccessible(true);
		}
		catch (Throwable throwable)
		{
			throw new RuntimeException("Failed injecting WGFastEvents$Inj.hasAccessInj() method!", throwable);
		}
	}

	public static final class Inj
	{
		public static final boolean hasAccessInj(Player player, int x, int y, int z, boolean checkPvP)
		{
			BukkitPlayer bPlayer = new BukkitPlayer(WorldGuardPlugin.inst(), player);
			for (ProtectedRegion region : WorldGuardPlugin.inst().getRegionManager(player.getWorld()).getApplicableRegions(new Vector(x, y, z)))
				if (!region.getId().equals(ProtectedRegion.GLOBAL_REGION))
					if (!region.isMember(bPlayer) && !region.isOwner(bPlayer))
						return false;
					else if (checkPvP && region.getFlag(DefaultFlag.PVP) == State.DENY)
						return false;
			return true;
		}
	}
}