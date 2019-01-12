package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.EventHelper;
import com.gamerforea.eventhelper.inject.InjectionManager;
import com.google.common.base.Strings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

import static com.gamerforea.eventhelper.util.ConvertUtils.*;
import static net.minecraft.util.MathHelper.floor_double;

public final class EventUtils
{
	public static boolean cantBreak(@Nonnull EntityPlayer player, int x, int y, int z)
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
			EventHelper.error(throwable, "Failed call BlockBreakEvent: [Player: {}, X:{}, Y:{}, Z:{}]", String.valueOf(player), x, y, z);
			return true;
		}
	}

	public static boolean cantBreak(@Nonnull EntityPlayer player, double x, double y, double z)
	{
		int xx = floor_double(x);
		int yy = floor_double(y);
		int zz = floor_double(z);
		return cantBreak(player, xx, yy, zz);
	}

	public static boolean cantDamage(@Nonnull Entity attacker, @Nonnull Entity victim)
	{
		try
		{
			EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(toBukkitEntity(attacker), toBukkitEntity(victim), DamageCause.ENTITY_ATTACK, 0D);
			EventHelper.callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed call EntityDamageByEntityEvent: [Attacker: {}, Victim: {}]", String.valueOf(attacker), String.valueOf(victim));
			return true;
		}
	}

	public static boolean cantInteract(
			@Nonnull EntityPlayer player, @Nullable ItemStack stack, int x, int y, int z, @Nonnull ForgeDirection side)
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
			EventHelper.error(throwable, "Failed call PlayerInteractEvent: [Player: {}, Item: {}, X:{}, Y:{}, Z:{}, Side: {}]", String.valueOf(player), String.valueOf(stack), x, y, z, String.valueOf(side));
			return true;
		}
	}

	public static boolean cantFromTo(@Nonnull World world, int fromX, int fromY, int fromZ, int toX, int toY, int toZ)
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
			EventHelper.error(throwable, "Failed call BlockFromToEvent: [FromX: {}, FromY: {}, FromZ: {}, ToX: {}, ToY: {}, ToZ: {}]", fromX, fromY, fromZ, toX, toY, toZ);
			return true;
		}
	}

	public static boolean cantFromTo(
			@Nonnull World world, int fromX, int fromY, int fromZ, @Nonnull ForgeDirection direction)
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
			EventHelper.error(throwable, "Failed call BlockFromToEvent: [FromX: {}, FromY: {}, FromZ: {}, Direction: {}]", fromX, fromY, fromZ, String.valueOf(direction));
			return true;
		}
	}

	public static boolean isInPrivate(@Nonnull World world, int x, int y, int z)
	{
		try
		{
			return InjectionManager.isInPrivate(toBukkitWorld(world), x, y, z);
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed check private: [World: {}, X: {}, Y: {}, Z: {}]", world.getWorldInfo().getWorldName(), x, y, z);
			return true;
		}
	}

	public static boolean isPrivateMember(@Nonnull EntityPlayer player, double x, double y, double z)
	{
		int xx = floor_double(x);
		int yy = floor_double(y);
		int zz = floor_double(z);
		return isPrivateMember(player, xx, yy, zz);
	}

	public static boolean isPrivateMember(@Nonnull EntityPlayer player, int x, int y, int z)
	{
		try
		{
			return InjectionManager.isPrivateMember(toBukkitEntity(player), x, y, z);
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed check private member: [Player: {}, X: {}, Y: {}, Z: {}]", String.valueOf(player), x, y, z);
			return true;
		}
	}

	public static boolean isPrivateOwner(@Nonnull EntityPlayer player, double x, double y, double z)
	{
		int xx = floor_double(x);
		int yy = floor_double(y);
		int zz = floor_double(z);
		return isPrivateOwner(player, xx, yy, zz);
	}

	public static boolean isPrivateOwner(@Nonnull EntityPlayer player, int x, int y, int z)
	{
		try
		{
			return InjectionManager.isPrivateOwner(toBukkitEntity(player), x, y, z);
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed check private owner: [Player: {}, X: {}, Y: {}, Z: {}]", String.valueOf(player), x, y, z);
			return true;
		}
	}

	public static boolean isInPrivate(@Nonnull Entity entity)
	{
		int x = floor_double(entity.posX);
		int y = floor_double(entity.posY);
		int z = floor_double(entity.posZ);
		return isInPrivate(entity.worldObj, x, y, z);
	}

	public static boolean hasPermission(@Nullable EntityPlayer player, @Nonnull String permission)
	{
		if (player == null)
			return false;

		try
		{
			Player bPlayer = toBukkitEntity(player);
			return bPlayer != null && bPlayer.hasPermission(permission);
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed checking permission: [Player: {}, Permission: {}]", player, permission);
			return false;
		}
	}

	public static boolean hasPermission(@Nullable UUID playerId, @Nonnull String permission)
	{
		if (playerId == null)
			return false;

		try
		{
			Player player = Bukkit.getPlayer(playerId);
			return player != null && player.hasPermission(permission);
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed checking permission: [Player name: {}, Permission: {}]", playerId, permission);
			return false;
		}
	}

	public static boolean hasPermission(@Nullable String playerName, @Nonnull String permission)
	{
		if (Strings.isNullOrEmpty(playerName))
			return false;

		try
		{
			Player player = Bukkit.getPlayerExact(playerName);
			return player != null && player.hasPermission(permission);
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed checking permission: [Player UUID: {}, Permission: {}]", playerName, permission);
			return false;
		}
	}
}
