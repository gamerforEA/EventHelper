package com.gamerforea.eventhelper.util;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

public class CooldownManager<T>
{
	private final TObjectLongMap<T> cooldowns = new TObjectLongHashMap<>();
	private final long cooldown;

	public CooldownManager(long cooldown, @Nonnull TimeUnit timeUnit)
	{
		this(cooldown <= 0 ? 0 : timeUnit.toSeconds(cooldown) * 20);
	}

	public CooldownManager(long cooldownInTicks)
	{
		this.cooldown = Math.max(0, cooldownInTicks);
		if (this.cooldown > 0)
			MinecraftForge.EVENT_BUS.register(this);
	}

	public boolean canAdd(@Nonnull T key)
	{
		return this.cooldown <= 0 || !this.cooldowns.containsKey(key);
	}

	public boolean add(@Nonnull T key)
	{
		if (this.cooldown <= 0)
			return true;
		if (this.canAdd(key))
		{
			this.cooldowns.put(key, this.cooldown);
			return true;
		}
		return false;
	}

	public long getCooldown(@Nonnull T key)
	{
		return this.cooldown <= 0 ? 0 : this.cooldowns.get(key);
	}

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event)
	{
		for (TObjectLongIterator<T> iterator = this.cooldowns.iterator(); iterator.hasNext(); )
		{
			iterator.advance();
			long timer = iterator.value() - 1;
			if (timer <= 0)
				iterator.remove();
			else
				iterator.setValue(timer);
		}
	}
}
