package com.gamerforea.eventhelper.util;

import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public final class WorldCooldownManager extends CooldownManager<Integer>
{
	public WorldCooldownManager(long cooldown, @Nonnull TimeUnit timeUnit)
	{
		super(cooldown, timeUnit);
	}

	public WorldCooldownManager(long cooldownInTicks)
	{
		super(cooldownInTicks);
	}

	public boolean canAdd(@Nonnull World world)
	{
		return this.canAdd(world.provider.getDimension());
	}

	public boolean add(@Nonnull World world)
	{
		return this.add(world.provider.getDimension());
	}

	public long getCooldown(@Nonnull World world)
	{
		return this.getCooldown(world.provider.getDimension());
	}
}
