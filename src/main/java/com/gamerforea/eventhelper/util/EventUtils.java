package com.gamerforea.eventhelper.util;

import static com.gamerforea.eventhelper.util.ConvertUtils.toBukkitEntity;
import static com.gamerforea.eventhelper.util.ConvertUtils.toBukkitFace;
import static com.gamerforea.eventhelper.util.ConvertUtils.toBukkitItemStackMirror;
import static com.gamerforea.eventhelper.util.ConvertUtils.toBukkitWorld;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import com.gamerforea.eventhelper.EventHelper;
import com.gamerforea.eventhelper.wg.WGRegionChecker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public final class EventUtils
{
	public static final boolean cantBreak(EntityPlayer player, int x, int y, int z)
	{
		try
		{
			Player bPlayer = toBukkitEntity(player);
			BlockBreakEvent event = new BlockBreakEvent(bPlayer.getWorld().getBlockAt(x, y, z), bPlayer);
			EventHelper.callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			err("Failed call BlockBreakEvent: [Player: %s, X:%d, Y:%d, Z:%d]", String.valueOf(player), x, y, z);
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean cantBreak(EntityPlayer player, double x, double y, double z)
	{
		int xx = MathHelper.floor_double(x);
		int yy = MathHelper.floor_double(y);
		int zz = MathHelper.floor_double(z);
		return cantBreak(player, xx, yy, zz);
	}

	public static final boolean cantDamage(Entity damager, Entity damagee)
	{
		try
		{
			EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(toBukkitEntity(damager), toBukkitEntity(damagee), DamageCause.ENTITY_ATTACK, 0D);
			EventHelper.callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			err("Failed call EntityDamageByEntityEvent: [Damager: %s, Damagee: %s]", String.valueOf(damager), String.valueOf(damagee));
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean cantInteract(EntityPlayer player, ItemStack stack, int x, int y, int z, ForgeDirection side)
	{
		try
		{
			Player bPlayer = toBukkitEntity(player);
			PlayerInteractEvent event = new PlayerInteractEvent(bPlayer, Action.RIGHT_CLICK_BLOCK, toBukkitItemStackMirror(stack), bPlayer.getWorld().getBlockAt(x, y, z), toBukkitFace(side));
			EventHelper.callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			err("Failed call PlayerInteractEvent: [Player: %s, Item: %s, X:%d, Y:%d, Z:%d, Side: %s]", String.valueOf(player), String.valueOf(stack), x, y, z, String.valueOf(side));
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean cantFromTo(World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ)
	{
		try
		{
			org.bukkit.World bWorld = toBukkitWorld(world);
			BlockFromToEvent event = new BlockFromToEvent(bWorld.getBlockAt(fromX, fromY, fromZ), bWorld.getBlockAt(toX, toY, toZ));
			EventHelper.callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			err("Failed call BlockFromToEvent: [FromX: %d, FromY: %d, FromZ: %d, ToX: %d, ToY: %d, ToZ: %d]", fromX, fromY, fromZ, toX, toY, toZ);
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean cantFromTo(World world, int fromX, int fromY, int fromZ, ForgeDirection direction)
	{
		try
		{
			org.bukkit.World bWorld = toBukkitWorld(world);
			BlockFromToEvent event = new BlockFromToEvent(bWorld.getBlockAt(fromX, fromY, fromZ), toBukkitFace(direction));
			EventHelper.callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			err("Failed call BlockFromToEvent: [FromX: %d, FromY: %d, FromZ: %d, Direction: %s]", fromX, fromY, fromZ, String.valueOf(direction));
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean isInPrivate(World world, int x, int y, int z)
	{
		try
		{
			return WGRegionChecker.isInPrivate(toBukkitWorld(world), x, y, z);
		}
		catch (Throwable throwable)
		{
			err("Failed check private: [World: %s, X: %d, Y: %d, Z: %d]", world.getWorldInfo().getWorldName(), x, y, z);
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean isInPrivate(Entity entity)
	{
		int x = MathHelper.floor_double(entity.posX);
		int y = MathHelper.floor_double(entity.posY);
		int z = MathHelper.floor_double(entity.posZ);
		return isInPrivate(entity.worldObj, x, y, z);
	}

	private static final void err(String format, Object... args)
	{
		System.err.println(String.format(format, args));
	}
}
