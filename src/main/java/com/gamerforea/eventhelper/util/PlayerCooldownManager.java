package com.gamerforea.eventhelper.util;

import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class PlayerCooldownManager extends CooldownManager<UUID>
{
	public PlayerCooldownManager(long cooldown, @Nonnull TimeUnit timeUnit)
	{
		super(cooldown, timeUnit);
	}

	public PlayerCooldownManager(long cooldownInTicks)
	{
		super(cooldownInTicks);
	}

	public boolean canAdd(@Nonnull EntityPlayer player)
	{
		return this.canAdd(player.getUniqueID());
	}

	public boolean add(@Nonnull EntityPlayer player)
	{
		return this.add(player.getUniqueID());
	}

	public long getCooldown(@Nonnull EntityPlayer player)
	{
		return this.getCooldown(player.getUniqueID());
	}
}
