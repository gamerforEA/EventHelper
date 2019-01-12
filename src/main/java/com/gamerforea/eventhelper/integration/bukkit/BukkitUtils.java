package com.gamerforea.eventhelper.integration.bukkit;

import com.gamerforea.eventhelper.EventHelperMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public final class BukkitUtils
{
	private static final Method getBukkitEntity;

	@Nonnull
	public static Player getPlayer(@Nonnull EntityPlayer player)
	{
		return (Player) getEntity(player);
	}

	@Nonnull
	public static org.bukkit.entity.Entity getEntity(@Nonnull Entity entity)
	{
		try
		{
			return (org.bukkit.entity.Entity) Objects.requireNonNull(getBukkitEntity.invoke(entity), "Entity.getBukkitEntity() result must not be null");
		}
		catch (IllegalAccessException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Nonnull
	public static BlockFace getBlockFace(@Nonnull EnumFacing side)
	{
		switch (side)
		{
			case DOWN:
				return BlockFace.DOWN;
			case UP:
				return BlockFace.UP;
			case NORTH:
				return BlockFace.NORTH;
			case SOUTH:
				return BlockFace.SOUTH;
			case WEST:
				return BlockFace.WEST;
			case EAST:
				return BlockFace.EAST;
			default:
				return BlockFace.SELF;
		}
	}

	static
	{
		Method getBukkitEntityMethod = null;
		try
		{
			getBukkitEntityMethod = Entity.class.getDeclaredMethod("getBukkitEntity");
			getBukkitEntityMethod.setAccessible(true);
		}
		catch (Throwable throwable)
		{
			EventHelperMod.LOGGER.warn("Failed hooking CraftBukkit methods", throwable);
		}
		getBukkitEntity = getBukkitEntityMethod;
	}
}
