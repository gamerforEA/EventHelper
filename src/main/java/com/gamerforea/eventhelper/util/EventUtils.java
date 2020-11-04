package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.EventHelper;
import com.gamerforea.eventhelper.inject.InjectionManager;
import com.gamerforea.eventhelper.util.function.ThrowableFunction;
import com.gamerforea.eventhelper.util.function.TriFunction;
import com.google.common.base.Strings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

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

	public static boolean cantTeleport(EntityPlayer player, World toWorld, double toX, double toY, double toZ)
	{
		return cantTeleport0(player, toWorld, toX, toY, toZ, ConvertUtils::toBukkitEntity, PlayerTeleportEvent::new, PlayerTeleportEvent.class);
	}

	public static boolean cantTeleport(EntityPlayer player, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ)
	{
		return cantTeleport0(player, fromWorld, fromX, fromY, fromZ, toWorld, toX, toY, toZ, ConvertUtils::toBukkitEntity, PlayerTeleportEvent::new, PlayerTeleportEvent.class);
	}

	public static boolean cantTeleport(Entity entity, World toWorld, double toX, double toY, double toZ)
	{
		if (entity instanceof EntityPlayer)
			return cantTeleport((EntityPlayer) entity, toWorld, toX, toY, toZ);
		return cantTeleport0(entity, toWorld, toX, toY, toZ, ConvertUtils::toBukkitEntity, EntityTeleportEvent::new, EntityTeleportEvent.class);
	}

	public static boolean cantTeleport(Entity entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ)
	{
		if (entity instanceof EntityPlayer)
			return cantTeleport((EntityPlayer) entity, fromWorld, fromX, fromY, fromZ, toWorld, toX, toY, toZ);
		return cantTeleport0(entity, fromWorld, fromX, fromY, fromZ, toWorld, toX, toY, toZ, ConvertUtils::toBukkitEntity, EntityTeleportEvent::new, EntityTeleportEvent.class);
	}

	private static <E extends Entity, BE extends org.bukkit.entity.Entity, EV extends Event & Cancellable> boolean cantTeleport0(E entity, World toWorld, double toX, double toY, double toZ, ThrowableFunction<? super E, ? extends BE, ?> entityConverter, TriFunction<? super BE, ? super Location, ? super Location, ? extends EV> eventConstuctor, Class<? extends EV> eventClass)
	{
		try
		{
			BE bukkitEntity = entityConverter.apply(entity);
			Location from = bukkitEntity.getLocation();
			Location to = new Location(ConvertUtils.toBukkitWorld(toWorld), toX, toY, toZ, from.getYaw(), from.getPitch());
			EV event = eventConstuctor.apply(bukkitEntity, from, to);
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed call {}: [Entity: {}, ToWorld: {}, ToX: {}, ToY: {}, ToZ: {}]", eventClass.getSimpleName(), String.valueOf(entity), toWorld.getWorldInfo().getWorldName(), toX, toY, toZ);
			return true;
		}
	}

	private static <E extends Entity, BE extends org.bukkit.entity.Entity, EV extends Event & Cancellable> boolean cantTeleport0(E entity, World fromWorld, double fromX, double fromY, double fromZ, World toWorld, double toX, double toY, double toZ, ThrowableFunction<? super E, ? extends BE, ?> entityConverter, TriFunction<? super BE, ? super Location, ? super Location, ? extends EV> eventConstuctor, Class<? extends EV> eventClass)
	{
		try
		{
			BE bukkitEntity = entityConverter.apply(entity);
			Location entityLocation = bukkitEntity.getLocation();
			Location from = new Location(ConvertUtils.toBukkitWorld(fromWorld), fromX, fromY, fromZ, entityLocation.getYaw(), entityLocation.getPitch());
			Location to = new Location(ConvertUtils.toBukkitWorld(toWorld), toX, toY, toZ, from.getYaw(), from.getPitch());
			EV event = eventConstuctor.apply(bukkitEntity, from, to);
			Bukkit.getPluginManager().callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			EventHelper.error(throwable, "Failed call {}: [Entity: {}, FromWorld: {}, FromX: {}, FromY: {}, FromZ: {}, ToWorld: {}, ToX: {}, ToY: {}, ToZ: {}]", eventClass.getSimpleName(), String.valueOf(entity), fromWorld.getWorldInfo().getWorldName(), fromX, fromY, fromZ, toWorld.getWorldInfo().getWorldName(), toX, toY, toZ);
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
