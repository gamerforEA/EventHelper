package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.EventHelperMod;
import com.gamerforea.eventhelper.integration.IIntegration;
import com.gamerforea.eventhelper.integration.bukkit.BukkitIntegration;
import com.gamerforea.eventhelper.integration.sponge.SpongeIntegration;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.UUID;

import static com.gamerforea.eventhelper.EventHelperMod.LOGGER;

public final class EventUtils
{
	private static final IIntegration INTEGRATION;

	public static boolean cantBreak(@Nonnull EntityPlayer player, @Nonnull BlockPos pos)
	{
		try
		{
			return INTEGRATION.cantBreak(player, pos);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call ChangeBlockEvent.Break: [Player: {}, Pos: {}]", player, pos);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantAttack(@Nonnull EntityPlayer player, @Nonnull Entity victim)
	{
		try
		{
			return INTEGRATION.cantAttack(player, victim);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call AttackEntityEvent: [Player: {}, Victim: {}]", player, victim);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantInteract(
			@Nonnull EntityPlayer player,
			@Nonnull EnumHand hand, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide)
	{
		try
		{
			return INTEGRATION.cantInteract(player, hand, targetPos, targetSide);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call PlayerInteractEvent: [Player: {}, Hand: {}, Pos: {}, Side: {}]", player, hand, targetPos, targetSide);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean cantInteract(
			@Nonnull EntityPlayer player,
			@Nonnull EnumHand hand,
			@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide)
	{
		try
		{
			return INTEGRATION.cantInteract(player, hand, interactionPos, targetPos, targetSide);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed call PlayerInteractEvent: [Player: {}, Hand: {}, Pos: {}, Side: {}]", player, hand, targetPos, targetSide);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return true;
		}
	}

	public static boolean hasPermission(@Nonnull EntityPlayer player, @Nonnull String permission)
	{
		try
		{
			return INTEGRATION.hasPermission(player, permission);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed checking permission: [Player: {}, Permission: {}]", player, permission);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return false;
		}
	}

	public static boolean hasPermission(@Nonnull UUID playerId, @Nonnull String permission)
	{
		try
		{
			return INTEGRATION.hasPermission(playerId, permission);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed checking permission: [Player name: {}, Permission: {}]", playerId, permission);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return false;
		}
	}

	public static boolean hasPermission(@Nonnull String playerName, @Nonnull String permission)
	{
		try
		{
			return INTEGRATION.hasPermission(playerName, permission);
		}
		catch (Throwable throwable)
		{
			LOGGER.error("Failed checking permission: [Player UUID: {}, Permission: {}]", playerName, permission);
			if (EventHelperMod.debug)
				throwable.printStackTrace();
			return false;
		}
	}

	static
	{
		IIntegration integration = null;
		switch (EventHelperMod.integrationType)
		{
			case AUTO:
				integration = SpongeIntegration.getIntegration();
				if (integration == null)
					integration = BukkitIntegration.getIntegration();
				break;
			case SPONGE:
				integration = SpongeIntegration.getIntegration();
				break;
			case BUKKIT:
				integration = BukkitIntegration.getIntegration();
				break;
		}
		INTEGRATION = Objects.requireNonNull(integration, "Integration not found");
	}
}