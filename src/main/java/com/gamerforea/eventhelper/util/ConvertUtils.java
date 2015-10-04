package com.gamerforea.eventhelper.util;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public final class ConvertUtils
{
	private static final Method getBukkitEntity;

	public static final org.bukkit.entity.Entity toBukkitEntity(Entity entity) throws Exception
	{
		return (org.bukkit.entity.Entity) getBukkitEntity.invoke(entity);
	}

	public static final org.bukkit.World toBukkitWorld(World world)
	{
		return Bukkit.getWorld(world.getWorldInfo().getWorldName());
	}

	static
	{
		try
		{
			getBukkitEntity = Entity.class.getDeclaredMethod("getBukkitEntity");
			getBukkitEntity.setAccessible(true);
		}
		catch (Throwable throwable)
		{
			throw new RuntimeException("Failed hooking Entity.getBukkitEntity() method!", throwable);
		}
	}
}