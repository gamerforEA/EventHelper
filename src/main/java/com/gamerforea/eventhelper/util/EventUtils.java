package com.gamerforea.eventhelper.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.util.Direction;

import com.flowpowered.math.vector.Vector3d;
import com.gamerforea.eventhelper.EventHelper;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

public final class EventUtils
{
	public static final boolean cantBreak(EntityPlayer player, BlockPos pos)
	{
		try
		{
			Cause cause = SpongeUtils.getCause(player);
			org.spongepowered.api.world.World world = SpongeUtils.getWorld(player.world);
			BlockSnapshot original = world.createSnapshot(pos.getX(), pos.getY(), pos.getZ());
			Transaction<BlockSnapshot> transaction = new Transaction<BlockSnapshot>(original, BlockSnapshot.NONE);
			transaction.setCustom(SpongeUtils.getAirSnapshot(world, pos));
			List<Transaction<BlockSnapshot>> transactions = Collections.singletonList(transaction);

			Event event = SpongeEventFactory.createChangeBlockEventBreak(cause, world, transactions);
			return Sponge.getEventManager().post(event);
		}
		catch (Throwable throwable)
		{
			err("Failed call ChangeBlockEvent.Break: [Player: {}, Pos: {}]", player, pos);
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean cantDamage(EntityPlayer player, Entity target)
	{
		try
		{
			Cause cause = SpongeUtils.getCause(player);
			org.spongepowered.api.entity.Entity targetEntity = (org.spongepowered.api.entity.Entity) target;

			Event event = SpongeEventFactory.createAttackEntityEvent(cause, Collections.EMPTY_LIST, targetEntity, 0, 0);
			return Sponge.getEventManager().post(event);
		}
		catch (Throwable throwable)
		{
			err("Failed call AttackEntityEvent: [Player: {}, Target: {}]", player, target);
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static final boolean cantInteract(EntityPlayer player, EnumHand hand, BlockPos pos, EnumFacing side)
	{
		try
		{
			Cause cause = SpongeUtils.getCause(player);
			HandType handType = SpongeUtils.getHandType(hand);
			Optional<Vector3d> interactionPoint = Optional.<Vector3d>empty();
			org.spongepowered.api.world.World world = SpongeUtils.getWorld(player.world);
			BlockSnapshot block = world.createSnapshot(pos.getX(), pos.getY(), pos.getZ());
			Direction direction = SpongeUtils.getDirection(side);

			Event event;
			switch (hand)
			{
				case OFF_HAND:
					event = SpongeEventFactory.createInteractBlockEventPrimaryOffHand(cause, handType, interactionPoint, block, direction);
					break;
				default:
					event = SpongeEventFactory.createInteractBlockEventPrimaryMainHand(cause, handType, interactionPoint, block, direction);
					break;
			}
			return Sponge.getEventManager().post(event);
		}
		catch (Throwable throwable)
		{
			err("Failed call PlayerInteractEvent: [Player: {}, Hand: {}, Pos: {}, Side: {}]", player, hand, pos, side);
			if (EventHelper.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	private static final void err(String format, Object... args)
	{
		EventHelper.logger.error(format, args);
	}
}