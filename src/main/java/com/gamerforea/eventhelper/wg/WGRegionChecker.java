package com.gamerforea.eventhelper.wg;

import java.lang.reflect.Method;

import org.bukkit.World;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public final class WGRegionChecker
{
	private static final Method isInPrivate;

	public static final boolean isInPrivate(World world, int x, int y, int z) throws Throwable
	{
		return (Boolean) isInPrivate.invoke(null, world, x, y, z);
	}

	static
	{
		try
		{
			Class<?> clazz = WGReflection.injectIntoWG(WGRegionChecker.class);
			isInPrivate = clazz.getDeclaredMethod("isInPrivateInj", World.class, int.class, int.class, int.class);
			isInPrivate.setAccessible(true);
		}
		catch (Throwable throwable)
		{
			throw new RuntimeException("Failed injecting WGRegionCheckerInj.isInPrivateInj() method!", throwable);
		}
	}

	public static final class Inj
	{
		public static final Boolean isInPrivateInj(World world, int x, int y, int z)
		{
			for (ProtectedRegion region : WorldGuardPlugin.inst().getRegionManager(world).getApplicableRegions(new Vector(x, y, z)))
				if (!region.getId().equals(ProtectedRegion.GLOBAL_REGION))
					return true;
			return false;
		}
	}
}