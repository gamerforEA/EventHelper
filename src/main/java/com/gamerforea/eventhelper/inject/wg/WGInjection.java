package com.gamerforea.eventhelper.inject.wg;

import com.gamerforea.eventhelper.inject.PluginInjection;
import com.gamerforea.eventhelper.util.InjectionUtils;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class WGInjection
{
	public static PluginInjection getInjection()
	{
		Class<?> clazz = InjectionUtils.injectClass("WorldGuard", WGInjection.class);
		if (clazz != null)
			try
			{
				return (PluginInjection) clazz.newInstance();
			}
			catch (Throwable throwable)
			{
				throwable.printStackTrace();
			}
		return null;
	}

	public static final class Inj implements PluginInjection
	{
		@Override
		public boolean isInPrivate(World world, int x, int y, int z)
		{
			for (ProtectedRegion region : WorldGuardPlugin.inst().getRegionManager(world).getApplicableRegions(new Vector(x, y, z)))
			{
				if (!region.getId().equals(ProtectedRegion.GLOBAL_REGION))
					return true;
			}
			return false;
		}

		@Override
		public boolean isPrivateMember(Player player, int x, int y, int z)
		{
			WorldGuardPlugin wg = WorldGuardPlugin.inst();
			return wg.getRegionManager(player.getWorld()).getApplicableRegions(new Vector(x, y, z)).isMemberOfAll(wg.wrapPlayer(player, true));
		}

		@Override
		public boolean isPrivateOwner(Player player, int x, int y, int z)
		{
			WorldGuardPlugin wg = WorldGuardPlugin.inst();
			return wg.getRegionManager(player.getWorld()).getApplicableRegions(new Vector(x, y, z)).isOwnerOfAll(wg.wrapPlayer(player, true));
		}
	}
}
