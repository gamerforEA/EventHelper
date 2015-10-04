package com.gamerforea.eventhelper.util;

import static com.gamerforea.eventhelper.util.ConvertUtils.toBukkitEntity;
import static com.gamerforea.eventhelper.util.ConvertUtils.toBukkitWorld;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.gamerforea.eventhelper.EventHelper;
import com.gamerforea.eventhelper.wg.WGRegionChecker;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public final class EventUtils
{
	public static final boolean cantBreak(EntityPlayer player, int x, int y, int z)
	{
		try
		{
			BlockBreakEvent event = new BlockBreakEvent(toBukkitWorld(player.worldObj).getBlockAt(x, y, z), (Player) toBukkitEntity(player));
			EventHelper.callEvent(event);
			return event.isCancelled();
		}
		catch (Throwable throwable)
		{
			System.err.println(String.format("Failed call BlockBreakEvent: [Player: %s, X:%d, Y:%d, Z:%d]", player.toString(), x, y, z));
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
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
			System.err.println(String.format("Failed call EntityDamageByEntityEvent [Damager: %s, Damagee: %s]", damager.toString(), damagee.toString()));
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean isInPrivate(World world, int x, int y, int z)
	{
		try
		{
			return WGRegionChecker.isInPrivate(ConvertUtils.toBukkitWorld(world), x, y, z);
		}
		catch (Throwable throwable)
		{
			System.err.println(String.format("Failed check private [World: %s, X: %d, Y: %d, Z: %d]", world.getWorldInfo().getWorldName(), x, y, z));
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
}