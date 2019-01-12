package com.gamerforea.eventhelper.inject.gpp;

import com.gamerforea.eventhelper.inject.PluginInjection;
import com.gamerforea.eventhelper.util.InjectionUtils;
import net.kaikk.mc.gpp.Claim;
import net.kaikk.mc.gpp.GriefPreventionPlus;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public final class GPPInjection
{
	public static PluginInjection getInjection()
	{
		Class<?> clazz = InjectionUtils.injectClass("GriefPreventionPlus", GPPInjection.class);
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
			GriefPreventionPlus plugin = GriefPreventionPlus.getInstance();
			Claim claim = plugin.getDataStore().getClaimAt(new Location(world, x, y, z), false);
			return claim != null;
		}

		@Override
		public boolean isPrivateMember(Player player, int x, int y, int z)
		{
			return this.isPrivateOwner(player, x, y, z);
		}

		@Override
		public boolean isPrivateOwner(Player player, int x, int y, int z)
		{
			GriefPreventionPlus plugin = GriefPreventionPlus.getInstance();
			Claim claim = plugin.getDataStore().getClaimAt(new Location(player.getWorld(), x, y, z), false);
			return claim != null && player.getUniqueId().equals(claim.getOwnerID());
		}
	}
}
