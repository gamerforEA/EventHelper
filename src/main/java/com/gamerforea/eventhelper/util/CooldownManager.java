package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.ModConstants;
import gnu.trove.iterator.TObjectLongIterator;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Mod.EventBusSubscriber(modid = ModConstants.MODID)
public class CooldownManager<T>
{
	private static final List<WeakReference<CooldownManager<?>>> COOLDOWN_MANAGERS = new ArrayList<>();
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
			synchronized (COOLDOWN_MANAGERS)
			{
				COOLDOWN_MANAGERS.add(new WeakReference<>(this));
			}
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

	@Deprecated
	@SubscribeEvent
	public static void onTick(TickEvent.ServerTickEvent event)
	{
		if (event.phase != TickEvent.Phase.END)
			return;

		synchronized (COOLDOWN_MANAGERS)
		{
			for (Iterator<WeakReference<CooldownManager<?>>> it = COOLDOWN_MANAGERS.iterator(); it.hasNext(); )
			{
				CooldownManager<?> cooldownManager = it.next().get();
				if (cooldownManager == null)
				{
					it.remove();
					continue;
				}

				for (TObjectLongIterator<?> iterator = cooldownManager.cooldowns.iterator(); iterator.hasNext(); )
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
	}
}
