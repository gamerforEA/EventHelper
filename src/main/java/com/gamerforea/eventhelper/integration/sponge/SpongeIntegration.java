package com.gamerforea.eventhelper.integration.sponge;

import com.flowpowered.math.vector.Vector3d;
import com.gamerforea.eventhelper.EventHelperMod;
import com.gamerforea.eventhelper.integration.IIntegration;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.FakePlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.gamerforea.eventhelper.integration.sponge.SpongeUtils.*;
import static org.spongepowered.api.event.SpongeEventFactory.*;

public final class SpongeIntegration
{
	public static final String SPONGE_API_MODID = "spongeapi";
	private static final IIntegration INTEGRATION;

	private SpongeIntegration()
	{
	}

	public static boolean isSpongePresent()
	{
		return INTEGRATION != null;
	}

	@Nullable
	public static IIntegration getIntegration()
	{
		return INTEGRATION;
	}

	static
	{
		IIntegration integration = null;

		try
		{
			Class.forName("org.spongepowered.api.Server");
			integration = new SpongeIntegration0();
		}
		catch (NoClassDefFoundError | ClassNotFoundException e)
		{
			if (EventHelperMod.debug)
				EventHelperMod.LOGGER.info("SpongeAPI not found", e);
			else
				EventHelperMod.LOGGER.info("SpongeAPI not found");
		}

		INTEGRATION = integration;
	}

	private static final class SpongeIntegration0 implements IIntegration
	{
		private SpongeIntegration0()
		{
		}

		@Override
		public boolean cantBreak(@Nonnull EntityPlayer player, @Nonnull BlockPos pos)
		{
			try (CauseStackManager.StackFrame stackFrame = Sponge.getGame().getCauseStackManager().pushCauseFrame())
			{
				stackFrame.pushCause(player);
				if (player instanceof FakePlayer && player instanceof Player)
					stackFrame.addContext(EventContextKeys.FAKE_PLAYER, (Player) player);

				Cause cause = stackFrame.getCurrentCause();
				World world = getWorld(player.world);
				BlockSnapshot original = world.createSnapshot(pos.getX(), pos.getY(), pos.getZ());
				Transaction<BlockSnapshot> transaction = new Transaction<>(original, getAirSnapshot(world, pos));
				return Sponge.getEventManager().post(createChangeBlockEventBreak(cause, ImmutableList.of(transaction)));
			}
		}

		@Override
		public boolean cantPlace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState)
		{
			try (CauseStackManager.StackFrame stackFrame = Sponge.getGame().getCauseStackManager().pushCauseFrame())
			{
				stackFrame.pushCause(player);
				if (player instanceof FakePlayer && player instanceof Player)
					stackFrame.addContext(EventContextKeys.FAKE_PLAYER, (Player) player);

				Cause cause = stackFrame.getCurrentCause();
				World world = getWorld(player.world);
				BlockSnapshot original = world.createSnapshot(pos.getX(), pos.getY(), pos.getZ());
				Transaction<BlockSnapshot> transaction = new Transaction<>(original, getBlockSnapshot(world, pos, blockState));
				return Sponge.getEventManager().post(createChangeBlockEventPlace(cause, ImmutableList.of(transaction)));
			}
		}

		@Override
		public boolean cantReplace(@Nonnull EntityPlayer player, @Nonnull BlockPos pos, @Nonnull IBlockState blockState)
		{
			// TODO Use org.spongepowered.api.event.block.ChangeBlockEvent.Modify event
			return this.cantPlace(player, pos, blockState);
		}

		@Override
		public boolean cantAttack(@Nonnull EntityPlayer player, @Nonnull Entity victim)
		{
			try (CauseStackManager.StackFrame stackFrame = Sponge.getGame().getCauseStackManager().pushCauseFrame())
			{
				stackFrame.pushCause(player);
				if (player instanceof FakePlayer && player instanceof Player)
					stackFrame.addContext(EventContextKeys.FAKE_PLAYER, (Player) player);

				Cause cause = stackFrame.getCurrentCause();
				org.spongepowered.api.entity.Entity spongeVictim = (org.spongepowered.api.entity.Entity) victim;
				Event event = createAttackEntityEvent(cause, new ArrayList<>(), spongeVictim, 0, 0);
				return Sponge.getEventManager().post(event);
			}
		}

		@Override
		public boolean cantInteract(
				@Nonnull EntityPlayer player,
				@Nonnull EnumHand hand,
				@Nonnull BlockPos interactionPos, @Nonnull BlockPos targetPos, @Nonnull EnumFacing targetSide)
		{
			try (CauseStackManager.StackFrame stackFrame = Sponge.getGame().getCauseStackManager().pushCauseFrame())
			{
				stackFrame.pushCause(player);
				if (player instanceof FakePlayer && player instanceof Player)
					stackFrame.addContext(EventContextKeys.FAKE_PLAYER, (Player) player);

				Cause cause = stackFrame.getCurrentCause();
				HandType handType = getHandType(hand);
				Optional<Vector3d> interactionPoint = Optional.of(new Vector3d(interactionPos.getX(), interactionPos.getY(), interactionPos.getZ()));
				World world = getWorld(player.world);
				BlockSnapshot block = world.createSnapshot(targetPos.getX(), targetPos.getY(), targetPos.getZ());
				Direction targetSideSponge = getDirection(targetSide);
				Event event = hand == EnumHand.MAIN_HAND ? createInteractBlockEventPrimaryMainHand(cause, handType, interactionPoint, block, targetSideSponge) : createInteractBlockEventPrimaryOffHand(cause, handType, interactionPoint, block, targetSideSponge);
				return Sponge.getEventManager().post(event);
			}
		}

		@Override
		public boolean hasPermission(@Nonnull EntityPlayer player, @Nonnull String permission)
		{
			if (player instanceof Subject)
				return ((Subject) player).hasPermission(permission);
			Optional<User> user = getUser(player);
			return user.isPresent() && user.get().hasPermission(permission);
		}

		@Override
		public boolean hasPermission(@Nonnull UUID playerId, @Nonnull String permission)
		{
			Optional<User> user = getUser(playerId);
			return user.isPresent() && user.get().hasPermission(permission);
		}

		@Override
		public boolean hasPermission(@Nonnull String playerName, @Nonnull String permission)
		{
			Optional<User> user = getUser(playerName);
			return user.isPresent() && user.get().hasPermission(permission);
		}
	}
}
